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

package org.vast.stt.provider.tiling;

import java.util.ArrayList;
import java.util.Iterator;
import org.vast.cdm.common.DataComponent;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.DataException;
import org.vast.stt.dynamics.SceneBboxUpdater;
import org.vast.stt.dynamics.SpatialExtentUpdater;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.tiling.QuadTreeItem;
import org.vast.stt.provider.tiling.TiledMapProvider;
import org.vast.util.SpatialExtent;


/**
 * <p><b>Title:</b>
 * Spatial Tiling Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Generic Spatial Tiling Provider used by wrapping another
 * spatially subsetable provider.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 14, 2006
 * @version 1.0
 */
public class SpatialTilingProvider extends TiledMapProvider
{
    protected DataProvider subProvider;
    
    
    public SpatialTilingProvider(DataProvider subProvider)
    {
        super(256, 256, 12);
        
    	this.subProvider = subProvider;
        this.setSpatialExtent(subProvider.getSpatialExtent());
        this.subProvider.setSpatialExtent(new STTSpatialExtent());
        this.quadTree = new QuadTree();
        
        // set quad tree root extent (= max request)
        maxBbox = new SpatialExtent();
        maxBbox.setMinX(-180);
        maxBbox.setMaxX(+180);
        maxBbox.setMinY(-90);
        maxBbox.setMaxY(+90);
        quadTree.init(maxBbox);
        
        // set max proj extent for splitting bbox correctly
        SpatialExtent maxExtent = new SpatialExtent();
        maxExtent.setMinX(-180);
        maxExtent.setMaxX(+180);
        maxExtent.setMinY(-90);
        maxExtent.setMaxY(+90);
        tileSelector.setMaxExtent(maxExtent);
        
        initThreadPool(4);
    }
    
    
    @Override
    public void init() throws DataException
    {
        subProvider.init();
        
        // duplicate block lists of sub provider
        ArrayList<BlockList> subProviderLists = subProvider.getDataNode().getListArray();
        blockLists = new BlockList[subProviderLists.size()];
        for (int i=0; i<blockLists.length; i++)
        {
            DataComponent blockStructure = subProviderLists.get(i).getBlockStructure();
            blockLists[i] = dataNode.createList(blockStructure.copy());
        }
        
        dataNode.setNodeStructureReady(true);
        
        // set tile sizes in updater
        SpatialExtentUpdater updater = this.getSpatialExtent().getUpdater();
        if (updater != null)
        {
            if (updater instanceof SceneBboxUpdater)
                ((SceneBboxUpdater)updater).setTilesize(tileWidth, tileHeight);
            updater.update();
        }
    }
    
    
    @Override
    protected SpatialExtent transformBbox(SpatialExtent extent)
    {
        return extent.copy();
    }
    
    
    @Override
    protected void getNewTile(QuadTreeItem item)
    {
        try
        {
            // run sub provider with item extent
            STTSpatialExtent tileExtent = subProvider.getSpatialExtent();
            tileExtent.setMinX(item.getMinX());
            tileExtent.setMinY(item.getMinY());
            tileExtent.setMaxX(item.getMaxX());
            tileExtent.setMaxY(item.getMaxY());
            tileExtent.setTilingEnabled(false);
            subProvider.updateData();
            
            // add blocks to dataNode
            if (!canceled)
            {
                BlockListItem[] blockArray = new BlockListItem[blockLists.length];
                
                // get blocks from underlying provider
                ArrayList<BlockList> subProviderLists = subProvider.getDataNode().getListArray();
                for (int i=0; i<blockLists.length; i++)
                {
                    Iterator<BlockListItem> it = subProviderLists.get(i).getIterator();
                    if (it.hasNext())
                    {
                        blockArray[i] = it.next();
                        subProviderLists.get(i).remove(blockArray[i]);
                    }
                    else
                        return;
                }
                
                // add blocks to quad tree cache
                item.setData(blockArray);
            }
        }
        catch (DataException e)
        {
            if (!canceled)
                e.printStackTrace();
        }
    }
    
    
    public void setMaxSpatialExtent(STTSpatialExtent spatialExtent)
    {
    	this.maxSpatialExtent = spatialExtent;
    	
    	// set quad tree root extent (= max request)
        quadTree.init(spatialExtent);
    }
    
    
    @Override
    public String getDescription()
    {
        return subProvider.getDescription();
    }


    @Override
    public String getName()
    {
        return subProvider.getName();
    }


    @Override
    public void setDescription(String description)
    {
        subProvider.setDescription(description);
    }


    @Override
    public void setName(String name)
    {
        subProvider.setName(name);
    }


    @Override
    public boolean isSpatialSubsetSupported()
    {
        return subProvider.isSpatialSubsetSupported();
    }


    @Override
    public boolean isTimeSubsetSupported()
    {
        return subProvider.isTimeSubsetSupported();
    }
}