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

package org.vast.stt.scene;

import org.vast.util.DateTime;


/**
 * <p><b>Title:</b><br/>
 * Scene Time Settings
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Contains information about a scene time settings
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 9, 2005
 * @version 1.0
 */
public class TimeSettings
{
	protected DateTime currentTime;
	protected double timeStep;
	protected boolean realTime;
}
