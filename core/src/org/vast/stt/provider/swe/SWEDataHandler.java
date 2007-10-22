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

package org.vast.stt.provider.swe;

import java.util.Hashtable;
import org.vast.cdm.common.DataBlock;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataHandler;
import org.vast.cdm.common.DataType;
import org.vast.data.AbstractDataBlock;
import org.vast.stt.data.BlockList;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.DataProvider;
import org.vast.unit.UnitConversion;
import org.vast.unit.UnitConverter;


/**
 * <p><b>Title:</b><br/>
 * SOS Data Handler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Handles parser events and copy data to DataNode.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 11, 2005
 * @version 1.0
 */
public class SWEDataHandler implements DataHandler
{
	protected Hashtable<DataComponent, UnitConverter> converters;
    protected BlockList blockList;
    protected DataProvider provider;
	protected boolean firstBlock = true;
    

	public SWEDataHandler(DataProvider provider)
	{
		this.provider = provider;
        converters = new Hashtable<DataComponent, UnitConverter>();
	}
	
	
	public void setBlockList(BlockList blockList)
	{
        this.blockList = blockList;
	}
    
    
    public void reset()
    {
        firstBlock = true;
    }
	
	
	public void endData(DataComponent info, DataBlock data)
	{
	    // clear data node before adding first block
        if (firstBlock)
        {
            provider.getDataNode().clearAll();
            firstBlock = false;
        }        
        
        blockList.addBlock((AbstractDataBlock)data);
        provider.dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
	}


	public void endDataAtom(DataComponent info, DataBlock data)
	{
		DataType dataType = data.getDataType();
        
        if (dataType != DataType.UTF_STRING && dataType != DataType.ASCII_STRING)
        {
            UnitConverter converter = converters.get(info);
    		
    		if (converter == null)
    		{
    			String uom = (String)info.getProperty(DataComponent.UOM_CODE);
    			converter = UnitConversion.createConverterToSI(uom);
    	    	converters.put(info, converter);
    		}
    		
        	// convert to SI
    		double newVal = converter.convert(data.getDoubleValue());
    		data.setDoubleValue(newVal);
        }
	}


	public void endDataBlock(DataComponent info, DataBlock data)
	{
	}


	public void startData(DataComponent info)
	{
	}


	public void startDataBlock(DataComponent info)
	{
	}


    public void beginDataAtom(DataComponent info)
    {       
    }
}