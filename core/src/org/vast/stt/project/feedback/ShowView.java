/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the
 University of Alabama in Huntsville (UAH).
 Portions created by the Initial Developer are Copyright (C) 2006
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
 Alexandre Robin <robin@nsstc.uah.edu>
 
 ******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.project.feedback;

import org.vast.ows.sld.Color;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;


/**
 * <p><b>Title:</b>
 * Show View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Used to open a separete view of rendered data as a result
 * to a user action.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Oct 19, 2006
 * @version 1.0
 */
public class ShowView extends UserAction
{
    protected ScalarParameter title;
    protected Color backgroundColor;
    protected boolean detached;
    protected boolean autoUpdate; // if true, popup content will be updated when data changes
    protected Symbolizer content; // DataTable, DataChart, Label, Raster and Video supported


    public boolean isAutoUpdate()
    {
        return autoUpdate;
    }


    public void setAutoUpdate(boolean autoUpdate)
    {
        this.autoUpdate = autoUpdate;
    }


    public Color getBackgroundColor()
    {
        return backgroundColor;
    }


    public void setBackgroundColor(Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }


    public Symbolizer getContent()
    {
        return content;
    }


    public void setContent(Symbolizer style)
    {
        this.content = style;
    }


    public ScalarParameter getTitle()
    {
        return title;
    }


    public void setTitle(ScalarParameter title)
    {
        this.title = title;
    }
}
