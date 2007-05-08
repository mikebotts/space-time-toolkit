/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

package org.vast.stt.provider.sml;

import java.util.ArrayList;

import org.vast.cdm.common.DataComponent;
import org.vast.data.*;
import org.vast.physics.TimeExtent;
import org.vast.process.DataProcess;
import org.vast.process.ProcessChain;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.DataException;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.AbstractProvider;


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
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Feb 28, 2006
 * @version 1.0
 */
public class SMLProvider extends AbstractProvider
{
    protected DataProcess process;
    protected ArrayList<BlockList> blockListArray;
    
    
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
            
            process.initProcess();
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
                    
                    if (checkCancel())
                        return;                    
                    
                    // execute process until keep running is false
                    boolean keepRunning = true;
                    do
                    {
                        process.createNewOutputBlocks();
                        process.runProcess();
                        
                        if (checkCancel())
                            return;
                        
                        // add data to list only if output data was generated
                        keepRunning = process.getOutputConnections().get(0).isNeeded();
                        if (keepRunning)
                        {                        
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
                            }
                            
                            // send event for redraw
                            dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
                            
                            // reset input needed flags to avoid a process chain to set 
                            // internal availability to true
                            for (int n=0; n<process.getInputConnections().size(); n++)
                                process.getInputConnections().get(n).setNeeded(false);
                        }
                    }
                    while (keepRunning);                 
                }
            }
            
            outputs.clearData();
        }
        catch (Exception e)
        {
            throw new DataException(updateError + this.getName(), e);
        }
    }
    
    
    protected boolean checkCancel() throws Exception
    {
        if (canceled)
        {
            init();
            return true;
        }
        
        return false;
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
}
