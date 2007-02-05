/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the
 University of Alabama in Huntsville (UAH).
 Portions created by the Initial Developer are Copyright (C) 2006
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.project.feedback;

import java.util.Hashtable;
import org.vast.xml.DOMHelper;
import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.w3c.dom.Element;


/**
 * <p><b>Title:</b>
 * Data Item Event Reader
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Reads feedback options within a DataItem.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Sep 13, 2006
 * @version 1.0
 */
public class ItemActionReader extends XMLReader implements XMLModuleReader
{
    public ItemActionReader()
    {
        
    }
    
    
    @Override
    public void setObjectIds(Hashtable<String, Object> objectIds)
    {
        super.setObjectIds(objectIds);
        
    }


    public Object read(DOMHelper dom, Element elt)
    {
        // TODO Auto-generated method stub
        return new ShowPopup();
    }
}
