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

package org.vast.sttx.provider.smart;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataStreamParser;
import org.vast.ows.sos.SOSResponseReader;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.DataException;
import org.vast.stt.provider.AbstractProvider;
import org.vast.stt.provider.swe.SWEDataHandler;
import org.vast.util.DateTimeFormat;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;


/**
 * <p><b>Title:</b><br/>
 * Phenomenon Detection Provider 
 * </p>
 *
 * <p><b>Description:</b><br/>
 * 
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date May 14, 2007
 * @version 1.0
 */
public class PhenomenaDetectionProvider extends AbstractProvider
{
    protected DataStreamParser dataParser;
    protected SWEDataHandler dataHandler;
    protected double minPressure, maxPressure;
    protected String server = "http://smartdev.itsc.uah.edu:81/CCDOM/services/PhenomenaDetection2"; 
    
    
    public PhenomenaDetectionProvider()
    {
        dataHandler = new SWEDataHandler(this);
    }


    @Override
    public void init() throws DataException
    {
        
    }


    @Override
    public boolean isSpatialSubsetSupported()
    {
        return true;
    }


    @Override
    public boolean isTimeSubsetSupported()
    {
        return true;
    }


    @Override
    public void updateData() throws DataException
    {
        try
        {    
            // init request using spatial + time extent + pressure range 
            DOMHelper dom = buildRequest();
                        
            // create reader
            SOSResponseReader reader = new SOSResponseReader();
            
            if (canceled)
                return;
            
            // send request to server using POST
            URL serverUrl = new URL(server);
            URLConnection connection = serverUrl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            dom.serialize(dom.getBaseElement(), connection.getOutputStream(), true);
            dataStream = connection.getInputStream();
            
            if (canceled)
                return;
                
            // parse response
            dataHandler.reset();
            reader.parse(dataStream);
            
            // create block list if not done yet
            if (!dataNode.isNodeStructureReady())
            {
                DataComponent dataInfo = reader.getDataComponents();
                BlockList blockList = dataNode.createList(dataInfo.copy());
                dataNode.setNodeStructureReady(true);
                dataHandler.setBlockList(blockList);
            }
                
            dataParser = reader.getDataParser();
            dataParser.setDataHandler(dataHandler);
            
            if (canceled)
                return;
            
             // start parsing
            dataParser.parse(reader.getDataStream());
        }
        catch (Exception e)
        {
            if (!canceled)
            {
                throw new DataException("Error while reading data from " + server, e);
            }
        }
        finally
        {
            endRequest();
        }        
    }
    
    
    protected DOMHelper buildRequest()
    {
        DOMHelper dom = new DOMHelper();
        
        Element root = dom.addElement("PhenomenaDetectionRequest");
        
        // time step
        dom.setElementValue(root, "TimeStep", Integer.toString((int)timeExtent.getTimeStep()));
        
        // pressure range
        dom.setElementValue(root, "PressureRange/Min", Double.toString(0.0));
        dom.setElementValue(root, "PressureRange/Max", Double.toString(100.0));
        
        // time range
        dom.setElementValue(root, "TimeRange/BeginDateTime", DateTimeFormat.formatIso(timeExtent.getAdjustedLagTime(), 0));
        dom.setElementValue(root, "TimeRange/EndDateTime", DateTimeFormat.formatIso(timeExtent.getAdjustedLeadTime(), 0));
        
        // bbox
        dom.setElementValue(root, "Region/North", Double.toString(spatialExtent.getMaxY()));
        dom.setElementValue(root, "Region/South", Double.toString(spatialExtent.getMinY()));
        dom.setElementValue(root, "Region/East", Double.toString(spatialExtent.getMaxX()));
        dom.setElementValue(root, "Region/West", Double.toString(spatialExtent.getMinX()));
        
        try
        {
            dom.serialize(dom.getBaseElement(), System.out, true);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dom;
    }
    
    
    protected void endRequest()
    {
        // close stream(s)
        try
        {
            if (dataParser != null)
                dataParser.stop();
            
            if (dataStream != null)
                dataStream.close();
            
            dataStream = null;
            dataParser = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    
    @Override
    public void cancelUpdate()
    {
        canceled = true;
        endRequest();
    }


    public double getMaxPressure()
    {
        return maxPressure;
    }


    public void setMaxPressure(double maxPressure)
    {
        this.maxPressure = maxPressure;
    }


    public double getMinPressure()
    {
        return minPressure;
    }


    public void setMinPressure(double minPressure)
    {
        this.minPressure = minPressure;
    }
}