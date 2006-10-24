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

package org.vast.stt.gui.widgets.table;

import java.util.ArrayList;
import java.util.Hashtable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.project.table.DataColumn;
import org.vast.stt.project.table.TableSymbolizer;
import org.vast.stt.project.table.TableItem;
import org.vast.stt.style.*;


public class RichTable extends Composite implements StylerVisitor
{
    protected Composite rootPane;
    protected boolean embedded = false;
    protected final static int EMBEDDED = 3457628;
    protected ArrayList<DataStyler> stylers;
    protected Hashtable<Symbolizer, DataStyler> stylerTable;
    protected TableItem tableItem;
    protected TableSymbolizer tableInfo;
    protected boolean moreRows;
    protected Composite currentCell;
    protected ScrolledComposite mainSC;
    
    
    public RichTable(Composite parent, int style)
    {
        super(parent, SWT.NONE);
        this.setLayout(new FillLayout());
        
        mainSC = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
        mainSC.setExpandVertical(true);
        mainSC.setExpandHorizontal(true);
        
        if (style == EMBEDDED)
            embedded = true;
        
        stylers = new ArrayList<DataStyler>(1);
        stylerTable = new Hashtable<Symbolizer, DataStyler>();
    }
    
    
    public void setTable(TableItem tableItem, TableSymbolizer tableInfo)
    {
        this.tableItem = tableItem;
        this.tableInfo = tableInfo;
    }
    
    
    public void updateTable()
    {
        if (rootPane != null)
            rootPane.dispose();
        
        ArrayList<DataColumn> columns = tableInfo.getColumns();
        int colCount = columns.size();
        rootPane = new Composite(mainSC, SWT.NONE);
        GridLayout panelLayout = new GridLayout(colCount, false);
        panelLayout.horizontalSpacing = 0;
        panelLayout.verticalSpacing = 0;
        rootPane.setLayout(panelLayout);
        
        // create layout for all cells
        GridData gridData = new GridData();
        gridData.heightHint = 16;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        FillLayout cellLayout = new FillLayout();
        cellLayout.marginWidth = 2;
        
        // add columns
        for (int i = 0; i < colCount; i++)
        {
            //Button colButton = new Button(rootPane, SWT.PUSH);
            currentCell = new Composite(rootPane, SWT.BORDER);
            currentCell.setLayout(cellLayout);
            currentCell.setLayoutData(gridData);
            currentCell.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
            Label colLabel = new Label(currentCell, SWT.NONE);
            colLabel.setText(columns.get(i).getName());
            colLabel.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
            
            // create stylers the first time
            if (stylers.size() < colCount)
                updateSymbolizer(columns.get(i).getSymbolizer());
            
            // reset styler            
            ((AbstractStyler)stylers.get(i)).resetIterators();
        }

        moreRows = true;
        //while(moreRows)
        for (int row=0; row<this.getClientArea().height; row+=16)
        {
            for (int i = 0; i < colCount; i++)
            {
                currentCell = new Composite(rootPane, SWT.BORDER);
                currentCell.setLayout(cellLayout);
                currentCell.setLayoutData(gridData);
                currentCell.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));                                
                stylers.get(i).accept(this);
            }
        }
        
        rootPane.pack();
        mainSC.setContent(rootPane);
        mainSC.setMinSize(this.computeSize(SWT.DEFAULT, SWT.DEFAULT));        
    }
    
    
    public void updateSymbolizer(Symbolizer sym)
    {
        // try to find corresponding styler
        DataStyler styler = stylerTable.get(sym);
        
        // create new one if not found
        if (styler == null)
        {
            if (!sym.isEnabled())
                return;
            
            styler = StylerFactory.createStyler(sym);
            styler.setDataItem(tableItem);
            stylers.add(styler);
            stylerTable.put(sym, styler);
        }
        
        // otherwise update existing one
        else
        {
            if (sym.isEnabled())
            {
                styler.updateDataMappings();
            }
            else
            {
                // completely remove if disabled
                stylers.remove(styler);
                stylerTable.remove(sym);
            }                
        }
    }


    public void visit(PointStyler styler)
    {        
    }


    public void visit(LineStyler styler)
    {       
    }


    public void visit(PolygonStyler styler)
    {        
    }


    public void visit(LabelStyler styler)
    {
        LabelGraphic label = styler.nextPoint();
        
        if (label == null)
        {
            if (styler.hasMoreBlocks())
            {
                styler.nextBlock();
                label = styler.nextPoint();
            }
            else
            {
                moreRows = false;
                return;
            }
        }        
        
        Label widget = new Label(currentCell, SWT.SHADOW_NONE);
        widget.setText(label.text);
        widget.setForeground(getColor(label.r, label.g, label.b));
        widget.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    }


    public void visit(GridMeshStyler styler)
    {       
    }


    public void visit(GridFillStyler styler)
    {
    }


    public void visit(GridBorderStyler styler)
    {
    }


    public void visit(TextureStyler styler)
    {
    }
    
    
    public void visit(RasterStyler styler)
    {       
    }
    
    
    public void visit(TableStyler styler)
    {
        RichTable table = new RichTable(currentCell, SWT.NONE);
        table.setTable(this.tableItem, styler.getSymbolizer());
        table.update();
    }
    
    
    ArrayList<Resource> allocatedGraphics = new ArrayList<Resource>();
    protected Color getColor(float r, float g, float b)
    {
        Color color = new Color(this.getDisplay(), (int)r*255, (int)g*255, (int)b*255);
        allocatedGraphics.add(color);
        return color;
    }


    @Override
    public void dispose()
    {
        // dispose of all graphics
        for (int i=0; i<allocatedGraphics.size(); i++)
            allocatedGraphics.get(i).dispose();
        
        super.dispose();
    }
}
