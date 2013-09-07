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
    Alexandre Robin <robin@nsstc.uah.edu>    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.widgets.catalog;

import java.util.List;
import org.sensorML.process.SOS_Process;
import org.vast.data.DataBlockString;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.math.Vector3d;
import org.vast.ows.sld.Geometry;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.process.DataProcess;
import org.vast.stt.data.DataException;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.STTTimeExtent;
import org.vast.stt.provider.ows.SOSProvider;
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.stt.style.SymbolizerFactory;
import org.vast.util.Bbox;
import org.vast.util.TimeExtent;


/**
 * <p><b>Title:</b>
 *  SOSLayerFactory
 * </p>
 *
 * <p><b>Description:</b><br/>
 * 
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Apr 4, 2007
 * @version 1.0
 */
public class SOSLayerFactory 
{
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
			String [] mappings, String symType, Vector3d foi){
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
		List<TimeExtent> times = caps.getTimeList();
		TimeExtent t0 = times.get(0);
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
		sym.setEnabled(true);
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
        		switch(i){
        		case 0:
        			sp.setConstantValue(new Float(foi.x*Math.PI/180.0));
        			geom.setX(sp);
        			continue;
        		case 1:
        			sp.setConstantValue(new Float(foi.y*Math.PI/180.0));
        			geom.setY(sp);
        			continue;
        		case 2:
        			sp.setConstantValue(new Float(foi.z));
        			geom.setZ(sp);
        			continue;
        		default:
        			System.err.println("SOSLayerFactory error: FOI can only be mapped to X, Y, or Z");
        		}
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
    			//geom.setT(sp);
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

