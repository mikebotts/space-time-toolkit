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
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Jul 8, 2006
 * @version 1.0
 */
public enum EventType
{
    TIME_EXTENT_CHANGED,
    SPATIAL_EXTENT_CHANGED,
    
    ITEM_SYMBOLIZER_CHANGED,
    ITEM_OPTIONS_CHANGED,
    ITEM_VISIBILITY_CHANGED,
    
    SCENE_OPTIONS_CHANGED,
    SCENE_TREE_CHANGED,
    SCENE_VIEW_CHANGED,
    SCENE_PROJECTION_CHANGED,
    SCENE_ITEM_CHANGED,
    
    PROVIDER_OPTIONS_CHANGED,    
    PROVIDER_DATA_CHANGED,
    PROVIDER_DATA_CLEARED,
    PROVIDER_DATA_ADDED,
    PROVIDER_DATA_REMOVED,
    PROVIDER_ERROR
}
