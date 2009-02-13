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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.Hashtable;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.vast.stt.project.chart.ChartScene;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.GridBorderStyler;
import org.vast.stt.style.GridFillStyler;
import org.vast.stt.style.GridMeshStyler;
import org.vast.stt.style.LabelStyler;
import org.vast.stt.style.LinePointGraphic;
import org.vast.stt.style.LineStyler;
import org.vast.stt.style.PointGraphic;
import org.vast.stt.style.PointStyler;
import org.vast.stt.style.PolygonStyler;
import org.vast.stt.style.RasterStyler;
import org.vast.stt.style.StylerVisitor;
import org.vast.stt.style.TextureStyler;
import org.vast.stt.style.VectorStyler;


/**
 * <p><b>Title:</b>
 * Plot Builder
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This class builds a JFree Plot based on info defined
 * in the sld Symbolizers.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 21, 2006
 * @version 1.0
 */
public class XYPlotBuilder implements StylerVisitor
{
	protected ChartScene scene; 
    protected XYPlot plot;
    protected int datasetCount = 0;
    protected int rangeAxisCount = 0;
    protected Hashtable<DataItem, Integer> axisTable;
    protected DataItem currentItem;
    
    public XYPlotBuilder()
    {
        axisTable = new Hashtable<DataItem, Integer>();
    }
    
    
    public void addDataToPlot(DataStyler styler, DataItem item)
    {
        currentItem = item;
        styler.accept(this);        
    }
    
    
    public void addAxisToPlot(DataItem item)
    {
        String axisName = item.getName();
        NumberAxis rangeAxis = new NumberAxis(axisName);
        rangeAxis.setAutoRangeIncludesZero(false);
        rangeAxis.setAutoRange(false);
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        rangeAxis.setNumberFormatOverride(format);
        rangeAxis.setLowerBound(scene.getRangeMin());
        rangeAxis.setUpperBound(scene.getRangeMax());
        plot.setRangeAxis(rangeAxisCount, rangeAxis);
        axisTable.put(item, rangeAxisCount);
        rangeAxisCount++;
    }
    
    
    public void createPlot(ChartScene scene)
    {
    	this.scene = scene; //  scene contains the axis info, so passing it in here for now
    	
    	datasetCount = 0;
        rangeAxisCount = 0;
        currentItem = null;
        axisTable.clear();
        plot = new XYPlot();
        
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        String axisName = "Time (s)";
        NumberAxis domainAxis = new NumberAxis(axisName);
        domainAxis.setAutoRangeIncludesZero(false);
        domainAxis.setAutoRange(false);
        domainAxis.setNumberFormatOverride(new DateFormat());
        domainAxis.setTickLabelInsets(new RectangleInsets(0, 30, 0, 30));
        domainAxis.setLowerBound(scene.getDomainMin());
        domainAxis.setUpperBound(scene.getDomainMax());
        plot.setDomainAxis(datasetCount, domainAxis);
    }
    
    
    protected void addDataSet(XYDataset dataset, XYItemRenderer renderer)
    {
        //XYPlot plot = (XYPlot)this.plot;
        plot.setRenderer(datasetCount, renderer);
        plot.setDataset(datasetCount, dataset);        
        int rangeAxisID = axisTable.get(currentItem);
        plot.mapDatasetToRangeAxis(datasetCount, rangeAxisID);
//    	System.err.println("Range: " + plot.getRangeAxis().getRange().getLength());
//    	System.err.println("Domain: " + plot.getDomainAxis().getRange().getLength());
        datasetCount++;
    }
    
    
    public void visit(PointStyler styler)
    {
        styler.resetIterators();
        if (styler.getNumPoints() == 0)
            return;
        
        // create dataset and renderer            
        XYDataset dataset = new ChartXYDataset(currentItem.getName(), styler);
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES);
        
        // point color and size
        PointGraphic point = styler.getPoint(0);
        renderer.setPaint(new Color(point.r, point.g, point.b, point.a));
        
        // point shape
        Shape shape = null;            
        switch (point.shape)
        {
            case SQUARE:
                shape = new Rectangle2D.Float(-point.size/2, -point.size/2, point.size, point.size);
                break;

            case CIRCLE:
                shape = new Ellipse2D.Float(-point.size/2, -point.size/2, point.size, point.size);
                break;
                
            case TRIANGLE:
                int[] xPoints = new int[] {-(int)point.size/2, 0, (int)point.size/2};
                int[] yPoints = new int[] {(int)point.size/2, -(int)point.size/2, (int)point.size/2};
                shape = new Polygon(xPoints, yPoints, 3);
                break;
        }            
        renderer.setShape(shape);
        
        // add new dataset and corresponding renderer and range axis
        addDataSet(dataset, renderer);
    }


    public void visit(LineStyler styler)
    {
        styler.resetIterators();        
        if (styler.getNumPoints() == 0)
            return;
        
        // create dataset and renderer
        XYDataset dataset = new ChartXYDataset(currentItem.getName(), styler);
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
        
        // curve color and width
        LinePointGraphic point = styler.getPoint(0);
        renderer.setPaint(new Color(point.r, point.g, point.b, point.a));
        renderer.setStroke(new BasicStroke(point.width));
        
        // add new dataset and corresponding renderer and range axis
        addDataSet(dataset, renderer);
    }


    public void visit(PolygonStyler styler)
    {
        // TODO Auto-generated method stub
    }
    
    
    public void visit(VectorStyler styler)
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


    public Plot getPlot()
    {
        return plot;
    }
    
    
    public boolean isReady()
    {
        return (plot.getDataset(0) != null);
    }
}
