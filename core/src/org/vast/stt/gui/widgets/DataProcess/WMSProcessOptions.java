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

package org.vast.stt.gui.widgets.DataProcess;

import org.vast.cdm.common.DataBlock;
import org.vast.cdm.common.DataComponent;
import org.vast.data.DataBlockBoolean;
import org.vast.data.DataBlockString;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.stt.process.WMS_Process;

/**
 * <p><b>Title:</b>
 * WMSProcessOptions
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Convenience class for setting options in a WMSProcess
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Apr 4, 2007
 * @version 1.0
 */

public class WMSProcessOptions 
{
	WMS_Process wmsProcess;
	DataGroup wmsParams;
	DataGroup wmsOptions;

	public WMSProcessOptions(WMS_Process process){
		this.wmsProcess = process;
		wmsParams = (DataGroup) process.getParameterList();
		wmsOptions = (DataGroup) wmsParams.getComponent(0);
	}
	
	public int getInputImageWidth() {
        DataComponent wComp = wmsOptions.getComponent("imageWidth");
        DataBlock wBlock = wComp.getData();
        int w = wBlock.getIntValue();
        return w;
	}
	
	public int getInputImageHeight(){
        int h = wmsOptions.getComponent("imageHeight").getData().getIntValue();
        return h;
	}
	
	public String getVersion(){
        String version = wmsOptions.getComponent("version").getData().getStringValue();
		return version;
	}
	
	public String getSRS(){
		//  TODO uncomment when SRS supported by process
        //String version = wmsParams.getComponent("srs").getData().getStringValue();
		return "EPSG:4326";
	}
	
	public String getFormat(){
        String format = wmsOptions.getComponent("format").getData().getStringValue();
        return format;
	}
	
	public boolean getTransparency(){
        boolean transparent = wmsOptions.getComponent("imageTransparency").getData().getBooleanValue();
        return transparent;
	}
	
	public boolean getMaintainAspect(){
		return wmsProcess.getPreserveAspectRatio();
	}	
	
	public void setEndpoint(String url){
		DataValue endPt = (DataValue) wmsOptions.getComponent("endPoint");
		DataBlockString dbs = new DataBlockString(1);
		dbs.setStringValue(url);
		endPt.setData(dbs);
	}

	public void setInputImageHeight(int h){
        wmsOptions.getComponent("imageHeight").getData().setIntValue(h);
	}        
	
	public void setInputImageWidth(int w){
		wmsOptions.getComponent("imageWidth").getData().setIntValue(w);
	}
	
	public void setLayer(String layer){
		DataValue layerDV = (DataValue) wmsOptions.getComponent("layer");
		//String layerStr = wmsCaps.getName();
		DataBlockString dbs = new DataBlockString(1);
		dbs.setStringValue(layer);
		layerDV.setData(dbs);
	}
	
	public void setFormat(String format){
		DataValue formatDV = (DataValue) wmsOptions.getComponent("format");
		DataBlockString dbs = new DataBlockString(1);
		dbs.setStringValue(format);
		formatDV.setData(dbs);
	}
	
	public void setTransparency(boolean b){
		DataValue transDV = (DataValue) wmsOptions.getComponent("imageTransparency");
		DataBlockBoolean dbb = new DataBlockBoolean(1);
		dbb.setBooleanValue(b);
		transDV.setData(dbb);
	}
	
	public void setSRS(String srs){
		DataBlockString dbs = new DataBlockString(1);
		dbs.setStringValue(srs);
		DataValue srsDV = (DataValue) wmsOptions.getComponent("srs");
		srsDV.setData(dbs);
	}
	
	public void setStyle(String style){
		DataBlockString dbs = new DataBlockString(1);
		dbs.setStringValue(style);
		DataValue stylesDV = (DataValue) wmsOptions.getComponent("styles");
		stylesDV.setData(dbs);
	}
}

