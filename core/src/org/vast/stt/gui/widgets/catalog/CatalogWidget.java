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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.vast.io.xml.DOMReaderException;
import org.vast.ows.OWSException;
import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.OWSServiceCapabilities;
import org.vast.ows.sos.SOSCapabilitiesReader;
import org.vast.ows.wrs.WRSQuery;
import org.vast.ows.wrs.WRSRequestWriter;
import org.vast.ows.wrs.WRSResponseReader;

/**
 * <p><b>Title:</b>
 *   Catalog Widget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *    Allow user to search OGC Catalog by keyword, service type, roi, or provider organization
 *    TODO ADD borders to all combos and text fields
 *    TODO add time
 *    TODO add queries for other service types (only SOS for OWS4 demo)
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class CatalogWidget  
{
	Composite mainGroup;
	//  TODO Add Other Catalog servers, possibly
	String [] catServers = {
//			"http://dev.ionicsoft.com:8082/ows4catalog/wrs/WRS"
			"Ionic"
	};
	LayerTree layerTree;
	
	public CatalogWidget(Composite parent, int style) {
		initGui(parent);
	}

	public void initGui(Composite parent){
		GridData gd;
		
		mainGroup = new Composite(parent, 0x0);
		mainGroup.setLayout(new GridLayout(1, false));
		mainGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false ));
		
		Group optionGroup = new Group(mainGroup, SWT.BORDER);
		optionGroup.setLayout(new GridLayout(2, false));
		optionGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false ));
		optionGroup.setText("Serarch Options");

		//  Servers (Only one initially, but still making it a combo)
		Label serverLabel = new Label(optionGroup, SWT.LEFT);  
		serverLabel.setText("Server:");
		Combo serverCombo = new Combo(optionGroup, SWT.BORDER | SWT.READ_ONLY);
		serverCombo.setTextLimit(20);
		serverCombo.setItems(catServers);
		//typesCombo.setItems(new String [] {"WMS", "WCS", "WFS", "SOS"});
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		serverCombo.setLayoutData(gd);
		serverCombo.select(0);
		serverCombo.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		//  Search Criteria
		//  Service Type (Combo)
		
		//  Keyword (Text)
		Label kwLabel = new Label(optionGroup, SWT.LEFT);
		kwLabel.setText("Keywords:");
		kwLabel.setToolTipText("Space-Separated keyowrds");
		Text kwText = new Text(optionGroup, SWT.BORDER | SWT.LEFT);
		gd = new GridData(SWT.FILL,SWT.CENTER, true, false);
		//gd.minimumWidth = 100;
		kwText.setLayoutData(gd);
		
		//  Provder (Text)
		
		//  ROI (text)
		
		//  Submit Btn (and potentially edit btn to add new Catalog)
		Button submitBtn = new Button(optionGroup, SWT.PUSH);
		submitBtn.setText("Submit");
		gd = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1);
		submitBtn.setLayoutData(gd);
		submitBtn.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				searchCatalog("http://dev.ionicsoft.com:8082/ows4catalog/wrs/WRS", "2.0.0");
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		
		//  Layer Tree
		layerTree = new LayerTree(mainGroup);
		layerTree.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));		
	}	

	private List<OWSLayerCapabilities> submitCatalogQuery(String server, String version){
		WRSRequestWriter wrsRW = new WRSRequestWriter();
		WRSQuery query = new WRSQuery();
		query.setPostServer(server);
		query.setVersion(version);
		InputStream is = null;
		List<String>sosUri = null;
		try {
			is = wrsRW.sendRequest(query, true).getInputStream();
			WRSResponseReader wrsReader = new WRSResponseReader();
			sosUri = wrsReader.parseAllSOS(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMReaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(sosUri == null || sosUri.isEmpty()) {
			 return null;
		}
		//  Cycle through SOS's and get all ObsOfferings
		SOSCapabilitiesReader capsReader = new SOSCapabilitiesReader();
		List<OWSLayerCapabilities> layerCaps = new ArrayList<OWSLayerCapabilities>();
		for(String sosTmp : sosUri){
			try {
				//  Not sure how to detect version prior to this call...
				OWSServiceCapabilities caps = capsReader.readCapabilities(sosTmp, "0.0.31");
				List<OWSLayerCapabilities> capsTmp = caps.getLayers();
				layerCaps.addAll(capsTmp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return layerCaps;
	}
	
	private class SearchCatalogRunnable implements IRunnableWithProgress {
		String server;
		String version;
		List<OWSLayerCapabilities> layerCaps;
		

		public SearchCatalogRunnable(String server, String version){
			this.server = server;
			this.version = version;
		}
		
		public void run(IProgressMonitor monitor) 
			throws InvocationTargetException, InterruptedException {
				String msg = "Attempting to read Catalog at: " + server;
				monitor.beginTask(msg, IProgressMonitor.UNKNOWN);
				layerCaps = submitCatalogQuery(server, version);
		};
		
		public List<OWSLayerCapabilities> getLayerCaps(){
			return layerCaps;
		}
	}
	
	protected void searchCatalog(String server, String version) {
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(
				PlatformUI.getWorkbench().getDisplay().getActiveShell());
		
		SearchCatalogRunnable runnable = new SearchCatalogRunnable(server, version);
		
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
            		"STT Error", "Error reading Catalog from " + server);
		}
	}
}	

