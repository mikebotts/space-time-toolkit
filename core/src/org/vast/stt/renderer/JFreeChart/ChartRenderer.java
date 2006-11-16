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
import java.util.ArrayList;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.vast.math.Vector3d;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.world.ViewSettings;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.renderer.PickFilter;
import org.vast.stt.renderer.PickedObject;
import org.vast.stt.renderer.SceneRenderer;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.GridBorderStyler;
import org.vast.stt.style.GridFillStyler;
import org.vast.stt.style.GridMeshStyler;
import org.vast.stt.style.LabelStyler;
import org.vast.stt.style.LineStyler;
import org.vast.stt.style.PointStyler;
import org.vast.stt.style.PolygonStyler;
import org.vast.stt.style.RasterStyler;
import org.vast.stt.style.TextureStyler;


public class ChartRenderer extends SceneRenderer
{
    protected Frame rootFrame;
    protected JFreeChart chart;
    protected ArrayList<DataStyler> stylers;
    
    
    public ChartRenderer(Composite parent)
    {
        rootFrame = SWT_AWT.new_Frame(parent);
    }
    
    
    public void visit(LineStyler styler)
    {
        
    }


    public void handleEvent(STTEvent e)
    {
        switch (e.type)
        {
            case ITEM_OPTIONS_CHANGED:
            case ITEM_SYMBOLIZER_CHANGED:
                
        }        
    }


    @Override
    public void init()
    {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void drawScene(WorldScene scene)
    {
        //chart = ChartFactory.createXYLineChart();
        
        // visit all stylers
        for (int i = 0; i < stylers.size(); i++)
        {
            DataStyler nextStyler = stylers.get(i);
            if (nextStyler.getSymbolizer().isEnabled())
                nextStyler.accept(null);
        }
        
        rootFrame.add(new ChartPanel(chart, false, false, false, true, true));        
    }


    @Override
    public void drawItem(SceneItem item)
    {
        // TODO Auto-generated method stub        
    }


    @Override
    public void cleanup(DataStyler styler, CleanupSection section)
    {
        // TODO Auto-generated method stub        
    }


    @Override
    public void cleanup(DataStyler styler, Object[] objects, CleanupSection section)
    {
        // TODO Auto-generated method stub        
    }


    @Override
    public void project(double worldX, double worldY, double worldZ, Vector3d viewPos)
    {
        // TODO Auto-generated method stub        
    }


    @Override
    public void unproject(double viewX, double viewY, double viewZ, Vector3d worldPos)
    {
        // TODO Auto-generated method stub        
    }


    @Override
    public PickedObject pick(WorldScene scene, PickFilter filter)
    {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void resizeView(int width, int height)
    {
        // TODO Auto-generated method stub        
    }


    @Override
    public void setupView(ViewSettings viewSettings)
    {
        // TODO Auto-generated method stub        
    }


    @Override
    public void dispose()
    {
        // TODO Auto-generated method stub        
    }


    public void visit(PointStyler styler)
    {
        // TODO Auto-generated method stub        
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
