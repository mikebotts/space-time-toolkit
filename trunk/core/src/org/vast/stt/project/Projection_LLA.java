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

package org.vast.stt.project;

import org.vast.math.Vector3d;
import org.vast.stt.project.ViewSettings.MotionConstraint;
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
    
    
    public Vector3d getDefaultCameraLookDirection()
    {
        return new Vector3d(0,0,-1);
    }
    
    
    public Vector3d getDefaultCameraUpDirection()
    {
        return new Vector3d(0,1,0);
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
