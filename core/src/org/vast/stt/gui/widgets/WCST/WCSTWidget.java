/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.widgets.WCST;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.OWSReference;
import org.vast.ows.OWSUtils;
import org.vast.ows.wcs.CoverageRefGroup;
import org.vast.ows.wcst.CoverageTransaction;
import org.vast.ows.wcst.WCSTransactionRequest;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.STTSpatialExtent;


/**
 * <p><b>Title:</b>
 *  WCS-T Widget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Widget for Submitting an image to a WCS-T server.
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Tony Cook
 * @date Mar 17, 2008
 * @version 1.0
 */

public class WCSTWidget implements SelectionListener
{
	WorldScene scene;
	DataItem dataItem;
	private Group mainGroup; 
	String [] servers;
	Text nlatText, slatText, wlonText, elonText;
	Combo formatCombo;
	private Button submitBtn;
	private Text idText;
	private Combo serverCombo;
	
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
	}

	public WCSTWidget(Composite parent)
	{
		initServers();
		initGui(parent);
	}

	public void initGui(Composite parent)
	{
		//  Scroller
		final ScrolledComposite scroller = 
			new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		
		scroller.setExpandVertical(true);
		scroller.setExpandHorizontal(true);
		scroller.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

	    mainGroup = new Group(scroller, 0x0);
		mainGroup.setText("itemName");
		scroller.setContent(mainGroup);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 10;
		layout.makeColumnsEqualWidth = true;
		mainGroup.setLayout(layout);
		
		Label idLabel = new Label(mainGroup, 0x0);
		idLabel.setText("Coverage ID: ");
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.END;
		gd.verticalAlignment = SWT.CENTER;
		idLabel.setLayoutData(gd);
		
		idText = new Text(mainGroup, 0x0);
		gd = new GridData();
		gd.horizontalAlignment = SWT.BEGINNING;
		gd.verticalAlignment = SWT.CENTER;
		idText.setLayoutData(gd);
		idText.setText("Coverage01");
		
		Label serverLabel = new Label(mainGroup, SWT.CENTER);
		serverLabel.setText("Server: ");
		gd = new GridData();
		gd.horizontalAlignment = SWT.END;
		gd.verticalAlignment = SWT.CENTER;
		serverLabel.setLayoutData(gd);
		serverCombo = new Combo(mainGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		serverCombo.addSelectionListener(this);
		serverCombo.add(servers[0]);
		serverCombo.add(servers[1]);
		serverCombo.select(0);
		gd = new GridData();
		gd.horizontalAlignment = SWT.BEGINNING;
		gd.verticalAlignment = SWT.CENTER;
		serverCombo.setLayoutData(gd);
//		
		Label selLabel = new Label(mainGroup, SWT.CENTER);
		selLabel.setText("Selection Method: ");
		gd = new GridData();
		gd.horizontalAlignment = SWT.END;
		gd.verticalAlignment = SWT.CENTER;
		selLabel.setLayoutData(gd);
		
		Combo selCombo  = new Combo(mainGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		selCombo.addSelectionListener(this);
		selCombo.add("View Bounds");
		selCombo.add("Compass");
		selCombo.select(0);
		gd = new GridData();
		gd.horizontalAlignment = SWT.BEGINNING;
		gd.verticalAlignment = SWT.CENTER;
		selCombo.setLayoutData(gd);
		
		//  BBox widget (active only if selection Method==Bbox
		createCompass(mainGroup);
		
	//  Submit Button
		submitBtn = new Button(mainGroup, SWT.PUSH);
		submitBtn.setText("Submit Request");
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		gd.horizontalSpan = 2;
		submitBtn.setLayoutData(gd);
		submitBtn.addSelectionListener(this);
		
	//  Set Default scroller size
		scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	//  Need to move this to its own class...
	private void createCompass(Composite parent){
	//  Create a Group for the compass Grid
		Composite compassGroup = new Composite(parent, SWT.SHADOW_NONE);
		compassGroup.setEnabled(false);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
        gridData.horizontalSpan = 2;
		compassGroup.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		compassGroup.setLayout(gridLayout);
		//  Create Buttons
		Group nlatGroup = new Group(compassGroup, 0x0);
		nlatGroup.setText("northLat");
		FillLayout fl = new FillLayout();
		fl.marginHeight = 4;
		fl.marginWidth = 4;
		nlatGroup.setLayout(fl);
		nlatText = new Text(nlatGroup, SWT.RIGHT);
		Group wlonGroup = new Group(compassGroup, 0x0);
		wlonGroup.setText("westLon");
		fl = new FillLayout();
		fl.marginHeight = 4;
		fl.marginWidth = 4;
		wlonGroup.setLayout(fl);
		wlonText = new Text(wlonGroup, SWT.RIGHT);
		Label compassBtn = new Label(compassGroup, SWT.SHADOW_IN);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.CENTER;
		gridData.horizontalAlignment = SWT.CENTER;
		compassBtn.setLayoutData(gridData);
		ImageDescriptor descriptor = STTPlugin.getImageDescriptor("icons/compass1.gif");
		Image compImg = descriptor.createImage();
		compassBtn.setImage(compImg);
		//compassBtn.setImage(Image)
		Group elonGroup = new Group(compassGroup, 0x0);
		elonGroup.setText("eastLon");
		fl = new FillLayout();
		fl.marginHeight = 4;
		fl.marginWidth = 4;
		elonGroup.setLayout(fl);
		elonText = new Text(elonGroup, SWT.RIGHT);
		Group slatGroup = new Group(compassGroup, 0x0);
		slatGroup.setText("southLat");
		fl = new FillLayout();
		fl.marginHeight = 4;
		fl.marginWidth = 4;
		slatGroup.setLayout(fl);
		slatText = new Text(slatGroup, SWT.RIGHT);
		//  restrict text fields to numeric input
//		wlonText.addKeyListener(this);
//		elonText.addKeyListener(this);
//		nlatText.addKeyListener(this);
//		slatText.addKeyListener(this);
//		
		//  Layout Btns
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 3;
		nlatGroup.setLayoutData(gridData);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		gridData.horizontalSpan = 1;
		gridData.verticalAlignment = SWT.CENTER;
		wlonGroup.setLayoutData(gridData);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		gridData.horizontalSpan = 1;
		gridData.verticalAlignment = SWT.CENTER;
		elonGroup.setLayoutData(gridData);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 3;
		slatGroup.setLayoutData(gridData);
		
		//  Format combo
		Composite formatGrp = new Composite(mainGroup, 0x0);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
        gridData.horizontalSpan = 2;
		formatGrp.setLayoutData(gridData);
		RowLayout formatLayout = new RowLayout(SWT.HORIZONTAL);
		formatGrp.setLayout(formatLayout);
		Label formatLbl = new Label(formatGrp, SWT.CENTER);
		formatLbl.setText("Format:");
		formatCombo = new Combo(formatGrp, SWT.DROP_DOWN | SWT.READ_ONLY);
		formatCombo.addSelectionListener(this);
		//  May add options for dd'mm"ss later
		formatCombo.add("degrees");
		formatCombo.add("radians");
		formatCombo.select(0);
	}


	public void setDataItem(DataItem item){
		this.dataItem = item;
		mainGroup.setText(item.getName());
		DataProvider prov = item.getDataProvider();
		//  If provider is null, this widget isn't supported.
		if(prov!=null) {
			STTSpatialExtent ext = prov.getSpatialExtent();
			this.setSpatialExtent(ext);
		}
	}
	
//  Use org.vast.stt.util.SpatialExtent, or Bbox here? 
	public void setSpatialExtent(STTSpatialExtent bbox){
		setValue(nlatText, bbox.getMaxY());
		setValue(slatText, bbox.getMinY());
		setValue(wlonText, bbox.getMinX());
		setValue(elonText, bbox.getMaxX());
	}
	
    
    public void setScene(WorldScene scene){
        this.scene = scene;
    }
	
	public void setValue(Text text, double d){
		String dubStr = Double.toString(d);
		text.setText(dubStr);
	}
	
	//  Obviously, we want something a little more elegant here
	private void initServers(){
		servers = new String[2];
		servers[0] = "PCI WCS-T";
		servers[1] = "SPOT WCS-T";
	}
	
	public void widgetSelected(SelectionEvent e)
	{
		Control control = (Control) e.getSource();

		if (control == submitBtn){
			System.err.println("Submit");
			WCSTransactionRequest req = buildRequest();
			ProgressMonitorDialog pmd = new ProgressMonitorDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
			AddCoverageRunnable runnable = new AddCoverageRunnable(req);

			try
			{
				pmd.run(true, false, runnable);
				String response = runnable.getResponse();
			//  For now, just print the response in a dialog
				int responseIndex = (response==null) ? -1 : response.indexOf("<Coverage>");
				if(responseIndex >= 0) {
					String success = "AddCoverage request succeeded.  Server response:  \n";
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							"AddCoverage Response", success + response);
				} else {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							"STT Error", "Server error while performing AddCoverage operation: " + response);
				}
			}
			catch (InvocationTargetException ite)
			{
				// TODO Auto-generated catch block
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						"STT Error", "Thread error while performing AddCoverage operation.\n" + ite.getMessage());
				ite.printStackTrace();
				ite.getCause().printStackTrace();
			}
			catch (InterruptedException ex)
			{
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}
	
	private  WCSTransactionRequest buildRequest() {
		WCSTransactionRequest request = new WCSTransactionRequest();
		request.setVersion("1.1.1");
		//  These need to be moved, probably to the conf/server.xml file
		String pciUrl = "http://ogcdemo.pcigeomatics.com:8181/swe/wcs";
		String spotUrl = "http://ws.spotimage.com/axis2/services/WcstLevel1A";
		if(serverCombo.getSelectionIndex() == 0)
			request.setPostServer(pciUrl);
		else
			request.setPostServer(spotUrl);

		//  Add 1 Coverage Transctn	to request
		CoverageTransaction transPixels = new CoverageTransaction();
		transPixels.setTitle("Add");
		transPixels.setDescription("OWS5 WCS-T add coverage demo");
		
		//  Add 2 OWSReference to transactn
		OWSReference refPixels = new OWSReference();
		refPixels.setRole(CoverageRefGroup.ROLE_COV_PIXELS);
		//  COV_PIXELS href is either 
		//  (1)  JPIP query- need to build a valid one and get the covDesc 
		//    or
		//  (2)  Static JP2 image- this requires us to save the image and send a reference to it,
		//                         but does not require CoverageDesc
		refPixels.setHref("http://vast.uah.edu/tmp/WcsLevel1A_10Dec2007.jp2");
		refPixels.setFormat("image/jp2; type=urn:ogc:def:wcs:jp2:1.0.0:jpip");
		transPixels.getReferenceList().add(refPixels);
		
		//  Don't need covDesc for JP2 image (according to SK from PCI)
//		OWSReference refDesc = new OWSReference();
//		refDesc.setRole(CoverageRefGroup.ROLE_COV_DESCRIPTION);
////		refDesc.setHref("cid:coveragedescription"); 
//		refDesc.setHref("http://vast.uah.edu/tmp/TestCovDesc.xml");  
//		transPixels.getReferenceList().add(refDesc);
		
		request.getInputCoverages().add(transPixels);
		
		return request;
	}
	
	public String submitRequest(WCSTransactionRequest request){
		OWSUtils owsUtils = new OWSUtils();
		StringBuffer buff = new StringBuffer(100);
		
		try {
			HttpURLConnection con = owsUtils.sendPostRequest(request);
			InputStream is = con.getInputStream();
			boolean eos = false;
			int b = 0;
			while(!eos) {
				b= is.read();
				if(b==-1)
					break;
				buff.append((char)b);
			}
		} catch (Exception e) {
			//  MessgaeBox
			System.err.println(e.getMessage());
		}
		return buff.toString();
	}
	
	private class AddCoverageRunnable implements IRunnableWithProgress
	{
		WCSTransactionRequest request;
		String response;

		public AddCoverageRunnable(WCSTransactionRequest request)
		{
			this.request = request;
		}


		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
		{
			String msg = "Sending AddCoverage Request...";
			monitor.beginTask(msg, IProgressMonitor.UNKNOWN);
			response = submitRequest(request);
			
		};
		
		public String getResponse(){
			return response;
		}

	}

}
