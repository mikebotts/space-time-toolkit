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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.OWSCapabilitiesReader;
import org.vast.ows.OWSException;
import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.OWSServiceCapabilities;
import org.vast.ows.sos.SOSCapabilitiesReader;
import org.vast.ows.wcs.WCSCapabilitiesReader;
import org.vast.ows.wfs.WFSCapabilitiesReader;
import org.vast.ows.wms.WMSCapabilitiesReader;

/**
 * <p><b>Title:</b>
 *  Capabilities Widget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Widget for selecting Caps Servers from a user-defined list, reading
 *  caps docs from those servers, and loading available layers into a 
 *  treeViewer.  Layers may be dragged from the TreeViewer to the SceneTreeView.
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class CapabilitiesWidget implements SelectionListener 
{
	CapServers capServers;
	Composite mainGroup;
	private Button getCapsBtn;
	private Combo typesCombo;
	private Combo serverCombo;
	private LayerTree layerTree;
	
	public CapabilitiesWidget(Composite parent) {
		capServers = new CapServers();
		capServers.loadServerData();
		initGui(parent);
	}

	public void initGui(Composite parent){
		GridData gd;
		
		mainGroup = new Composite(parent, 0x0);
		mainGroup.setLayout(new GridLayout());
		
		Group topGroup = new Group(mainGroup, 0x0);
		topGroup.setLayout(new GridLayout(2, false));
		topGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false ));
		topGroup.setText("Servers");

		//  ServerType
		Label typesLabel = new Label(topGroup, SWT.LEFT);  
		typesLabel.setText("Service Types:");
		typesCombo = new Combo(topGroup, SWT.READ_ONLY);
		typesCombo.setItems(capServers.getServiceTypes());
		//typesCombo.setItems(new String [] {"WMS", "WCS", "WFS", "SOS"});
		gd = new GridData(SWT.LEFT, SWT.FILL, false, true);
		typesCombo.setLayoutData(gd);
		typesCombo.select(3);
		typesCombo.addSelectionListener(this);
		
		//  Server  
		Label serverLabel = new Label(topGroup, SWT.LEFT);
		serverLabel.setText("Server:");
		serverCombo = new Combo(topGroup, SWT.READ_ONLY);
		serverCombo.setItems(new String [] {"AAA", "bbb"});
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		serverCombo.setLayoutData(gd);
		setServerComboItems();
		serverCombo.addSelectionListener(this);
		
		//  Edit/GetCaps btns
		//  NOTE that I had to put in a new composite to get buttons right aligned
		Composite btnComp = new Composite(topGroup, 0x0);
		btnComp.setLayout(new GridLayout(2, false));
		gd = new GridData(SWT.END, SWT.CENTER, false, false);
		gd.horizontalSpan = 2;
		btnComp.setLayoutData(gd);
		
		Button editBtn = new Button(btnComp, SWT.PUSH);
		editBtn.setText("Edit");
		editBtn.setToolTipText("Edit Capabilities Server List");
		gd = new GridData(SWT.END, SWT.CENTER, false, false);
		gd.widthHint = 60;
		editBtn.setLayoutData(gd);
		editBtn.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				new EditCapServerDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), capServers);
			}
			
			public void widgetDefaultSelected(SelectionEvent e){};
		});
		
		getCapsBtn = new Button(btnComp, SWT.PUSH);
		getCapsBtn.setText("Get Caps");
		getCapsBtn.setToolTipText("Get Capabilities from selected Server");
		gd = new GridData(SWT.END, SWT.CENTER, false, false);
		gd.widthHint = 60;
		getCapsBtn.setLayoutData(gd);
		getCapsBtn.addSelectionListener(this);
		
		layerTree = new LayerTree(mainGroup);
		layerTree.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));		
	}

	//  repopulate serverCombo with selected type's servers 
	//  and select the 0th entry
	protected void setServerComboItems(){
		String type = typesCombo.getText();
		String [] types = capServers.getServers(type);
		//  types can be null still, so check it
		if(types == null)
			types =  new String[]{};
		serverCombo.setItems(types);
		serverCombo.select(0);
	}

	private class GetCapsRunnable implements IRunnableWithProgress {
		
		String server;
		ServiceType type;
		List<OWSLayerCapabilities> caps;
		
		public GetCapsRunnable(String server, ServiceType type){
			this.server = server;
			this.type = type;
		}
		
		public void run(IProgressMonitor monitor) 
			throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Attempting to read Capabilities Document from\n" + server + "...", 
					IProgressMonitor.UNKNOWN);
				caps = readCapabilities(server, type);

		};
		
		public List<OWSLayerCapabilities> getLayerCaps(){
			return caps;
		}
	}
	
	protected void getCapabilities(String server, ServiceType type){
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(
				PlatformUI.getWorkbench().getDisplay().getActiveShell());
		
		GetCapsRunnable runnable = new GetCapsRunnable(server, type);
		
		try {
			pmd.run(true, false, runnable);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<OWSLayerCapabilities> caps = runnable.getLayerCaps();
		if(caps != null)
			layerTree.setInput(caps);
		else {
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
            		"STT Error", "Error reading caps from " + server);
		}
	}
		
	/**
	 *  get the Capabilities from the currently selected server
	 *  TODO:  spawn another thread for this and add Prog Bars
	 */
	//protected void getCapabilities(String server, ServiceType type, int j){
	protected List<OWSLayerCapabilities> readCapabilities(String server, ServiceType type){
		ServerInfo info = capServers.getServerInfo(server, type);
		OWSCapabilitiesReader reader;
		switch(type){
		case WMS:
			reader = new WMSCapabilitiesReader();
			break;
		case WCS:
			reader = new WCSCapabilitiesReader();
			break;
		case WFS:
			reader = new WFSCapabilitiesReader();
			break;
		case SOS:
			reader = new SOSCapabilitiesReader();
			break;
		default:
			return null;
		}
		OWSServiceCapabilities caps = null;
		try {		
			caps = reader.readCapabilities(info.url, info.version);
		} catch (OWSException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
		return caps.getLayers();
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		
		if(control == getCapsBtn){
			String server = serverCombo.getText();
			ServiceType type = ServiceType.getServiceType(typesCombo.getText());
			getCapabilities(server, type);
		} else if(control == typesCombo) {
			//  repopulate servers with selected type
			setServerComboItems();
		} else if(control == serverCombo) {
			//  do nothing
		}
	}
}
