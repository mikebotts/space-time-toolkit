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
import org.vast.stt.project.chart.ChartItemReader;
import org.vast.stt.project.tree.DataTreeReader;
import org.vast.stt.provider.DataProvider;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * <p><b>Title:</b>
 * Table Item Reader
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Reads TableItem options from project XML.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Sep 13, 2006
 * @version 1.0
 */
public class TableItemReader extends XMLReader implements XMLModuleReader
{
    protected DataTreeReader dataTreeReader;
    protected ChartItemReader chartItemReader;
    
    
    public TableItemReader()
    {
        dataTreeReader = new DataTreeReader();
        chartItemReader = new ChartItemReader();
    }
    
    
    @Override
    public void setObjectIds(Hashtable<String, Object> objectIds)
    {
        super.setObjectIds(objectIds);
        dataTreeReader.setObjectIds(objectIds);
        chartItemReader.setObjectIds(objectIds);
    }
    
    
    public Object read(DOMReader dom, Element itemElt)
    {
        TableItem item = new TableItem();
        
        // data provider
        Element providerElt = dom.getElement(itemElt, "dataProvider/*");
        DataProvider provider = dataTreeReader.readDataProvider(dom, providerElt);
        item.setDataProvider(provider);
        
        // read table style options
        TableSymbolizer table = readTable(dom, itemElt);
        item.setTableInfo(table);
        
        // enabled ?
        String enabled = dom.getAttributeValue(itemElt, "enabled");
        if ((enabled != null) && (enabled.equalsIgnoreCase("true")))
            item.setEnabled(true);
        else
            item.setEnabled(false);
        
        return item;
    }
    
    
    public TableSymbolizer readTable(DOMReader dom, Element tableElt)
    {
        TableSymbolizer table = new TableSymbolizer();
        
        // column list
        NodeList columnElts = dom.getElements(tableElt, "Column");
        int listSize = columnElts.getLength();
        
        // read all columns
        for (int i=0; i<listSize; i++)
        {
            Element columnElt = (Element)columnElts.item(i);
            DataColumn nextCol = readColumn(dom, columnElt);            
            table.getColumns().add(nextCol);
        }
        
        return table;
    }
    
    
    protected DataColumn readColumn(DOMReader dom, Element columnElt)
    {
        DataColumn column = new DataColumn();
        
        // style/symbolizer list
        NodeList symElts = dom.getElements(columnElt, "style");
        int listSize = symElts.getLength();
        
        // read all stylers
        for (int i=0; i<listSize; i++)
        {
            Element styleElt = (Element)symElts.item(i);
            Symbolizer symbolizer = null;
            Element symElt = dom.getFirstChildElement(styleElt);
            
            // read column symbolizer (also supports sub chart or table)
            if (symElt.getLocalName().endsWith("ChartSymbolizer"))
                symbolizer = chartItemReader.readChart(dom, styleElt);
            else if (symElt.getLocalName().endsWith("TableSymbolizer"))
                symbolizer = readTable(dom, styleElt);
            else
                symbolizer = dataTreeReader.readSymbolizer(dom, styleElt);
            
            if (symbolizer != null)
                column.setSymbolizer(symbolizer);
        }
        
        // set column width
        int width = Integer.parseInt(dom.getElementValue(columnElt, "width"));
        column.setWidth(width);
        
        // set name
        column.setName(dom.getElementValue(columnElt, "name"));
        
        return column;
    }
}
