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

package org.vast.stt.provider.sml;

import java.util.ArrayList;

import org.vast.cdm.common.DataComponent;
import org.vast.data.*;
import org.vast.process.DataProcess;
import org.vast.process.ProcessChain;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.DataException;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.AbstractProvider;
import org.vast.util.TimeExtent;


/**
 * <p><b>Title:</b><br/>
 * SensorML Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Provider using a SensorML process model or chain to produce data.
 * A chain can itself include data sources and further processing steps.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Feb 28, 2006
 * @version 1.0
 */
public class SMLProvider extends AbstractProvider
{
    protected DataProcess process;
    protected ArrayList<BlockList> blockListArray;
    protected int persistency = -1;
    
    
	public SMLProvider()
	{      
        blockListArray = new ArrayList<BlockList>();
	}
	
    
    @Override
    public void init() throws DataException
    {
        try
        {
            if (process instanceof ProcessChain)
                ((ProcessChain)process).setChildrenThreadsOn(false);
            
            process.init();
            process.createNewInputBlocks();
            
            DataComponent outputs = process.getOutputList();
            int outputCount = outputs.getComponentCount();
            
            for (int i=0; i<outputCount; i++)
            {
                BlockList blockList = dataNode.createList(outputs.getComponent(i).copy());
                blockListArray.add(blockList);
            }
            
            dataNode.setNodeStructureReady(true);
        }
        catch (Exception e)
        {
            throw new DataException(initError + this.getName(), e);
        }
    }
    
    
    @Override
    public void updateData() throws DataException
    {
        boolean doClear = true;
        
        // init DataNode if not done yet
        if (!dataNode.isNodeStructureReady())
            init();
        
        try
        {
            int tileCountX;
            int tileCountY;
            
            if (this.spatialExtent.isTilingEnabled())
            {
                tileCountX = (int)this.spatialExtent.getXTiles();
                tileCountY = (int)this.spatialExtent.getYTiles();
            }
            else
            {
                tileCountX = 1;
                tileCountY = 1;
            }
       
            DataComponent outputs = process.getOutputList();
            
            for (int i=0; i<tileCountX; i++)
            {
                for (int j=0; j<tileCountY; j++)
                {
                    process.reset();
                    generateTile(tileCountX, tileCountY, i, j);
                    
                    // time is expected to be a DataGroup of start/stop/step
                    DataComponent timeInput = process.getInputList().getComponent("time");
                    if (timeInput != null)
                    {
                        double start, stop, step;
                        
                        // get start time
                        if (timeExtent.isBeginNow() || timeExtent.isBaseAtNow())
                            start = TimeExtent.NOW;
                        else
                            start = timeExtent.getAdjustedLagTime();
                        
                        // get stop time
                        if (timeExtent.isEndNow() || timeExtent.isBaseAtNow())
                            stop = TimeExtent.NOW;
                        else
                            stop = timeExtent.getAdjustedLeadTime();
                        
                        // get step time
                        step = timeExtent.getTimeStep();
                        
                        // set values to chain time input
                        timeInput.getComponent("start").getData().setDoubleValue(start);
                        timeInput.getComponent("stop").getData().setDoubleValue(stop);
                        if (timeInput.getComponent("step") != null)
                            timeInput.getComponent("step").getData().setDoubleValue(step);
                    }                
                    
                    // execute process until more inputs are needed
                    int count = 0;
                    do
                    {
                        if (canceled)
                            return;
                        
                        process.createNewOutputBlocks();
                        process.execute();
                        
                        // break if no output was generated!
                        if (!process.getOutputConnections().get(0).isNeeded())
                            break;
                        
                        // clear data node right before 1st block is added
                        if (doClear)
                        {
                            dataNode.clearAll();
                            doClear = false;
                        }
                        
                        // transfer block for each output
                        for (int c=0; c<blockListArray.size(); c++)
                        {
                            BlockList blockList = blockListArray.get(c);
                            blockList.addBlock((AbstractDataBlock)outputs.getComponent(c).getData());
                            
                            // remove blocks if num blocks > persistency limit
                            if (persistency > 0 && blockList.getSize() > persistency)
                            {
                                BlockListItem item = blockList.getFirstItem();
                                blockList.remove(item);
                                
                                // send event to clear rendering cache
                                dispatchEvent(new STTEvent(new Object[] {item}, EventType.PROVIDER_DATA_REMOVED), false);
                            }
                        }
                        
                        // send event for redraw
                        if (count == 0)
                        	dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED), true);
                        
                        // count++;
                        // if (count == 50)
                        //     count = 0;                       
                        
                        // reset input needed flags to avoid a process chain to set 
                        // internal availability to true
                        for (int n=0; n<process.getInputConnections().size(); n++)
                            process.getInputConnections().get(n).setNeeded(false);
                    }
                    while (process.needSync());// && !process.getInputConnections().get(0).isNeeded());
                }
            }
            
            outputs.clearData();
        }
        catch (Exception e)
        {
        	  e.printStackTrace();
            throw new DataException(updateError + this.getName(), e);
          
        }
    }
    
    
    protected void generateTile(int countX, int countY, int numX, int numY)
    {
        double dX = (spatialExtent.getMaxX() - spatialExtent.getMinX()) / countX;
        double dY = (spatialExtent.getMaxY() - spatialExtent.getMinY()) / countY;
        
        double minX = this.spatialExtent.getMinX() + numX*dX;
        double maxX = minX + dX;
        double minY = this.spatialExtent.getMinY() + numY*dY;
        double maxY = minY + dY;
        
        // bbox is expected to be a DataGroup of two vectors lat/lon
        DataComponent bboxInput = process.getInputList().getComponent("bbox");
        if (bboxInput != null)
        {
            DataComponent corner1 = bboxInput.getComponent("corner1");
            DataComponent lat1 = corner1.getComponent("lat");
            lat1.getData().setDoubleValue(0, minY);
            DataComponent lon1 = corner1.getComponent("lon");
            lon1.getData().setDoubleValue(0, minX);
            
            DataComponent corner2 = bboxInput.getComponent("corner2");
            DataComponent lat2 = corner2.getComponent("lat");
            lat2.getData().setDoubleValue(0, maxY);
            DataComponent lon2 = corner2.getComponent("lon");
            lon2.getData().setDoubleValue(0, maxX);
        }
    }  


	public boolean isSpatialSubsetSupported()
	{
        if (process.getInputList().getComponent("bbox") != null)
            return true;
        else
            return false;
	}


	public boolean isTimeSubsetSupported()
	{
		if (process.getInputList().getComponent("time") != null)
            return true;
        else
            return false;
	}


    public DataProcess getProcess()
    {
        return process;
    }


    public void setProcess(DataProcess process)
    {
        this.process = process;
    }


    public int getPersistency()
    {
        return persistency;
    }


    public void setPersistency(int persistency)
    {
        this.persistency = persistency;
    }
}
