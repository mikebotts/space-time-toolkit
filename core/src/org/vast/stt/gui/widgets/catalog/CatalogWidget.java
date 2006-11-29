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
import org.vast.ows.util.Bbox;
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
 *    TODO make this and CapWidget extend ScrolledComp (see AdvGeomTab)
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
	private Text minXText;
	private Text maxXText;
	private Text minYText;
	private Text maxYText;
	
	private static final int NO_BBOX_SET = 0;
	private static final int BBOX_ERROR = -1;
	private static final int BBOX_OK = 1;
	
	public CatalogWidget(Composite parent, int style) {
		initGui(parent);
	}

	public void initGui(Composite parent){
		GridData gd;
		
		mainGroup = new Composite(parent, 0x0);
		mainGroup.setLayout(new GridLayout(1, false));
		mainGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false ));
		
		Group optionGroup = new Group(mainGroup, SWT.BORDER);
		GridLayout optLayout = new GridLayout(4, false);
		optLayout.verticalSpacing = 10;
		optionGroup.setLayout(optLayout);
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
		gd.horizontalSpan = 3;
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
		Text kwText = new Text(optionGroup, SWT.BORDER | SWT.LEFT);
		gd = new GridData(SWT.FILL,SWT.CENTER, true, false);
		gd.horizontalSpan = 3;
		kwText.setLayoutData(gd);
		kwText.setToolTipText("Space-Separated keyowrds");
		
		//  Provder (Text)
		
		//  ROI (text)
		//  TODO enforce numeric entry on ROI Text widgets
		Label minXLabel = new Label(optionGroup, SWT.LEFT);
		minXLabel.setText("minX:");
		gd = new GridData(SWT.RIGHT,SWT.CENTER, false, false);
		minXLabel.setLayoutData(gd);
		minXText = new Text(optionGroup, SWT.BORDER | SWT.LEFT);
		minXText.setTextLimit(15);
		gd = new GridData(SWT.LEFT,SWT.CENTER, false, false);
		gd.widthHint = 45;
		minXText.setLayoutData(gd);
		
		Label maxXLabel = new Label(optionGroup, SWT.LEFT);
		maxXLabel.setText("maxX:");
		maxXText = new Text(optionGroup, SWT.BORDER | SWT.LEFT);
		maxXText.setTextLimit(15);
		gd = new GridData(SWT.LEFT,SWT.CENTER, false, false);
		gd.widthHint = 45;
		maxXText.setLayoutData(gd);
		
		Label minYLabel = new Label(optionGroup, SWT.LEFT);
		minYLabel.setText("minY:");
		gd = new GridData(SWT.RIGHT,SWT.CENTER, false, false);
		minYLabel.setLayoutData(gd);
		minYText = new Text(optionGroup, SWT.BORDER | SWT.LEFT);
		minYText.setTextLimit(15);
		gd = new GridData(SWT.LEFT,SWT.CENTER, false, false);
		gd.widthHint = 45;
		minYText.setLayoutData(gd);
		
		Label maxYLabel = new Label(optionGroup, SWT.LEFT);
		maxYLabel.setText("maxY:");
		maxYText = new Text(optionGroup, SWT.BORDER | SWT.LEFT);
		maxYText.setTextLimit(15);
		gd = new GridData(SWT.LEFT,SWT.CENTER, false, false);
		gd.widthHint = 45;
		maxYText.setLayoutData(gd);
		
		//  Submit Btn (and potentially edit btn to add new Catalog)
		Button submitBtn = new Button(optionGroup, SWT.PUSH);
		submitBtn.setText("Submit");
		gd = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 4, 1);
		submitBtn.setLayoutData(gd);
		submitBtn.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				WRSQuery query = buildQuery();
				if(query != null)
					searchCatalog(query);
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		
		//  Layer Tree
		layerTree = new LayerTree(mainGroup);
		layerTree.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));		
	}	

	private WRSQuery buildQuery(){
		WRSQuery query = new WRSQuery();
		query.setPostServer("http://dev.ionicsoft.com:8082/ows4catalog/wrs/WRS");
		query.setVersion("2.0.0");
		Bbox bbox = query.getBbox();
		int bboxStatus = loadQueryBbox(query);
		if(bboxStatus == BBOX_ERROR)
			return null;
		
		return query;
	}
	
	private List<OWSLayerCapabilities> submitCatalogQuery(WRSQuery query){
		WRSRequestWriter wrsRW = new WRSRequestWriter();
		
		InputStream is = null;
		List<String>sosUri = null;
		try {
			wrsRW.showPostOutput = true;
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
		List<OWSLayerCapabilities> layerCaps;
		WRSQuery query;

		public SearchCatalogRunnable(WRSQuery query){
			this.query = query;
		}
		
		public void run(IProgressMonitor monitor) 
			throws InvocationTargetException, InterruptedException {
				String msg = "Attempting to read Catalog at: " + query.getPostServer();
				monitor.beginTask(msg, IProgressMonitor.UNKNOWN);
				layerCaps = submitCatalogQuery(query);
		};
		
		public List<OWSLayerCapabilities> getLayerCaps(){
			return layerCaps;
		}
	}
	
	protected void searchCatalog(WRSQuery query) {
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(
				PlatformUI.getWorkbench().getDisplay().getActiveShell());
		
		SearchCatalogRunnable runnable = new SearchCatalogRunnable(query);
		//  Load Bbox, keywords...
		
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
            		"STT Error", "Error reading Catalog from " + query.getPostServer());
		}
	}
	
	private int loadQueryBbox(WRSQuery query){
		try {
			String minXstr = minXText.getText().trim();
			String maxXstr = maxXText.getText().trim();
			String minYstr = minYText.getText().trim();
			String maxYstr = maxYText.getText().trim();
			if(minXstr.length() == 0 && minYstr.length() == 0 &&
			   maxXstr.length() == 0 && maxYstr.length() == 0) {
				return NO_BBOX_SET;
			}
			double minX = Double.parseDouble(minXstr);
			double maxX = Double.parseDouble(minXstr);
			double minY = Double.parseDouble(minXstr);
			double maxY = Double.parseDouble(minXstr);
			Bbox bbox = new Bbox();
			bbox.setMinX(minX);
			bbox.setMaxX(maxX);
			bbox.setMinX(minY);
			bbox.setMaxY(maxY);
			query.setBbox(bbox);
			return BBOX_OK;
		} catch (Exception e) {
			//  TODO hilite incorrect fields
			 MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					 "STT Error", "BBox is invalid. Correct and resubmit.");
			 return BBOX_ERROR;
		}
	}
	
	
}	

