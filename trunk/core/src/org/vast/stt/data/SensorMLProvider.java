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

package org.vast.stt.data;

import org.ogc.cdm.common.DataBlock;
import org.ogc.cdm.common.DataComponent;
import org.ogc.process.ProcessException;
import org.vast.data.*;
import org.vast.process.DataProcess;
import org.vast.stt.style.BlockList;


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
public class SensorMLProvider extends AbstractProvider
{
    protected DataProcess process;
    protected BlockList blockList;
    
    
	public SensorMLProvider()
	{
	    this.spatialExtent.setMinX(-87.2);
        this.spatialExtent.setMinY(34.4);
        this.spatialExtent.setMaxX(-86.3);
        this.spatialExtent.setMaxY(35.0);        
	}
	
    
    @Override
    public void updateData() throws DataException
    {
        try
        {
            process.init();
            cachedData = new DataNode();
            blockList = cachedData.createList(process.getOutputList().getComponent(0));
            blockList.sendUpdates = true;
            
            int tileCountX = 5;
            int tileCountY = 5;
                        
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
                    
                    process.execute();            
                    blockList.addBlock((AbstractDataBlock)process.getOutputList().getComponent(0).getData());
                }
            }
        }
        catch (ProcessException e)
        {
            e.printStackTrace();
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
            DataBlock bboxData = new DataBlockDouble(4);
            bboxData.setDoubleValue(0, minY);
            bboxData.setDoubleValue(1, minX);
            bboxData.setDoubleValue(2, maxY);
            bboxData.setDoubleValue(3, maxX);
            bboxInput.setData(bboxData);
        }
    }
    
    
	/*
	@Override
	public void updateData() throws DataException
	{
        try
        {
            // TODO first check if we already have this data in dataNode
            
            // TODO load data in input queues (BBOX and Time Range)
            //internalProcess.start();            
            
            if (cachedData == null)
                cachedData = new DataNode();
            
            int arraySize = 401;
            
            DataGroup arrayGroup = new DataGroup();
            DataArray array = new DataArray();
            DataGroup pointGroup = new DataGroup(2);
            pointGroup.addComponent("lat", new DataValue(DataType.DOUBLE));
            pointGroup.addComponent("lon", new DataValue(DataType.DOUBLE));
            array.addComponent("point", pointGroup);
            array.setSize(arraySize);
            arrayGroup.addComponent("time1", new DataValue(DataType.DOUBLE));
            arrayGroup.addComponent("time2", new DataValue(DataType.DOUBLE));
            arrayGroup.addComponent("array", array);
            arrayGroup.setName("arrayGroup");
            //BlockList dataList = cachedData.createList(arrayGroup);
            BlockList dataList = cachedData.createList(array);
            
            if (process instanceof ProcessChain)
                ((ProcessChain)process).setChildrenThreadsOn(false);
            
            process.init();
            //arrayGroup.assignNewDataBlock();
            array.assignNewDataBlock();
            double inc = 23999.0/(double)(arraySize-1);
            
            for (double i=0; i<24000; i+=inc)
            {
                process.getInputList().getComponent("lineIndex").getData().setDoubleValue(i+1);
                int count = 0;
                
                for (double j=0; j<24000; j+=inc)
                {
                    process.getInputList().getComponent("pixelIndex").getData().setDoubleValue(j+1);
                    process.execute();
                    //DataBlock newPoint = pointData.clone();
                    double lat = process.getOutputList().getComponent("sampleLocation").getComponent(0).getData().getDoubleValue();
                    double lon = process.getOutputList().getComponent("sampleLocation").getComponent(1).getData().getDoubleValue();
                    
                    if (lon > Math.PI)
                        lon -= 2*Math.PI;
                    
                    //arrayGroup.getData().setDoubleValue(2*count+2, lat);
                    //arrayGroup.getData().setDoubleValue(2*count+3, lon);
                    array.getData().setDoubleValue(2*count, lat);
                    array.getData().setDoubleValue(2*count+1, lon);
                    count++;
                    
                    if (count == arraySize)
                    {
                        //dataList.addBlock((AbstractDataBlock)arrayGroup.getData());
                        //arrayGroup.renewDataBlock();
                        dataList.addBlock((AbstractDataBlock)array.getData());
                        array.renewDataBlock();
                        count = 0;
                    }
                }
            }
        }
        catch (ProcessException e)
        {
            e.printStackTrace();
        }
	}
	*/
	
	@Override
	public void cancelUpdate()
	{
	}    


	public boolean isSpatialSubsetSupported()
	{
		// TODO if it has one or more vector location inputs, YES...
        
        return false;
	}


	public boolean isTimeSubsetSupported()
	{
		// TODO if it has one or more time onputs, YES
        
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
