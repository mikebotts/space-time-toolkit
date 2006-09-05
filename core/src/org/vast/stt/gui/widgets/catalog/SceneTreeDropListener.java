package org.vast.stt.gui.widgets.catalog;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.wcs.WCSLayerCapabilities;
import org.vast.ows.wfs.WFSLayerCapabilities;
import org.vast.ows.wms.WMSLayerCapabilities;
import org.vast.stt.data.SensorMLProvider;
import org.vast.stt.project.DataEntry;
import org.vast.stt.project.DataFolder;
import org.vast.stt.project.DataItem;
import org.vast.stt.project.DataProvider;

public class SceneTreeDropListener extends ViewerDropAdapter 
{
	public SceneTreeDropListener(StructuredViewer viewer){
		super(viewer);
	}
	
   /**
    *  This is called when a Layer from LayerTree in CapabilitiesView 
    *  is dropped onto the SceneTree.  The data is an instance of 
    *  OWSLayerCapabilities
    */
   public boolean performDrop(Object data) {
	   OWSLayerCapabilities caps = (OWSLayerCapabilities)data;
	   DataItem newItem = new DataItem();
	   newItem.setName(caps.getName());
	   
	   if(data instanceof SOSLayerCapabilities) {
		   DataProvider prov = createSensorMLProvider(caps);
		   newItem.setDataProvider(prov);
		   //  add dataItem to target DataFolder
		   return dropItem(newItem);
	   } else if (data instanceof WMSLayerCapabilities) {
		   newItem.setName(caps.getName());
		   DataProvider prov = createSensorMLProvider(caps);
		   newItem.setDataProvider(prov);
		   //  add dataItem to target DataFolder
		   return dropItem(newItem);
	   } else if (data instanceof WCSLayerCapabilities) {
		   System.err.println("Add WCS drop support");
		   return false;
	   } else if (data instanceof WFSLayerCapabilities) {
		   System.err.println("Add WFS drop support");
		   return false;
	   } else  {
		   System.err.println("SceneTreeDropListener.performDrop():  Unknown Caps type.");
		   return false;
	   }
   }
   
   /**
    * Method declared on ViewerDropAdapter
    */
   public boolean validateDrop(Object target, int op, TransferData type) {
	   boolean dropOk = LayerTransfer.getInstance().isSupportedType(type);
	   dropOk = dropOk && (target instanceof DataEntry);
	   return dropOk;
   }
	
   protected boolean dropItem(DataItem item){
	   DataEntry dropTarget = (DataEntry)this.getCurrentTarget();
	   if(dropTarget instanceof DataFolder) {
		   ((DataFolder)dropTarget).add(item);
		   this.getViewer().refresh();
		   return true;
	   }
	   //  dropTarget MUST be a DataItem
	   TreeViewer vwr = (TreeViewer)this.getViewer();
	   //  didn't work...
	   vwr.add(dropTarget, item);
	   this.getViewer().refresh();
	   return true;
   }
   
   protected DataProvider createSensorMLProvider(OWSLayerCapabilities caps){
	  SensorMLProvider prov = new SensorMLProvider();
	  
	  // ... 
	  
	  return prov;
   }
   
}
