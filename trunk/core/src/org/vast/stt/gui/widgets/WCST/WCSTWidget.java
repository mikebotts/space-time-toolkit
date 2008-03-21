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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.vast.stt.apps.STTPlugin;


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
	private Group mainGroup; 
	String [] servers;
	Text nlatText, slatText, wlonText, elonText;
	Combo formatCombo;
	private Button submitBtn;
	private Text idText;
	
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
//		
		Combo serverCombo  = new Combo(mainGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
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
			submitRequest();
		}
	}
	
	public void submitRequest(){
		//  Read the params from the controls and build a WCS-T request
	}
}
