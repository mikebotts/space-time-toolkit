package org.vast.stt.gui.widgets.catalog;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
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

public class SceneTreeDropListener extends ViewerDropAdapter {

	DataEntry currentDropLocation;
	DataFolder currentDropFolder;
	
	public SceneTreeDropListener(StructuredViewer viewer){
		super(viewer);
	}
	
	//
	private boolean setDropLocation(){
		//  set drop location based on where user has dropped item
		//  it's either an item or Folder.
		if(currentDropLocation instanceof DataFolder) {
			currentDropFolder = (DataFolder)currentDropLocation;
			return true;
		}
		//  it MUST be a DataItem. Recurse up and find parent DataFolder, if possible
		DataItem targetItem = (DataItem)currentDropLocation;
		
		return false;
	}
	
   /**
    *  This is called when a Layer from LayerTree in CapabilitiesView 
    *  is dropped onto the SceneTree.  The data is an instance of 
    *  OWSLayerCapabilities
    */
   public boolean performDrop(Object data) {
//	   System.err.println("Perf drop, vieweer is " + this.getViewer());
	   //  First, go ahead and figure out where we're dropping
	   if(currentDropLocation == null)
		   return false;
	   boolean dropOk = setDropLocation();
	   if(!dropOk)
		   return false;  // illegal drop location
	   
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
	   if(target == null) {
		   currentDropLocation = null;
		   return false;  // don't allow drop outside of tree data
	   } else if(target instanceof DataEntry){
		   currentDropLocation = (DataEntry)target;
	   } else {
		   System.err.println("Unknown target - " + target);
		   return false;
	   }
	   
	   boolean dropOk = LayerTransfer.getInstance().isSupportedType(type);
	   
	   return dropOk;
   }
	
   protected boolean dropItem(DataItem item){
	   if(currentDropFolder == null)
		   return false;
	   
	   currentDropFolder.add(item);
	   this.getViewer().refresh();
	   return true;

   }
   
   protected DataProvider createSensorMLProvider(OWSLayerCapabilities caps){
	  SensorMLProvider prov = new SensorMLProvider();
	  
	  // ... 
	  
	  return prov;
   }
   
}
