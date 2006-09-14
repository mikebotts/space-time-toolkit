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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.vast.stt.project.table.DataTable;


public class RichTable extends Composite
{
    protected Composite rootPane;
    
    
    public RichTable(Composite parent, int style)
    {
        super(parent, style);
        
    }
    
    
    public void drawTable(DataTable table)
    {
        if (rootPane != null)
        {
            rootPane.dispose();
            rootPane = null;
        }
        
        // create rootPane
        rootPane = new Composite(this, SWT.NONE);
        GridLayout panelLayout = new GridLayout(3, false);
        panelLayout.horizontalSpacing = 0;
        panelLayout.verticalSpacing = 0;
        rootPane.setLayout(panelLayout);
        rootPane.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
        
        // add columns
        for (int i = 0; i < 3; i++)
        {
            Button colButton = new Button(rootPane, SWT.PUSH);
            GridData gridData = new GridData();
            gridData.heightHint = 16;
            gridData.grabExcessHorizontalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
            colButton.setLayoutData(gridData);
            colButton.setText("col" + i);
        }

        // add cells
        for (int i = 0; i < 30; i++)
        {
            Label label = new Label(rootPane, SWT.SHADOW_NONE);
            GridData gridData = new GridData();
            gridData.horizontalAlignment = GridData.FILL;
            label.setLayoutData(gridData);
            label.setText("text");
        }
    }
}
