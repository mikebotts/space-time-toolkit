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

package org.vast.sttx.provider.smart;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataStreamParser;
import org.vast.ogc.om.ObservationStreamReader;
import org.vast.ogc.om.ObservationStreamReaderV10;
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
    protected String server = "http://smartdev.itsc.uah.edu:81/CCDOM/services/PhenomenaDetection"; 
    protected InputStream dataStream;
    
    
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
                        
            if (canceled)
                return;
            
            // send request to server using POST
            URL serverUrl = new URL(server);
            HttpURLConnection connection = (HttpURLConnection)serverUrl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty( "Content-type", "text/xml");
            connection.setRequestProperty( "SOAPAction", "SOAPAction");
            OutputStream reqStream = connection.getOutputStream();
            dom.serialize(dom.getBaseElement(), reqStream, true);
            reqStream.flush();
            connection.connect();
            reqStream.close();
            dataStream = connection.getInputStream();
            
//            int c; 
//            while((c = dataStream.read()) != -1)
//                System.out.print((char)c);
//            if (true) return;
            
            if (canceled)
                return;
                
            // parse response
            dataHandler.reset();
            ObservationStreamReader reader = new ObservationStreamReaderV10();
            reader.parse(dataStream, null);
            
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
        dom.addUserPrefix("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        dom.addUserPrefix("ns1", "urn:PhenomenaDetection2");
        dom.addUserPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        dom.getXmlDocument().addNS("xsd", "http://www.w3.org/2001/XMLSchema");               
        Element msgRoot = dom.addElement("soap:Envelope/soap:Body/PhenomenaDetectionRequest");
        
        // time step
        dom.setElementValue(msgRoot, "TimeStep", Integer.toString((int)timeExtent.getTimeStep()));
        
        // pressure range
        dom.setElementValue(msgRoot, "PressureRange/Min", Double.toString(minPressure));
        dom.setElementValue(msgRoot, "PressureRange/Max", Double.toString(maxPressure));
        
        // time range
        dom.setElementValue(msgRoot, "TimeRange/BeginDateTime", DateTimeFormat.formatIso(timeExtent.getAdjustedLagTime(), 0));
        dom.setElementValue(msgRoot, "TimeRange/EndDateTime", DateTimeFormat.formatIso(timeExtent.getAdjustedLeadTime(), 0));
        
        // bbox
        dom.setElementValue(msgRoot, "Region/North", Double.toString(spatialExtent.getMaxY()));
        dom.setElementValue(msgRoot, "Region/South", Double.toString(spatialExtent.getMinY()));
        dom.setElementValue(msgRoot, "Region/East", Double.toString(spatialExtent.getMaxX()));
        dom.setElementValue(msgRoot, "Region/West", Double.toString(spatialExtent.getMinX()));

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
    
    
    public static void main(String[] args)
    {
        try
        {
            PhenomenaDetectionProvider provider = new PhenomenaDetectionProvider();
            provider.setMinPressure(200);
            provider.setMaxPressure(1000);
            provider.getTimeExtent().setBaseTime(DateTimeFormat.parseIso("2007-06-03T06:00:00Z"));
            provider.getTimeExtent().setLeadTimeDelta(7200);
            provider.getSpatialExtent().setMinX(-89.9);
            provider.getSpatialExtent().setMaxX(-61.2);
            provider.getSpatialExtent().setMinY(15.4);
            provider.getSpatialExtent().setMaxY(45.4);
            provider.updateData();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}