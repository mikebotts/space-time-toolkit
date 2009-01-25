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

package org.vast.stt.renderer.JFreeChart;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;
import org.vast.stt.style.DataStyler1D;
import org.vast.stt.style.PrimitiveGraphic;


public class ChartXYDataset implements XYDataset
{
    protected DataStyler1D styler;
    protected int previousItem = -1;
    protected PrimitiveGraphic point;
    protected String name;
    

    public ChartXYDataset(String name, DataStyler1D styler)
    {
        this.name = name;
        this.styler = styler;
    }
    
    
    public DomainOrder getDomainOrder()
    {
        return DomainOrder.ASCENDING;
    }


    public int getItemCount(int arg0)
    {
        return styler.getNumPoints();
    }


    public Number getX(int series, int item)
    {
        return new Double(getXValue(series, item));
    }


    public double getXValue(int series, int item)
    {
        if (item == previousItem)
        {
            return point.x;
        }
        else
        {
            previousItem = item;
            point = styler.getPoint(item);
            return point.x;
        }
    }


    public Number getY(int series, int item)
    {
        return new Double(getYValue(series, item));
    }


    public double getYValue(int series, int item)
    {
        if (item == previousItem)
        {
            return point.y;
        }
        else
        {
            previousItem = item;
            point = styler.getPoint(item);
            return point.y;
        }
    }


    public int getSeriesCount()
    {
        return 1;
    }


    public Comparable<?> getSeriesKey(int series)
    {
        return name;
    }


    public int indexOf(Comparable key)
    {
        return 0;
    }


    public void addChangeListener(DatasetChangeListener arg0)
    {
       
    }


    public void removeChangeListener(DatasetChangeListener arg0)
    {
       
    }


    public DatasetGroup getGroup()
    {
        return null;
    }


    public void setGroup(DatasetGroup arg0)
    {       
    }
}
