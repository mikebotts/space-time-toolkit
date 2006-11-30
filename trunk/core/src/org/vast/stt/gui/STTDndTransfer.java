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

package org.vast.stt.gui;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;


public class STTDndTransfer extends ByteArrayTransfer
{
    private static STTDndTransfer instance = new STTDndTransfer();
    private static final String TYPE_NAME = "layer-transfer-format";
    private static final int TYPEID = registerType(TYPE_NAME);
    private Object transferObject;


    public static STTDndTransfer getInstance()
    {
        return instance;
    }


    private STTDndTransfer()
    {
    }


    protected int[] getTypeIds()
    {
        return new int[] { TYPEID };
    }


    protected String[] getTypeNames()
    {
        return new String[] { TYPE_NAME };
    }


    protected void javaToNative(Object object, TransferData transferData)
    {
        this.transferObject = object;
        byte [] dum = new byte[1];
        super.javaToNative(dum, transferData);
    }


    protected Object nativeToJava(TransferData transferData)
    {
        super.nativeToJava(transferData);
        return this.transferObject;
    }
}
