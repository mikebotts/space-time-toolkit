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

package org.vast.stt.project.world;

import org.vast.math.Vector3d;
import org.vast.physics.MapProjection;
import org.vast.physics.SpatialExtent;
import org.vast.stt.project.world.ViewSettings.MotionConstraint;
import org.vast.stt.renderer.SceneRenderer;
import org.vast.stt.style.PrimitiveGraphic;


/**
 * <p><b>Title:</b>
 * Lat Lon Alt Projection (EPSG 4329)
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Contains methods to adjust geometries for LLA projection
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Aug 8, 2006
 * @version 1.0
 */
public class Projection_Mercator implements Projection
{
    protected final static double PI = Math.PI;
    protected final static double HALF_PI = Math.PI/2;
    protected final static double TWO_PI = 2*Math.PI;
    protected final static double RTD = 180 / Math.PI;
    
    protected double centerLongitude = 0.0;
    protected double xSav = Double.NaN;
    protected double ySav = Double.NaN;
    
    
    public Projection_Mercator()
    {        
    }
    
    
    public Projection_Mercator(double centerLongitude)
    {
        this.centerLongitude = centerLongitude;
    }
    
    
    public void adjust(Crs sourceCrs, PrimitiveGraphic point)
    {
        // execute crs transform
        project(sourceCrs, point);
        
        // clip geometry to map boundary
        clip(point);
        
        // break geometry if needed
        if (xSav != Double.NaN && !point.graphBreak)
        {
            if (Math.abs(point.x - xSav) > PI)
                point.graphBreak = true;
            
            else if (Math.abs(point.y - ySav) > HALF_PI)
                point.graphBreak = true;
        }
        
        xSav = point.x;
        ySav = point.y;
    }
    
    
    public void project(Crs sourceCrs, PrimitiveGraphic point)
    {
        switch (sourceCrs)
        {
            case EPSG4329:
                double[] ecef = MapProjection.LLAtoMerc(point.y, point.x, point.z);
                point.x = ecef[0];
                point.y = ecef[1];
                point.z = ecef[2];
                break;
        }
    }
    
    
    public void clip(PrimitiveGraphic point)
    {
        // clip longitude to -PI:PI range
        point.x = adjustX(point.x);
        
        // clip latitude to PI range
        point.y = adjustY(point.y);
    }
    
    
    protected double adjustY(double y)
    {
        if (y > PI)
            return y - TWO_PI;
        
        else if (y < -PI)
            return y + TWO_PI;
        
        return y;
    }
    
    
    protected double adjustX(double x)
    {
        if (x > getMaxX())
            return x - TWO_PI;
        
        else if (x < getMinX())
            return x + TWO_PI;
        
        return x;
    }
    
    
    public void fitViewToBbox(SpatialExtent bbox, WorldScene scene, boolean adjustZRange)
    {
        ViewSettings view = scene.getViewSettings();
        
        // compute bbox 3D diagonal distance
        double dist = bbox.getDiagonalDistance();
        
        // change camera target to center of bbox on XY plane       
        Vector3d center = bbox.getCenter();
        center.z = 0.0;
        view.setTargetPos(center);
        
        // change camera pos
        Vector3d newCameraPos = new Vector3d(center.x, center.y, dist*10);
        view.setCameraPos(newCameraPos);
        
        // change camera up direction
        view.getUpDirection().set(0, 1, 0);
        
        // adjust z range        
        if (adjustZRange)
        {
            view.setFarClip(dist*20);
            view.setNearClip(dist);
        }
                
        // get dimensions of projection of bbox
        double dx = Math.abs(bbox.getMaxX() - bbox.getMinX());
        double dy = Math.abs(bbox.getMaxY() - bbox.getMinY());
        
        // set new orthowidth
        SceneRenderer<?> renderer = scene.getRenderer();
        double viewWidth = (double)renderer.getViewWidth();
        double viewHeight = (double)renderer.getViewHeight();
        double viewAspectRatio = viewWidth / viewHeight;
        double bboxAspectRatio = dx / dy;
        
        if (bboxAspectRatio >= viewAspectRatio)
            view.setOrthoWidth(dx);
        else
            view.setOrthoWidth(dy * viewAspectRatio);
    }
    
    
    public void fitBboxToView(SpatialExtent bbox, WorldScene scene)
    {
        ViewSettings view = scene.getViewSettings();
        SceneRenderer<?> renderer = scene.getRenderer();
        
        double centerX = view.getTargetPos().x * RTD;
        double centerY = view.getTargetPos().y * RTD;
        double dX = view.getOrthoWidth()/2 * RTD;
        double dY = dX * renderer.getViewHeight() / renderer.getViewWidth();
        
        bbox.setMinX(Math.max(centerX - dX, -180));
        bbox.setMaxX(Math.min(centerX + dX, +180));
        bbox.setMinY(Math.max(centerY - dY, -90));
        bbox.setMaxY(Math.min(centerY + dY, +90));
    }
    
    
    public boolean pointOnMap(int x, int y, WorldScene scene, Vector3d pos)
    {
        return false;
    }
    
    
    public MotionConstraint getDefaultRotationConstraint()
    {
        return MotionConstraint.XYZ;
    }


    public MotionConstraint getDefaultTranslationConstraint()
    {
        return MotionConstraint.XY;
    }


    public MotionConstraint getDefaultZoomConstraint()
    {
        return MotionConstraint.XYZ;
    }
    
    
    public double getMaxX()
    {
        return centerLongitude + PI;
    }
    
    
    public double getMinX()
    {
        return centerLongitude - PI;
    }
    
    
    public double getCenterLongitude()
    {
        return centerLongitude;
    }


    public void setCenterLongitude(double centerLongitude)
    {
        this.centerLongitude = centerLongitude;
    }
}
