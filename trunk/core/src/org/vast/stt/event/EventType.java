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

package org.vast.stt.event;


/**
 * <p><b>Title:</b>
 * Event Type
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Enumeration of event types.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 8, 2006
 * @version 1.0
 */
public enum EventType
{
    ITEM_SYMBOLIZER_CHANGED,
    ITEM_OPTIONS_CHANGED,
    ITEM_VISIBILITY_CHANGED,
    
    SCENE_OPTIONS_CHANGED,
    SCENE_TREE_CHANGED,
    SCENE_TIME_CHANGED,
    SCENE_VIEW_CHANGED,
    SCENE_PROJECTION_CHANGED,
    SCENE_ITEM_CHANGED,
    
    PROVIDER_OPTIONS_CHANGED,
    PROVIDER_TIME_EXTENT_CHANGED,
    PROVIDER_SPATIAL_EXTENT_CHANGED,
    PROVIDER_DATA_CHANGED,
    PROVIDER_DATA_CLEARED,
    PROVIDER_DATA_ADDED,
    PROVIDER_DATA_REMOVED,
    PROVIDER_ERROR
}
