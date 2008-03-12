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

package org.vast.stt.apps;

import org.vast.stt.project.XMLRegistry;
import org.vast.stt.project.chart.ChartSceneReader;
import org.vast.stt.project.table.TableSceneReader;
import org.vast.stt.project.world.WorldSceneReader;
//import org.vast.stt.provider.JPEG2000.JPEG2000ProviderReader;
import org.vast.stt.provider.google.GoogleMapProviderReader;
import org.vast.stt.provider.ows.OWSProviderReader;
import org.vast.stt.provider.sml.SMLProviderReader;
import org.vast.stt.provider.swe.SWEProviderReader;
import org.vast.stt.provider.ve.VirtualEarthProviderReader;
import org.vast.sttx.provider.smart.PhenomenaDetectionProviderReader;
import org.vast.sttx.provider.worldwind.WorldwindMapProviderReader;


public class STTConfig
{
	private static STTConfig currentConfig;
	
	
	// singleton constructor
	private STTConfig()
	{

	}
	
	
	public static STTConfig getInstance()
	{
		if (currentConfig == null)
			currentConfig = new STTConfig();

		return currentConfig;
	}
    
    
    public static void loadDefaultModules()
    {
        // register basic data providers reader/writers            
        XMLRegistry.registerReader("SWEDataProvider", SWEProviderReader.class);
        XMLRegistry.registerReader("OWSDataProvider", OWSProviderReader.class);
        XMLRegistry.registerReader("SensorMLProvider", SMLProviderReader.class);
        XMLRegistry.registerReader("VirtualEarthProvider", VirtualEarthProviderReader.class);
        XMLRegistry.registerReader("GoogleMapProvider", GoogleMapProviderReader.class);
        XMLRegistry.registerReader("WorldwindProvider", WorldwindMapProviderReader.class);
        XMLRegistry.registerReader("PhenomenonDetectionProvider", PhenomenaDetectionProviderReader.class);
//        XMLRegistry.registerReader("JPEG2000Provider", JPEG2000ProviderReader.class);
        
        // register basic display type reader/writers
        XMLRegistry.registerReader("Scene", WorldSceneReader.class);
        XMLRegistry.registerReader("WorldScene", WorldSceneReader.class);
        XMLRegistry.registerReader("ChartScene", ChartSceneReader.class);
        XMLRegistry.registerReader("TableScene", TableSceneReader.class);
    }
}
