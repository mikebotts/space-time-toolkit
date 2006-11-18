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

package org.vast.stt.project.world;

import org.vast.math.Quat4d;
import org.vast.math.Vector3d;
import org.vast.stt.project.world.ViewSettings.CameraMode;
import org.vast.stt.project.world.ViewSettings.MotionConstraint;
import org.vast.stt.renderer.SceneRenderer;


/**
 * <p><b>Title:</b>
 * Base Camera Control
 * </p>
 *
 * <p><b>Description:</b><br/>
 * 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 11, 2006
 * @version 1.0
 */
public class CameraControl_Base implements CameraControl
{
    protected WorldScene scene;
    protected Vector3d P0 = new Vector3d();
    protected Vector3d P1 = new Vector3d();
    protected Vector3d C = new Vector3d();
    
    
    public CameraControl_Base(WorldScene scene)
    {
        this.setScene(scene);
    }
    
    
    public void setScene(WorldScene scene)
    {
        this.scene = scene;
    }


    public void doRotation(int x0, int y0, int x1, int y1)
    {
        ViewSettings viewSettings = scene.getViewSettings();
        MotionConstraint rotConstraint = viewSettings.getRotConstraint();
        
        if (rotConstraint != MotionConstraint.NO_MOTION)
        {
            SceneRenderer<?> renderer = scene.getRenderer();

            // actual camera position
            Vector3d up = viewSettings.getUpDirection();
            Vector3d pos = viewSettings.getCameraPos();
            Vector3d target = viewSettings.getTargetPos();
            
            // ge actual viewport dimensions
            int viewHeight = renderer.getViewHeight();
            int viewWidth = renderer.getViewWidth();
            
            // unproject to world view
            renderer.unproject(x0, y0, 0.0, P0);
            renderer.unproject(x1, y1, 0.0, P1);
            renderer.unproject(viewWidth/2, viewHeight/2, 0.0, C);
            
            // viewZ vector = target - pos
            Vector3d oldZ = new Vector3d(target);
            oldZ.sub(pos);
            oldZ.normalize();
            
            // arcball radius
            double r = viewSettings.getArcballRadius();
            if (r < 0)
                r = viewSettings.getOrthoWidth() / 2;

            // pos of point 0 on arcball
            P0.sub(C);
            P0.scale(1/r);
            double P0_2 = P0.lengthSquared();
            if (P0_2 < 1)
            {
                Vector3d fakeZ = oldZ.copy();
                fakeZ.scale(-Math.sqrt(1 - P0_2));
                P0.add(fakeZ);
            }
            P0.normalize();
            
            // pos of point 1 on arcball
            P1.sub(C);
            P1.scale(1/r);            
            double P1_2 = P1.lengthSquared();
            if (P1_2 < 1)
            {
                Vector3d fakeZ = oldZ.copy();
                fakeZ.scale(-Math.sqrt(1 - P1_2));
                P1.add(fakeZ);
            }
            P1.normalize();
            
            // compute rotation quaternion
            Vector3d crossP = new Vector3d();
            crossP.cross(P0, P1);
            crossP.normalize();
            Quat4d qRot = new Quat4d(crossP, -Math.acos(P0.dot(P1)));

            // rotate current camera position
            pos.sub(target);
            pos.rotate(qRot);
            pos.add(target);
            
            // also update up axis
            up.rotate(qRot);
        }
    }


    public void doTranslation(int x0, int y0, int x1, int y1)
    {
        ViewSettings viewSettings = scene.getViewSettings();
        Projection projection = viewSettings.getProjection();
        //MotionConstraint transConstraint = viewSettings.getTransConstraint();
        boolean found;
        
        found = projection.pointOnMap(x0, y0, scene, P0);
        if (!found)
            return;
        
        found = projection.pointOnMap(x1, y1, scene, P1);
        if (!found)
            return;
        
        P0.sub(P1);        
        viewSettings.getTargetPos().add(P0);
        viewSettings.getCameraPos().add(P0);
        
        /*
        if (transConstraint != MotionConstraint.NO_MOTION)
        {
            SceneRenderer renderer = scene.getRenderer();
            
            renderer.unproject(x0, -y0, 0.0, P0);
            renderer.unproject(x1, -y1, 0.0, P1);
            P1.sub(P0);
            
            Vector3d pos = viewSettings.getCameraPos();
            Vector3d target = viewSettings.getTargetPos();
            
            // viewZ vector = target - pos
            Vector3d viewZ = new Vector3d(target);
            viewZ.sub(pos);
            viewZ.normalize();
            
            double s;
            double maxS = P1.length()*2;
            
            switch (transConstraint)
            {
                case XY:
                    // P1 + s*viewZ = [x,y,0]
                    s = -P1.z / viewZ.z;
                    if (Math.abs(s) > maxS) s = Math.signum(s) * maxS;
                    viewZ.scale(s);
                    P1.add(viewZ);
                    P1.z = 0.0;
                    break;
                    
                case XZ:
                    // P1 + s*viewZ = [x,0,z]
                    s = -P1.y / viewZ.y;
                    viewZ.scale(s);
                    P1.add(viewZ);
                    P1.y = 0.0;
                    break;
                    
                case YZ:
                    // P1 + s*viewZ = [0,y,z]
                    s = -P1.x / viewZ.x;
                    viewZ.scale(s);
                    P1.add(viewZ);
                    P1.x = 0.0;
                    break;
            }
            
            viewSettings.getTargetPos().sub(P1);
            viewSettings.getCameraPos().sub(P1);
        }
        */
    }


    public void doZoom(int x0, int y0, int x1, int y1)
    {
        SceneRenderer<?> renderer = scene.getRenderer();
        double amount = 2.0 * ((double)(y0 - y1)) / ((double)renderer.getViewHeight());
        doZoom(amount);
    }
    
    
    public void doZoom(double amount)
    {
        ViewSettings viewSettings = scene.getViewSettings();
        MotionConstraint zoomConstraint = viewSettings.getZoomConstraint();
        
        if (zoomConstraint != MotionConstraint.NO_MOTION)
        {
            // zoom in ortho mode
            if (viewSettings.getCameraMode() == CameraMode.ORTHO)
            {
                double currentWidth = viewSettings.getOrthoWidth();         
                double newWidth = currentWidth + amount*currentWidth;
                
                if (newWidth == 0.0 && amount > 0.0)
                    newWidth = amount;
                else if (newWidth < 0.0)
                    newWidth = 0.0;
                
                viewSettings.setOrthoWidth(newWidth);
            }
        }
    }
}
