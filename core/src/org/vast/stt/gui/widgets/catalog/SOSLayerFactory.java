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

import org.vast.data.DataBlockString;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.ows.sld.Geometry;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.util.Bbox;
import org.vast.ows.util.TimeInfo;
import org.vast.process.DataProcess;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.data.DataException;
import org.vast.stt.process.SOS_Process;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.tree.DataTreeReader;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.STTTimeExtent;
import org.vast.stt.provider.ows.SOSProvider;
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.stt.style.SymbolizerFactory;
import org.vast.util.ExceptionSystem;
import org.vast.xml.DOMHelper;

/**
 * <p><b>Title:</b>
 *  SOSLayerFactory
 * </p>
 *
 * <p><b>Description:</b><br/>
 * 
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Apr 4, 2007
 * @version 1.0
 */

public class SOSLayerFactory 
{
	public static DataItem[] createSOSLayer(SOSLayerCapabilities caps){
		try {
			Enumeration e;
			String templateName = "CSIRO_gatton-HUMIDITY_TEMP.xml";
			String capsServer = caps.getParent().getGetServers().get("GetCapabilities");
			
			//  Hardcoded switch to hack what we're dropping- clean up later
			//  TODO:  How to unhack this?
			if(capsServer.contains("muenster"))
				templateName = "IFGI_WeatherNY.xml";
			else
				templateName = "CSIRO_gatton-HUMIDITY_TEMP.xml";
			e = STTPlugin.getDefault().getBundle().findEntries(
					"templates", templateName, false);
			String fileLocation = null;
			if (e.hasMoreElements())
				fileLocation = (String) e.nextElement().toString();

			if (fileLocation == null) {
				ExceptionSystem.display(new Exception(
						"STT error: Cannot find template\\" + templateName));
				return null;
			}

            DOMHelper dom = new DOMHelper(fileLocation, false);
			DataTreeReader dataReader = new DataTreeReader();
//			DataItem tmpItem = (DataItem)dataReader.readDataEntry(dom, dom.getRootElement());
			List<String> procs = caps.getProcedureList();
			int numItems = procs.size();
			DataItem [] items = new DataItem[numItems];
			for(int i=0; i<numItems;i++) {
				//  No way to clone items currently.  Reread template file each time, for now
				items[i] = (DataItem)dataReader.readDataEntry(dom, dom.getRootElement());
				//  Override name from template
				items[i].setName(caps.getId());
				setSOSProcedure(items[i], procs.get(i));
			}
			
			return items;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static void setSOSProcedure(DataItem item, String procedure){
		SMLProvider provider = (SMLProvider)item.getDataProvider();
		DataProcess process = provider.getProcess();
		SOS_Process sosProc = (SOS_Process) process;
		DataGroup sosParams = (DataGroup) sosProc.getParameterList();
		DataGroup sosOptions = (DataGroup) sosParams.getComponent(0);
		//  Options
		//  TODO add some convenience methods and break this all out to separate class
		DataValue procDv = (DataValue) sosOptions.getComponent("procedures");
		DataBlockString dbs = new DataBlockString(1);
		dbs.setStringValue(procedure);
		procDv.setData(dbs);
		item.setName(item.getName() + "_" + procedure);
	}
	
	public static DataItem createSOSLayer(String offering, SOSLayerCapabilities caps, 
			String [] mappings, String symType, int usesSMLProvider_notWorkingYet){
		System.err.println("Create SOS layer: " + offering + " " + mappings[0] + " " + symType);
		DataItem item = new DataItem();
		item.setName(offering);
		
		SMLProvider provider = new SMLProvider();
		List<String> procs = caps.getProcedureList();
		SOS_Process process = new SOS_Process();
		provider.setProcess(process);
		item.setDataProvider(provider);
		provider.setEnabled(true);
		//provider.setTimeExtent(timeExtent);
		//provider.setSpatialExtent(spatialExtent);
		try {
			provider.init();
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//SOSLayerFactory.setSOSProcedure(item,procs.get(0));
		Symbolizer sym = SymbolizerFactory.createDefaultSymbolizer(offering, symType);
        item.getSymbolizers().add(sym);
		 
        return item;
	}
	
	public static DataItem createSOSLayer(String offering, SOSLayerCapabilities caps, 
			String [] mappings, String symType){
		System.err.println("Create SOS layer: " + offering + " " + mappings[0] + " " + symType);
		DataItem item = new DataItem();
		item.setName(offering);
		
		SOSProvider provider = new SOSProvider();
		provider.setServiceCapabilities(caps.getParent());
//		List<String> procs = caps.getProcedureList();
//		SOS_Process process = new SOS_Process();
//		provider.setProcess(process);
		item.setDataProvider(provider);
		//provider.setEnabled(true);
		List<TimeInfo> times = caps.getTimeList();
		TimeInfo t0 = times.get(0);
		//  Copy this time extent to provider time extent
		//  NOTE:  I need to set this extent to the actual time I want requested 
		//         intially (should come from where?).  Then, provider.initRequest()
		//         uses it to set the query times.  
		
		//STTTimeExtent extent = STTTimeExtent.getSTTTimeExtent(t0); 
			//new STTTimeExtent(t0.getAdjustedTime());
		//extent.setBaseTime(t0.getAdjustedTime());
		//extent.setLagTimeDelta(600.0);
		STTTimeExtent extent = new STTTimeExtent();
//		extent.set/BeginNow(true);
		extent.setBaseTime(System.currentTimeMillis()/1000.0 - 600);
		extent.setLagTimeDelta(600.);
//		extent.setEndNow(true);
		// other time params  
		provider.setTimeExtent(extent);
		List<Bbox> bboxes = caps.getBboxList();
		if(bboxes != null && bboxes.size()>0){
			Bbox bbox0 = bboxes.get(0);
			STTSpatialExtent spEx = new STTSpatialExtent();
			//  TODO Add convenience methods to convert TimeINfo->TimeExtent and Bbox->SpatialExtent
			spEx.setMinX(bbox0.getMinX());
			spEx.setMinY(bbox0.getMinY());
			spEx.setMaxX(bbox0.getMaxX());
			spEx.setMaxY(bbox0.getMaxY());
			//  Z
			provider.setSpatialExtent(spEx);
		}
		try {
			
			//  call createDefQuery, otherwise, query is null in init()
			provider.createDefaultQuery();
			
			provider.init();
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//SOSLayerFactory.setSOSProcedure(item,procs.get(0));
		Symbolizer sym = SymbolizerFactory.createDefaultSymbolizer(symType.toString(), symType);
        item.getSymbolizers().add(sym);
        //  Creat mappings (geometry)
        Geometry geom = sym.getGeometry();
        for(int i=0; i<5; i++){
        	System.err.println(mappings[i]);
        	ScalarParameter sp = new ScalarParameter();
        	if(mappings[i].startsWith("FeatureOfInterest")){ 
        		//   Note that FOI mazy NOT be constant.  Need a better 
        		//   way to handle those cases
        		sp.setConstant(true);
        		sp.setConstantValue(new Double(1.0));
        	} else {
        		sp.setPropertyName(mappings[i]);
        	}
        	switch(i){
    		case 0:
    			geom.setX(sp);
    			break;
    		case 1:
    			geom.setY(sp);
    			break;
    		case 2:
    			geom.setZ(sp);
    			break;
    		case 3:
    			geom.setT(sp);
    			break;
    		case 4:
    			geom.setBreaks(sp);
    			break;
    		default:
    			break;
    		}
        }
		 
        return item;
	}
}

