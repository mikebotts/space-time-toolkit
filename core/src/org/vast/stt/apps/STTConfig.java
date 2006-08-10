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

package org.vast.stt.apps;


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
}
