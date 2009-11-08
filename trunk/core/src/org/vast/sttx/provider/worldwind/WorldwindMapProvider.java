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
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.sttx.provider.worldwind;

import java.awt.image.renderable.ParameterBlock;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.vast.cdm.common.DataBlock;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataBlockFactory;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.DataException;
import org.vast.data.DataArray;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.tiling.QuadTreeItem;
import org.vast.stt.provider.tiling.TiledMapProvider;
import org.vast.stt.provider.tiling.TiledMapSelector;
import org.vast.util.SpatialExtent;

import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import com.sun.media.jai.codec.PNGDecodeParam;


/**
 * <p><b>Title:</b><br/>
 * Windows Live Local Virtual Earth Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Requests Data from Google Map Keyhole servers
 * Ref: http://worldwind25.arc.nasa.gov
 * Requests: http://worldwind25.arc.nasa.gov/tile/tile.aspx?T=bmng.topo.bathy.200411&L=2&X=19&Y=11
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 14, 2005
 * @version 1.0
 */
public class WorldwindMapProvider extends TiledMapProvider
{
    protected String layerId = "satellite";
    protected int serverNum = 24;
        
    
    public WorldwindMapProvider()
    {
        super(512, 512, 17);
        tileSelector = new TiledMapSelector(3, 3, 3, 7);
        tileSelector.setItemLists(selectedItems, deletedItems, blockLists);
        useAlpha = false;
    }
    
    
    class GetTileRunnable implements Runnable
    {
        protected QuadTreeItem item;
        
        
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
                String urlString = null;
                
                if (layerId.startsWith("satellite"))
                {
                    // build request URL for road/boundary data
                    WorldwindMapTileXYZ tileXYZGen = new WorldwindMapTileXYZ();
                    item.accept(tileXYZGen);
                    int x = tileXYZGen.getX();
                    int y = tileXYZGen.getY();
                    int z = tileXYZGen.getZoom();
                    urlString = "http://worldwind" + serverNum +
                                ".arc.nasa.gov/tile/tile.aspx?T=bmng.topo.bathy.200411&L=" + z + "&X=" + x + "&Y=" + (y-1);
                }
                
                System.out.println(urlString);
                
                // increment server number
                serverNum++;
                if (serverNum > 25)
                    serverNum = 24;                
                                        
                //System.out.println(urlString);
                URL url = new URL(urlString);
                URLConnection connection = url.openConnection();
                connection.addRequestProperty("Connection", "keep-alive");
                connection.addRequestProperty("Keep-Alive", "300");
                
                // return if connection cannot be made
                InputStream is = null;
                try {is = connection.getInputStream();}
                catch (Exception e) {return;}
                
                //long connectTime = System.currentTimeMillis();
                MemoryCacheSeekableStream imgStream = new MemoryCacheSeekableStream(is);

                ParameterBlock pb = new ParameterBlock();
                pb.add(imgStream);
                
                if (connection.getContentType().equals("image/png"))
                {
                    PNGDecodeParam pngParams = new PNGDecodeParam();
                    pngParams.setExpandPalette(true);
                    pb.add(pngParams);
                }
                
                RenderedOp rop = JAI.create("stream", pb);
                RenderedImage img = rop.createInstance();
                imgStream.close();
                
                // if DataBufferByte, just wrap image data with a DataBlock
                DataBuffer buf = img.getData().getDataBuffer();
                DataBlock imageryDataBlock = null;
                if (buf instanceof DataBufferByte)
                {
                    byte[] data = ((DataBufferByte)buf).getData();
                    imageryDataBlock = DataBlockFactory.createBlock(data);
                }
//                else if (buf instanceof DataBufferInt)
//                {
//                    int[] data = ((DataBufferInt)buf).getData();
//                    byte[] byteData = new byte[data.length*3];
//                    
//                    for (int i=0; i<data.length; i++)
//                    {
//                        int b = i*3;
//                        byteData[b] = (byte)(data[i] & 0xFF);
//                        byteData[b+1] = (byte)((data[i] >> 8) & 0xFF);
//                        byteData[b+2] = (byte)((data[i] >> 16) & 0xFF);
//                    }
//                    
//                    imageryDataBlock = DataBlockFactory.createBlock(byteData);
//                }
                else
                    throw new IllegalStateException("DataBuffer Type not supported");
                
                // build grid
                int gridWidth = 10;
                int gridLength = 10;
                double minY = item.getMinY();
                double maxY = item.getMaxY();
                double minX = item.getMinX();
                double maxX = item.getMaxX();
                double dX = (maxX - minX) / (gridWidth-1);
                double dY = (maxY - minY) / (gridLength-1);
                DataBlock gridBlock = DataBlockFactory.createBlock(new double[gridLength*gridWidth*2]);
                
                // compute data for grid block
                int valCount = 0;
                for (int v=0; v<gridLength; v++)
                {
                    for (int u=0; u<gridWidth; u++)
                    {
                        double x = minX + dX * u;
                        double y = maxY - dY * v;
                        
                        // write lat and lon value
                        gridBlock.setDoubleValue(valCount, yToLat(y));
                        gridBlock.setDoubleValue(valCount+1, x);
                        valCount += 2;
                    }
                }
                
                // add blocks to dataNode
                if (!canceled)
                {
                    // add blocks to data node
                    item.setData(blockArray);
                    blockArray[0] = blockLists[0].addBlock((AbstractDataBlock)imageryDataBlock);
                    blockArray[1] = blockLists[1].addBlock((AbstractDataBlock)gridBlock);
                    
                    // remove sub and super items now that we have the new tile
                    removeHiddenChildren(item);
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
    }
    
    
    @Override
    public void init() throws DataException
    {
        super.init();
        DataArray imgArray = (DataArray)blockLists[0].getBlockStructure();
        imgArray.setSize(512);
        DataArray rowArray = (DataArray)imgArray.getComponent(0);
        rowArray.setSize(512);
    }
    
    
    @Override
    protected SpatialExtent transformBbox(SpatialExtent extent)
    {
        SpatialExtent mercatorExtent = new SpatialExtent();
        double minX = spatialExtent.getMinX() * DTR;
        double maxX = spatialExtent.getMaxX() * DTR;
        double minY = latToY(spatialExtent.getMinY() * DTR);
        double maxY = latToY(spatialExtent.getMaxY() * DTR);
        mercatorExtent.setMinX(minX);
        mercatorExtent.setMaxX(maxX);
        mercatorExtent.setMinY(minY);
        mercatorExtent.setMaxY(maxY);
        return mercatorExtent;
    }
    
    
    @Override
    protected void getNewTile(QuadTreeItem item)
    {
        GetTileRunnable getTile = new GetTileRunnable(item);
        //Thread newThread = new Thread(getTile, nextItem.toString());
        //newThread.start();
        getTile.run();
    }
    
    
    public void setLayer(String layerId)
    {
        this.layerId = layerId;
        //if (layerId.equals("roads"))
        //    useAlpha = true;
    }
}