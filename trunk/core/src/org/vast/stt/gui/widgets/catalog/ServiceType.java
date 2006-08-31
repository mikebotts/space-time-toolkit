package org.vast.stt.gui.widgets.catalog;

public enum ServiceType {
	
	//  Possible values
	WMS("WMS"), WCS("WCS"), WFS("WFS"), SOS("SOS");
	
	String type;
	
	ServiceType(String t){
		this.type = t;
	}
	
	public static ServiceType getServiceType(String typeStr){
		if(typeStr.equalsIgnoreCase("wms"))
			return WMS;
		else if(typeStr.equalsIgnoreCase("wcs"))
			return WCS;
		else if(typeStr.equalsIgnoreCase("wfs"))
			return WFS;
		else if(typeStr.equalsIgnoreCase("sos"))
			return SOS;
		else 
			return null;
	}
}

