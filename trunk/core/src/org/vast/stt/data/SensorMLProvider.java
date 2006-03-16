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
import org.ogc.cdm.common.DataType;
import org.ogc.process.ProcessException;
import org.vast.data.*;
import org.vast.process.DataProcess;
import org.vast.process.ProcessChain;
import org.vast.stt.project.SensorMLProcess;


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
	DataProcess process;
    
    
	public SensorMLProvider()
	{

	}
	
    
    @Override
    public void updateData() throws DataException
    {
        try
        {
            process.init();
            
            // bbox is expected to be a DataGroup of two vectors lat/lon
            DataComponent bboxInput = process.getInputList().getComponent("bbox");
            if (bboxInput != null)
            {
                DataBlock bboxData = new DataBlockDouble(4);
                bboxData.setDoubleValue(0, this.spatialExtent.getMinX());
                bboxData.setDoubleValue(1, this.spatialExtent.getMaxY());
                bboxData.setDoubleValue(2, this.spatialExtent.getMaxX());
                bboxData.setDoubleValue(3, this.spatialExtent.getMinY());
                bboxInput.setData(bboxData);
            }
            
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
            
            // put process output in the node
            cachedData = new DataNode();
            cachedData.addComponent(process.getOutputList().getComponent(0));
        }
        catch (ProcessException e)
        {
            e.printStackTrace();
        }
    }
    
	
//	@Override
//	public void updateData() throws DataException
//	{
//        try
//        {
//            // TODO first check if we already have this data in dataNode
//            
//            // TODO load data in input queues (BBOX and Time Range)
//            //internalProcess.start();            
//            
//            if (cachedData == null)
//                cachedData = new DataNode();
//            
//            DataGroup arrayGroup = new DataGroup();
//            DataArray array = new DataArray();
//            DataGroup pointGroup = new DataGroup(2);
//            pointGroup.addComponent("lat", new DataValue(DataType.DOUBLE));
//            pointGroup.addComponent("lon", new DataValue(DataType.DOUBLE));
//            pointGroup.assignNewDataBlock();
//            DataBlock pointData = pointGroup.getData();
//            array.addComponent("point", pointGroup);
//            array.setSize(5);
//            arrayGroup.addComponent("time1", new DataValue(DataType.DOUBLE));
//            arrayGroup.addComponent("time2", new DataValue(DataType.DOUBLE));
//            arrayGroup.addComponent("array", array);
//            cachedData.addComponent("arrayGroup", arrayGroup);
//            
//            process = ((SensorMLProcess)resource).getInternalProcess();
//            
//            if (process instanceof ProcessChain)
//                ((ProcessChain)process).setChildrenThreadsOn(false);
//            
//            process.init();
//            arrayGroup.assignNewDataBlock();
//            
//            for (int i=0; i<24000; i+=500)
//            {
//                process.getInputList().getComponent("lineIndex").getData().setIntValue(i+1);
//                int count=4;
//                for (int j=0; j<24000; j+=500)
//                {
//                    process.getInputList().getComponent("pixelIndex").getData().setIntValue(j+1);
//                    process.execute();
//                    //DataBlock newPoint = pointData.clone();
//                    double lat = process.getOutputList().getComponent("sampleLocation").getComponent(0).getData().getDoubleValue();
//                    double lon = process.getOutputList().getComponent("sampleLocation").getComponent(1).getData().getDoubleValue();
//                    
//                    if (lon > Math.PI)
//                        lon -= 2*Math.PI;
//                    
//                    arrayGroup.getData().setDoubleValue(2*count+2, lat);
//                    arrayGroup.getData().setDoubleValue(2*count+3, lon);
//                    count--;
//                    
//                    if (count == -1)
//                    {
//                        cachedData.addData(arrayGroup.getData());
//                        arrayGroup.renewDataBlock();
//                        count = 4;
//                    }
//                                                                                
//                    //cachedData.addData(newPoint);
//                    //System.out.print(lat * 180.0 / Math.PI + ",");
//                    //System.out.print(lon * 180.0 / Math.PI + "\n");
//                }
//            }
//        }
//        catch (ProcessException e)
//        {
//            e.printStackTrace();
//        }
//	}
	
	
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
}
