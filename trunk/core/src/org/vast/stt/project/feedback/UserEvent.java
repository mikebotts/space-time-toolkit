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

import org.vast.stt.project.tree.DataItem;

public class UserEvent
{
    public enum EventType
    {
        HOVER, LEFT_CLICK, RIGHT_CLICK, DBL_CLICK
    }

    protected EventType type;
    protected int cursorX, cursorY;
    protected DataItem sourceItem;
    protected int[] dataIndices; // block#, array1#, array2#


    public int getCursorX()
    {
        return cursorX;
    }


    public void setCursorX(int cursorX)
    {
        this.cursorX = cursorX;
    }


    public int getCursorY()
    {
        return cursorY;
    }


    public void setCursorY(int cursorY)
    {
        this.cursorY = cursorY;
    }


    public int[] getDataIndices()
    {
        return dataIndices;
    }


    public void setDataIndices(int[] dataIndices)
    {
        this.dataIndices = dataIndices;
    }


    public EventType getType()
    {
        return type;
    }


    public void setType(EventType eventType)
    {
        this.type = eventType;
    }


    public DataItem getSourceItem()
    {
        return sourceItem;
    }


    public void setSourceItem(DataItem sourceItem)
    {
        this.sourceItem = sourceItem;
    }

}
