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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.PlatformUI;
import org.vast.data.DataBlockString;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.OWSServiceCapabilities;
import org.vast.ows.sld.SLDReader;
import org.vast.ows.sld.TextureSymbolizer;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.util.Bbox;
import org.vast.ows.wcs.WCSLayerCapabilities;
import org.vast.ows.wfs.WFSLayerCapabilities;
import org.vast.ows.wms.WMSLayerCapabilities;
import org.vast.process.DataProcess;
import org.vast.process.ProcessChain;
import org.vast.process.ProcessException;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.dataProvider.WMSProcessOptions;
import org.vast.stt.process.SOS_Process;
import org.vast.stt.process.WMS_Process;
import org.vast.stt.project.tree.DataEntry;
import org.vast.stt.project.tree.DataFolder;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.tree.DataTree;
import org.vast.stt.project.tree.DataTreeReader;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.util.ExceptionSystem;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;

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

public class SceneTreeDropListener extends ViewerDropAdapter {
	
	public SceneTreeDropListener(StructuredViewer viewer) {
		super(viewer);
	}

	/**
	 *  This is called when a Layer from LayerTree in CapabilitiesView 
	 *  is dropped onto the SceneTree.  The data is an instance of 
	 *  OWSLayerCapabilities
	 */
	public boolean performDrop(Object data) {
		OWSLayerCapabilities caps = (OWSLayerCapabilities) data;

		DataItem newItem = new DataItem(); // use world item for stuffs to be rendered in the world view
		newItem.setName(caps.getName());

		if (data instanceof SOSLayerCapabilities) {
			startAddItemWizard((SOSLayerCapabilities)caps);
			//DataItem [] items = createSOSItem((SOSLayerCapabilities)caps);
			//			for(int i=0; i<items.length; i++) {
//				//  Popup addItem wizard BEFORE drop occurs
//				dropItem(items[i]);
//			}
			return true;
		} else if (data instanceof WMSLayerCapabilities) {
			DataItem item = createWMSItem((WMSLayerCapabilities)caps);
			return dropItem(item);
		} else if (data instanceof WCSLayerCapabilities) {
			System.err.println("Add WCS drop support");
			return false;
		} else if (data instanceof WFSLayerCapabilities) {
			System.err.println("Add WFS drop support");
			return false;
		} else {
			System.err
					.println("SceneTreeDropListener.performDrop():  Unknown Caps type.");
			return false;
		}
	}

	public void startAddItemWizard(SOSLayerCapabilities caps){
		AddSOSItemWizard addItemWizard = new AddSOSItemWizard(caps);
		addItemWizard.init(PlatformUI.getWorkbench(), null);
				
		// Instantiates the wizard container with the wizard and opens it
		WizardDialog dialog = 
			new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), addItemWizard);
		dialog.create();
		dialog.open();
	}
	
	
	/**
	 * Method declared on ViewerDropAdapter
	 */
	public boolean validateDrop(Object target, int op, TransferData type) {
		boolean dropOk = LayerTransfer.getInstance().isSupportedType(type);
		dropOk = dropOk && (target instanceof DataEntry);
//		return dropOk;
		return dropOk || true;
	}

	protected boolean dropItem(DataItem item) {
		if(item.getName()==null){
			System.err.println("DataItem name is null in SceneTreeDropListner.  Should not have gotten here.");
			return false;
		}
		
		DataEntry dropTarget = (DataEntry) this.getCurrentTarget();
		TreeViewer vwr = (TreeViewer) this.getViewer();
		//  If target is a folder, drop into folder and open it
		if (dropTarget instanceof DataFolder) {
			((DataFolder) dropTarget).add(item);
			item.dispatchEvent(new STTEvent(item, EventType.SCENE_TREE_CHANGED));
			//		   vwr.reveal(item);  //  vwr.reveal() does not work as advertised in Eclipse 3.1!!!
			vwr.expandToLevel(dropTarget, 2); //  ensure dropped item is visible
			vwr.refresh();
			return true;
		}
		DataTree tree = null;
		//  If target is scene (Un-expanded tree), drop at root of Tree and expand
		if (dropTarget instanceof WorldScene) {
			tree = ((WorldScene) dropTarget).getDataTree();
			tree.add(0, item);
			item.dispatchEvent(new STTEvent(item, EventType.SCENE_TREE_CHANGED));
			vwr.expandToLevel(2); //  ensure dropped item is visible
			vwr.refresh();
			return true;
		}

		//  If we get here, WorldScene MUST be expandedElements[0];
		Object[] elems = vwr.getExpandedElements();
		WorldScene scene = (WorldScene) elems[0];
		tree = scene.getDataTree();

		//  Iterate the DataTree to find the target item,
		//  and insert it there, then refresh
		DataEntry entryTmp = null;
		Iterator<DataEntry> it = tree.iterator();
		boolean done = false;
		int index = 0;
		while (it.hasNext() && !done) {
			entryTmp = it.next();
			if (entryTmp instanceof DataFolder) {
				done = recurseFolder((DataFolder) entryTmp,
						(DataItem) dropTarget, item);
			} else {
				if (entryTmp == dropTarget) {
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

	protected boolean recurseFolder(DataFolder folder, DataItem dropTarget,
			DataItem newItem) {
		Iterator<DataEntry> it = folder.iterator();
		DataEntry entryTmp;
		int index = 0;
		while (it.hasNext()) {
			entryTmp = it.next();
			if (entryTmp instanceof DataFolder) {
				recurseFolder((DataFolder) entryTmp, dropTarget, newItem);
			} else if (entryTmp == dropTarget) {
				folder.add(index, newItem);
				return true;
			}
			index++;
		}
		return false;
	}
	
	public DataItem[] createSOSItem(SOSLayerCapabilities caps){
		try {
			Enumeration e;
			String templateName = "CSIRO_gatton-HUMIDITY_TEMP.xml";
			String capsServer = caps.getParent().getGetServers().get("GetCapabilities");
			
			//  Hardcoded switch to hack what we're dropping- clean up later
			//  TODO:  How to unhack this?
			if(capsServer.contains("muenster"))
				templateName = "IFGI_WeatherNY.xml";
			else
				templateName = "CSIRO_gatton-HUMIDITY_TEMP.xml";
			e = STTPlugin.getDefault().getBundle().findEntries(
					"templates", templateName, false);
			String fileLocation = null;
			if (e.hasMoreElements())
				fileLocation = (String) e.nextElement().toString();

			if (fileLocation == null) {
				ExceptionSystem.display(new Exception(
						"STT error: Cannot find template\\" + templateName));
				return null;
			}

            DOMHelper dom = new DOMHelper(fileLocation, false);
			DataTreeReader dataReader = new DataTreeReader();
//			DataItem tmpItem = (DataItem)dataReader.readDataEntry(dom, dom.getRootElement());
			List<String> procs = caps.getProcedureList();
			int numItems = procs.size();
			DataItem [] items = new DataItem[numItems];
			for(int i=0; i<numItems;i++) {
				//  No way to clone items currently.  Reread template file each time, for now
				items[i] = (DataItem)dataReader.readDataEntry(dom, dom.getRootElement());
				//  Override name from template
				items[i].setName(caps.getId());
				setSOSProcedure(items[i], procs.get(i));
			}
			
			return items;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public void setSOSProcedure(DataItem item, String procedure){
		SMLProvider provider = (SMLProvider)item.getDataProvider();
		DataProcess process = provider.getProcess();
		SOS_Process sosProc = (SOS_Process) process;
		DataGroup sosParams = (DataGroup) sosProc.getParameterList();
		DataGroup sosOptions = (DataGroup) sosParams.getComponent(0);
		//  Options
		//  TODO add some convenience methods and break this all out to separate class
		DataValue procDv = (DataValue) sosOptions.getComponent("procedures");
		DataBlockString dbs = new DataBlockString(1);
		dbs.setStringValue(procedure);
		procDv.setData(dbs);
		item.setName(item.getName() + "_" + procedure);
	}
	
	public DataItem createWMSItem(WMSLayerCapabilities caps){
		try {
			String fileLocation = null;
			Enumeration e = STTPlugin.getDefault().getBundle().findEntries(
					"templates", "WMS_FlatGrid_Template.xml", false);
			if (e.hasMoreElements())
				fileLocation = (String) e.nextElement().toString();

			if (fileLocation == null) {
				ExceptionSystem.display(new Exception(
						"STT error: Cannot find template\\wms.xml"));
				return null;
			}

            DOMHelper dom = new DOMHelper(fileLocation, false);
			DataTreeReader dataReader = new DataTreeReader();
			DataItem worldItem = (DataItem)dataReader.readDataEntry(dom, dom.getRootElement());
			worldItem.setName(caps.getId());
			SMLProvider provider = (SMLProvider)worldItem.getDataProvider();
			//  load default fields from caps into SensorMLProvider 
			loadWMSProcess(provider, caps);
			
			return worldItem;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	//  TODO  Hardwired for WMS now- generalize
	protected void loadWMSProcess(SMLProvider provider, OWSLayerCapabilities caps) {
		ProcessChain process = null;
		WMS_Process wmsProc = null;
		WMSProcessOptions wmsOptions;
		
		try {
			process = (ProcessChain)provider.getProcess();
			
			//  Bold assumptions into processChain structure...
			WMSLayerCapabilities wmsCaps = (WMSLayerCapabilities) caps;
			OWSServiceCapabilities owsCaps = wmsCaps.getParent();
			wmsProc = (WMS_Process) process.getProcessList().get(0);
			wmsOptions = new WMSProcessOptions(wmsProc);
			//  Options
			//  Use 1st get Server for now
			Map serversMap = owsCaps.getGetServers();
			String getMapUrl = (String) serversMap.get("GetMap");
			wmsOptions.setEndpoint(getMapUrl);
			String layerStr = wmsCaps.getId();
			wmsOptions.setLayer(layerStr);
			// format
			List<String> formatList = wmsCaps.getFormatList();
			String formatStr;
			//  gif and png are not working properly
			if (formatList.contains("image/jpeg"))
				formatStr = "image/jpeg";
			else
				formatStr = formatList.get(0);
			wmsOptions.setFormat(formatStr);
			wmsOptions.setTransparency(wmsCaps.isOpaque());
			List<String> srsList = wmsCaps.getSrsList();
			String srs;
			if (srsList.contains("EPSG:4326") || srsList.size() == 0)
				srs = "EPSG:4326";
			else
				srs = srsList.get(0);
			wmsOptions.setSRS(srs);
			List<String> stylesList = wmsCaps.getStyleList();
			if (stylesList != null && stylesList.size() > 0) 
				wmsOptions.setStyle(stylesList.get(0));

			// set bbox input values (override these with caps vals?
			List<Bbox> bboxList = wmsCaps.getBboxList();
			STTSpatialExtent ext = new STTSpatialExtent();
			Bbox bbox = new Bbox();
			if (bboxList != null || bboxList.size() > 0) {
				bbox = bboxList.get(0);
				ext.setMinX(bbox.getMinX());
				ext.setMaxX(bbox.getMaxX());
				ext.setMinY(bbox.getMinY());
				ext.setMaxY(bbox.getMaxY());
				provider.setSpatialExtent(ext);
			}
			// intitialize process with new params
			process.init();

		} catch (ProcessException e) {
			e.printStackTrace(System.err);
		} 
		System.err.println(wmsProc);
	}


	public static void main(String[] args) throws IOException,
    DOMHelperException {
		SLDReader sldReader = new SLDReader();
		InputStream fileIs = new FileInputStream(
				"C:\\tcook\\work\\STT3\\templates\\wms.xml");
        DOMHelper dom = new DOMHelper(fileIs, false);
		//  Get to TextureSymbolizer Elt
		TextureSymbolizer sym = sldReader
				.readTexture(dom, dom.getRootElement());
	}
}
