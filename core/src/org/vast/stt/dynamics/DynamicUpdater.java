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

package org.vast.stt.dynamics;


/**
 * <p><b>Title:</b>
 * Dynamic Updater
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO DynamicUpdater type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 6, 2006
 * @version 1.0
 */
public abstract class DynamicUpdater
{
    protected boolean enabled;

    
    public boolean isEnabled()
    {
        return enabled;
    }

    
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
