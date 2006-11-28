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

package org.vast.stt.gui.widgets.catalog;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.ogc.cdm.common.DataComponent;
import org.vast.data.DataBlockBoolean;
import org.vast.data.DataBlockInt;
import org.vast.data.DataBlockString;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.io.xml.DOMReader;
import org.vast.io.xml.DOMReaderException;
import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.OWSServiceCapabilities;
import org.vast.ows.sld.SLDReader;
import org.vast.ows.sld.TextureSymbolizer;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.util.Bbox;
import org.vast.ows.wcs.WCSLayerCapabilities;
import org.vast.ows.wfs.WFSLayerCapabilities;
import org.vast.ows.wms.WMSCapabilitiesReader;
import org.vast.ows.wms.WMSLayerCapabilities;
import org.vast.process.DataProcess;
import org.vast.process.ProcessChain;
import org.vast.process.ProcessException;
import org.vast.sensorML.SMLException;
import org.vast.sensorML.reader.ProcessLoader;
import org.vast.sensorML.reader.ProcessReader;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.process.WMS_Process;
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

/**
 * <p><b>Title:</b>
 *  SceneTreeDropListener
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Performs Drops onto the SceneTree
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Nov 2006
 * @version 1.0
 */

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
	   TreeViewer vwr = (TreeViewer)this.getViewer();
	   //  If target is a folder, drop into folder and open it
	   if(dropTarget instanceof DataFolder) {
		   ((DataFolder)dropTarget).add(item);
		   item.dispatchEvent(new STTEvent(item, EventType.SCENE_TREE_CHANGED));
//		   vwr.reveal(item);  //  vwr.reveal() does not work as advertised in Eclipse 3.1!!!
		   vwr.expandToLevel(dropTarget, 2);  //  ensure dropped item is visible
		   vwr.refresh();
		   return true;
	   }
	   DataTree tree = null;
	   //  If target is scene (Un-expanded tree), drop at root of Tree and expand
	   if(dropTarget instanceof WorldScene){
		   tree = ((WorldScene)dropTarget).getDataTree();
		   tree.add(0, item);
		   item.dispatchEvent(new STTEvent(item, EventType.SCENE_TREE_CHANGED));
		   vwr.expandToLevel(2);   //  ensure dropped item is visible
		   vwr.refresh();
		   return true;
	   }
	   
	   //  If we get here, WorldScene MUST be expandedElements[0];
	   Object [] elems = vwr.getExpandedElements();
	   WorldScene scene = (WorldScene)elems[0];
	   tree = scene.getDataTree();
	   
	   //  Iterate the DataTree to find the target item,
	   //  and insert it there, then refresh
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
	   vwr.refresh();
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
   
   //  TODO  Hardwired for WMS now- generalize
   protected DataProvider createSensorMLProvider(OWSLayerCapabilities caps){
	  SMLProvider prov = new SMLProvider();
	  ProcessChain process = null;
	  WMS_Process wmsProc = null;
	  
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
    	  process = (ProcessChain)processReader.readProcess(dom.getBaseElement());
    	  
    	  //  Bold assumptions into processChain structure...
    	  WMSLayerCapabilities wmsCaps = (WMSLayerCapabilities)caps;
    	  OWSServiceCapabilities owsCaps = wmsCaps.getParent();
    	  wmsProc = (WMS_Process)process.getProcessList().get(0);
    	  DataGroup wmsParams = (DataGroup)wmsProc.getParameterList();
    	  DataGroup wmsOptions = (DataGroup)wmsParams.getComponent(0);
    	  //  Options
    	  //  TODO add some convenience methods and break this all out to separate class
    	  //  service endPt
    	  DataValue endPt = (DataValue)wmsOptions.getComponent("endPoint");
    	  DataBlockString dbs = new DataBlockString(1);
    	  //  Use 1st get Server for now
    	  Map serversMap = owsCaps.getGetServers();
    	  String getMapUrl = (String)serversMap.get("GetMap");
    	  dbs.setStringValue(getMapUrl);
    	  endPt.setData(dbs);
    	  //  layer
    	  DataValue layerDV = (DataValue)wmsOptions.getComponent("layer");
    	  String layerStr = wmsCaps.getName();
    	  dbs = new DataBlockString(1);
    	  dbs.setStringValue(layerStr);
    	  layerDV.setData(dbs);
    	  // format
    	  DataValue formatDV = (DataValue)wmsOptions.getComponent("format");
    	  List<String> formatList = wmsCaps.getFormatList();
    	  String formatStr;
    	  if(formatList.contains("image/png"))
    		  formatStr = "image/png";
    	  else
    		  formatStr = formatList.get(0);
    	  dbs = new DataBlockString(1);
    	  dbs.setStringValue(formatStr);
    	  formatDV.setData(dbs);
    	  // version- comes from Servers.xml, leave as default
    	  //  imageW & H -0 default to 512x512
    	  DataValue widthDV = (DataValue)wmsOptions.getComponent("imageWidth");
    	  DataBlockInt dbi = new DataBlockInt(1);
    	  dbi.setIntValue(512);
    	  widthDV.setData(dbi);
    	  DataValue heightDV = (DataValue)wmsOptions.getComponent("imageHeight");
    	  dbi = new DataBlockInt(1);
    	  dbi.setIntValue(512);
    	  heightDV.setData(dbi);
    	  // trasnparency
    	  DataValue transDV = (DataValue)wmsOptions.getComponent("imageTransparency");
    	  DataBlockBoolean dbb = new DataBlockBoolean(1);
    	  dbb.setBooleanValue(wmsCaps.isOpaque());
    	  transDV.setData(dbb);
    	  //  SRS - no provision to set 
    	  List<String> srsList = wmsCaps.getSrsList();
    	  dbs = new DataBlockString(1);
    	  if(srsList.contains("EPSG:4326") || srsList.size() == 0)
    		  dbs.setStringValue("EPSG:4326");
    	  else
    		  dbs.setStringValue(srsList.get(0));
    	  DataValue srsDV = (DataValue)wmsOptions.getComponent("srs");
    	  srsDV.setData(dbs);
    	  
    	  //  Styles
    	  List<String> stylesList = wmsCaps.getStyleList();
    	  dbs = new DataBlockString(1);
    	  if(stylesList != null && stylesList.size()>0 ) {
    		  dbs.setStringValue(stylesList.get(0));
	    	  DataValue stylesDV = (DataValue)wmsOptions.getComponent("styles");
	    	  stylesDV.setData(dbs);
    	  }
    	  
    	  // set bbox input values (override these with caps vals?
    	  List<Bbox>bboxList = wmsCaps.getBboxList();
    	  STTSpatialExtent ext = new STTSpatialExtent();
    	  Bbox bbox = new Bbox();
    	  if(bboxList != null || bboxList.size()>0) {
    		  bbox = bboxList.get(0);
	    	  ext.setMinX(bbox.getMinX());
	    	  ext.setMaxX(bbox.getMaxX());
	    	  ext.setMinY(bbox.getMinY());
	    	  ext.setMaxY(bbox.getMaxY());
//	    	  ext.setMinX(-74.220226);
//	    	  ext.setMaxX(-74.073766);
//	    	  ext.setMinY(40.630227);
//	    	  ext.setMaxY(40.741717);
	    	  prov.setSpatialExtent(ext);
    	  }
    	  // intitialize process with new params
    	  process.init();
    	  
	      
      } catch (ProcessException e){
    	  e.printStackTrace(System.err);
      } catch (SMLException smlEx) {
	      smlEx.printStackTrace(System.err);
      } catch (DOMReaderException domEx) {
    	  domEx.printStackTrace(System.err);
      }	  	  
      
      prov.setProcess(process);
      System.err.println(wmsProc);
      
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
