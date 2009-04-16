package org.vast.stt.gui.widgets.SPS;

import java.text.NumberFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
	private enum Mode { PICK, CANCEL};
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
	}
	
	/**  Remove if we really end up not needing **/
	public void setDataItem(DataItem item){
		// not needed
	}
	
    public void setScene(WorldScene scene){
        this.scene = scene;
    }
	
	public void widgetSelected(SelectionEvent e) { 
		Control control = (Control) e.getSource();

		if (control == widget.submitNowBtn){
//			Composite canvas = scene.getRenderer().getCanvas();
//			canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
			submitRequest();
//			canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
		} else if (control == widget.pickLLBtn) {
			if (currentMode == Mode.PICK) {
				oldLat = Double.parseDouble(widget.latText.getText().trim());
				oldLon = Double.parseDouble(widget.lonText.getText().trim());
				setPickLLBtnMode(Mode.CANCEL);
			} else {
				setPickLLBtnMode(Mode.CANCEL);
				setLatLon(oldLat, oldLon);
			}
		} else if (control == widget.autoSubmitBtn) {
			boolean b = widget.autoSubmitBtn.getSelection();
			widget.submitNowBtn.setEnabled(!b);
		}
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
			
			//System.err.println("Submit: " + lat + " " + lon + " " + alt + " " + zoom);
			Composite canvas = scene.getRenderer().getCanvas();
			canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
			boolean reqOk = spsSubmitter.requestSPS(lat, lon, alt, (double)zoom);
			canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_IBEAM));

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
			} else {
				setPickLLBtnMode(Mode.PICK);
			}
		}
	}
	
	protected void setPickLLBtnMode(Mode mode){
		if(currentMode == mode ) {
			System.err.println("Logic FLASSW!!!");
			return;
		}
		
		if(currentMode == Mode.PICK) {
			widget.pickLLBtn.setText(widget.pickCancelText);
			widget.pickLLBtn.setToolTipText(widget.pickCancelTooltipText);
			Composite canvas = scene.getRenderer().getCanvas();
			canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_CROSS));
			canvas.addMouseListener(this);
			//  The proper way to do this would be to have Cavnas be a HoverListener, or the equivalent of that
			//  But don't want to risk side effects of that before demo.  This shoudl work safely until then. T 
			boolean setOk = canvas.setFocus();
			canvas.addMouseMoveListener(this);
			currentMode = mode;
		} else  { //if(mode == Mode.CANCEL) {
			widget.pickLLBtn.setText(widget.pickText);
			widget.pickLLBtn.setToolTipText(widget.pickTooltipText);
			Composite canvas = scene.getRenderer().getCanvas();
			canvas.removeMouseListener(this);
			canvas.removeMouseMoveListener(this);
			canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
			currentMode = mode;
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


	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

