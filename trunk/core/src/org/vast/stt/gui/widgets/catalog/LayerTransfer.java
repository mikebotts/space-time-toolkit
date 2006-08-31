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

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.vast.ows.OWSLayerCapabilities;

/**
 * <p><b>Title:</b>
 *  LayerTransfer
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  TODO: Add Description
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 31, 2006
 * @version 1.0
 */

public class LayerTransfer extends ByteArrayTransfer {
	
	private static final String TYPE_NAME = "layer-transfer-format";
	private static final int TYPEID = registerType(TYPE_NAME);
	//  what internal thing do I really want here?
	private OWSLayerCapabilities caps;
	
	//  
	public LayerTransfer(OWSLayerCapabilities caps){
		this.caps = caps;
	}
	
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}
}

