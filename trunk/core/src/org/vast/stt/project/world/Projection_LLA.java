/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

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
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 8, 2006
 * @version 1.0
 */
public class Projection_LLA implements Projection
{
    protected final static double PI = Math.PI;
    protected final static double HALF_PI = Math.PI/2;
    protected final static double TWO_PI = 2*Math.PI;
    protected final static double RTD = 180 / Math.PI;
    
    protected double centerLongitude = 0.0;
    protected double altitudeDamping = 1e-6;
    protected double xSav = Double.NaN;
    protected double ySav = Double.NaN;
    
    
    public Projection_LLA()
    {        
    }
    
    
    public Projection_LLA(double centerLongitude)
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
                point.z = altitudeDamping * point.z;
                break;
                
            case ECEF:
                double[] lla = MapProjection.ECFtoLLA(point.x, point.y, point.z, null);
                point.x = lla[1];
                point.y = lla[0];
                point.z = lla[2];
                break;
        }
    }
    
    
    public void clip(PrimitiveGraphic point)
    {
        // adjust longitude to TWO_PI range
        point.x = adjustLongitude(point.x);
        
        // adjust latitude to PI range
        point.y = adjustLatitude(point.y);
    }
    
    
    protected double adjustLatitude(double lat)
    {
        if (lat > HALF_PI)
            return lat - HALF_PI;
        
        else if (lat < -HALF_PI)
            return lat + HALF_PI;
        
        return lat;
    }
    
    
    protected double adjustLongitude(double lon)
    {
        if (lon > getMaxLongitude())
            return lon - TWO_PI;
        
        else if (lon < getMinLongitude())
            return lon + TWO_PI;
        
        return lon;
    }
    
    
    public void fitViewToBbox(SpatialExtent bbox, WorldScene scene, boolean adjustZRange)
    {
        ViewSettings view = scene.getViewSettings();
        
        // compute bbox 3D diagonal distance
        double dist = bbox.getDiagonalDistance();
        
        // change camera target to center of bbox on XY plane       
        Vector3d center = bbox.getCenter();
        view.getTargetPos().x = center.x;
        view.getTargetPos().y = center.y;
        view.getTargetPos().z = 0.0;
        
        // change camera pos
        view.getCameraPos().x = center.x;
        view.getCameraPos().y = center.y;
        
        //adjust z range and camera distance
        if (adjustZRange)
        {
            view.getCameraPos().z = dist*10;
            view.setNearClip(dist);            
            view.setFarClip(dist*20);
        }
        
        // change camera up direction
        view.getUpDirection().set(0, 1, 0);
        
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
        double dY = dX * renderer.getViewHeight()/ renderer.getViewWidth();
        
        bbox.setMinX(Math.max(centerX - dX, -180));
        bbox.setMaxX(Math.min(centerX + dX, +180));
        bbox.setMinY(Math.max(centerY - dY, -90));
        bbox.setMaxY(Math.min(centerY + dY, +90));
    }
    
    
    public boolean pointOnMap(int x, int y, WorldScene scene, Vector3d pos)
    {
        ViewSettings view = scene.getViewSettings();
        
        Vector3d cameraPos = view.getCameraPos();
        Vector3d winPos = new Vector3d();
        scene.getRenderer().project(cameraPos.x, cameraPos.y, cameraPos.z, winPos);
        scene.getRenderer().unproject(x, y, winPos.z, pos);
        
        Vector3d viewDir = view.getTargetPos().copy();
        viewDir.sub(view.getCameraPos());
        
        double s = -pos.z / viewDir.z;        
        pos.x += viewDir.x * s;
        pos.y += viewDir.y * s;
        pos.z = 0.0;
        
        if (pos.x > getMaxLongitude() || pos.x < getMinLongitude())
            return false;
        
        if (pos.y > Math.PI/2 || pos.y < -Math.PI/2)
            return false;

        return true;
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
    
    
    public double getMaxLongitude()
    {
        return centerLongitude + PI;
    }
    
    
    public double getMinLongitude()
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


    public double getAltitudeDamping()
    {
        return altitudeDamping;
    }


    public void setAltitudeDamping(double altitudeDamping)
    {
        this.altitudeDamping = altitudeDamping;
    }
}
