
package org.vast.stt.gui.widgets;

import org.eclipse.swt.events.SelectionListener;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.project.tree.DataItem;


/**
 * <p><b>Title:</b>
 * Option Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Base abstract class for all Option Controllers
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook, Alexandre Robin
 * @date Jul 12, 2006
 * @version 1.0
 */
public abstract class OptionController implements SelectionListener
{
    protected OptionControl[] optionControls;
    protected Symbolizer symbolizer;
    protected DataItem dataItem;

    abstract public void loadFields();
    
	//  TODO  REMOVE THIS METHOD
    public OptionControl[] getControls()
    {
        return optionControls;
    }

    public DataItem getDataItem()
    {
        return dataItem;
    }
    
    public void setDataItem(DataItem dataItem)
    {
        this.dataItem = dataItem;
    }
    
    
    public Symbolizer getSymbolizer()
    {
        return symbolizer;
    }


    public void addSelectionListener(SelectionListener sl)
    {
        for (int i = 0; i < optionControls.length; i++)
            optionControls[i].addSelectionListener(sl);
    }

    public void removeSelectionListener(SelectionListener sl)
    {
        for (int i = 0; i < optionControls.length; i++)
        {
            optionControls[i].removeSelectionListener(sl);
        }
    }
}
