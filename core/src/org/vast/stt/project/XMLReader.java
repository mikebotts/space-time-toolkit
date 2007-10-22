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

package org.vast.stt.project;

import java.util.Hashtable;

import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;


public abstract class XMLReader
{
    protected Hashtable<String, Object> objectIds;  
    
    
    /**
     * Try to retrieve an object that has already been parsed
     * and should be referenced instead of creating a new instance.
     * @param objElt
     * @return
     */
    protected Object findExistingObject(DOMHelper dom, Element objElt)
    {
        Object obj = null;
        String id = dom.getAttributeValue(objElt, "id");
        if (id != null)
            obj = objectIds.get(id);
        return obj;
    }
    
    
    /**
     * Add an object to the id->object map table
     * @param objElt
     * @param obj
     */
    protected void registerObjectID(DOMHelper dom, Element objElt, Object obj)
    {
        String id = dom.getAttributeValue(objElt, "id");
        
        if ((id != null) && (obj != null))
            objectIds.put(id, obj);
    }

    
    /**
     * Used to share the object ID table between readers
     * @param objectIds
     */
    public void setObjectIds(Hashtable<String, Object> objectIds)
    {
        this.objectIds = objectIds;
    }
}
