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

package org.vast.stt.project.table;

import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.style.DataStyler;


/**
 * <p><b>Title:</b>
 * Table Scene Item
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Table entry carrying a DataItem and its stylers
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 12, 2006
 * @version 1.0
 */
public class TableSceneItem extends SceneItem<TableScene>
{
    
    
    public TableSceneItem(TableScene scene)
    {
        super(scene);
    }    
        
    
    protected void prepareStyler(DataStyler styler)
    {        
    }
}
