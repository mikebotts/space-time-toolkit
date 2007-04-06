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

package org.vast.stt.gui.widgets.catalog;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.OWSServiceCapabilities;
import org.vast.ows.util.Bbox;
import org.vast.ows.wms.WMSLayerCapabilities;
import org.vast.process.ProcessChain;
import org.vast.process.ProcessException;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.gui.widgets.DataProcess.WMSProcessOptions;
import org.vast.stt.process.WMS_Process;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.tree.DataTreeReader;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.util.ExceptionSystem;
import org.vast.xml.DOMHelper;

/**
 * <p><b>Title:</b>
 *  TODO:  Add Title
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  TODO: Add Description
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Apr 4, 2007
 * @version 1.0
 */

public class WMSLayerFactory
{
	public static DataItem createWMSLayer(WMSLayerCapabilities caps){
		try {
			String fileLocation = null;
			Enumeration e = STTPlugin.getDefault().getBundle().findEntries(
					"templates", "WMS_FlatGrid_Template.xml", false);
			if (e.hasMoreElements())
				fileLocation = (String) e.nextElement().toString();

			if (fileLocation == null) {
				ExceptionSystem.display(new Exception(
						"STT error: Cannot find template\\wms.xml"));
				return null;
			}

            DOMHelper dom = new DOMHelper(fileLocation, false);
			DataTreeReader dataReader = new DataTreeReader();
			DataItem worldItem = (DataItem)dataReader.readDataEntry(dom, dom.getRootElement());
			worldItem.setName(caps.getId());
			SMLProvider provider = (SMLProvider)worldItem.getDataProvider();
			//  load default fields from caps into SensorMLProvider 
			loadWMSProcess(provider, caps);
			
			return worldItem;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	//  TODO  Hardwired for WMS now- generalize
	protected static void loadWMSProcess(SMLProvider provider, OWSLayerCapabilities caps) {
		ProcessChain process = null;
		WMS_Process wmsProc = null;
		WMSProcessOptions wmsOptions;
		
		try {
			process = (ProcessChain)provider.getProcess();
			
			//  Bold assumptions into processChain structure...
			WMSLayerCapabilities wmsCaps = (WMSLayerCapabilities) caps;
			OWSServiceCapabilities owsCaps = wmsCaps.getParent();
			wmsProc = (WMS_Process) process.getProcessList().get(0);
			//  Add caps to the process.  Not sure if belongs here, but need caps
			//  to be persistent so the option chooser can know all available choices
			wmsProc.setCapabilities(wmsCaps);
			
			wmsOptions = new WMSProcessOptions(wmsProc);
			//  Options
			//  Use 1st get Server for now
			Map serversMap = owsCaps.getGetServers();
			String getMapUrl = (String) serversMap.get("GetMap");
			wmsOptions.setEndpoint(getMapUrl);
			String layerStr = wmsCaps.getId();
			wmsOptions.setLayer(layerStr);
			// format
			List<String> formatList = wmsCaps.getFormatList();
			String formatStr;
			//  gif and png are not working properly
			if (formatList.contains("image/jpeg"))
				formatStr = "image/jpeg";
			else
				formatStr = formatList.get(0);
			wmsOptions.setFormat(formatStr);
			wmsOptions.setTransparency(wmsCaps.isOpaque());
			List<String> srsList = wmsCaps.getSrsList();
			String srs;
			if (srsList.contains("EPSG:4326") || srsList.size() == 0)
				srs = "EPSG:4326";
			else
				srs = srsList.get(0);
			wmsOptions.setSRS(srs);
			List<String> stylesList = wmsCaps.getStyleList();
			if (stylesList != null && stylesList.size() > 0) 
				wmsOptions.setStyle(stylesList.get(0));

			// set bbox input values (override these with caps vals?
			List<Bbox> bboxList = wmsCaps.getBboxList();
			STTSpatialExtent ext = new STTSpatialExtent();
			Bbox bbox = new Bbox();
			if (bboxList != null || bboxList.size() > 0) {
				bbox = bboxList.get(0);
				ext.setMinX(bbox.getMinX());
				ext.setMaxX(bbox.getMaxX());
				ext.setMinY(bbox.getMinY());
				ext.setMaxY(bbox.getMaxY());
				provider.setSpatialExtent(ext);
			}
			// intitialize process with new params
			process.init();

		} catch (ProcessException e) {
			e.printStackTrace(System.err);
		} 
		System.err.println(wmsProc);
	}


}

