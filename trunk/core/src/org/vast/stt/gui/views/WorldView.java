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
    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.views;

import java.text.NumberFormat;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.vast.math.Vector3d;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.world.Projection;
import org.vast.stt.project.world.ViewSettings;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.project.world.WorldSceneRenderer;
import org.vast.stt.project.world.Projection.Crs;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.STTPolygonExtent;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.renderer.opengl.JOGLRenderer;


/**
 * <p><b>Title:</b>
 * World View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * The world view is the 2D/3D view where the data items of
 * the scene are rendered using the provided renderer.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Jul 10, 2006
 * @version 1.0
 */
public class WorldView extends SceneView<WorldScene> implements PaintListener, ControlListener, MouseMoveListener
{
	public static final String ID = "STT.WorldView";
    private Composite parent;
    private WorldViewController controller;
    private LatLonStatusLine llStatus;

   
	public WorldView()
    {
        controller = new WorldViewController();
    }
			

    @Override
	public void createPartControl(Composite parent)
	{
        this.parent = parent;
        parent.setLayout(new FillLayout());
        getSite().getPage().addPartListener(this);
	}
    	
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
        ImageDescriptor descriptor;
        
        // add select polygon action to toolbar
		IAction selectPolygonAction = new Action("Select Polygon", IAction.AS_CHECK_BOX)
		{
			public void run()
			{
				if (!scene.getSelectedItems().isEmpty())
				{
					if (this.isChecked())
					{
						SceneItem selectedItem = scene.getSelectedItems().get(0);
						DataProvider prov = selectedItem.getDataItem().getDataProvider();
						STTSpatialExtent extent = prov.getSpatialExtent();
						if (extent instanceof STTPolygonExtent)
						{
							((STTPolygonExtent)extent).getPointList().clear();
							controller.setPointSelectionMode(true);
							updateView();
						}
					}
					else
					{
						controller.setPointSelectionMode(false);
					}
				}
			}
		};
        descriptor = STTPlugin.getImageDescriptor("icons/select_point.gif");
        selectPolygonAction.setImageDescriptor(descriptor);
        site.getActionBars().getToolBarManager().add(selectPolygonAction);
        
        // add select object action to toolbar
        IAction selectObjectAction = new Action("Select Object", IAction.AS_CHECK_BOX)
        {
            public void run()
            {
                if (this.isChecked())
                    controller.setObjectSelectionMode(true);
                else
                    controller.setObjectSelectionMode(false);
            }
        };
        descriptor = STTPlugin.getImageDescriptor("icons/select_object.gif");
        selectObjectAction.setImageDescriptor(descriptor);
        site.getActionBars().getToolBarManager().add(selectObjectAction);
        
        // add show target action to toolbar
		IAction showTargetAction = new Action("Toggle Target Tripod")
		{
			public void run()
			{
				boolean targetShown = scene.getViewSettings().isShowCameraTarget();
				this.setChecked(!targetShown);
                scene.getViewSettings().setShowCameraTarget(!targetShown);
                scene.dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED), false);
			}
		};        
        descriptor = STTPlugin.getImageDescriptor("icons/tripod.gif");
        showTargetAction.setImageDescriptor(descriptor);
        site.getActionBars().getToolBarManager().add(showTargetAction);
        
		// add show arcball action to toolbar
        IAction showArcballAction = new Action("Toggle Arcball")
        {
            public void run()
            {
                boolean arcballShown = scene.getViewSettings().isShowArcball();
                this.setChecked(!arcballShown);
                scene.getViewSettings().setShowArcball(!arcballShown);
                scene.dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED), false);
            }
        };
        descriptor = STTPlugin.getImageDescriptor("icons/arcball.gif");
        showArcballAction.setImageDescriptor(descriptor);
        site.getActionBars().getToolBarManager().add(showArcballAction);
        
        // add show ROI action to toolbar
        IAction showROIAction = new Action("Toggle ROI")
        {
            public void run()
            {
                boolean roiShown = scene.getViewSettings().isShowItemROI();
                this.setChecked(!roiShown);
                scene.getViewSettings().setShowItemROI(!roiShown);
                scene.dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED), false);
            }
        };
        descriptor = STTPlugin.getImageDescriptor("icons/bbox.gif");
        showROIAction.setImageDescriptor(descriptor);
        site.getActionBars().getToolBarManager().add(showROIAction);
        
        // add lat/lon position to status bar
        llStatus = new LatLonStatusLine();
		site.getActionBars().getStatusLineManager().add(llStatus);
	}
	
	
	@Override
	public void dispose()
	{
        super.dispose();
        if (scene != null)
            scene.getRenderer().dispose();
	}
	
	
	@Override
	public void setFocus()
	{
        parent.setFocus();
	}
    
    
    @Override
    protected void assignScene()
    {
        ScenePageInput pageInput = (ScenePageInput) getSite().getPage().getInput();
        if (pageInput != null)
            setScene((WorldScene)pageInput.getScene());
        else
            setScene(null);
    }
    
    
    @Override
    public void setScene(WorldScene sc)
    {
        if (scene != sc)
        {
            // make sure we dispose previous scene renderer
            if (scene != null)
                scene.getRenderer().dispose();
    
            // call parent method !!
            super.setScene(sc);
            setPartName(scene.getName());
            
            // init the renderer
            WorldSceneRenderer renderer = new JOGLRenderer(); // TODO open renderer specified in project file??
            scene.setRenderer(renderer);
            renderer.setParent(parent);
            renderer.init();
            Composite canvas = renderer.getCanvas();
            
            // init size
            //Rectangle clientArea = canvas.getClientArea();
            //scene.getRenderer().resizeView(clientArea.width, clientArea.height);
            //scene.getViewSettings().setViewWidth(clientArea.width);
            //scene.getViewSettings().setViewHeight(clientArea.height);
            
            // create and register view controller
            controller.setScene(scene);
            canvas.addMouseListener(controller);
            canvas.addMouseMoveListener(controller);
            canvas.addMouseMoveListener(this);
            canvas.addListener(SWT.MouseWheel , controller);
            canvas.addControlListener(this);
            canvas.addPaintListener(this);
            
            // refresh display
            refreshViewAsync();
        }
    }
    
    
    // TODO should use WorldViewController point selection feature instead
    public Vector3d getProjectedPosition(int x, int y)
    {
        Vector3d P0 = new Vector3d();
        Projection projection = scene.getViewSettings().getProjection();
        boolean found = projection.pointOnMap(x, y, scene, P0);

        if (!found)
            return new Vector3d();

        // convert to LLA
        projection.unproject(Crs.EPSG4329, P0);

        double RTD = 180/Math.PI;
        P0.x *= RTD;
        P0.y *= RTD;
        
        return P0;
    }

    
    @Override
    public void updateView()
    {
        if (scene != null)
            ((WorldSceneRenderer)scene.getRenderer()).drawScene(scene);
    }
    
    
    @Override
    public void clearView()
    {
        // Clears the world view and unregister listeners
        parent.removeMouseListener(controller);
        parent.removeMouseMoveListener(controller);
        parent.removeListener(SWT.MouseWheel , controller);
        parent.redraw();
        setPartName("Nothing Open");
    }
    
    
    public void paintControl(PaintEvent e)
    {
        //System.out.println("Paint Event");
        refreshView();
    }
    
    
    protected void reportLLStatus(int x, int y)
    {
        Vector3d P0 = getProjectedPosition(x, y);
        StringBuffer llStrBuff = new StringBuffer(40);
        NumberFormat nf  = NumberFormat.getInstance();
        nf.setMaximumIntegerDigits(3);
        nf.setMinimumFractionDigits(4);
        String lonStr = nf.format(P0.x);
        String latStr = nf.format(P0.y);
        llStrBuff.append("Lat: " + latStr + "   ");
        llStrBuff.append("Lon: " + lonStr);
        llStatus.setText(llStrBuff.toString());
    }
    
    
    public void mouseMove(MouseEvent e)
    {
        int viewHeight = scene.getRenderer().getViewHeight();
        e.y = viewHeight - e.y;
        reportLLStatus(e.x, e.y);
    }
    
    
    @Override
    public void handleEvent(STTEvent e)
    {       
        switch (e.type)
        {
            case SCENE_OPTIONS_CHANGED:            
            case SCENE_VIEW_CHANGED:
            case SCENE_ITEM_CHANGED:
            case ITEM_VISIBILITY_CHANGED:
                //System.out.println(e);
                refreshViewAsync();
                break;
                
            case SCENE_PROJECTION_CHANGED:
                // create fit runnable for async exec
                Runnable runFitScene = new Runnable()
                {
                    public void run()
                    {
                        ViewSettings view = scene.getViewSettings();
                        view.getProjection().fitViewToBbox(scene.getBoundingBox(), scene, true);
                        refreshView();
                    }
                };
                // fit view to scene
                parent.getDisplay().syncExec(runFitScene);
                break;
        }
    }
    
    
    public void controlResized(ControlEvent e)
    {
        if (scene != null)
        {
            // redraw the whole scene
        	refreshView();
            scene.getViewSettings().dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED), false);
        }
    }
	
	
	public void controlMoved(ControlEvent e)
	{
	}
}
