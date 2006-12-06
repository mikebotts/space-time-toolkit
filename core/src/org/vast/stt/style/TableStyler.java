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

package org.vast.stt.style;

import java.util.ArrayList;
import java.util.List;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.project.table.DataColumn;
import org.vast.stt.project.table.RichTable;
import org.vast.stt.project.table.TableSymbolizer;


public class TableStyler extends AbstractStyler
{
    protected TableSymbolizer symbolizer;
    protected ArrayList<DataStyler> stylers;
    protected int currentColumn;
    
	
	public TableStyler()
	{
        
	}
    
    
    public DataStyler nextStyler()
    {
        DataStyler styler = stylers.get(currentColumn);
        currentColumn++;
        return styler;
    }
    
    
    @Override
	public void updateDataMappings()
	{
        List<DataColumn> cols = symbolizer.getColumns();
        stylers.clear();
        
        for (int i=0; i<cols.size(); i++)
        {
            Symbolizer sym = cols.get(i).getSymbolizer();
            DataStyler styler = StylerFactory.createStyler(sym);
            stylers.add(styler);
        }
	}
	
	
	public TableSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (TableSymbolizer)sym;
	}


    public void accept(StylerVisitor visitor)
    {
        dataNode = dataItem.getDataProvider().getDataNode();
        
        if (dataNode.isNodeStructureReady())
        {
            if (dataLists.length == 0)
                updateDataMappings();
                        
            ((RichTable)visitor).visit(this);
        }        
    }


    @Override
    public void resetIterators()
    {
        super.resetIterators();
        currentColumn = 0;
    }
    
    
    @Override
    public void computeBoundingBox()
    {

    }
}