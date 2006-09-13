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

import org.vast.stt.project.scene.ViewSettings;


/**
 * <p><b>Title:</b>
 * View Settings Updater
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO ViewSettingsUpdater type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 21, 2006
 * @version 1.0
 */
public abstract class ViewSettingsUpdater extends DynamicUpdater
{
    protected ViewSettings viewSettings;

    
    public void setViewSettings(ViewSettings viewSettings)
    {
        this.viewSettings = viewSettings;
    }
}
