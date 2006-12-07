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

package org.vast.stt.renderer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.vast.stt.project.scene.SceneItem;


public class PopupRenderer
{
    protected Composite composite;
    
    
    public PopupRenderer(Composite composite)
    {
        this.composite = composite;
    }
    
    
    public void showPopup(int x, int y, SceneItem item)
    {
        Display display = composite.getDisplay();
        Shell tip = new Shell(composite.getShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
        tip.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        FillLayout layout = new FillLayout ();
        layout.marginWidth = 2;
        tip.setLayout (layout);
        Label label = new Label (tip, SWT.NONE);
        label.setForeground (display.getSystemColor (SWT.COLOR_INFO_FOREGROUND));
        label.setBackground (display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
        label.setText (item.getName());
        Point pt = composite.toDisplay(x,y);
        Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        tip.setBounds (pt.x, pt.y, size.x, size.y);
        tip.setVisible (true);
        
        label.addListener(SWT.MouseDown, new Listener ()
        {
            public void handleEvent (Event event)
            {
                Label label = (Label)event.widget;
                switch (event.type)
                {
                    case SWT.MouseDown:
                        label.getShell().dispose ();
                        break;
                }
            }
         });
    }
}
