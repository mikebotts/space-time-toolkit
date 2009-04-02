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

import java.text.NumberFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.vast.math.Vector3d;
import org.vast.stt.gui.views.WorldView;
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
 * @date Oct 8, 2008
 * @version 1.0
 */

public class SPSWidget implements SelectionListener, MouseListener, MouseMoveListener
{
	WorldScene scene;
	DataItem dataItem;
	private Group mainGroup; 
	private Button submitBtn;
	private Button getLLBtn;
	private Text lonText;
	private Text latText;
	private Text altText;
	private Spinner zoomSpinner;
	private enum Mode { PICK, CANCEL};
	private Mode currentMode = Mode.PICK;
	private double defaultLat = 34.725, defaultLon = -86.645, defaultAlt = 300;
	private double oldLat = defaultLat, oldLon=defaultLon; 
	//  Temporary SPSSubmit class
	SPSSubmitTmp spsSubmitter;
	
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
	}

	public SPSWidget(Composite parent)
	{
		initGui(parent);
		spsSubmitter = new SPSSubmitTmp("http://vastcam1.nsstc.uah.edu");
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
		mainGroup.setText("SPS Camera Controller");
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
		latText.setText("      " + defaultLat);
		
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
		lonText.setText("     " + defaultLon);
		
		Label altlabel = new Label(mainGroup, 0x0);
		altlabel.setText("Altitude: ");
		gd = new GridData();
		gd.horizontalAlignment = SWT.END;
		gd.verticalAlignment = SWT.CENTER;
		altlabel.setLayoutData(gd);
		
		altText = new Text(mainGroup, 0x0);
		gd = new GridData();
		gd.horizontalAlignment = SWT.BEGINNING;
		gd.verticalAlignment = SWT.CENTER;
		altText.setLayoutData(gd);
		altText.setText("   " + defaultAlt);
		//  Fix temporarily with lenient value
		//altText.setEditable(false);
//		
		Label zoomLabel = new Label(mainGroup, 0x0);
		zoomLabel.setText("ZOOM: ");
		gd = new GridData();
		gd.horizontalAlignment = SWT.END;
		gd.verticalAlignment = SWT.CENTER;
		zoomLabel.setLayoutData(gd);
		
		zoomSpinner = new Spinner(mainGroup, 0x0);
		gd = new GridData();
		gd.horizontalAlignment = SWT.BEGINNING;
		gd.verticalAlignment = SWT.CENTER;
		zoomSpinner.setLayoutData(gd);
		zoomSpinner.setMinimum(1);
		zoomSpinner.setMinimum(70);
		zoomSpinner.setValues(10, 5, 70, 0, 1, 10);
		zoomSpinner.setSelection(55);
		
		getLLBtn = new Button(mainGroup, SWT.PUSH  | SWT.SHADOW_OUT);
		getLLBtn.setText("Get LatLon");
		getLLBtn.setToolTipText("Select LatLon from the Worldview");
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		getLLBtn.setLayoutData(gd);
		getLLBtn.addSelectionListener(this);
		getLLBtn.setEnabled(false);
		
		//  Submit Button
		submitBtn = new Button(mainGroup, SWT.PUSH);
		submitBtn.setText("Submit Request");
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		submitBtn.setLayoutData(gd);
		submitBtn.addSelectionListener(this);
		submitBtn.setEnabled(false);
		
	//  Set Default scroller size
		scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**  FIX!!! **/
	public void setDataItem(DataItem item){
	}
	
    public void setScene(WorldScene scene){
        this.scene = scene;
        getLLBtn.setEnabled(true);
        submitBtn.setEnabled(true);
    }
	
	public void widgetSelected(SelectionEvent e) { 
		Control control = (Control) e.getSource();

		if (control == submitBtn){
			submitRequest();
		} else if (control == getLLBtn) {
			if (currentMode == Mode.PICK) {
				pickLL();
			} else 
				cancelPick();
		}
	}
	
	private void setLatLon(double lat, double lon){
		NumberFormat nf  = NumberFormat.getInstance();
		
		nf.setMaximumIntegerDigits(3);
		nf.setMinimumFractionDigits(4);
		String lonStr = nf.format(lon);
		String latStr = nf.format(lat);
		latText.setText(latStr);
		lonText.setText(lonStr);
	}
	
	public void submitRequest() {
		//  Get Values
		try {
			double lat = Double.parseDouble(latText.getText().trim());
			double lon = Double.parseDouble(lonText.getText().trim());
			double alt = Double.parseDouble(altText.getText().trim());
			int zoom = zoomSpinner.getSelection();
			
			System.err.println("Submit: " + lat + " " + lon + " " + alt + " " + zoom);
			boolean reqOk = spsSubmitter.requestSPS(lat, lon, alt, (double)zoom);
			if(reqOk) {
				MessageDialog.openInformation(null, "SPS Submission Status", 
					"Parameters okay.  Submitting request to Vastcam SPS...");
			}else {
				MessageDialog.openError(null, "SPS Submission Error", 
				"Requested Lat/Lon/Alt parameters out of Range.\nRequest not submitted");
			}
		} catch (NumberFormatException e) {
			MessageDialog.openWarning(null, "SPS Submission error", 
					"Number Format error in Lat/Lon/Alt fields.");
			return;
		}
	}
	
	private void cancelPick(){
		getLLBtn.setText("Get LatLon");
		setLatLon(oldLat, oldLon);
		currentMode = Mode.PICK;
		getLLBtn.setToolTipText("Select LatLon from the Worldview");
		Composite canvas = scene.getRenderer().getCanvas();
		canvas.removeMouseListener(this);
		canvas.removeMouseMoveListener(this);
		canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));

	}
	
	public void pickLL(){
		oldLat = Double.parseDouble(latText.getText().trim());
		oldLon = Double.parseDouble(lonText.getText().trim());
		getLLBtn.setText("Cancel");
		currentMode = Mode.CANCEL;
		getLLBtn.setToolTipText("Cancel the LatLon Picking Process");
		// Change cursor
		Composite canvas = scene.getRenderer().getCanvas();
		canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_CROSS));
		canvas.addMouseListener(this);
		//  The proper way to do this would be to have Cavnas be a HoverListener, or the equivalent of that
		//  But don't want to risk side effects of that before demo.  This shoudl work safely until then. T 
		boolean setOk = canvas.setFocus();
		canvas.addMouseMoveListener(this);
	}
	

	public void mouseDoubleClick(MouseEvent arg0) {
	}

//  move to mouse up if desired behavior is to not submit until btn is released
	public void mouseDown(MouseEvent e) {
		if (e.button == 1 || e.button == 3) {  //  accept left or right button
			//  get LatLon from WV
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			WorldView view = (WorldView)page.findView(WorldView.ID);
			//e.y = viewHeight - e.y;
			Vector3d llPos = view.getProjectedPosition(e.x, e.y);
//			NumberFormat nf  = NumberFormat.getInstance();
//			
//			nf.setMaximumIntegerDigits(3);
//			nf.setMinimumFractionDigits(4);
//			String lonStr = nf.format(llPos.x);
//			String latStr = nf.format(llPos.y);
			setLatLon(llPos.y, llPos.x);
			getLLBtn.setText("Get LatLon");
			currentMode = Mode.PICK;
			getLLBtn.setToolTipText("Select LatLon from the Worldview");

			Composite canvas = scene.getRenderer().getCanvas();
			canvas.removeMouseListener(this);
			canvas.removeMouseMoveListener(this);
			canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));

			//view.restoreMouseListeners();
			
			//  reset canvas cursor
		}
	}

	//  I think Mousemove listener isn't working bc View loses Focus.  Note that the WorldViewController also stops getting mouseMoved events 
	//  until selection is made to bring Focus back to Canvas
	public void mouseMove(MouseEvent e)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		WorldView view = (WorldView)page.findView(WorldView.ID);
		//e.y = viewHeight - e.y;
		Vector3d llPos = view.getProjectedPosition(e.x, e.y);
		setLatLon(llPos.y, llPos.x);
	}
	
	public void mouseUp(MouseEvent arg0) {
	}
}
