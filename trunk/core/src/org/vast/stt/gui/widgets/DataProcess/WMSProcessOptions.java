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

package org.vast.stt.gui.widgets.DataProcess;

import org.vast.cdm.common.DataBlock;
import org.vast.cdm.common.DataComponent;
import org.vast.data.DataBlockBoolean;
import org.vast.data.DataBlockString;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.sensorML.process.WMS_Process;

/**
 * <p><b>Title:</b>
 * WMSProcessOptions
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Convenience class for setting options in a WMSProcess
 *   NOTE that this also needs to modify the underlying WMS_Process,
 *   not just the request params!
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Apr 4, 2007
 * @version 1.0
 * 
 *  TODO  modify underlying process based on user changes
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

