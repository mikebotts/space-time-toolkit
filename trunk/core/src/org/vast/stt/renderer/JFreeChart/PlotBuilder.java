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

package org.vast.stt.renderer.JFreeChart;

import java.awt.Color;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.GridBorderStyler;
import org.vast.stt.style.GridFillStyler;
import org.vast.stt.style.GridMeshStyler;
import org.vast.stt.style.LabelStyler;
import org.vast.stt.style.LineStyler;
import org.vast.stt.style.PointStyler;
import org.vast.stt.style.PolygonStyler;
import org.vast.stt.style.RasterStyler;
import org.vast.stt.style.StylerVisitor;
import org.vast.stt.style.TextureStyler;


public class PlotBuilder implements StylerVisitor
{
    protected Plot plot;
    protected int datasetCount = 0;
    
    
    public Plot addToPlot(Plot plot, DataStyler styler)
    {
        this.plot = plot;
        styler.accept(this);
        datasetCount++;
        return this.plot;
    }
    
    
    public void reset()
    {
        datasetCount = 0;
    }
    
    
    public void visit(PointStyler styler)
    {
        // TODO Auto-generated method stub

    }


    public void visit(LineStyler styler)
    {
        if (plot == null)
        {
            String axisName;
            XYPlot plot = new XYPlot();
            
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);
            
            axisName = "Time (s)";
            NumberAxis domainAxis = new NumberAxis(axisName);
            domainAxis.setAutoRangeIncludesZero(false);
            plot.setDomainAxis(domainAxis);
            
            this.plot = plot;
        }

        if (plot instanceof XYPlot)
        {
            // create dataset and renderer
            XYPlot plot = (XYPlot)this.plot;
            XYDataset data = new ChartXYDataset(styler);
            XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
            
            // curve color
            org.vast.ows.sld.Color color = styler.getSymbolizer().getStroke().getColor();
            float red = color.getRedValue();
            float green = color.getGreenValue();
            float blue = color.getBlueValue();
            float alpha = color.getAlphaValue();
            renderer.setPaint(new Color(red, green, blue, alpha));
            
            // add new dataset and corresponding renderer and range axis
            plot.setRenderer(datasetCount, renderer);
            plot.setDataset(datasetCount, data);
            String axisName = styler.getSymbolizer().getName();
            NumberAxis rangeAxis = new NumberAxis(axisName);
            rangeAxis.setAutoRangeIncludesZero(false);
            plot.setRangeAxis(datasetCount, rangeAxis);
        }
    }


    public void visit(PolygonStyler styler)
    {
        // TODO Auto-generated method stub
    }


    public void visit(LabelStyler styler)
    {
        // TODO Auto-generated method stub

    }


    public void visit(GridMeshStyler styler)
    {
        // TODO Auto-generated method stub

    }


    public void visit(GridFillStyler styler)
    {
        // TODO Auto-generated method stub

    }


    public void visit(GridBorderStyler styler)
    {
        // TODO Auto-generated method stub

    }


    public void visit(RasterStyler styler)
    {
        // TODO Auto-generated method stub

    }


    public void visit(TextureStyler styler)
    {
        // TODO Auto-generated method stub

    }

}
