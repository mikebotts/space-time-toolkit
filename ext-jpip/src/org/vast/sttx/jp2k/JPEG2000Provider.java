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
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/
package org.vast.sttx.jp2k;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.InputStream;
import java.util.Hashtable;
import javax.swing.JLabel;
import jj2000.j2k.decoder.Decoder;
import jj2000.j2k.util.ParameterList;
import org.vast.cdm.common.DataBlock;
import org.vast.cdm.common.DataType;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataArray;
import org.vast.data.DataBlockFactory;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
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

import sun.awt.image.ToolkitImage;

/**
 * <p><b>Title:</b><br/>
 * JPeg2000Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Load and geolocation a JPEG2000 image with RPC coordaintes embedded in the
 * "back" channel
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Jan 22, 2008
 * @version 1.0
 * 
 *   TODO:  connect this to inputStream from WCS
 *   TODO:  separate this into RPC JP2 and generic JP2 readers
 */

public class JPEG2000Provider extends AbstractProvider 
{
	protected String imageUrl;
	protected InputStream dataStream;
	protected BufferedImage image;  
	protected BlockList[] blockLists = new BlockList[2]; // 0 for imagery, 1 for grid
	protected DataArray gridData;
	int tileWidth = 1, tileHeight = 1;
	SpatialExtent bounds;  //  Need to implement tiling with JPeg2000/JPIP- hardwired for now
	private DataBlock gridBlock;
	private DataBlock imageBlock;
	private int imageWidth;
	private int imageHeight;
	protected ParameterList list;
	Decoder decoder;
	private String rpcBlock;
	private Decoder dec;
	
	public JPEG2000Provider() {
		//  dummy extents- pull out of JPEG2K header
		bounds = new SpatialExtent();
		bounds.setMinX(-90.0 * Math.PI/180.0);
		bounds.setMinY(30.0 * Math.PI/180.0) ;
		bounds.setMaxX(-85.0 * Math.PI/180.0);
		bounds.setMaxY(35.0 * Math.PI/180.0);
        ProcessLoader.processMap.put("urn:ogc:def:process:CSM:RPC:1.0", "org.sensorML.process.RPC_Process");
		
	}

	public void setImagePath(String url){
		imageUrl = url;
	}
    
	 public void init() throws DataException {
	        // create block list for textures
	        DataGroup pixelData = new DataGroup(3);
	        pixelData.setName("pixel");
	        pixelData.addComponent("red", new DataValue(DataType.BYTE));
	        pixelData.addComponent("green", new DataValue(DataType.BYTE));
	        pixelData.addComponent("blue", new DataValue(DataType.BYTE));
//	        if (useAlpha)
//	            pixelData.addComponent("alpha", new DataValue(DataType.BYTE));
	        
	        DataArray rowData = new DataArray(1000);
	        rowData.addComponent(pixelData);
	        rowData.setName("row");                  
	        DataArray imageData = new DataArray(1000);
	        imageData.addComponent(rowData);
	        imageData.setName("image");
	        blockLists[0] = dataNode.createList(imageData);
	        //System.out.println(imageData);
	        
	        // create block list for grid
	        DataGroup pointData = new DataGroup(4);
	        pointData.setName("point");
	        pointData.addComponent("lat", new DataValue(DataType.FLOAT));
	        pointData.addComponent("lon", new DataValue(DataType.FLOAT));
            pointData.addComponent("imgX", new DataValue(DataType.FLOAT));
            pointData.addComponent("imgY", new DataValue(DataType.FLOAT));
	        rowData = new DataArray(50);
	        rowData.addComponent(pointData);
	        rowData.setName("row");                  
	        gridData = new DataArray(50);
	        gridData.addComponent(rowData);
	        gridData.setName("grid");
	        blockLists[1] = dataNode.createList(gridData);
	        //System.out.println(gridData);
	        
	        // set tile sizes in updater
	        SpatialExtentUpdater updater = this.getSpatialExtent().getUpdater();
	        if (updater != null)
	        {
	            if (updater instanceof SceneBboxUpdater)
	                ((SceneBboxUpdater)updater).setTilesize(tileWidth, tileHeight);
	            updater.update();
	        }

	        dataNode.setNodeStructureReady(true);
	}
	
	protected void loadGrid() throws Exception {
		RPCGridGenerator  rpcGridGen = new RPCGridGenerator();
		rpcGridGen.setLength(50);
		rpcGridGen.setWidth(50);
		SpatialExtent bounds = new SpatialExtent();
		bounds.setMinX(27.078285);  //  These are from Spot GetCaps- dynamically load these
		bounds.setMinY(-23.839041);
		bounds.setMaxX(27.951421);
		bounds.setMaxY(-23.17148);       
		rpcGridGen.setBounds(bounds);
		//  mod this to load params from actual JP2 files or stream
		//rpcGridGen.loadRPCParams(rpcBlock);
		gridBlock = rpcGridGen.createGrid();
	}

	
	protected void loadGrid(int flatGridVersion){
		 // build grid
        int gridWidth = 50;
        int gridLength = 50;
        double minY = bounds.getMinY();
        double maxY = bounds.getMaxY();
        double minX = bounds.getMinX();
        double maxX = bounds.getMaxX();
        double dX = (maxX - minX) / (gridWidth-1);
        double dY = (maxY - minY) / (gridLength-1);
        gridBlock = DataBlockFactory.createBlock(new double[gridLength*gridWidth*2]);
		// compute data for grid block
        int valCount = 0;
        for (int v=0; v<gridLength; v++)
        {
            for (int u=0; u<gridWidth; u++)
            {
                double x = minX + dX * u;
                double y = maxY - dY * v;
                
                // write lat and lon value
                gridBlock.setDoubleValue(valCount, y);
                gridBlock.setDoubleValue(valCount+1, x);
                valCount += 2;
            }
        }
	}
	
	//  TODO thread this so we don't block user actions
	public void loadImage(){
		// Create parameter list using defaults
		ParameterList list = new ParameterList(getDefaultParams());
		list.put("i", imageUrl);
		list.put("debug", "on");
		
		dec = new Decoder(list);
		// Run the decoder
		try {
			dec.run();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (dec.getExitCode() != 0) {
				System.exit(dec.getExitCode());
			}
		}
		//String rawBox = dec.getGmlBox();
		//  Strip junk off beginning and end of Box
		//rpcBlock = trimRPCBlock(rawBox);
		Image imTmp = null;//dec.getImage();
		waitForImage(imTmp);
		image = ((ToolkitImage)imTmp).getBufferedImage();
		putImageDataInBlock();
		//System.err.println(gmlBox);
	}
	
	private void waitForImage(Image img){
		MediaTracker tracker = new MediaTracker(new JLabel());
		tracker.addImage(img, 1);
		try {
			System.err.println("waiting...");
			tracker.waitForID(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private void putImageDataInBlock(){
    	 // if DataBufferByte, just wrap image data with a DataBlock
        DataBuffer buf = image.getData().getDataBuffer();
        if (buf instanceof DataBufferByte)
        {
            byte[] data = ((DataBufferByte)buf).getData();
            imageBlock = DataBlockFactory.createBlock(data);
        }
        else if (buf instanceof DataBufferInt)
        {
            int[] data = ((DataBufferInt)buf).getData();
            byte[] byteData = new byte[data.length*3];
            
            for (int i=0; i<data.length; i++)
            {
                int b = i*3;
                byteData[b+2] = (byte)(data[i] & 0xFF);
                byteData[b+1] = (byte)((data[i] >> 8) & 0xFF);
                byteData[b] = (byte)((data[i] >> 16) & 0xFF);
            }
            
            imageBlock = DataBlockFactory.createBlock(byteData);
        }
    }
	
	protected ParameterList getDefaultParams(){
		ParameterList def  = new ParameterList();
		String[][] param = Decoder.getAllParameters();

		for (int i = param.length - 1; i >= 0; i--) {
			if (param[i][3] != null)
				def.put(param[i][0], param[i][3]);
		
		}
		return def;
	}

	@Override
	public void updateData() throws DataException {
		// init DataNode if not done yet
        if (!dataNode.isNodeStructureReady())
            init();
        
        BlockListItem[] blockArray = new BlockListItem[2];
		
		loadImage();
		//
		try {
			loadGrid();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        // add blocks to data node
        blockLists[0].addBlock((AbstractDataBlock)imageBlock);
        blockLists[1].addBlock((AbstractDataBlock)gridBlock);
                
        //  redraw
        dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
	}

	/**
	 * Strips unneeded info off the beginning and end of the GML Box- not sure
	 * how to do this consistently...
	 */
	private String trimRPCBlock(String oldBox){
		//  Strip off gml-root-instance stuff at beginning
		//  Will this ALWAYS be 63 characters?  Doubt it...
		String newBox = oldBox.substring(63);
		int lastBracket = newBox.lastIndexOf('>');
		newBox = newBox.substring(0,lastBracket+1);
		return newBox;
	}
	
	public String getGMLBox() {
		return rpcBlock;
	}

	@Override
	public boolean isSpatialSubsetSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTimeSubsetSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String [] args){
		try
        {
            // Create parameter list using defaults
            ProcessLoader.processMap = new Hashtable<String,String>();
            JPEG2000Provider prov = new JPEG2000Provider();
            prov.setImagePath("D:\\WcsLevel1A.jp2");
            //prov.loadImage();
            prov.loadGrid();
            //prov.dispImg();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

}

