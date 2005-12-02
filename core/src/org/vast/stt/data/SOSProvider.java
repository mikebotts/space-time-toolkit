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

import java.io.*;
import org.ogc.cdm.common.*;
import org.ogc.cdm.reader.*;
import org.vast.ows.OWSQuery;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.sos.SOSObservationReader;
import org.vast.ows.sos.SOSQuery;
import org.vast.ows.sos.SOSRequestWriter;
import org.vast.stt.project.Resource;
import org.vast.stt.project.ServiceDataSet;
import org.vast.stt.util.SpatialExtent;
import org.vast.stt.util.TimeExtent;


/**
 * <p><b>Title:</b><br/>
 * SOS Data Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Requests Data from an SOS server and fill up a DataNode
 * with it.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 11, 2005
 * @version 1.0
 */
public class SOSProvider extends AbstractProvider implements OWSProvider
{
	protected SOSLayerCapabilities layerCaps;
	protected SOSQuery query;
	protected SOSRequestWriter requestBuilder;
	protected DataStreamParser dataParser;
	protected SWEDataHandler dataHandler;
	

	public SOSProvider()
	{
		requestBuilder = new SOSRequestWriter();
		dataHandler = new SWEDataHandler();
	}
	
	
	public void updateData() throws DataException
	{
		try
		{
			initRequest();	
			
			// create reader
			SOSObservationReader reader = new SOSObservationReader();
			
			// select request type (post or get)
			boolean usePost = true;
			String url = layerCaps.getParent().getPostServers().get("GetObservation");
			if (url != null)
			{
				query.setPostServer(url);
			}
			else
			{
				url = layerCaps.getParent().getGetServers().get("GetObservation");
				
				if (url != null)
					query.setGetServer(url);
				else
					throw new DataException("No server specified");
			}			
			
			dataStream = requestBuilder.sendRequest(query, usePost);
						
			// parse response
			// TODO handle SOS service exception here -> remove from observation reader
			reader.parse(dataStream);
			
			// display data structure and encoding
			dataParser = reader.getDataParser();
			DataComponent dataInfo = dataParser.getDataComponents();
        	DataEncoding dataEnc = dataParser.getDataEncoding();
        	System.out.println("--- " + dataInfo.getName() + " ---");
        	System.out.println(dataInfo);
        	System.out.println(dataEnc);
        	cachedData.addComponent(dataInfo);
			
        	// reset node - HACK
        	//DataNode node = item.getNode();
        	//node.changePointCount(0);
        	
			// register the CDM data handler
        	//SWEDataNodeHandler dataNodeHandler = new SWEDataNodeHandler(dataInfo, node);
        	dataHandler.setDataNode(cachedData);
        	dataParser.setDataHandler(dataHandler);
        	
        	// start parsing if not cancelled
        	if (!canceled)
        		dataParser.parse(reader.getDataStream());
		}
		catch (Exception e)
		{
			if (!canceled)
				throw new DataException(e.getMessage(), e);
		}
		finally
		{
			endRequest();
		}		
	}
	
	
	protected void initRequest()
	{
		// make sure previous request is cancelled
		endRequest();	
		canceled = false;
		
		if (cachedData == null)
			cachedData = new DataNode();
		else
			cachedData.removeAllComponents();
	}
	
	
	protected void endRequest()
	{
		canceled = true;
		
		// stop parser
		if (dataParser != null)
			dataParser.stop();
		
		// close stream(s)
		try
		{
			if (dataStream != null)
				dataStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void createDefaultQuery()
	{
		if (layerCaps == null) return;
		
		String request = "GetObservation";
		query = new SOSQuery();
		query.setGetServer(layerCaps.getParent().getGetServers().get(request));
		query.setPostServer(layerCaps.getParent().getPostServers().get(request));
		query.setService(layerCaps.getParent().getService());
		query.setVersion(layerCaps.getParent().getVersion());
		query.setRequest(request);
		query.setOffering(layerCaps.getId());
		query.setFormat(layerCaps.getFormatList().get(0));
		query.getObservables().add(layerCaps.getObservableList().get(0));
		query.getProcedures().add(layerCaps.getProcedureList().get(0));
		query.getTime().setStartTime(layerCaps.getTimeList().get(0).getStartTime());
		query.getTime().setStopTime(query.getTime().getStartTime());		
	}
	
	
	public void cancelOperation()
	{
		endRequest();
	}	


	public SOSQuery getQuery()
	{
		return query;
	}


	public void setQuery(OWSQuery query)
	{
		this.query = (SOSQuery)query;
	}
	
	
	public void updateQuery()
	{
		// update time range
		if (timeExtent != null)
		{
			query.getTime().setStartTime(timeExtent.getAdjustedLagTime());
			query.getTime().setStopTime(timeExtent.getAdjustedLeadTime());
		}
		
		// update bounding box
		if (spatialExtent != null)
		{
			query.getBbox().setMinX(spatialExtent.getMinX());
			query.getBbox().setMaxX(spatialExtent.getMaxX());
			query.getBbox().setMinY(spatialExtent.getMinY());
			query.getBbox().setMaxY(spatialExtent.getMaxY());
		}
	}


	@Override
	public void setResource(Resource resource)
	{
		super.setResource(resource);		
		this.layerCaps = (SOSLayerCapabilities) ((ServiceDataSet)resource).getCapabilities();
	}


	@Override
	public void setSpatialExtent(SpatialExtent spatialExtent)
	{
		super.setSpatialExtent(spatialExtent);
		updateQuery();
	}


	@Override
	public void setTimeExtent(TimeExtent timeExtent)
	{
		super.setTimeExtent(timeExtent);
		updateQuery();	
	}
}