package org.vast.stt.commands;

import java.util.Iterator;

import org.vast.stt.scene.DataItem;
import org.vast.stt.scene.Scene;
import org.vast.stt.style.DataStyler;
import org.vast.stt.util.SpatialExtent;

/**
 *  FitView either to an entire scene or a single DataItem
 *   
 * @author tcook
 * @date Feb 27, 2006
 * 
 * TODO:  - Need to convert from SpatialExtent to ViewSettings   
 *          before this will actually work    
 */

public class FitView implements Command 
{
	private Scene scene;
	private DataItem item;
	
	public FitView(DataItem item){
		this.item = item;
	}
	
	public FitView(Scene scene){
		this.scene = scene;
	}
	
	public void execute() {
		if(scene != null)
			fitScene();
		else if(item != null)
			fitItem();
	}

	public boolean isUndoAvailable() {
		return false;
	}

	public void unexecute() {
		// TODO Auto-generated method stub
	}

	public void fitScene()
	{
		Iterator<DataItem> it = scene.getDataList().getItemIterator();
		
		double minX, minY, minZ;
		minX = minY = minZ = Double.MAX_VALUE; 
		double maxX, maxY, maxZ;
		maxX = maxY = maxZ = Double.MIN_VALUE; 
		boolean boundsChanged = false;
		while (it.hasNext()) {
			DataItem item = it.next();
			if (!item.isEnabled())
				continue;
			
			DataStyler styler = item.getStyler();
			SpatialExtent extTmp;
			double tminX, tminY, tminZ, tmaxX, tmaxY, tmaxZ;
			if ((styler != null) && styler.isEnabled()) {
				extTmp = styler.getBoundingBox();
				//  Should spatialExtent ever be null here?  It is now.
				if(extTmp == null)
					continue;
				boundsChanged = true;
				tminX = extTmp.getMinX();
				tminY = extTmp.getMinY();
				tminZ = extTmp.getMinZ();
				tmaxX = extTmp.getMaxX();
				tmaxY = extTmp.getMaxY();
				tmaxZ = extTmp.getMaxZ();
				if(tminX < minX)  minX = tminX;
				if(tminY < minY)  minY = tminY;
				if(tminZ < minZ)  minZ = tminZ;
				if(tmaxX > maxX)  maxX = tmaxX;
				if(tmaxY > maxY)  maxY = tmaxY;
				if(tmaxZ > maxZ)  maxZ = tmaxZ;
			}
		}
		
		if(boundsChanged){
			SpatialExtent newExtent = new SpatialExtent();
			newExtent.setMinX(minX);
			newExtent.setMinY(minY);
			newExtent.setMinZ(minZ);
			newExtent.setMaxX(maxX);
			newExtent.setMaxY(maxY);
			newExtent.setMaxZ(maxZ);
			//newExtent.setCrs(oldCrs);
			//  Need to convert Bbox to ViewSettings here...
			//scene.getViewSettings().setBoundingBox(newExtent);
		}
	}

	public void fitItem()
	{
		DataStyler styler = item.getStyler();
		if (styler != null) {// && styler.isEnabled())
			SpatialExtent extent = styler.getBoundingBox();
			//scene.getViewSettings().setSpatitalExtent(extent);
		}
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public void setItem(DataItem item) {
		this.item = item;
	}
}
