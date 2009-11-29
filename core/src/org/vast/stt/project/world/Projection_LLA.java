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
import org.vast.util.Bbox;
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
public class Projection_LLA implements Projection
{
    protected final static double PI = Math.PI;
    protected final static double HALF_PI = Math.PI/2;
    protected final static double TWO_PI = 2*Math.PI;
    protected final static double RTD = 180 / Math.PI;
    
    protected double centerLongitude = 0.0;
    //protected double altitudeDamping = 1e-6;
    protected double altitudeDamping = 1.75e-7;
    protected double xSav = Double.NaN;
    protected double ySav = Double.NaN;
    protected Vector3d tempPoint = new Vector3d();
    protected boolean insertBreaks = true;
    
    
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
        if (insertBreaks && xSav != Double.NaN && !point.graphBreak)
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
        double[] lla;
        
        switch (sourceCrs)
        {
            case ECEF:
                lla = MapProjection.ECFtoLLA(point.x, point.y, point.z, null);
                point.x = lla[0];
                point.y = lla[1];
                point.z = lla[2];                
                break;
                
            case MERC:
                lla = MapProjection.MerctoLLA(point.x, point.y, point.z);
                point.x = lla[0];
                point.y = lla[1];
                point.z = lla[2];                
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
            case ECEF:
            	double[] ecf = MapProjection.LLAtoECF(point.x, point.y, point.z, null);
                point.x = ecf[0];
                point.y = ecf[1];
                point.z = ecf[2];                
                break;
                
            case MERC:
            	double[] merc = MapProjection.LLAtoMerc(point.x, point.y, point.z);
                point.x = merc[0];
                point.y = merc[1];
                point.z = merc[2];                
                break;
        }
    }
    
    
    public void clip(PrimitiveGraphic point)
    {
        // clip longitude to TWO_PI range
        point.x = adjustLongitude(point.x);
        
        // clip latitude to PI range
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
        SceneRenderer<?> renderer = scene.getRenderer();
        int width = renderer.getViewWidth();
        int height = renderer.getViewHeight();
        
        // compute intersection of 4 screen corners with map plane
        Vector3d ul = new Vector3d();
        this.pointOnMap(0, 0, scene, ul);        
        Vector3d ur = new Vector3d();
        this.pointOnMap(width, 0, scene, ur);        
        Vector3d ll = new Vector3d();
        this.pointOnMap(0, height, scene, ll);
        Vector3d lr = new Vector3d();
        this.pointOnMap(width, height, scene, lr);
        
        // compute enclosing bbox
        Bbox tmpBox = new Bbox();
        tmpBox.resizeToContain(ul.x, ul.y, ul.z);
        tmpBox.resizeToContain(ur.x, ur.y, ur.z);
        tmpBox.resizeToContain(ll.x, ll.y, ll.z);
        tmpBox.resizeToContain(lr.x, lr.y, lr.z);
        
        // set bbox after clamping        
        bbox.setMinX(Math.max(tmpBox.getMinX()*RTD, -180));
        bbox.setMaxX(Math.min(tmpBox.getMaxX()*RTD, +180));
        bbox.setMinY(Math.max(tmpBox.getMinY()*RTD, -90));
        bbox.setMaxY(Math.min(tmpBox.getMaxY()*RTD, +90));
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
    
    
    public boolean isInsertBreaks()
    {
        return insertBreaks;
    }


    public void setInsertBreaks(boolean clip)
    {
        this.insertBreaks = clip;
    }
}
