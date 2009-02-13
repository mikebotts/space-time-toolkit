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

import java.awt.Frame;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.vast.math.Vector3d;
import org.vast.stt.project.chart.ChartScene;
import org.vast.stt.project.chart.ChartSceneRenderer;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.world.ViewSettings;
import org.vast.stt.renderer.PickFilter;
import org.vast.stt.renderer.PickedObject;
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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 16, 2006
 * @version 1.0
 */
public class JFreeChartRenderer extends ChartSceneRenderer implements PaintListener
{
    protected Composite swt_awt;
    protected ChartPanel chartPanel;
    protected XYPlotBuilder plotBuilder = new XYPlotBuilder();
    //protected Image chartImg;
    //protected PaletteData palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
    
    
    public JFreeChartRenderer()
    {
    }
    
    
    @Override
    public Composite getCanvas()
    {
        return swt_awt;
    }


    @Override
    public void init()
    {
        swt_awt = new Composite(composite, SWT.EMBEDDED | SWT.DOUBLE_BUFFERED);
        Frame rootFrame = SWT_AWT.new_Frame(swt_awt);
        swt_awt.setBounds(0, 0, composite.getClientArea().width, composite.getClientArea().height);
        chartPanel = new ChartPanel(null);
        chartPanel.setDoubleBuffered(true);
        //chartPanel.setPopupMenu(null);
        rootFrame.add(chartPanel);
        //composite.addPaintListener(this);
    }


    @Override
    public void drawScene(ChartScene sc)
    {
        if (composite.isDisposed())
            return;
        
        // initialize plot builder
        plotBuilder.createPlot(sc);
        
        // draw all items
        ChartScene scene = (ChartScene)sc;
        List<SceneItem> sceneItems = scene.getSceneItems();
        for (int i = 0; i < sceneItems.size(); i++)
        {
            SceneItem nextItem = sceneItems.get(i);

            if (!nextItem.isVisible())
                continue;

            if (!nextItem.getDataItem().isEnabled())
                continue;

            drawItem(nextItem);
        }
        
        // create a new chart and assign to panel
        if (plotBuilder.isReady())
        {
            JFreeChart chart = new JFreeChart(plotBuilder.getPlot());
            chart.setTitle(scene.getName());
            chartPanel.setChart(chart);            
        }
        else
        {
            chartPanel.setChart(null);
        }
        //chart.draw((Graphics2D)img1.getGraphics(), img1.getGraphics().getClipBounds());
        //BufferedImage img = chart.createBufferedImage(800, 600);
        //byte[] data = ((DataBufferByte)img1.getData().getDataBuffer()).getData();
        //ImageData imgData = new ImageData(img1.getWidth(), img1.getHeight(), img1.getColorModel().getPixelSize(), palette);
        //imgData.setPixels(0, 0, img1.getWidth(), data, 0);
        //chartImg = new Image(composite.getDisplay(), imgData);
    }

    
    protected void drawItem(SceneItem item)
    {
        // build plot using plotBuilder
        plotBuilder.addAxisToPlot(item.getDataItem());
        List<DataStyler> stylers = item.getStylers();
        
        for (int i = 0; i < stylers.size(); i++)
        {
            DataStyler nextStyler = stylers.get(i);
            if (nextStyler.getSymbolizer().isEnabled())
                plotBuilder.addDataToPlot(nextStyler, item.getDataItem());
        }
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
    public PickedObject pick(ChartScene scene, PickFilter filter)
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


    public void paintControl(PaintEvent e)
    {
        //if (chartImg != null)
        //    e.gc.drawImage(chartImg, 0, 0);        
    }
}
