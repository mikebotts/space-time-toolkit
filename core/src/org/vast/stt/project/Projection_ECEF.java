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
import org.vast.physics.MapProjection;
import org.vast.stt.project.ViewSettings.MotionConstraint;
import org.vast.stt.style.PrimitiveGraphic;


/**
 * <p><b>Title:</b>
 * ECEF Projection
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Contains methods to adjust geometries for ECEF projection
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 8, 2006
 * @version 1.0
 */
public class Projection_ECEF implements Projection
{
    
    public void adjust(Crs sourceCrs, PrimitiveGraphic point)
    {
        switch (sourceCrs)
        {
            case EPSG4329:
                double[] ecef = MapProjection.LLAtoECF(point.y, point.x, point.z, null);
                point.x = ecef[0];
                point.y = ecef[1];
                point.z = ecef[2];
                break;
        }
    }
    
    
    public Vector3d getDefaultCameraLookDirection(Vector3d targetPos)
    {
        // get a vector always oriented toward center of earth
        Vector3d cameraLook = new Vector3d();
        cameraLook.sub(targetPos);
        cameraLook.normalize();
        return cameraLook;
    }
    
    
    public Vector3d getDefaultCameraUpDirection(Vector3d targetPos)
    {
        // get the up vector so that the earth z is going up on the screen
        Vector3d cameraUp = new Vector3d();
        cameraUp.sub(targetPos);
        
        Vector3d sideDir = new Vector3d(0,0,1);
        sideDir.cross(sideDir, cameraUp);
        cameraUp.cross(cameraUp, sideDir);
        cameraUp.normalize();
        
        return cameraUp;
    }
    
    
    public MotionConstraint getDefaultRotationConstraint()
    {
        return MotionConstraint.XYZ;
    }


    public MotionConstraint getDefaultTranslationConstraint()
    {
        return MotionConstraint.XYZ;
    }


    public MotionConstraint getDefaultZoomConstraint()
    {
        return MotionConstraint.XYZ;
    }
}
