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

package org.vast.stt.project;

import java.util.ArrayList;
import org.vast.stt.style.StylerVisitor;


/**
 * <p><b>Title:</b><br/>
 * Data Styler List
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Contains the list of styler hold by a DataItem.
 * This class provide helper methods to loop through stylers in the list.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 08, 2006
 * @version 1.0
 */
public class DataStylerList extends ArrayList<DataStyler>
{
    private final static long serialVersionUID = 0;


    public DataStylerList()
    {
        super(1);
    }


    public DataStylerList(int numStylers)
    {
        super(numStylers);
    }


    public void accept(StylerVisitor visitor)
    {
        // loop through all child stylers
        DataStyler stylerTmp;
        for (int i = 0; i < this.size(); i++)
        {
            stylerTmp = this.get(i);
            if ((stylerTmp != null) && (stylerTmp.getSymbolizer().isEnabled()))
                stylerTmp.accept(visitor);
        }
    }
}
