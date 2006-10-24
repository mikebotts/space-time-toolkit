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

import java.util.ArrayList;
import java.util.List;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.project.tree.DataItem;


/**
 * <p><b>Title:</b>
 * Table Item
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO TableItem type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Oct 23, 2006
 * @version 1.0
 */
public class TableItem extends DataItem
{
    protected TableSymbolizer tableInfo;
    
    
    public TableSymbolizer getTableInfo()
    {
        return tableInfo;
    }


    public void setTableInfo(TableSymbolizer tableInfo)
    {
        this.tableInfo = tableInfo;
    }
    
    
    public List<Symbolizer> getSymbolizers()
    {
        ArrayList<Symbolizer> columnStyles = new ArrayList<Symbolizer>();
        
        if (tableInfo != null)
        {
            List<DataColumn> cols = tableInfo.getColumns();
            for (int i=0; i<cols.size(); i++)
            {
                Symbolizer sym = cols.get(i).getSymbolizer();
                if (sym != null)
                    columnStyles.add(sym);
            }
        }
        
        return columnStyles;
    }
}
