package org.vast.stt.gui.widgets.SPS;

import java.text.NumberFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.vast.math.Vector3d;
import org.vast.stt.gui.views.WorldView;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;

public class SPSWidgetController implements SelectionListener, MouseListener, MouseMoveListener
{
	private static final String vastcam1Server = "http://vastcam1.nsstc.uah.edu";
	DataItem dataItem;
//	private enum Mode { PICK, CANCEL, IDLE};
	private enum Mode { PICK, IDLE};
	private Mode currentMode = Mode.PICK;
	private double defaultLat = 34.725, defaultLon = -86.645, defaultAlt = 300;
	private double oldLat = defaultLat, oldLon=defaultLon; 
	//  Temporary SPSSubmit class
	SPSSubmitTmp spsSubmitter;
	SPSWidget widget;
//  Scene hnadle needed to allow picking
	WorldScene scene;
	
	
	public SPSWidgetController(SPSWidget widget){
		this.widget = widget;
		spsSubmitter = new SPSSubmitTmp(vastcam1Server);
		currentMode = Mode.IDLE;
	}
	
	/**  Remove if we really end up not needing **/
	public void setDataItem(DataItem item){
		// not needed
	}
	
    public void setScene(WorldScene scene){
        this.scene = scene;
    }

	private void setLatLon(double lat, double lon){
		NumberFormat nf  = NumberFormat.getInstance();
		
		nf.setMaximumIntegerDigits(3);
		nf.setMinimumFractionDigits(4);
		String lonStr = nf.format(lon);
		String latStr = nf.format(lat);
		widget.latText.setText(latStr);
		widget.lonText.setText(lonStr);
	}
	
	public void submitRequest() {
		//  Get Values
		try {	
			double lat = Double.parseDouble(widget.latText.getText().trim());
			double lon = Double.parseDouble(widget.lonText.getText().trim());
			double alt = Double.parseDouble(widget.altText.getText().trim());
			int zoom = widget.zoomSpinner.getSelection();
			
//			System.err.println("spsSubmitter.reqSPS " + lat + " " + lon + " " + alt + " " + zoom);
			boolean reqOk = spsSubmitter.requestSPS(lat, lon, alt, (double)zoom);
//			System.err.println("submitter.reqSPS: back");

			if(!reqOk) {
				MessageDialog.openError(null, "SPS Submission Error", 
				"Requested Lat/Lon/Alt parameters out of Range.\nRequest not submitted");
			}
		} catch (NumberFormatException e) {
			MessageDialog.openWarning(null, "SPS Submission error", 
					"Number Format error in Lat/Lon/Alt fields.");
			return;
		}
	}
	

	
	protected void listenToCanvas(boolean on) {
		if(on) {
			//  Scene can be NULL here IF no item yet sel'd from tree (happens when
			//  widget is insantiated before proj open and user selects PICJ 
			//  before selecting anything from tree).  FIX!
			Composite canvas = scene.getRenderer().getCanvas();
			canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_CROSS));
			canvas.addMouseListener(this);
			//  The proper way to do this would be to have Cavnas be a HoverListener, or the equivalent of that
			//  But don't want to risk side effects of that before demo.  This shoudl work safely until then. T 
			boolean setOk = canvas.setFocus();
			canvas.addMouseMoveListener(this);
		} else {
			//  Disable mouse listeners and change cursor
			Composite canvas = scene.getRenderer().getCanvas();
			canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
			//  
			canvas.removeMouseListener(this);
			canvas.removeMouseMoveListener(this);
		}
			
			
	}
	
	protected void pickButtonPushed(){
		//  If mode IDLE, instigate picking sequnce...
		if(currentMode == Mode.IDLE) {
			currentMode = Mode.PICK;
			//  Check status of autoPick if needed
			//  Get current LAT/lON tf vals for restore	 on cancel
			//  change Button to CANCEL
			widget.pickLLBtn.setText(widget.pickCancelText);
			widget.pickLLBtn.setToolTipText(widget.pickCancelTooltipText);
			listenToCanvas(true);
		} 
		//  If mode is already PICK,< can we assume cancel?
		else if(currentMode == Mode.PICK) {
			if(!widget.pickLLBtn.getText().equals(widget.pickCancelText)) {
				System.err.println("SPSWidCont.pickBtnPushed():  NO, we cannot assume cancle");
			}
			currentMode = Mode.IDLE;
		    //  Check status of autoPick if needed
			// Reset LAT/lON tf vals for restorOnCancel
			//  change Button to CANCEL
			widget.pickLLBtn.setText(widget.pickText);
			widget.pickLLBtn.setToolTipText(widget.pickTooltipText);
			// Disable Canvas selction (incl cursor change in this) - 
			//  ...should be safe even if this is not currently a listener to canvas
			Composite canvas = scene.getRenderer().getCanvas();
			canvas.removeMouseListener(this);
			canvas.removeMouseMoveListener(this);
			canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
		}
	}
	
	public void widgetSelected(SelectionEvent e) { 
		Control control = (Control) e.getSource();

		if (control == widget.submitNowBtn){
			System.err.println("SPSWidCont:  SUBMIT...");
			submitRequest();
		} else if (control == widget.pickLLBtn) {
			pickButtonPushed();
		} else if (control == widget.autoSubmitBtn) {
			boolean b = widget.autoSubmitBtn.getSelection();
			widget.submitNowBtn.setEnabled(!b);
		}
	}
	
	public void mouseDoubleClick(MouseEvent arg0) {
	}

	//  Called when user selects point on Canvas
	//  Note: can move to mouse up if desired behavior is to not submit until btn is released
	public void mouseDown(MouseEvent e) {
		if (e.button == 1 || e.button == 3) {  //  accept left or right button
			//  get LatLon from WV
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			WorldView view = (WorldView)page.findView(WorldView.ID);
			//e.y = viewHeight - e.y;
			Vector3d llPos = view.getProjectedPosition(e.x, e.y);
			setLatLon(llPos.y, llPos.x);
			
			if(widget.autoSubmitBtn.getSelection()) {
				submitRequest();
				//  Reset to cross since Canvas changes it, but we want to stay in 'select mode'
				//  Does not work here- Canvas overrides
				Composite canvas = scene.getRenderer().getCanvas();
				canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_CROSS));
			} else {
				//  May not even need to reset LatLon if trackin on mouseMove also
				IWorkbenchPage ipage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				WorldView wview = (WorldView)page.findView(WorldView.ID);
				//e.y = viewHeight - e.y;
				Vector3d new_llPos = view.getProjectedPosition(e.x, e.y);
				setLatLon(new_llPos.y, new_llPos.x);
				//  Disable mouse listeners and change cursor
				listenToCanvas(false);
				// PushButtonText
			//  change Button to PICK, mode to IDLE
				widget.pickLLBtn.setText(widget.pickText);
				widget.pickLLBtn.setToolTipText(widget.pickTooltipText);
				currentMode= Mode.IDLE;
			}
		}
	}
	
	//  I think Mousemove listener isn't working bc View loses Focus.  Note that the WorldViewController also stops getting mouseMoved events 
	//  until selection is made to bring Focus back to Canvas
	public void mouseMove(MouseEvent e)
	{
		// cursor hack- fix public methods in JOGLRednderer or wherever to override cursor 
		
		Cursor canvasCursor = ((Control) e.widget).getCursor();
		if(canvasCursor !=  e.widget.getDisplay().getSystemCursor(SWT.CURSOR_CROSS))
			((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_CROSS));
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		WorldView view = (WorldView)page.findView(WorldView.ID);
		//e.y = viewHeight - e.y;
		Vector3d llPos = view.getProjectedPosition(e.x, e.y);
		setLatLon(llPos.y, llPos.x);
	}
	
	public void mouseUp(MouseEvent arg0) {
	}


	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

