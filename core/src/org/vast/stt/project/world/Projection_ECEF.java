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
import org.vast.physics.Datum;
import org.vast.physics.MapProjection;
import org.vast.physics.RayIntersectEllipsoid;
import org.vast.physics.SpatialExtent;
import org.vast.stt.project.world.ViewSettings.MotionConstraint;
import org.vast.stt.renderer.SceneRenderer;
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
    protected final static double RTD = 180 / Math.PI;
    protected RayIntersectEllipsoid rie;
    protected Datum datum;
    
    
    public Projection_ECEF()
    {
        datum = new Datum();
        rie = new RayIntersectEllipsoid(datum);
    }
    
    
    public void adjust(Crs sourceCrs, PrimitiveGraphic point)
    {
        project(sourceCrs, point);
    }
    
    
    public void project(Crs sourceCrs, PrimitiveGraphic point)
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
    
    
    public void clip(PrimitiveGraphic point)
    {
        
    }
    
    
    public void fitViewToBbox(SpatialExtent bbox, WorldScene scene, boolean adjustZRange)
    {
        ViewSettings view = scene.getViewSettings();
        
        // compute bbox 3D diagonal distance
        double dist = bbox.getDiagonalDistance();
        
        // change camera target to center of bbox or 0,0,0
        Vector3d center = bbox.getCenter();
        Vector3d newCameraPos = new Vector3d();
        newCameraPos.add(center);
        
        if (newCameraPos.length() < 1e6)
        {
            view.getCameraPos().set(dist*10, 0.0, 0.0);
            view.getUpDirection().set(0.0, 0.0, 1.0);
            view.getTargetPos().set(0.0, 0.0, 0.0);
        }
        else
        {
            // change camera pos
            newCameraPos.normalize();
            newCameraPos.scale(dist*10);
            newCameraPos.add(center);
            view.setCameraPos(newCameraPos);
            
            // change camera up direction
            Vector3d newUp = new Vector3d();
            Vector3d sideDir = new Vector3d(0,0,1);
            sideDir.cross(sideDir, newCameraPos);
            newUp.cross(newCameraPos, sideDir);
            newUp.normalize();        
            view.setUpDirection(newUp);
            
            // change camera target
            view.setTargetPos(center);
        }
        
        // adjust z range
        view.setNearClip(dist);
        if (adjustZRange)
            view.setFarClip(dist*20);
        
        // now use renderer to find projection of bbox on screen
        SceneRenderer<?> renderer = scene.getRenderer();
        renderer.setupView(view);
        Vector3d winPoint1 = new Vector3d();
        Vector3d winPoint2 = new Vector3d();
        renderer.project(bbox.getMinX(), bbox.getMinY(), bbox.getMinZ(), winPoint1);
        renderer.project(bbox.getMaxX(), bbox.getMaxY(), bbox.getMaxZ(), winPoint2);
                
        // get dimensions of projection of bbox
        double dx = Math.abs(winPoint1.x - winPoint2.x);
        double dy = Math.abs(winPoint1.y - winPoint2.y);
        
        // set new orthowidth
        double viewWidth = (double)renderer.getViewWidth();
        double viewHeight = (double)renderer.getViewHeight();
        double viewAspectRatio = viewWidth / viewHeight;
        double bboxAspectRatio = dx / dy;
        double oldWidth = view.getOrthoWidth();
        
        if (bboxAspectRatio >= viewAspectRatio)
            view.setOrthoWidth(oldWidth * dx / viewWidth);
        else
            view.setOrthoWidth(oldWidth * dy / viewHeight);
    }
    
    
    public void fitBboxToView(SpatialExtent bbox, WorldScene scene)
    {
        ViewSettings view = scene.getViewSettings(); 
        Vector3d pos = view.getCameraPos();
        Vector3d dir = view.getTargetPos().copy();
        dir.sub(pos);
        double[] intersect = rie.getIntersection(pos, dir);
        
        if (rie.getFoundFlag())
        {
            double[] llaCenter = MapProjection.ECFtoLLA(intersect[0], intersect[1], intersect[2], null);
            double centerX = llaCenter[1] * RTD;
            if (centerX > 180)
                centerX -= 360;
            double centerY = llaCenter[0] * RTD;
            double c = Math.min(6378137, view.getOrthoWidth()/2);
            double dX = Math.asin(c/6378137) * RTD * (1 + Math.abs(centerY)/90);
            double dY = dX;
            if (90 - Math.abs(centerY) < dX) dX = 180;

            bbox.setMinX(centerX - dX);
            bbox.setMaxX(centerX + dX);
            bbox.setMinY(Math.max(centerY - dY, -90));
            bbox.setMaxY(Math.min(centerY + dY, +90));
        }
        else
        {
            bbox.setMinX(-180);
            bbox.setMaxX(+180);
            bbox.setMinY(-90);
            bbox.setMaxY(+90);
        }        
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
        
        double[] intersect = rie.getIntersection(pos, viewDir);
        if (rie.getFoundFlag())
        {
            pos.set(intersect);
            return true;
        }
        
        return false;
    }
    
    
    public Vector3d getDefaultCameraUpDirection(Vector3d targetPos)
    {
        // get the up vector so that the earth z is going up on the screen
        Vector3d cameraUp = new Vector3d();
        cameraUp.sub(targetPos);
        
        if (cameraUp.length() < 1e6)
        {
            targetPos.set(0.0, 0.0, 0.0);
            return new Vector3d(0, 0, 1);
        }
        else
        {
            Vector3d sideDir = new Vector3d(0,0,1);
            sideDir.cross(sideDir, cameraUp);
            cameraUp.cross(cameraUp, sideDir);
            cameraUp.normalize();        
            return cameraUp;
        }
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
