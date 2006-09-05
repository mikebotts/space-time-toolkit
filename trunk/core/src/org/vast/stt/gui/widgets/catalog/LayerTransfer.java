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
import org.eclipse.swt.dnd.TransferData;
import org.vast.ows.OWSLayerCapabilities;

/**
 * <p><b>Title:</b>
 *  LayerTransfer
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  LayerTransfer bypasses the "ByteArrayTransfer/serialization" technique, 
 *  and directly sets the sourceCaps as the object to be transferred.  
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 31, 2006
 * @version 1.0
 */

public class LayerTransfer extends ByteArrayTransfer {
	private static LayerTransfer instance = new LayerTransfer();
	private static final String TYPE_NAME = "layer-transfer-format";
	private static final int TYPEID = registerType(TYPE_NAME);
	//  The object to be Transferred
	private OWSLayerCapabilities caps;
	
	public static LayerTransfer getInstance() {
		return instance;
	}

	private LayerTransfer(){
	}
	
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	//  This doesn't really use the byte array.  This was the simplest way
	//  to get the DND to work.  There may be some platformn-dependent issues
	//  that we're risking here, but I doubt it.  TC 9/4/06
	protected void javaToNative(Object object, TransferData transferData) {
		this.caps = ((OWSLayerCapabilities) object);
		
		byte [] dum = new byte[1];
		//if (bytes != null)
		super.javaToNative(dum, transferData);
	}

	//  This doesn't really use the byte array.  This was the simplest way
	//  to get the DND to work.  There may be some platformn-dependent issues
	//  that we're risking here, but I doubt it.  TC 9/4/06
	protected Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		return this.caps;
	}
	
}

