package org.vast.stt.gui.widgets.catalog;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.vast.io.xml.DOMReader;
import org.vast.io.xml.DOMReaderException;
import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.sld.SLDReader;
import org.vast.ows.sld.TextureSymbolizer;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.wcs.WCSLayerCapabilities;
import org.vast.ows.wfs.WFSLayerCapabilities;
import org.vast.ows.wms.WMSLayerCapabilities;
import org.vast.process.DataProcess;
import org.vast.process.ProcessException;
import org.vast.sensorML.SMLException;
import org.vast.sensorML.reader.ProcessLoader;
import org.vast.sensorML.reader.ProcessReader;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.views.ExceptionPopup;
import org.vast.stt.project.tree.DataEntry;
import org.vast.stt.project.tree.DataFolder;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.tree.DataTree;
import org.vast.stt.project.tree.WorldItem;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.ows.SOSProvider;
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.stt.style.StylerFactory;
import org.vast.stt.style.TextureStyler;
import org.vast.util.ExceptionSystem;

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
	   DataItem newItem = new WorldItem(); // use world item for stuffs to be rendered in the world view
	   newItem.setName(caps.getName());
	   
	   if(data instanceof SOSLayerCapabilities) {
//		   DataProvider prov = createSensorMLProvider(caps);
		   //  For now, use SOSProvider
		   DataProvider prov = createSosProvider(caps);
		   newItem.setDataProvider(prov);
		   //  add dataItem to target DataFolder
		   return dropItem(newItem);
	   } else if (data instanceof WMSLayerCapabilities) {
		   newItem.setName(caps.getName());
		   DataProvider prov = createSensorMLProvider(caps);
		   newItem.setDataProvider(prov);
		   TextureStyler styler = StylerFactory.createDefaultTextureStyler(newItem);
       	   //  add dataItem to target DataFolder
		   styler.setDataItem(newItem);
           styler.getSymbolizer().setEnabled(true);
		   newItem.getSymbolizers().add(styler.getSymbolizer());
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
	   
	   //  ASSumes WorldScene is always 0th tree element, which I think 
	   //  is a valid assumption
	   WorldScene scene = (WorldScene)expElms[0];
	   DataTree tree = scene.getDataTree();
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
	  DataProcess process = null;
	
      try  {
	      // Input mappings
    	  Enumeration e = STTPlugin.getDefault().getBundle().findEntries("templates", "WMS_FlatGrid_Process.xml", false);
    	  String processFileUrl = null;
    	  if (e.hasMoreElements())
    		  processFileUrl = (String)e.nextElement().toString();
    	  
    	  if(processFileUrl == null) {
    		  ExceptionSystem.display(new Exception("STT error: Cannot find template\\WMS_FlatGrid_Process.xml"));
    		  return null;
    	  }
    	  
    	  DOMReader dom = new DOMReader(processFileUrl+"#PROCESS", false);
    	  ProcessReader processReader = new ProcessReader(dom);
    	  processReader.setReadMetadata(false);
    	  processReader.setCreateExecutableProcess(true);

    	  // load process map and parse process chain
    	  String processMapUrl = null;
    	  e = STTPlugin.getDefault().getBundle().findEntries("conf", "ProcessMap.xml", false);
    	  if (e.hasMoreElements())
    		  processMapUrl = (String)e.nextElement().toString();
    	  ProcessLoader.reloadMaps(processMapUrl);
    	  process = processReader.readProcess(dom.getBaseElement());
    	  
    	  // intitialize process and print out info
    	  process.init();
    	  
    	  // set bbox input values
    	  STTSpatialExtent ext = new STTSpatialExtent();
    	  ext.setMinX(-90.0);
    	  ext.setMaxX(-86.0);
    	  ext.setMinY(34.0);
    	  ext.setMaxY(38.0);
    	  prov.setSpatialExtent(ext);
	      
      } catch (ProcessException e){
    	  e.printStackTrace(System.err);
      } catch (SMLException smlEx) {
	      smlEx.printStackTrace(System.err);
      } catch (DOMReaderException domEx) {
    	  domEx.printStackTrace(System.err);
      }	  	  
      
      prov.setProcess(process);
      System.err.println(process);
	  
      
	  return prov;
   }
   
   protected DataProvider createSosProvider(OWSLayerCapabilities caps){
	  SOSProvider prov = new SOSProvider();
	  
	  // ... 
	  
	  return prov;
   }
   
   public static void main (String [] args)throws IOException, DOMReaderException {
	   SLDReader sldReader = new SLDReader();
	   InputStream fileIs = new FileInputStream("C:\\tcook\\work\\STT3\\templates\\wms.xml");
	   DOMReader dom = new DOMReader(fileIs, false);
	   //  Get to TextureSymbolizer Elt
	   TextureSymbolizer sym = sldReader.readTexture(dom, dom.getRootElement());
   }
}
