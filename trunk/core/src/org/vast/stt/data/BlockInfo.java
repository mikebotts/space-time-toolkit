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

package org.vast.stt.data;

import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.STTTimeExtent;


/**
 * <p><b>Title:</b><br/>
 * Block Info
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Additional (optional) info for a BlockListItem.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Apr 1, 2006
 * @version 1.0
 */
public class BlockInfo
{
    protected STTSpatialExtent spatialExtent;
    protected STTTimeExtent timeExtent;
    //protected double geometryStepAverage;
    //protected double geometryStepVariance;
    
    
    public STTSpatialExtent getSpatialExtent()
    {
        if (spatialExtent == null)
            spatialExtent = new STTSpatialExtent();
        
        return spatialExtent;
    }


    public void setSpatialExtent(STTSpatialExtent spatialExtent)
    {
        this.spatialExtent = spatialExtent;
    }


    public STTTimeExtent getTimeExtent()
    {
        if (timeExtent == null)
            timeExtent = new STTTimeExtent();
        
        return timeExtent;
    }


    public void setTimeExtent(STTTimeExtent timeExtent)
    {
        this.timeExtent = timeExtent;
    }
}
