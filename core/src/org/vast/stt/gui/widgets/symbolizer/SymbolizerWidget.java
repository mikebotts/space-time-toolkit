
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
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.CheckOptionTable;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.project.DataItem;
import org.vast.ows.sld.*;


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
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 14, 2006
 * @version 1.0
 * 
 */
public class SymbolizerWidget extends CheckOptionTable
{
    Symbolizer activeSymbolizer;
    OptionListener optListener;


    public SymbolizerWidget(Composite parent)
    {
        checkboxTableLabel = "Rendering Options:";
        init(parent);
        setCheckboxTableContentProvider(new StyleTableContentProvider());
        setCheckboxTableLabelProvider(new StyleTableLabelProvider());
    }


    public OptionChooser createOptionChooser(Composite parent)
    {
        optListener = new OptionListener();
        SymbolizerOptionChooser basicOptionChooser = new SymbolizerOptionChooser(parent, optListener);
        return basicOptionChooser;
    }


    public void setDataItem(DataItem item)
    {
        super.setDataItem(item);
        setSymbolizers(item.getSymbolizers());
    }


    /**
     * Make this DataStyler the currently active Styler in the StyleWidget.
     * This is now being called by StyleView.updateView() because of a 
     * ITEM_STYLE_CHANGED event.  This caused a side-effect in the behavior
     * which reset the activeSymbolizer every time any change was made.  I 
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


    void addStyle(Symbolizer symbolizer)
    {
        //  Add Checkbox to stylers Set and rerender Table
        dataItem.getSymbolizers().add(symbolizer);
        activeSymbolizer = null;
        //  Change options panel to show Point options
        checkboxTableViewer.setInput(dataItem.getSymbolizers());
    }


    /**
     * create a new styler and call addStyle() with it
     */
//    public void createNewStyler(String stylerName, StylerType stylerType)
//    {
//        DataStyler newStyler = StylerFactory.createDefaultStyler(stylerName, stylerType, dataItem);
//        if (newStyler != null)
//        {
//            //  Hack to set geom
//            newStyler.getSymbolizer().setGeometry(activeStyler.getSymbolizer().getGeometry());
//            newStyler.setName(stylerName);
//            newStyler.updateDataMappings();
//            newStyler.setEnabled(false);
//            addStyle(newStyler);
//        }
//
//    }


    private void removeStyle(Symbolizer symbolizer)
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
        dataItem.dispatchEvent(new STTEvent(this, EventType.ITEM_SYMBOLIZER_CHANGED));
    }


    //  Selecting label causes ONLY selChanged event
    public void selectionChanged(SelectionChangedEvent e)
    {
        StructuredSelection selection = (StructuredSelection) e.getSelection();
        Symbolizer symbolizer = (Symbolizer) selection.getFirstElement();
        System.err.println("sel,activ Symb is" + symbolizer + ", " + activeSymbolizer);
        
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
        if (symbolizer == activeSymbolizer)
        {
            System.err.println("Selection not really changed");
            return;
        }
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
            openAddStyleDialog();
        }
        else if (control == deleteButton)
        {
            if (activeSymbolizer != null)
                removeStyle(activeSymbolizer); //  remove currently selected row   
        }
        else if (control == enabledButton)
        {
            if (dataItem != null)
            {
                dataItem.setEnabled(enabledButton.getSelection());
                dataItem.dispatchEvent(new STTEvent(this, EventType.ITEM_SYMBOLIZER_CHANGED));
            }
        }
        else if (control == advancedButton)
        {
            createAdvancedStyleDialog();
        }
    }


    //  TODO:  Disable Advanced Button until Data is in DataNode?
    private void createAdvancedStyleDialog()
    {
        try
        {
            new AdvancedSymbolizerDialog(dataItem, activeSymbolizer, optListener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "STT Warning", "Advanced Style Dialog could not be opened.\n" + ""
                    + "DataNode is  probably still empty.");
        }
    }


    private void openAddStyleDialog()
    {
        AddSymbolizerDialog asd = new AddSymbolizerDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
        int rc = asd.getReturnCode();
        if (rc == IDialogConstants.OK_ID)
        {
            //createNewStyler(asd.getStylerName(), asd.getStylerType());
        }

    }


    //  Called when parent styleView is closed.  Set basicControls and
    //  basicStyler to null in OptListener (not sure this is sufficient 
    //  for the case of StyleView being closed, but AdvancedDialog still 
    //  open.  Also not sure if optListener will still be valid).
    public void close()
    {
        optListener.setBasicController(null);
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


class StyleTableLabelProvider extends LabelProvider
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
