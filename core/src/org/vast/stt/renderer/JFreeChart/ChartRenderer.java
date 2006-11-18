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

import java.awt.Frame;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.vast.math.Vector3d;
import org.vast.stt.project.chart.ChartScene;
import org.vast.stt.project.chart.ChartSceneItem;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.world.ViewSettings;
import org.vast.stt.renderer.PickFilter;
import org.vast.stt.renderer.PickedObject;
import org.vast.stt.renderer.SceneRenderer;
import org.vast.stt.style.DataStyler;


/**
 * <p><b>Title:</b>
 * Chart Renderer
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Chart renderer using the JFreeChart library
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 16, 2006
 * @version 1.0
 */
public class ChartRenderer extends SceneRenderer<Scene<ChartSceneItem>>
{
    protected ChartPanel chartPanel;
    protected PlotBuilder plotBuilder = new PlotBuilder();
    
    
    public ChartRenderer()
    {
    }


    @Override
    public void init()
    {
        composite.setLayout(new FillLayout());
        Composite swt_awt = new Composite(composite, SWT.EMBEDDED);
        Frame rootFrame = SWT_AWT.new_Frame(swt_awt);
        swt_awt.setBounds(0, 0, composite.getClientArea().width, composite.getClientArea().height);
        chartPanel = new ChartPanel(null);
        //chartPanel.setPopupMenu(null);
        rootFrame.add(chartPanel);
    }


    @Override
    public void drawScene(Scene<ChartSceneItem> sc)
    {
        plotBuilder.reset();
        Plot plot = null;
        
        if (composite.isDisposed())
            return;
        
        // draw all items
        ChartScene scene = (ChartScene)sc;
        List<ChartSceneItem> sceneItems = scene.getSceneItems();
        for (int i = 0; i < sceneItems.size(); i++)
        {
            SceneItem<?> nextItem = sceneItems.get(i);

            if (!nextItem.isVisible())
                continue;

            if (!nextItem.getDataItem().isEnabled())
                continue;

            plot = drawChartItem(nextItem, plot);
        }
        
        if (plot != null)
        {
            // create a new chart
            JFreeChart chart = new JFreeChart(plot);
            chart.setTitle(scene.getName());
            
            // assign to panel
            chartPanel.setChart(chart);
            chartPanel.repaint();
        }
    }


    @Override
    public void drawItem(SceneItem<?> item)
    {
        plotBuilder.reset();
        Plot plot = drawChartItem(item, null);
        
        // create a new chart
        JFreeChart chart = new JFreeChart(plot);
        chart.setTitle(item.getName());
        
        // assign to panel
        chartPanel.setChart(chart);
        chartPanel.repaint();
    }
    
    
    protected Plot drawChartItem(SceneItem<?> item, Plot plot)
    {
        // build plot using plotBuilder
        List<DataStyler> stylers = item.getStylers();
        for (int i = 0; i < stylers.size(); i++)
        {
            DataStyler nextStyler = stylers.get(i);
            plot = plotBuilder.addToPlot(plot, nextStyler);
        }        
        
        return plot;
    }


    @Override
    public void cleanup(DataStyler styler, CleanupSection section)
    {
    }


    @Override
    public void cleanup(DataStyler styler, Object[] objects, CleanupSection section)
    {
    }


    @Override
    public void project(double worldX, double worldY, double worldZ, Vector3d viewPos)
    {
    }


    @Override
    public void unproject(double viewX, double viewY, double viewZ, Vector3d worldPos)
    {
    }


    @Override
    public PickedObject pick(Scene<ChartSceneItem> scene, PickFilter filter)
    {
        return null;
    }


    @Override
    public void setupView(ViewSettings viewSettings)
    {
    }


    @Override
    public void dispose()
    {
    }
}
