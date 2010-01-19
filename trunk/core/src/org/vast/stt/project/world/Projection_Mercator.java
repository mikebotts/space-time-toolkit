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
import org.vast.stt.project.world.ViewSettings.MotionConstraint;
import org.vast.stt.renderer.SceneRenderer;
import org.vast.stt.style.PrimitiveGraphic;
import org.vast.util.SpatialExtent;


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
    protected double altitudeDamping = 1e-6;
    protected double xSav = Double.NaN;
    protected double ySav = Double.NaN;
    protected Vector3d tempPoint = new Vector3d();
    
    
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
    
    
    protected void project(Crs sourceCrs, PrimitiveGraphic point)
    {
    	point.toVector3d(tempPoint);    	
    	project(sourceCrs, tempPoint);
    	point.fromVector3d(tempPoint);
    }
    
    
    public void project(Crs sourceCrs, Vector3d point)
    {
        double[] merc, lla;
        
        switch (sourceCrs)
        {
            case EPSG4329:
                merc = MapProjection.LLAtoMerc(point.x, point.y, point.z);
                point.x = merc[0];
                point.y = merc[1];
                point.z = merc[2];
                break;
                
            case ECEF:
                lla = MapProjection.ECFtoLLA(point.x, point.y, point.z, null);
                merc = MapProjection.LLAtoMerc(lla[0], lla[1], lla[2]);
                point.x = merc[0];
                point.y = merc[1];
                point.z = merc[2];
                break;
        }
        
        // always apply altitude damping
        point.z = altitudeDamping * point.z;
    }
    
    
    public void unproject(Crs destCrs, Vector3d point)
    {
    	// always remove altitude damping
        point.z = point.z / altitudeDamping;
        
    	switch (destCrs)
        {
        	case EPSG4329:
            	double[] lla = MapProjection.MerctoLLA(point.x, point.y, point.z);
                point.x = lla[0];
                point.y = lla[1];
                point.z = lla[2];                
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
        
        Vector3d targetPos = view.getTargetPos();
        Vector3d cameraPos = view.getCameraPos();
        
        double centerX = targetPos.x;
        double centerY = targetPos.y;
        double dX = view.getOrthoWidth()/2;
        double dY = dX * renderer.getViewHeight() / renderer.getViewWidth();
        
        // calculate secante (see http://fr.wikipedia.org/wiki/Fonction_trigonométrique)
        Vector3d diff = cameraPos.copy();
        diff.sub(targetPos);
        diff.normalize();
        double secante = 8;
        if (diff.z != 0)
        {
        	secante = 1 / diff.z;
        	secante = Math.min(secante, 8);
        }        
        
        // scale bbox size
	    dX = dX * secante;
	    dY = dY * secante;
	    
        // compute bbox in mercator crs
        double minX = Math.max(centerX - dX, -PI);
        double maxX = Math.min(centerX + dX, +PI);
        double minY = Math.max(centerY - dY, -PI);
        double maxY = Math.min(centerY + dY, +PI);
        
        // convert to LLA crs
        double[] min = MapProjection.MerctoLLA(minX, minY, 0);
        double[] max = MapProjection.MerctoLLA(maxX, maxY, 0);        
        bbox.setMinX(min[0] * RTD);
        bbox.setMaxX(max[0] * RTD);
        bbox.setMinY(min[1] * RTD);
        bbox.setMaxY(max[1] * RTD);
    }
    
    
    public boolean pointOnMap(int x, int y, WorldScene scene, Vector3d pos)
    {
        ViewSettings view = scene.getViewSettings();
        scene.getRenderer().unproject(x, y, 0.0, pos);
        
        Vector3d viewDir = view.getTargetPos().copy();
        viewDir.sub(view.getCameraPos());
        
        double s = -pos.z / viewDir.z;
        pos.x += viewDir.x * s;
        pos.y += viewDir.y * s;
        pos.z = 0.0;
        
        if (pos.x > getMaxX() || pos.x < getMinX())
            return false;
        
        if (pos.y > PI || pos.y < -PI)
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
