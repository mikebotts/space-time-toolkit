/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the
 University of Alabama in Huntsville (UAH).
 Portions created by the Initial Developer are Copyright (C) 2006
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.process;

import org.ogc.cdm.common.DataBlock;
import org.vast.data.*;
import org.vast.process.*;


/**
 * <p><b>Title:</b><br/>
 * Flat Grid Generator Process
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Generates a Rectangular grid using given bounding box
 * and width and height parameters.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jan 20, 2006
 * @version 1.0
 */
public class FlatGridGenerator_Process extends DataProcess
{
    protected DataValue bboxLat1, bboxLon1, bboxLat2, bboxLon2;
    protected DataValue outputWidth, outputLength;
    protected DataArray outputGrid;
    protected int width, length;
    

    public FlatGridGenerator_Process()
    {
    }


    /**
     * Initializes the process
     * Gets handles to input/output components and read fixed parameters values
     */
    public void init() throws ProcessException
    {
        try
        {
            // Input mappings
            DataGroup input = (DataGroup)inputData.getComponent("bbox");
            bboxLat1 = (DataValue)input.getComponent("corner1").getComponent(0);
            bboxLon1 = (DataValue)input.getComponent("corner1").getComponent(1);
            bboxLat2 = (DataValue)input.getComponent("corner2").getComponent(0);
            bboxLon2 = (DataValue)input.getComponent("corner2").getComponent(1);
            input.assignNewDataBlock();
            
            // Output mappings
            DataGroup output = (DataGroup)outputData.getComponent("gridData");
            outputGrid = (DataArray)output.getComponent("grid");
            outputWidth = (DataValue)output.getComponent("width");
            outputLength = (DataValue)output.getComponent("length");
        }
        catch (Exception e)
        {
            throw new ProcessException("Invalid I/O structure", e);
        }
        
        try
        {
            // Read parameter values
            width = paramData.getComponent("gridWidth").getData().getIntValue();
            length = paramData.getComponent("gridLength").getData().getIntValue();
            outputWidth.getData().setIntValue(width);
            outputLength.getData().setIntValue(length);
            outputGrid.setSize(length);
            ((DataArray)outputGrid.getComponent(0)).setSize(width);
            outputGrid.assignNewDataBlock();
        }
        catch (Exception e)
        {
            throw new ProcessException("Invalid Parameters", e);
        }
    }


    /**
     * Executes process algorithm on inputs and set output data
     */
    public void execute() throws ProcessException
    {
        double lon1 = bboxLon1.getData().getDoubleValue() * Math.PI/180;
        double lat1 = bboxLat1.getData().getDoubleValue() * Math.PI/180;
        double lon2 = bboxLon2.getData().getDoubleValue() * Math.PI/180;
        double lat2 = bboxLat2.getData().getDoubleValue() * Math.PI/180;
                
        double minX = Math.min(lon1, lon2);
        double maxX = Math.max(lon1, lon2);
        double minY = Math.min(lat1, lat2);
        double maxY = Math.max(lat1, lat2);
        
        double xStep = (maxX - minX) / (width - 1);
        double yStep = (maxY - minY) / (length - 1);
        
        int pointNum = 0;
        DataBlock gridData = outputGrid.getData();
        for (int j=0; j<length; j++)
        {
            for (int i=0; i<width; i++)
            {
                double x = minX + xStep * i;
                double y = maxY - yStep * j;
                
                gridData.setDoubleValue(pointNum, x);
                pointNum++;
                gridData.setDoubleValue(pointNum, y);
                pointNum++;
                gridData.setDoubleValue(pointNum, 0.0);
                pointNum++;
            }
        }
        
        // adjust width and height of the output
        outputWidth.getData().setIntValue(width);
        outputLength.getData().setIntValue(length);
    }
}