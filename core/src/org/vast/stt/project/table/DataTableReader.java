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

package org.vast.stt.project.table;

import java.util.Hashtable;

import org.vast.io.xml.DOMReader;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.vast.stt.project.tree.DataTreeReader;
import org.vast.stt.provider.DataProvider;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * <p><b>Title:</b>
 * Data Table Reader
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Reads DataTable options from project XML.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Sep 13, 2006
 * @version 1.0
 */
public class DataTableReader extends XMLReader implements XMLModuleReader
{
    protected DataTreeReader dataTreeReader;
    
    
    public DataTableReader()
    {
        dataTreeReader = new DataTreeReader();
    }
    
    
    @Override
    public void setObjectIds(Hashtable<String, Object> objectIds)
    {
        super.setObjectIds(objectIds);
        dataTreeReader.setObjectIds(objectIds);
    }
    
    
    public Object read(DOMReader dom, Element tableElt)
    {
        DataTable table = new DataTable();
        
        // data provider
        Element providerElt = dom.getElement(tableElt, "dataProvider/*");
        DataProvider provider = dataTreeReader.readDataProvider(dom, providerElt);
        table.setDataProvider(provider);
        
        // column list
        NodeList columnElts = dom.getElements(tableElt, "column/Column");
        int listSize = columnElts.getLength();
        
        // read all stylers
        for (int i=0; i<listSize; i++)
        {
            Element columnElt = (Element)columnElts.item(i);
            DataColumn nextCol = readColumn(dom, columnElt);            
            table.getSymbolizers().add(nextCol.getStyle());
        }
        
        // enabled ?
        String enabled = dom.getAttributeValue(tableElt, "enabled");
        if ((enabled != null) && (enabled.equalsIgnoreCase("true")))
            table.setEnabled(true);
        else
            table.setEnabled(false);
        
        return table;
    }
    
    
    protected DataColumn readColumn(DOMReader dom, Element columnElt)
    {
        DataColumn column = new DataColumn();
        
        Element styleElt = dom.getElement(columnElt, "style");
        Symbolizer symbolizer = dataTreeReader.readSymbolizer(dom, styleElt);
        if (symbolizer != null)
            column.setStyle(symbolizer);
        
        // set column width
        int width = Integer.parseInt(dom.getElementValue(columnElt, "width"));
        column.setWidth(width);
        
        // set name
        column.setName(dom.getElementValue(columnElt, "name"));
        
        return column;
    }
}
