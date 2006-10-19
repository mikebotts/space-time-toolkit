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

import org.vast.ows.sld.ScalarParameter;


/**
 * <p><b>Title:</b>
 * Show URL
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Used to open a webpage as a popup or in a separate window.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Oct 19, 2006
 * @version 1.0
 */
public class ShowURL extends UserAction
{
    public enum WindowType
    {
        VIEW, POPUP, BROWSER
    }
    
    protected ScalarParameter title;
    protected ScalarParameter url;
    protected WindowType windowType;


    public ScalarParameter getUrl()
    {
        return url;
    }


    public void setUrl(ScalarParameter url)
    {
        this.url = url;
    }


    public ScalarParameter getTitle()
    {
        return title;
    }


    public void setTitle(ScalarParameter title)
    {
        this.title = title;
    }


    public WindowType getWindowType()
    {
        return windowType;
    }


    public void setWindowType(WindowType windowType)
    {
        this.windowType = windowType;
    }
}
