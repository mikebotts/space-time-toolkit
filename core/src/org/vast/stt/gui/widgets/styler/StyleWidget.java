
package org.vast.stt.gui.widgets.styler;

import java.util.Iterator;
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
import org.vast.stt.gui.widgets.CheckOptionTable;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.project.DataItem;
import org.vast.stt.project.DataStyler;
import org.vast.stt.project.DataStylerList;
import org.vast.stt.style.StylerFactory;
import org.vast.stt.style.StylerFactory.StylerType;


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
public class StyleWidget extends CheckOptionTable
{
    DataStyler activeStyler;
    OptionListener optListener;


    public StyleWidget(Composite parent)
    {
        checkboxTableLabel = "Styles:";
        init(parent);
        setCheckboxTableContentProvider(new StyleTableContentProvider());
        setCheckboxTableLabelProvider(new StyleTableLabelProvider());
    }


    public OptionChooser createOptionChooser(Composite parent)
    {
        optListener = new OptionListener();
        StyleOptionChooser basicOptionChooser = new StyleOptionChooser(parent, optListener);
        return basicOptionChooser;
    }


    public void setDataItem(DataItem item)
    {
        super.setDataItem(item);
        setStyler(item.getStylerList());
    }


    /**
     * Make this DataStyler the currently active Styler in the StyleWidget
     * @param newStyler
     */
    private void setStyler(DataStylerList stylers)
    {
        //  Change cbTableViewer contents
        checkboxTableViewer.setInput(stylers);
        Iterator it = stylers.iterator();
        DataStyler stylerTmp;
        //  Set init state of checkboxes
        while (it.hasNext())
        {
            stylerTmp = (DataStyler) it.next();
            checkboxTableViewer.setChecked(stylerTmp, stylerTmp.isEnabled());
        }
        checkboxTableViewer.getTable().setSelection(0);
        ISelection selection = checkboxTableViewer.getSelection();
        checkboxTableViewer.setSelection(selection);
    }


    void addStyle(DataStyler styler)
    {
        //  Add Checkbox to stylers Set and rerender Table
        dataItem.getStylerList().add(styler);
        activeStyler = null;
        //  Change options panel to show Point options
        checkboxTableViewer.setInput(dataItem.getStylerList());
    }


    /**
     * create a new styler and call addStyle() with it
     */
    public void createNewStyler(String stylerName, StylerType stylerType)
    {
        DataStyler newStyler = StylerFactory.createDefaultStyler(stylerName, stylerType, dataItem.getDataProvider());
        if (newStyler != null)
        {
            //  Hack to set geom
            newStyler.getSymbolizer().setGeometry(activeStyler.getSymbolizer().getGeometry());
            newStyler.setName(stylerName);
            newStyler.updateDataMappings();
            newStyler.setEnabled(false);
            addStyle(newStyler);
        }

    }


    private void removeStyle(DataStyler styler)
    {
        dataItem.getStylerList().remove(styler);
        //  reset activeStyler\
        // ...
        //TableItem [] items = checkboxTableViewer.getTable().getItems();
        checkboxTableViewer.setInput(dataItem.getStylerList());
    }


    //  enabling checkbox causes ckState AND selChanged events
    public void checkStateChanged(CheckStateChangedEvent e)
    {
        // TODO Auto-generated method stub
        //  e.getElement returns checked Styler
        DataStyler styler = (DataStyler) e.getElement();
        styler.setEnabled(e.getChecked());
    }


    //  Selecting label causes ONLY selChanged event
    public void selectionChanged(SelectionChangedEvent e)
    {
        System.err.println("sel source is" + e.getSource());
        StructuredSelection selection = (StructuredSelection) e.getSelection();
        DataStyler styler = (DataStyler) selection.getFirstElement();
        //  Check for empty selection (happens when buildControls() is called)
        if (styler == null)
        {
            Iterator it = dataItem.getStylerList().iterator();
            if (!it.hasNext())
            {
                //  stylerSet is currently empty
                optionChooser.removeOldControls();
                return;
            }
            //  Reset selected to first in Table
            checkboxTableViewer.getTable().setSelection(0);
            selection = (StructuredSelection) checkboxTableViewer.getSelection();
            styler = (DataStyler) selection.getFirstElement();
        }
        //  Check to see if selected Styler has really changed
        if (styler == activeStyler)
        {
            System.err.println("Selection not really changed");
            return;
        }
        System.err.println("Selection CHANGED");
        activeStyler = styler;
        optionChooser.buildControls(styler);
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
            if (activeStyler != null)
                removeStyle(activeStyler); //  remove currently selected row   
        }
        else if (control == enabledButton)
        {
            if (dataItem != null)
                dataItem.setEnabled(enabledButton.getSelection());
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
            new AdvancedStyleDialog(dataItem, activeStyler, optListener);
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
        AddStylerDialog asd = new AddStylerDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
        int rc = asd.getReturnCode();
        if (rc == IDialogConstants.OK_ID)
        {
            createNewStyler(asd.getStylerName(), asd.getStylerType());
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
        DataStylerList stylers = (DataStylerList)inputElement;
        DataStyler[] stylerArr = stylers.toArray(new DataStyler[] {});
        return stylerArr;
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
        DataStyler styler = (DataStyler) element;
        return styler.getName();
    }
}
