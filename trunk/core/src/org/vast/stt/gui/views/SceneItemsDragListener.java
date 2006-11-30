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

package org.vast.stt.gui.views;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;


public class SceneItemsDragListener extends DragSourceAdapter
{
    private StructuredViewer viewer;


    public SceneItemsDragListener(StructuredViewer viewer)
    {
        this.viewer = viewer;
    }


    public void dragStart(DragSourceEvent event)
    {
        event.doit = (viewer.getSelection() != null &&
                      !viewer.getSelection().isEmpty());
    }


    public void dragSetData(DragSourceEvent event)
    {
        event.data = ((StructuredSelection)viewer.getSelection()).getFirstElement();
    }
}
