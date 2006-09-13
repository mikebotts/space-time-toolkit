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

import org.ogc.cdm.common.DataBlock;
import org.ogc.cdm.common.DataComponent;
import org.vast.data.*;
import org.vast.process.DataProcess;
import org.vast.process.ProcessChain;
import org.vast.process.ProcessException;
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
            
            process.init();
            process.createNewInputBlocks();
            
            DataComponent outputs = process.getOutputList();
            int outputCount = outputs.getComponentCount();
            
            for (int i=0; i<outputCount; i++)
            {
                BlockList blockList = dataNode.createList(outputs.getComponent(i));
                blockListArray.add(blockList);
            }
            
            dataNode.setNodeStructureReady(true);
        }
        catch (ProcessException e)
        {
            throw new DataException("Error while parsing process " + process.getName() + "(" + process.getType() + ")", e);
        }
    }
    
    
    @Override
    public void updateData() throws DataException
    {
        // init DataNode if not done yet
        if (!dataNode.isNodeStructureReady())
            init();
        
        try
        {
            int tileCountX;
            int tileCountY;
            
            if (this.spatialExtent.isTilingEnabled())
            {
                tileCountX = this.spatialExtent.getXTiles();
                tileCountY = this.spatialExtent.getYTiles();
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
                        DataBlock timeData = new DataBlockDouble(3);
                        timeData.setDoubleValue(0, this.timeExtent.getAdjustedLagTime());
                        timeData.setDoubleValue(1, this.timeExtent.getAdjustedLeadTime());
                        timeData.setDoubleValue(2, this.timeExtent.getTimeStep());
                        timeInput.setData(timeData);
                    }
                    
                    if (canceled)
                        return;
                    
                    process.createNewOutputBlocks();
                    process.execute();
                    
                    if (canceled)
                        return;
                    
                    // transfer block for each output
                    for (int c=0; c<blockListArray.size(); c++)
                    {
                        BlockList blockList = blockListArray.get(c);
                        blockList.addBlock((AbstractDataBlock)outputs.getComponent(c).getData());
                    }
                    
                    // send event for redraw
                    dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
                }
            }
            
            outputs.clearData();
        }
        catch (ProcessException e)
        {
            throw new DataException("Error while running SensorML process in " + this.getName(), e);
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
}
