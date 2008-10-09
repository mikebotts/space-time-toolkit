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

package org.vast.stt.gui.widgets.SPS;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.DataProvider;


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

public class SPSWidget implements SelectionListener
{
	WorldScene scene;
	DataItem dataItem;
	private Group mainGroup; 
	private Button submitBtn;
	private Button getLLBtn;
	private Text lonText;
	private Text latText;
	private Control canvas;
	
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
	}

	public SPSWidget(Composite parent)
	{
		initGui(parent);
	}

	public void initGui(Composite parent)
	{
		GridData gd;
		
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
		
		Label latLabel = new Label(mainGroup, 0x0);
		latLabel.setText("Latitude: ");
		gd = new GridData();
		gd.horizontalAlignment = SWT.END;
		gd.verticalAlignment = SWT.CENTER;
		latLabel.setLayoutData(gd);
		
		latText = new Text(mainGroup, 0x0);
		gd = new GridData();
		gd.horizontalAlignment = SWT.BEGINNING;
		gd.verticalAlignment = SWT.CENTER;
		latText.setLayoutData(gd);
//		labelText.setText("");
		
		Label lonLabel = new Label(mainGroup, 0x0);
		lonLabel.setText("Longitude: ");
		gd = new GridData();
		gd.horizontalAlignment = SWT.END;
		gd.verticalAlignment = SWT.CENTER;
		lonLabel.setLayoutData(gd);
		
		lonText = new Text(mainGroup, 0x0);
		gd = new GridData();
		gd.horizontalAlignment = SWT.BEGINNING;
		gd.verticalAlignment = SWT.CENTER;
		lonText.setLayoutData(gd);
//		labelText.setText("");
		
		Label altlabel = new Label(mainGroup, 0x0);
		altlabel.setText("Altitude: ");
		gd = new GridData();
		gd.horizontalAlignment = SWT.END;
		gd.verticalAlignment = SWT.CENTER;
		altlabel.setLayoutData(gd);
		
		Text altText = new Text(mainGroup, 0x0);
		gd = new GridData();
		gd.horizontalAlignment = SWT.BEGINNING;
		gd.verticalAlignment = SWT.CENTER;
		altText.setLayoutData(gd);
//		labelText.setText("");
//		
		Label fovLabel = new Label(mainGroup, 0x0);
		fovLabel.setText("FOV: ");
		gd = new GridData();
		gd.horizontalAlignment = SWT.END;
		gd.verticalAlignment = SWT.CENTER;
		fovLabel.setLayoutData(gd);
		
		Spinner fovSpinner = new Spinner(mainGroup, 0x0);
		gd = new GridData();
		gd.horizontalAlignment = SWT.BEGINNING;
		gd.verticalAlignment = SWT.CENTER;
		fovSpinner.setLayoutData(gd);
		fovSpinner.setMinimum(1);
		fovSpinner.setMinimum(70);
		fovSpinner.setValues(10, 1, 70, 0, 1, 5);
		
		getLLBtn = new Button(mainGroup, SWT.PUSH);
		getLLBtn.setText("Get LatLon");
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		getLLBtn.setLayoutData(gd);
		getLLBtn.addSelectionListener(this);
		
		//  Submit Button
		submitBtn = new Button(mainGroup, SWT.PUSH);
		submitBtn.setText("Submit Request");
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		submitBtn.setLayoutData(gd);
		submitBtn.addSelectionListener(this);
		
	//  Set Default scroller size
		scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	public void setDataItem(DataItem item){
		this.dataItem = item;
		mainGroup.setText(item.getName());
		DataProvider prov = item.getDataProvider();
		//  If provider isn't SPS/SML Process, this widget isn't supported.
		//  Should catch this and never pop the widget up, actually.
//		if(prov!=null) {
//			STTSpatialExtent ext = prov.getSpatialExtent();
//			this.setSpatialExtent(ext);
//		}
	}
	
    public void setScene(WorldScene scene){
        this.scene = scene;
    }
	
	public void widgetSelected(SelectionEvent e) { 
		Control control = (Control) e.getSource();

		if (control == submitBtn){
			submitRequest();
		} else if (control == getLLBtn) {
			pickLL();
		}
	}
	
	private void setLatLon(double lat, double lon){
		latText.setText("" + lat);
		lonText.setText("" + lon);
	}
	
	public void submitRequest(){
		
	}
	
	public void pickLL(){
		// uspend normal UI actions 
//		worldViewController.suspendListeners();
		
		// Change cursor
		canvas = scene.getRenderer().getCanvas();
		canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_CROSS));
		
		//  Allow user left-click only?
		
		//  get coords
		
		//  load into LL fields
		setLatLon(30.1,-90.2);
	}
	

}
