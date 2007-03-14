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

package org.vast.stt.project.table;

import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.vast.math.Vector3d;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.world.ViewSettings;
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
import org.vast.stt.style.StylerVisitor;
import org.vast.stt.style.TextureStyler;


/**
 * <p><b>Title:</b>
 * Table Renderer
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Table renderer using SWT controls
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Dec 05, 2006
 * @version 1.0
 */
public class TableRenderer extends SceneRenderer<TableScene> implements StylerVisitor
{
    private ScrolledComposite mainSC;
    
    
    public TableRenderer()
    {
    }
    
    
    @Override
    public Composite getCanvas()
    {
        return mainSC;
    }


    @Override
    public void init()
    {
        composite.setLayout(new FillLayout());
        mainSC = new ScrolledComposite(composite, SWT.H_SCROLL | SWT.V_SCROLL);
        mainSC.setExpandVertical(true);
        mainSC.setExpandHorizontal(true);
    }


    @Override
    public void drawScene(TableScene sc)
    {
        if (composite.isDisposed())
            return;
        
        // draw all items
        TableScene scene = (TableScene)sc;
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
    }
    
    
    protected void drawItem(SceneItem item)
    {
        if (item.getStylers().isEmpty())
            return;
        
        // render only using the first symbolizer
        DataStyler firstStyler = item.getStylers().get(0);
        if (firstStyler.getSymbolizer().isEnabled())
            firstStyler.accept(this);
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
    public PickedObject pick(TableScene scene, PickFilter filter)
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


    public void visit(PointStyler styler)
    {
        // TODO Auto-generated method stub
        
    }


    public void visit(LineStyler styler)
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
