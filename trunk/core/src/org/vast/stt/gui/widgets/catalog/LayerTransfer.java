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
 * <p>Copyright (c) 2007</p>
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

