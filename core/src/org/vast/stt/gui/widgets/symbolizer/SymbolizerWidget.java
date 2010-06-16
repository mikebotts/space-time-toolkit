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
    Alexandre Robin <robin@nsstc.uah.edu>    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.widgets.symbolizer;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.sld.Geometry;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.views.ImageView;
import org.vast.stt.gui.views.SymbolizerView;
import org.vast.stt.gui.widgets.CheckOptionTable;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.style.SymbolizerFactory;


/**
 * <p><b>Title:</b><br/>
 * StyleWidget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for controlling styler options for a DataItem.  Note that I 
 *  used a trial version of SWTDesigner to build portions of this widget.     
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Jan 14, 2006
 * @version 1.0
 * 
 */
public class SymbolizerWidget extends CheckOptionTable
{
    Symbolizer activeSymbolizer;
    SymbolizerView view; 
    String imageViewId = "STT.ImageView";
    
    //  NOTE:  adding view parent to this class for OWS7 demo- I need it to 
    //  feed dataItems to this view. There may be a better way to do it though.
    //  If so, remove
    public SymbolizerWidget(Composite parent, SymbolizerView view)
    {
    	this.view = view;
        checkboxTableLabel = "Graphics:";
        init(parent);
        setCheckboxTableContentProvider(new StyleTableContentProvider());
        setCheckboxTableLabelProvider(new SymTableLabelProvider());
    }


    public OptionChooser createOptionChooser(Composite parent)
    {
        SymbolizerOptionChooser basicOptionChooser = new SymbolizerOptionChooser(parent); 
        return basicOptionChooser;
    }


    public void setDataItem(DataItem item)
    {
        super.setDataItem(item);
        setSymbolizers(item.getSymbolizers());
        //  repaint the optionControllers...
        optionChooser.optionController.loadFields();
    }

    /**
     * Make this Symbolizer the currently active Symbolizer in the SymbolizerWidget.
     * This is now being called by SymbolizerView.updateView() because of a 
     * ITEM_STYLE_CHANGED event.  This caused a side-effect in the behavior
     * which resets the activeSymbolizer every time any change was made.  I 
     * added the activeSymbolier==null check to prevent.  Need to consider
     * if this is the best way to go about this.  TC
     * @param newStyler
     */
    private void setSymbolizers(List<Symbolizer> symbolizerList)
    {
        //  Change cbTableViewer contents
        checkboxTableViewer.setInput(symbolizerList);
        Iterator<Symbolizer> it = symbolizerList.iterator();
        Symbolizer symTmp;
        //  Set init state of checkboxes
        while (it.hasNext())
        {
            symTmp = it.next();
            checkboxTableViewer.setChecked(symTmp, symTmp.isEnabled());
        }
        //  added check, 7/19/06
        if(activeSymbolizer == null) {
	        checkboxTableViewer.getTable().setSelection(0);
	        ISelection selection = checkboxTableViewer.getSelection();
	        checkboxTableViewer.setSelection(selection);
        }
    }


    void addSymbolizer(Symbolizer symbolizer)
    {
        //  Add Checkbox to stylers Set and rerender Table
        dataItem.getSymbolizers().add(symbolizer);
        activeSymbolizer = null;
        checkboxTableViewer.setInput(dataItem.getSymbolizers());
    }


    private void removeSymbolizer(Symbolizer symbolizer)
    {
        dataItem.getSymbolizers().remove(symbolizer);
        //  reset activeStyler\
        // ...
        //TableItem [] items = checkboxTableViewer.getTable().getItems();
        checkboxTableViewer.setInput(dataItem.getSymbolizers());
    }


    //  enabling checkbox causes ckState AND selChanged events
    public void checkStateChanged(CheckStateChangedEvent e)
    {
        // TODO Auto-generated method stub
        //  e.getElement returns checked Styler
        Symbolizer symbolizer = (Symbolizer) e.getElement();
        symbolizer.setEnabled(e.getChecked());
        dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
    }


    //  Selecting label causes ONLY selChanged event
    public void selectionChanged(SelectionChangedEvent e)
    {
        StructuredSelection selection = (StructuredSelection) e.getSelection();
        Symbolizer symbolizer = (Symbolizer) selection.getFirstElement();
        
        //  Check for empty selection (happens when buildControls() is called)
        if (symbolizer == null)
        {
            Iterator it = dataItem.getSymbolizers().iterator();
            if (!it.hasNext())
            {
                //  stylerSet is currently empty
                optionChooser.removeOldControls();
                return;
            }
            //  Reset selected to first in Table
            checkboxTableViewer.getTable().setSelection(0);
            selection = (StructuredSelection) checkboxTableViewer.getSelection();
            symbolizer = (Symbolizer) selection.getFirstElement();
        }
        //  Check to see if selected Styler has really changed
//        if (symbolizer == activeSymbolizer)
//        {
//            //System.err.println("Selection not really changed");
//            return;
//        }
        //System.err.println("Selection CHANGED");
        activeSymbolizer = symbolizer;
        ((SymbolizerOptionChooser)optionChooser).setDataItem(dataItem);
        optionChooser.buildControls(symbolizer);
    }

    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    public void widgetSelected(SelectionEvent e)
    {
        // TODO Auto-generated method stub
        Control control = (Control) e.getSource();
        
        if (control == addButton)
        {
            openAddSymDialog();
        }
        else if (control == deleteButton)
        {
            if (activeSymbolizer != null)
                removeSymbolizer(activeSymbolizer); //  remove currently selected row   
        }
        else if (control == enabledButton)
        {
            if (dataItem != null)
            {
                dataItem.setEnabled(enabledButton.getSelection());
                dataItem.dispatchEvent(new STTEvent(activeSymbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
            }
        }
        else if (control == advancedButton)
        {
            createAdvancedSymDialog();
        } 
        else if (control == popupImageBtn) {
//        	if(dataItem == null)
//        		return;
//        	ImagePopup ip = new ImagePopup(PlatformUI.getWorkbench().getDisplay());
//        	ip.setDataItem(dataItem);
//        	PlatformUI.getWorkbench().getDisplay().findWidget(handle);
        	
        	ImageView imView = (ImageView)(view.getSite().getPage().findView(imageViewId));
        	imView.addDataItem(dataItem);
        }
       
    }

    //  TODO:  Disable Advanced Button until Data is in DataNode?
    private void createAdvancedSymDialog()
    {
        try
        {
            new AdvancedSymbolizerDialog(dataItem, activeSymbolizer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "STT Warning", "Advanced Style Dialog could not be opened.\n" + ""
                    + "DataNode is  probably still empty.");
        }
    }


    private void openAddSymDialog()
    {
//    	if(dataItem == null)
//    		return;
        AddSymbolizerDialog asd = new AddSymbolizerDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
        int rc = asd.getReturnCode();
        if (rc == IDialogConstants.OK_ID)
        {
            //  I still need to create a Styler to get a Symbolizer, right?
        	Geometry geom;
        	if(activeSymbolizer != null)
        		geom = activeSymbolizer.getGeometry();
        	else 
                //  How to assign default geom?  Take from existing symbolizer, or force 
                //  user selection with wizard/dialog
        		geom = new Geometry();
        	//Symbolizer sym = styler.getSymbolizer();
        	Symbolizer sym = 
        		SymbolizerFactory.createDefaultSymbolizer(asd.getStylerName().trim(), asd.getSymbolizerType());
            sym.setGeometry(geom);
        	this.addSymbolizer(sym);
        }
    }
}


class StyleTableContentProvider implements IStructuredContentProvider
{
    public Object[] getElements(Object inputElement)
    {
        List symbolizers = (List)inputElement;
        return symbolizers.toArray();
    }


    public void dispose()
    {
    }


    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }
}


class SymTableLabelProvider extends LabelProvider
{

    public Image getImage(Object element)
    {
        return null;
    }


    public String getText(Object element)
    {
        Symbolizer sym = (Symbolizer) element;
        return sym.getName();
    }
}
