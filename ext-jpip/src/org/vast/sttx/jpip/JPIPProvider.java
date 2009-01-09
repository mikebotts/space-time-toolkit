/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2008
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <alex.robin@sensiasoftware.com>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.sttx.jpip;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import jj2000.j2k.codestream.HeaderInfo;
import jj2000.j2k.codestream.PrecInfo;
import jj2000.j2k.codestream.reader.HeaderDecoder;
import jj2000.j2k.decoder.Decoder;
import jj2000.j2k.decoder.DecoderSpecs;
import jj2000.j2k.entropy.decoder.EntropyDecoder;
import jj2000.j2k.image.BlkImgDataSrc;
import jj2000.j2k.image.DataBlkInt;
import jj2000.j2k.image.ImgDataConverter;
import jj2000.j2k.image.invcomptransf.InvCompTransf;
import jj2000.j2k.io.RandomAccessIO;
import jj2000.j2k.quantization.dequantizer.Dequantizer;
import jj2000.j2k.util.ParameterList;
import jj2000.j2k.wavelet.synthesis.InverseWT;
import org.vast.cdm.common.DataBlock;
import org.vast.cdm.common.DataType;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataArray;
import org.vast.data.DataBlockByte;
import org.vast.data.DataBlockFactory;
import org.vast.data.DataBlockFloat;
import org.vast.data.DataBlockInt;
import org.vast.data.DataBlockMixed;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.jpip.JPIPBitstreamReader;
import org.vast.jpip.JPIPHttpClient;
import org.vast.jpip.JPIPListener;
import org.vast.jpip.message.ViewWindowField;
import org.vast.math.Matrix3d;
import org.vast.math.Vector3d;
import org.vast.util.Bbox;
import org.vast.util.SpatialExtent;
import org.vast.sensorML.ProcessLoader;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.DataException;
import org.vast.stt.dynamics.SceneBboxUpdater;
import org.vast.stt.dynamics.SpatialExtentUpdater;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.AbstractProvider;
import org.vast.stt.provider.google.GoogleMapTileNumber;
import org.vast.stt.provider.google.GoogleMapTileXYZ;
import org.vast.stt.provider.tiling.QuadTreeItem;
import org.vast.stt.provider.tiling.TiledMapProvider;
import org.vast.stt.provider.tiling.TiledMapSelector;
import org.vast.sttx.jp2k.RPCGridGenerator;


/**
 * <p><b>Title:</b><br/>
 * JPIP Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * 
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin <alex.robin@sensiasoftware.com>
 * @date Mar 10, 2008
 * @version 1.0
 */
public class JPIPProvider extends TiledMapProvider implements JPIPListener
{
	protected static final int GRAY = 0;
    protected static final int RGB = 1;
    protected static final int RGBA = 2;
    
	protected String server;
    protected String target;
    protected int[] components;
    protected BlkImgDataSrc decodedImage;
    protected JPIPBitstreamReader jpipReader;
    protected InverseWT inverseWT;
    
    protected byte[] imgData;
    protected int imgResLevel = 3;
    protected float offULat = -0.139071f;
    protected float offULon = +0.755574f;
    protected float offVLat = -0.528738f;
    protected float offVLon = -0.117475f;
    protected float lon0 = +27.306630f;
    protected float lat0 = -22.673679f;    
    protected Vector3d origin = new Vector3d(lon0, lat0, 0);
    protected Matrix3d mat = new Matrix3d(offULon, offVLon, 0, offULat, offVLat, 0, 0, 0, 1);
    
    
    public JPIPProvider()
    {
    	ProcessLoader.processMap.put("urn:ogc:def:process:CSM:RPC:1.0", "org.sensorML.process.RPC_Process");
    	mat.inverse();
    }
    
    
    public void setFootprint(float[][] corners)
    {
    	lon0 = corners[0][0];
    	lat0 = corners[0][1];
    	
    	offULon = corners[1][0] - lon0;
    	offULat = corners[1][1] - lat0;
    	
    	offVLon = corners[3][0] - lon0;
    	offVLat = corners[3][1] - lat0;
    	
    	origin = new Vector3d(lon0, lat0, 0);
        mat = new Matrix3d(offULon, offVLon, 0, offULat, offVLat, 0, 0, 0, 1);
        mat.inverse();
    }
    
    
    class GetTileRunnable implements Runnable
    {
        protected QuadTreeItem item;
        protected byte[] tileData;
        
        
        public GetTileRunnable(QuadTreeItem item)
        {
            this.item = item;
        }
        
        
        public void run()
        {
            try
            {
                // create treeObject
                BlockListItem[] blockArray = new BlockListItem[2];

                // set resolution levels
                int resLevel = item.getLevel();
                inverseWT.setImgResLevel(resLevel);
                inverseWT.setTile(0, 0);
                
                // prepare tile
                int width = decodedImage.getImgWidth();
                int height = decodedImage.getImgHeight();
                int tileWidth = (int)(item.getSizeX() * width);
                int tileHeight = (int)(item.getSizeY() * height);
                int tileOX = (int)(item.getMinX() * width);
                int tileOY = (int)(item.getMinY() * height);
                tileData = new byte[tileWidth*tileHeight*3];
                                
                // retrieve jpip data and build image
                int[] fsiz = new int[] { width, height, ViewWindowField.CLOSEST };
                int[] rsiz = new int[] { tileWidth, tileHeight };
                int[] roff = new int[] { tileOX, tileOY };
                int[][] comps = new int[][] { { 0, 2 } };
                int layers = 10;
                jpipReader.requestData(fsiz, rsiz, roff, comps, layers);
                refreshTile(decodedImage, tileOX, tileOY, tileWidth, tileHeight, 3);
                DataBlockByte imageArrayBlock = DataBlockFactory.createBlock(tileData);
                DataBlockInt imageWidthBlock = DataBlockFactory.createBlock(new int[] {tileWidth});
                DataBlockInt imageHeightBlock = DataBlockFactory.createBlock(new int[] {tileHeight});
                DataBlockMixed imageBlock = DataBlockFactory.createMixedBlock(imageWidthBlock, imageHeightBlock, imageArrayBlock);
                
                // build grid
                int gridWidth = 10;
                int gridLength = 10;
                double minY = item.getMinY();
                double maxY = item.getMaxY();
                double minX = item.getMinX();
                double maxX = item.getMaxX();
                double dX = (maxX - minX) / (gridWidth - 1);
                double dY = (maxY - minY) / (gridLength - 1);
                DataBlockFloat gridArrayBlock = DataBlockFactory.createBlock(new float[gridLength*gridWidth*4]);
                DataBlockInt gridWidthBlock = DataBlockFactory.createBlock(new int[] {10});
                DataBlockInt gridHeightBlock = DataBlockFactory.createBlock(new int[] {10});
                DataBlockMixed gridBlock = DataBlockFactory.createMixedBlock(gridWidthBlock, gridHeightBlock, gridArrayBlock);
                
                // compute data for grid block
                int valCount = 0;
                for (int v=0; v<gridLength; v++)
                {
                    for (int u=0; u<gridWidth; u++)
                    {
                        double x = lon0 + offULon * (minX + dX*u) + offVLon * (minY + dY*v);
                    	double y = lat0 + offULat * (minX + dX*u) + offVLat * (minY + dY*v);
                        x *= DTR;
                        y *= DTR;
                        
                        // write lat and lon value
                        gridArrayBlock.setDoubleValue(valCount, y);
                        gridArrayBlock.setDoubleValue(valCount+1, x);
                        gridArrayBlock.setDoubleValue(valCount+2, (float)u / ((float)gridWidth-1));
                        gridArrayBlock.setDoubleValue(valCount+3, (float)v / ((float)gridLength-1));
                        valCount += 4;
                    }
                }
                
                // add blocks to dataNode
                if (!canceled)
                {
                    // add blocks to data node
                    item.setData(blockArray);
                    blockArray[0] = blockLists[0].addBlock(imageBlock);
                    blockArray[1] = blockLists[1].addBlock(gridBlock);
                    
                    // remove sub and super items now that we have the new tile
                    removeChildrenData(item);
                    removeHiddenParent(item);
                    
                    // send event for redraw
                    dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
                }
            }
            catch (Exception e)
            {
                if (!canceled)
                    e.printStackTrace();
            }           
        }
        
        
        public void refreshTile(BlkImgDataSrc src, int ox, int oy, int width, int height, int numComps)
        {
            int tmp1, tmp2, tmp3, tmp4; // temporary storage for sample values
            int mv1, mv2, mv3, mv4; // max value for each component
            int ls1, ls2, ls3, ls4; // level shift for each component
            int fb1, fb2, fb3, fb4; // fractional bits for each component
            int[] data1, data2, data3, data4; // references to data buffers
            DataBlkInt db1, db2, db3, db4; // data-blocks to request data from src
            boolean prog; // Flag for progressive data

            if (numComps < 0)
                numComps = src.getNumComps();
            
            //System.out.println("Redrawing " + ox + "," + oy + " - " + (ox+width) + "," + (oy+height));
            
            // Check for image type
            int type;
            switch (numComps)
            {
                case 1:
                    type = GRAY;
                    break;
                case 3:
                    type = RGB;
                    break;
                case 4:
                    type = RGBA;
                    break;
                default:
                    throw new IllegalArgumentException("Only 1, 3, and 4 components " + "supported");
            }

            // Initialize
            ls2 = fb2 = mv2 = 0; // to keep compiler happy
            ls3 = fb3 = mv3 = 0; // to keep compiler happy
            ls4 = fb4 = mv4 = 0; // to keep compiler happy
            db1 = db2 = db3 = db4 = null; // to keep compiler happy
            
            switch (type)
            {
                case RGBA:
                    //db4 = new DataBlkInt(); // Alpha plane
                    ls4 = 1 << (src.getNomRangeBits(3) - 1);
                    mv4 = (1 << src.getNomRangeBits(3)) - 1;
                    fb4 = src.getFixedPoint(3);
                    
                case RGB:
                    //db3 = new DataBlkInt(); // Blue plane
                    ls3 = 1 << (src.getNomRangeBits(2) - 1);
                    mv3 = (1 << src.getNomRangeBits(2)) - 1;
                    fb3 = src.getFixedPoint(2);
                    //db2 = new DataBlkInt(); // Green plane
                    ls2 = 1 << (src.getNomRangeBits(1) - 1);
                    mv2 = (1 << src.getNomRangeBits(1)) - 1;
                    fb2 = src.getFixedPoint(1);
                    
                case GRAY:
                    //db1 = new DataBlkInt(); // Gray or Red plane
                    ls1 = 1 << (src.getNomRangeBits(0) - 1);
                    mv1 = (1 << src.getNomRangeBits(0)) - 1;
                    fb1 = src.getFixedPoint(0);
                    break;
                    
                default:
                    throw new Error("Internal JJ2000 error");
            }
            
            src.setTile(0, 0);
            //int height = src.getImgHeight();
            //int width = src.getImgWidth();
            
            // Request tile data
            prog = false;
            switch (type)
            {
                case RGBA:
                    db4 = new DataBlkInt(ox, oy, width, height);
                    src.getInternCompData(db4, 3);
                    prog = prog || db4.progressive;
                    
                case RGB:
                    db2 = new DataBlkInt(ox, oy, width, height);
                    db3 = new DataBlkInt(ox, oy, width, height);
                    src.getInternCompData(db3, 2);
                    prog = prog || db3.progressive;
                    src.getInternCompData(db2, 1);
                    prog = prog || db2.progressive;
                    
                case GRAY:
                    db1 = new DataBlkInt(ox, oy, width, height);
                    src.getInternCompData(db1, 0);
                    prog = prog || db1.progressive;
                    break;
            }
            
            // Put pixel data in buffer
            switch (type)
            {
                case GRAY:
                    data1 = db1.data;
                    for (int i = 0; i < data1.length; i++)
                    {
                        tmp1 = (data1[i] >> fb1) + ls1;
                        tmp1 = (tmp1 < 0) ? 0 : ((tmp1 > mv1) ? mv1 : tmp1);
                        //int val = (0xFF << 24) | (tmp1 << 16) | (tmp1 << 8) | tmp1;
                        tileData[i] = (byte)tmp1;
                    }
                    break;
                    
                case RGB:
                    data1 = db1.data; // red
                    data2 = db2.data; // green
                    data3 = db3.data; // blue
                    for (int i = 0; i < data1.length; i++)
                    {
                        tmp1 = (data1[i] >> fb1) + ls1;
                        tmp1 = (tmp1 < 0) ? 0 : ((tmp1 > mv1) ? mv1 : tmp1);
                        tmp2 = (data2[i] >> fb2) + ls2;
                        tmp2 = (tmp2 < 0) ? 0 : ((tmp2 > mv2) ? mv2 : tmp2);
                        tmp3 = (data3[i] >> fb3) + ls3;
                        tmp3 = (tmp3 < 0) ? 0 : ((tmp3 > mv3) ? mv3 : tmp3);
                        //int val = (0xFF << 24) | (tmp1 << 16) | (tmp2 << 8) | tmp3;
                        int s = i * 3;
                        tileData[s] = (byte)tmp1;
                        tileData[s+1] = (byte)tmp2;
                        tileData[s+2] = (byte)tmp3;
                    }
                    break;
                    
                case RGBA:
                    data1 = db1.data; // red
                    data2 = db2.data; // green
                    data3 = db3.data; // blue
                    data4 = db4.data; // alpha
                    for (int i = 0; i < data1.length; i++)
                    {
                        tmp1 = (data1[i] >> fb1) + ls1;
                        tmp1 = (tmp1 < 0) ? 0 : ((tmp1 > mv1) ? mv1 : tmp1);
                        tmp2 = (data2[i] >> fb2) + ls2;
                        tmp2 = (tmp2 < 0) ? 0 : ((tmp2 > mv2) ? mv2 : tmp2);
                        tmp3 = (data3[i] >> fb3) + ls3;
                        tmp3 = (tmp3 < 0) ? 0 : ((tmp3 > mv3) ? mv3 : tmp3);
                        tmp4 = (data4[i] >> fb4) + ls4;
                        tmp4 = (tmp4 < 0) ? 0 : ((tmp4 > mv4) ? mv4 : tmp4);
                        //int val = (tmp4 << 24) | (tmp1 << 16) | (tmp2 << 8) | tmp3;
                        //buffer[i + l*imgwidth] = val;
                        int s = i * 4;
                        tileData[s] = (byte)tmp1;
                        tileData[s+1] = (byte)tmp2;
                        tileData[s+2] = (byte)tmp3;
                        tileData[s+3] = (byte)tmp4;
                    }
                    break;
            }
        }
    }
    
    
    
    @Override
    protected SpatialExtent transformBbox(SpatialExtent extent)
    {
    	// convert to rectified grid extent
    	SpatialExtent projExtent = new SpatialExtent();
    	Vector3d point;
    	
    	point = new Vector3d(extent.getMinX(), extent.getMinY(), 0);
    	increaseExtent(projExtent, point);
    	
    	point = new Vector3d(extent.getMaxX(), extent.getMinY(), 0);
    	increaseExtent(projExtent, point);
    	
    	point = new Vector3d(extent.getMaxX(), extent.getMaxY(), 0);
    	increaseExtent(projExtent, point);
    	
    	point = new Vector3d(extent.getMinX(), extent.getMaxY(), 0);
    	increaseExtent(projExtent, point);
    	
    	return projExtent;
    }
    
    
    
    protected void increaseExtent(SpatialExtent projExtent, Vector3d point)
    {
    	point.sub(origin);
    	mat.transform(point);
    	point.x = Math.max(point.x, 0.0);
    	point.x = Math.min(point.x, 1.0);
    	point.y = Math.max(point.y, 0.0);
    	point.y = Math.min(point.y, 1.0);
    	projExtent.resizeToContain(point.x, point.y, 0.0);
    }
    
    
    @Override
    protected void getNewTile(QuadTreeItem item)
    {
        GetTileRunnable getTile = new GetTileRunnable(item);
        //Thread newThread = new Thread(getTile, nextItem.toString());
        //newThread.start();
        getTile.run();
    }
    
    
    protected ParameterList getDefaultParameters()
    {
        String[][] param = Decoder.getAllParameters();
        ParameterList defpl = new ParameterList();
        
        for (int i=param.length-1; i>=0; i--)
        {
            if (param[i][3] != null)
                defpl.put(param[i][0], param[i][3]);
        }

        // Create parameter list using defaults
        return new ParameterList(defpl);
    }


    @Override
    public void init() throws DataException
    {
        // create block list for raster tiles
        DataGroup pixelData = new DataGroup(3);
        pixelData.setName("pixel");
        pixelData.addComponent("red", new DataValue(DataType.BYTE));
        pixelData.addComponent("green", new DataValue(DataType.BYTE));
        pixelData.addComponent("blue", new DataValue(DataType.BYTE));

        DataValue imgWidthData = new DataValue("width", DataType.INT);
        DataValue imgHeightData = new DataValue("height", DataType.INT);
        
        DataArray imgRowData = new DataArray(imgWidthData, true);
        imgRowData.addComponent(pixelData);
        imgRowData.setName("row");
        
        DataArray imgArrayData = new DataArray(imgHeightData, true);
        imgArrayData.addComponent(imgRowData);
        imgArrayData.setName("array");
                
        DataGroup rasterData = new DataGroup(3, "image");
        rasterData.addComponent(imgWidthData);
        rasterData.addComponent(imgHeightData);
        rasterData.addComponent(imgArrayData);
        
                
        // create block list for grid tiles
        DataGroup pointData = new DataGroup(4);
        pointData.setName("point");
        pointData.addComponent("lat", new DataValue(DataType.FLOAT));
        pointData.addComponent("lon", new DataValue(DataType.FLOAT));
        pointData.addComponent("imgX", new DataValue(DataType.FLOAT));
        pointData.addComponent("imgY", new DataValue(DataType.FLOAT));
        
        DataValue gridWidthData = new DataValue("width", DataType.INT);
        DataValue gridHeightData = new DataValue("height", DataType.INT);
        
        DataArray gridRowData = new DataArray(gridWidthData, true);
        gridRowData.addComponent(pointData);
        gridRowData.setName("row");
        
        DataArray gridArrayData = new DataArray(gridHeightData, true);
        gridArrayData.addComponent(gridRowData);
        gridArrayData.setName("array");
        
        DataGroup gridData = new DataGroup(3, "grid");
        gridData.addComponent(gridWidthData);
        gridData.addComponent(gridHeightData);
        gridData.addComponent(gridArrayData);
        
        //System.out.println(imageData);
        //System.out.println(gridData);
        blockLists[0] = dataNode.createList(rasterData);        
        blockLists[1] = dataNode.createList(gridData);
        dataNode.setNodeStructureReady(true);
        
        
        // init jpip session + get image info
    	try
		{
    		ParameterList params = getDefaultParameters();
    		jpipReader = JPIPBitstreamReader.createJpipReader(server, target, params, true);
			//jpipReader.setListener(this);
			
			// read header options
			HeaderDecoder hd = jpipReader.getHeaderDecoder();
			DecoderSpecs decSpec = jpipReader.getDecoderSpecs();
			int nComps = hd.getNumComps();
			int[] bitDepths = new int[nComps];
			for (int i = 0; i < nComps; i++)
			    bitDepths[i] = hd.getOriginalBitDepth(i);

			// construct JJ2000 processing chain
			EntropyDecoder entdec = hd.createEntropyDecoder(jpipReader, params);
			//ROIDeScaler roids = hd.createROIDeScaler(entdec, params, decSpec);
			Dequantizer deq = hd.createDequantizer(entdec, bitDepths, decSpec);
			inverseWT = InverseWT.createInstance(deq, decSpec);
			ImgDataConverter converter = new ImgDataConverter(inverseWT, 0);
			InvCompTransf ictransf = new InvCompTransf(converter, decSpec, bitDepths, params);
			decodedImage = ictransf;
			
			// set tile selector res levels
			int resLevels = (Integer)decSpec.dls.getTileDef(0);
			tileSelector = new TiledMapSelector(3, 3, 0, resLevels);
	        tileSelector.setItemLists(selectedItems, deletedItems, blockLists);
	        
	        // set quad tree root extent (= max request)
	        SpatialExtent extent = new SpatialExtent();
	        extent.setMinX(0.0);
	        extent.setMaxX(1.0);
	        extent.setMinY(0.0);
	        extent.setMaxY(1.0);
	        quadTree.init(extent);
	        tileSelector.setMaxExtent(extent);
	        
	        // set tile sizes in updater
	        int tileWidth = hd.getImgWidth() >> resLevels;
	        int tileHeight = hd.getImgHeight() >> resLevels;
	        SpatialExtentUpdater updater = this.getSpatialExtent().getUpdater();
	        if (updater != null)
	        {
	            if (updater instanceof SceneBboxUpdater)
	                ((SceneBboxUpdater) updater).setTilesize(tileWidth, tileHeight);
	            updater.update();
	        }
	        
	        //decodingThread.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }
    
    
    @Override
	public void cancelUpdate()
	{
		this.canceled = true;
		if (jpipReader != null)
			jpipReader.cancelRequest();
	}


    protected void loadGrid() throws Exception
    {
        RPCGridGenerator rpcGridGen = new org.vast.sttx.jp2k.RPCGridGenerator();
        rpcGridGen.setLength(50);
        rpcGridGen.setWidth(50);
        SpatialExtent bounds = new SpatialExtent();
        // TODO dynamically set this up!
        bounds.setMinX(27.078285);
        bounds.setMinY(-23.839041);
        bounds.setMaxX(27.951421);
        bounds.setMaxY(-23.17148);
        rpcGridGen.setBounds(bounds);
        DataBlock gridBlock = rpcGridGen.createGrid();
    }
    
    
    boolean newPacketsAvailable = false;
    Bbox updateBox = new Bbox();
    
    public void precinctReceived(int comp, int packetResLevel, PrecInfo prec)
    {
        synchronized (updateBox)
        {
            this.newPacketsAvailable = true;
            
            // figure out what region of the image to update!!
            System.out.println("res=" + packetResLevel + " -> " + prec.ulx + "," + prec.uly + " - " + prec.w + "x" + prec.h);
            int shift = imgResLevel - packetResLevel;
            int ox = prec.ulx << shift;
            int oy = prec.uly << shift;
            int w = prec.w << shift;
            int h = prec.h << shift;
            
            // add to updateBbox
            updateBox.resizeToContain(ox, oy, 0);
            updateBox.resizeToContain(ox+w, oy+h, 0);            
            updateBox.notify();
        }
    }
    
    
    Thread decodingThread = new Thread("JPIP Decoding")
    {
        public void run()
        {
            while (true)
            {
                Bbox updateBoxCpy = null;
                
                try
                {
                	synchronized (updateBox)
                    {
                        while (!newPacketsAvailable)
                        	updateBox.wait();
                        
                        updateBoxCpy = updateBox.copy();
                        updateBox.nullify();
                        newPacketsAvailable = false;
                    }
                    
                    int ox = (int)updateBoxCpy.getMinX();
                    int oy = (int)updateBoxCpy.getMinY();
                    int w = (int)updateBoxCpy.getSizeX();
                    int h = (int)updateBoxCpy.getSizeY();
                    refreshImage(decodedImage, ox, oy, w, h, 3);
                    
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
    };


    public void refreshImage(BlkImgDataSrc src, int ox, int oy, int width, int height, int numComps)
    {
        int i, k1, k2, k3, k4, l; // counters
        int tmp1, tmp2, tmp3, tmp4; // temporary storage for sample values
        int mv1, mv2, mv3, mv4; // max value for each component
        int ls1, ls2, ls3, ls4; // level shift for each component
        int fb1, fb2, fb3, fb4; // fractional bits for each component
        int[] data1, data2, data3, data4; // references to data buffers
        DataBlkInt db1, db2, db3, db4; // data-blocks to request data from src
        boolean prog; // Flag for progressive data

        if (numComps < 0)
            numComps = src.getNumComps();
        
        int imgheight = src.getImgHeight();
        int imgwidth = src.getImgWidth();
        
        if (ox >= imgwidth)
            ox -= imgwidth;
        
        if (oy >= imgheight)
            oy -= imgheight;
        
        int dw = (ox + width) - imgwidth;
        if (dw > 0)
            width -= dw;
        
        int dh = (oy + height) - imgheight;
        if (dh > 0)
            height -= dh;
        
        //System.out.println("Redrawing " + ox + "," + oy + " - " + (ox+width) + "," + (oy+height));
        
        // Check for image type
        int type;
        switch (numComps)
        {
            case 1:
                type = GRAY;
                break;
            case 3:
                type = RGB;
                break;
            case 4:
                type = RGBA;
                break;
            default:
                throw new IllegalArgumentException("Only 1, 3, and 4 components " + "supported");
        }

        // Initialize
        ls2 = fb2 = mv2 = 0; // to keep compiler happy
        ls3 = fb3 = mv3 = 0; // to keep compiler happy
        ls4 = fb4 = mv4 = 0; // to keep compiler happy
        db1 = db2 = db3 = db4 = null; // to keep compiler happy
        
        switch (type)
        {
            case RGBA:
                //db4 = new DataBlkInt(); // Alpha plane
                ls4 = 1 << (src.getNomRangeBits(3) - 1);
                mv4 = (1 << src.getNomRangeBits(3)) - 1;
                fb4 = src.getFixedPoint(3);
                
            case RGB:
                //db3 = new DataBlkInt(); // Blue plane
                ls3 = 1 << (src.getNomRangeBits(2) - 1);
                mv3 = (1 << src.getNomRangeBits(2)) - 1;
                fb3 = src.getFixedPoint(2);
                //db2 = new DataBlkInt(); // Green plane
                ls2 = 1 << (src.getNomRangeBits(1) - 1);
                mv2 = (1 << src.getNomRangeBits(1)) - 1;
                fb2 = src.getFixedPoint(1);
                
            case GRAY:
                //db1 = new DataBlkInt(); // Gray or Red plane
                ls1 = 1 << (src.getNomRangeBits(0) - 1);
                mv1 = (1 << src.getNomRangeBits(0)) - 1;
                fb1 = src.getFixedPoint(0);
                break;
                
            default:
                throw new Error("Internal JJ2000 error");
        }
        
        src.setTile(0, 0);
        //int height = src.getImgHeight();
        //int width = src.getImgWidth();
        
        // Deliver in lines to reduce memory usage
        for (l = oy; l < oy+height; l++)
        {
            // Request line data
            prog = false;
            switch (type)
            {
                case RGBA:
                    // Request alpha plane
                    //db4.ulx = ox;
                    //db4.uly = l;
                    //db4.w = width;
                    //db4.h = 1;
                    db4 = new DataBlkInt(ox, l, width, 1);
                    src.getInternCompData(db4, 3);
                    prog = prog || db4.progressive;
                    
                case RGB:
                    // Request blue and green planes
                    //db2.ulx = db3.ulx = ox;
                    //db2.uly = db3.uly = l;
                    //db2.w = db3.w = width;
                    //db2.h = db3.h = 1;
                    db2 = new DataBlkInt(ox, l, width, 1);
                    db3 = new DataBlkInt(ox, l, width, 1);
                    src.getInternCompData(db3, 2);
                    prog = prog || db3.progressive;
                    src.getInternCompData(db2, 1);
                    prog = prog || db2.progressive;
                    
                case GRAY:
                    // Request 
                    //db1.ulx = ox;
                    //db1.uly = l;
                    //db1.w = width;
                    //db1.h = 1;
                    db1 = new DataBlkInt(ox, l, width, 1);
                    src.getInternCompData(db1, 0);
                    prog = prog || db1.progressive;
                    break;
            }
            
            if (prog)
            { // Progressive data not supported
                // We use abort since maybe at a later time 
                // the data won't
                // be progressive anymore
                // (DSC: this need to be improved of course)
                
                return; // can not continue processing
            }
            
            // Put pixel data in buffer
            switch (type)
            {
                case GRAY:
                    data1 = db1.data;
                    k1 = db1.offset + width - 1;
                    for (i = ox + width - 1; i >= ox; i--)
                    {
                        tmp1 = (data1[k1--] >> fb1) + ls1;
                        tmp1 = (tmp1 < 0) ? 0 : ((tmp1 > mv1) ? mv1 : tmp1);
                        //int val = (0xFF << 24) | (tmp1 << 16) | (tmp1 << 8) | tmp1;
                        //buffer[i + l*imgwidth] = val;
                        int s = i + l*imgwidth;
                        imgData[s] = (byte)tmp1;
                    }
                    break;
                    
                case RGB:
                    data1 = db1.data; // red
                    data2 = db2.data; // green
                    data3 = db3.data; // blue
                    k1 = db1.offset + width - 1;
                    k2 = db2.offset + width - 1;
                    k3 = db3.offset + width - 1;
                    for (i = ox + width - 1; i >= ox; i--)
                    {
                        tmp1 = (data1[k1--] >> fb1) + ls1;
                        tmp1 = (tmp1 < 0) ? 0 : ((tmp1 > mv1) ? mv1 : tmp1);
                        tmp2 = (data2[k2--] >> fb2) + ls2;
                        tmp2 = (tmp2 < 0) ? 0 : ((tmp2 > mv2) ? mv2 : tmp2);
                        tmp3 = (data3[k3--] >> fb3) + ls3;
                        tmp3 = (tmp3 < 0) ? 0 : ((tmp3 > mv3) ? mv3 : tmp3);
                        //int val = (0xFF << 24) | (tmp1 << 16) | (tmp2 << 8) | tmp3;
                        //buffer[i + l*imgwidth] = val;
                        int s = (i + l*imgwidth) * 3;
                        imgData[s] = (byte)tmp1;
                        imgData[s+1] = (byte)tmp2;
                        imgData[s+2] = (byte)tmp3;
                    }
                    break;
                    
                case RGBA:
                    data1 = db1.data; // red
                    data2 = db2.data; // green
                    data3 = db3.data; // blue
                    data4 = db4.data; // alpha
                    k1 = db1.offset + width - 1;
                    k2 = db2.offset + width - 1;
                    k3 = db3.offset + width - 1;
                    k4 = db4.offset + width - 1;
                    for (i = ox + width - 1; i >= ox; i--)
                    {
                        tmp1 = (data1[k1--] >> fb1) + ls1;
                        tmp1 = (tmp1 < 0) ? 0 : ((tmp1 > mv1) ? mv1 : tmp1);
                        tmp2 = (data2[k2--] >> fb2) + ls2;
                        tmp2 = (tmp2 < 0) ? 0 : ((tmp2 > mv2) ? mv2 : tmp2);
                        tmp3 = (data3[k3--] >> fb3) + ls3;
                        tmp3 = (tmp3 < 0) ? 0 : ((tmp3 > mv3) ? mv3 : tmp3);
                        tmp4 = (data4[k4--] >> fb4) + ls4;
                        tmp4 = (tmp4 < 0) ? 0 : ((tmp4 > mv4) ? mv4 : tmp4);
                        //int val = (tmp4 << 24) | (tmp1 << 16) | (tmp2 << 8) | tmp3;
                        //buffer[i + l*imgwidth] = val;
                        int s = (i + l*imgwidth) * 4;
                        imgData[s] = (byte)tmp1;
                        imgData[s+1] = (byte)tmp2;
                        imgData[s+2] = (byte)tmp3;
                        imgData[s+3] = (byte)tmp4;
                    }
                    break;
            }
        }
        
        // add data block to output
        DataBlockByte imageBlock = DataBlockFactory.createBlock(imgData);
        blockLists[0].clear();
        blockLists[0].addBlock(imageBlock);
        
        // redraw
        dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
    }
    
    
    @Override
    public boolean isSpatialSubsetSupported()
    {
        return true;
    }


    @Override
    public boolean isTimeSubsetSupported()
    {
        return false;
    }


    public String getServer()
    {
        return server;
    }
    

    public void setServer(String server)
    {
        this.server = server;
    }
    

    public String getTarget()
    {
        return target;
    }
    

    public void setTarget(String target)
    {
        this.target = target;
    }


    public int[] getComponents()
    {
        return components;
    }


    public void setComponents(int[] components)
    {
        this.components = components;
    }
}
