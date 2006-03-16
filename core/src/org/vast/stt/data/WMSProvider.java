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

import java.awt.image.renderable.ParameterBlock;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.ogc.cdm.common.DataBlock;
import org.ogc.cdm.common.DataType;
import org.ogc.cdm.reader.*;
import org.vast.data.DataArray;
import org.vast.data.DataBlockFactory;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.ows.OWSExceptionReader;
import org.vast.ows.OWSQuery;
import org.vast.ows.wms.WMSLayerCapabilities;
import org.vast.ows.wms.WMSQuery;
import org.vast.ows.wms.WMSRequestWriter;
import org.vast.stt.project.ServiceDataSet;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;


/**
 * <p><b>Title:</b><br/>
 * WMS Data Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Requests Data from a WMS server and fill up a DataNode
 * with it.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 14, 2005
 * @version 1.0
 */
public class WMSProvider extends AbstractProvider implements OWSProvider
{
	protected WMSLayerCapabilities layerCaps;
	protected WMSQuery query;
	protected WMSRequestWriter requestBuilder;
	protected DataStreamParser dataParser;


	public WMSProvider()
	{
		requestBuilder = new WMSRequestWriter();
	}


	public void updateData() throws DataException
	{
		URL url = null;

		try
		{
			initRequest();

			updating = true;
			String urlString = requestBuilder.buildGetRequest(query);
			url = new URL(urlString);
			URLConnection urlCon = url.openConnection();

			//  Check mime for exception/xml type
			if (urlCon == null)
			{
				throw new DataException("Connection to " + url + " could be initialized");
			}

			//  Check on mimeType catches all three types (blank, inimage, xml)
			//  of OGC service exceptions
			String mimeType = urlCon.getContentType();
			if (mimeType.contains("xml") || mimeType.startsWith("application"))
			{
				OWSExceptionReader reader = new OWSExceptionReader();
				reader.parseException(urlCon.getInputStream());
			}
			else
			{
				dataStream = new MemoryCacheSeekableStream(url.openStream());

				// Create the ParameterBlock and add the SeekableStream to it.
				ParameterBlock pb = new ParameterBlock();
				pb.add(dataStream);

				// decode image using JAI
				RenderedOp rop = JAI.create("stream", pb);
				RenderedImage renderedImage;
				if (rop != null)
				{
					renderedImage = rop.createInstance();
                    
					//renderedImage.getClass();				

					// copy image to DataNode
					byte[] data = ((DataBufferByte)renderedImage.getData().getDataBuffer()).getData();                   
                    
                    cachedData = new DataNode();
                    
                    DataGroup pixelData = new DataGroup(renderedImage.getData().getNumBands());
                    pixelData.addComponent("blue", new DataValue(DataType.BYTE));
                    pixelData.addComponent("green", new DataValue(DataType.BYTE));
                    pixelData.addComponent("red", new DataValue(DataType.BYTE));                    
                    DataArray rowData = new DataArray(renderedImage.getWidth());
                    rowData.addComponent(pixelData);                    
                    DataArray imageData = new DataArray(renderedImage.getHeight());
                    imageData.addComponent(rowData);                    
                    cachedData.addComponent("image", imageData);
                    System.out.println(imageData);
                    
                    DataBlock dataBlock = DataBlockFactory.createBlock(data);
                    ((DataNode)cachedData).addData(dataBlock);
                }
			}
		}
		catch (Exception e)
		{
			if (!canceled)
				throw new DataException("Error while requesting data from WMS server:\n" + url, e);
		}
		finally
		{
			endRequest();
			updating = false;
			canceled = false;
		}
	}


	protected void updateQuery()
	{
		// update time range
		query.getTime().setStartTime(timeExtent.getAdjustedLagTime());
		query.getTime().setStopTime(timeExtent.getAdjustedLeadTime());


		// update bounding box
		query.getBbox().setMinX(spatialExtent.getMinX());
		query.getBbox().setMaxX(spatialExtent.getMaxX());
		query.getBbox().setMinY(spatialExtent.getMinY());
		query.getBbox().setMaxY(spatialExtent.getMaxY());
	}


	protected void initRequest()
	{
		// make sure previous request is cancelled
		endRequest();
		canceled = false;
	}


	protected void endRequest()
	{
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
		WMSLayerCapabilities layerCaps = (WMSLayerCapabilities) ((ServiceDataSet) resource).getCapabilities();
		if (layerCaps == null)
			return;

		// create query and set default parameters
		query = new WMSQuery();
		query.setVersion(layerCaps.getParent().getVersion());
		query.setService(layerCaps.getParent().getService());
		query.setGetServer(layerCaps.getParent().getGetServers().get("GetMap"));
		query.setWidth(900);
		query.setHeight(700);
		query.setTransparent(true);
		query.setExceptionType("application/vnd.ogc.se_xml");

		// try to select EPSG:4326 if available
		int srsIndex = layerCaps.getSrsList().indexOf("EPSG:4326");
		if (srsIndex == -1)
			srsIndex = 0;
		query.setSrs(layerCaps.getSrsList().get(srsIndex));

		// try to select jpg, gif or png if available
		int formatIndex = layerCaps.getFormatList().indexOf("image/jpg");
		if (formatIndex == -1)
			formatIndex = layerCaps.getFormatList().indexOf("image/jpeg");
		if (formatIndex == -1)
			formatIndex = layerCaps.getFormatList().indexOf("image/gif");
		if (formatIndex == -1)
			formatIndex = layerCaps.getFormatList().indexOf("image/png");
		if (formatIndex == -1)
			formatIndex = 0;
		query.setFormat(layerCaps.getFormatList().get(formatIndex));

		query.getLayers().add(layerCaps.getId());
		query.setBbox(layerCaps.getBboxList().get(0));
	}


	public WMSQuery getQuery()
	{
		return query;
	}


	public void setQuery(OWSQuery query)
	{
		this.query = (WMSQuery) query;

		// set up spatial extent
		if (this.query.getBbox() != null)
		{
			this.spatialExtent.setMaxX(this.query.getBbox().getMaxX());
			this.spatialExtent.setMaxY(this.query.getBbox().getMaxY());
			this.spatialExtent.setMinX(this.query.getBbox().getMinX());
			this.spatialExtent.setMinY(this.query.getBbox().getMinY());
		}
		
		// set up time extent
		if (this.query.getTime() != null)
		{
			double start = this.query.getTime().getStartTime();
			double stop = this.query.getTime().getStopTime();
			this.timeExtent.setBaseTime(start);
			this.timeExtent.setLagTimeDelta(stop - start);
		}
	}


	public boolean isSpatialSubsetSupported()
	{
		return true;
	}


	public boolean isTimeSubsetSupported()
	{
		// TODO support time in WMS
		return false;
	}

	//  This should probably be in OWSProvider interface
	public WMSLayerCapabilities getCapabilities() {
		return layerCaps;
	}
}