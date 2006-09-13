package org.vast.stt.gui.widgets.catalog;

import java.util.Iterator;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.wcs.WCSLayerCapabilities;
import org.vast.ows.wfs.WFSLayerCapabilities;
import org.vast.ows.wms.WMSLayerCapabilities;
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.tree.DataEntry;
import org.vast.stt.project.tree.DataFolder;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.tree.DataTree;
import org.vast.stt.provider.DataProvider;

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
	   //  Itterate the DataTree to find the target item,
	   //  and insert it there, then refresh
	   Object [] expElms = vwr.getExpandedElements();
	   //  ASSumes DataTree is always 0th tree element, which I think 
	   //  is a valid assumption
	   DataTree tree = (DataTree)expElms[0];
	   DataEntry entryTmp = null;

	   Iterator<DataEntry> it = tree.iterator();
	   boolean done = false;
	   int index = 0;
	   while(it.hasNext() && !done){
		   entryTmp = it.next();
		   if(entryTmp instanceof DataFolder){
			   done = recurseFolder((DataFolder)entryTmp, (DataItem)dropTarget, item);
		   } else {
			   if(entryTmp == dropTarget) {
				   tree.add(index, item);
				   break;
			   }
		   }
		   index++;
	   }
       item.dispatchEvent(new STTEvent(item, EventType.SCENE_TREE_CHANGED));
	   this.getViewer().refresh();
	   return true;
   }
   
   protected boolean recurseFolder(DataFolder folder, DataItem dropTarget, DataItem newItem){
	   Iterator<DataEntry> it = folder.iterator();
	   DataEntry entryTmp;
	   int index = 0;
	   while(it.hasNext()){
		   entryTmp = it.next();
		   if(entryTmp instanceof DataFolder){
			   recurseFolder((DataFolder)entryTmp, dropTarget, newItem);
		   } else if(entryTmp == dropTarget) { 
			   folder.add(index, newItem);
			   return true;
		   }
		   index++;
	   }
	   return false;
   }
   
   protected DataProvider createSensorMLProvider(OWSLayerCapabilities caps){
	  SMLProvider prov = new SMLProvider();
	  
	  // ... 
	  
	  return prov;
   }
   
}
