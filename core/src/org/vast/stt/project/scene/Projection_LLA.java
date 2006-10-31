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

package org.vast.stt.project.scene;

import org.vast.math.Vector3d;
import org.vast.physics.SpatialExtent;
import org.vast.stt.project.scene.ViewSettings.MotionConstraint;
import org.vast.stt.renderer.Renderer;
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
        // ECEF to LLA, etc...
        
        // adjust longitude to TWO_PI range
        point.x = adjustLongitude(point.x);
        
        // adjust latitude to PI range
        point.y = adjustLatitude(point.y);
        
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
    
    
    public void fitViewToBbox(SpatialExtent bbox, Scene scene, boolean adjustZRange)
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
        view.setNearClip(dist);
        if (adjustZRange)
            view.setFarClip(dist*20);
                
        // get dimensions of projection of bbox
        double dx = Math.abs(bbox.getMaxX() - bbox.getMinX());
        double dy = Math.abs(bbox.getMaxY() - bbox.getMinY());
        
        // set new orthowidth
        Renderer renderer = scene.getRenderer();
        double viewWidth = (double)renderer.getViewWidth();
        double viewHeight = (double)renderer.getViewHeight();
        double viewAspectRatio = viewWidth / viewHeight;
        double bboxAspectRatio = dx / dy;
        
        if (bboxAspectRatio >= viewAspectRatio)
            view.setOrthoWidth(dx);
        else
            view.setOrthoWidth(dy * viewAspectRatio);
    }
    
    
    public void fitBboxToView(SpatialExtent bbox, Scene scene)
    {
        ViewSettings view = scene.getViewSettings(); 
        Renderer renderer = scene.getRenderer();
        
        double centerX = view.getTargetPos().x * RTD;
        double centerY = view.getTargetPos().y * RTD;
        double dX = view.getOrthoWidth()/2 * RTD;
        double dY = dX * renderer.getViewHeight() / renderer.getViewWidth();
        
        bbox.setMinX(Math.max(centerX - dX, -180));
        bbox.setMaxX(Math.min(centerX + dX, +180));
        bbox.setMinY(Math.max(centerY - dY, -90));
        bbox.setMaxY(Math.min(centerY + dY, +90));
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
}
