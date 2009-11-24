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

package org.vast.stt.renderer;

import org.eclipse.swt.widgets.Composite;
import org.vast.math.Vector3d;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.world.ViewSettings;
import org.vast.stt.style.DataStyler;


/**
 * <p><b>Title:</b><br/>
 * Renderer
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Common interface for all scene renderers.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 9, 2005
 * @version 1.0
 */
public abstract class SceneRenderer<SceneType extends Scene>
{
    public enum CleanupSection
    {
        ALL,
        GEOMETRY,
        TEXTURES,
    }    
    
    protected Composite composite;
    

    public abstract void init();
    
    
    public abstract Composite getCanvas();
    
    
    public abstract void drawScene(SceneType scene);
       
    
    public abstract void cleanupSync(DataStyler styler, Object[] objects, CleanupSection section);
    
    
    public abstract void cleanupAsync(DataStyler styler, Object[] objects, CleanupSection section);
    
    
    public abstract void project(double worldX, double worldY, double worldZ, Vector3d viewPos);


    public abstract void unproject(double viewX, double viewY, double viewZ, Vector3d worldPos);
    
    
    public abstract PickedObject pick(SceneType scene, PickFilter filter);


    public abstract void setupView(ViewSettings viewSettings);
    
    
    public abstract void dispose();

    
    public int getViewWidth()
    {
        class MyRunnable implements Runnable
        {
            public int viewWidth = 0;
            public void run() {viewWidth = getCanvas().getClientArea().width;}
        };
        
        MyRunnable runnable = new MyRunnable();       
        composite.getDisplay().syncExec(runnable);
        return runnable.viewWidth;
    }


    public int getViewHeight()
    {
        class MyRunnable implements Runnable
        {
            public int viewHeight = 0;
            public void run() {viewHeight = getCanvas().getClientArea().height;}
        };
        
        MyRunnable runnable = new MyRunnable();       
        composite.getDisplay().syncExec(runnable);
        return runnable.viewHeight;
    }
    

    public void setParent(Composite composite)
    {
        this.composite = composite;
    }
}
