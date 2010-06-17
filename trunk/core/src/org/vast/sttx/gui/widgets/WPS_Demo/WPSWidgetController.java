package org.vast.sttx.gui.widgets.WPS_Demo;

import java.io.IOException;
import java.text.ParseException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.vast.cdm.common.DataBlock;
import org.vast.math.Vector3d;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.BlockListIterator;
import org.vast.stt.data.DataNode;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.views.WorldView;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.STTTimeExtent;
import org.vast.util.DateTimeFormat;
import org.vast.util.SpatialExtent;
import org.vast.xml.DOMHelperException;

public class WPSWidgetController implements SelectionListener, MouseListener, MouseMoveListener
{
	DataItem oldTrackItem;
	DataItem newTrackItem;
	private enum Mode { PICK, IDLE};
	private Mode currentMode = Mode.PICK;
	int currentPickBtn = 0;  // 0 or 1 for begin or end
	WPS_DemoWidget widget;
	//  Scene hnadle needed to allow picking
	WorldScene scene;
	private WPS_DemoSubmit sub;


	public WPSWidgetController(WPS_DemoWidget widget){
		this.widget = widget;
		currentMode = Mode.IDLE;
	}

	public void setScene(WorldScene scene){
		this.scene = scene;
	}

	private double lookupTimes(Vector3d lla, DataItem item){
		System.err.println("WPSWC.lookupTimes for " + lla.x + "  " + lla.y + " " + lla.z);
		DataProvider provider = item.getDataProvider();
		SpatialExtent spatialExtent = provider.getSpatialExtent();
		DataNode dataNode = provider.getDataNode();
		BlockList blockList = dataNode.getList("time");
		if(blockList == null) {
			System.err.println("WPSWidgetController.lookupTImes():  Geoloc info not in this item!");
			return 0.0;
		}
		BlockListIterator it = blockList.getIterator();
		double time = 0.0, lat, lon, alt;
		double distance, minDistance = Double.MAX_VALUE;
		double delLat, delLon, closestLat, closestLon, closestTime = 0.0;
		while(it.hasNext()){
			BlockListItem bli = it.next();
			DataBlock block = bli.getData();
			time = block.getDoubleValue(0);
			lat = block.getDoubleValue(1);
			lon = block.getDoubleValue(2);
			//			System.err.println("Data: t,ll: " + time + " " + lat + " " + lon);
			delLon = lla.x - lon;
			delLat = lla.y - lat;
			//			 distance = Math.sqrt(delX*delX + delY*delY);
			distance = Math.sqrt(delLon*delLon + delLat*delLat);
			if (distance < minDistance) {
				minDistance= distance;
				closestTime = time;
			}
		}
		return closestTime;
	}

	public void submitRequest() {
		//  Get Values from widgets
		double oldBegin = widget.oldBeginSpinner.getValue();
		double newBegin = widget.newBeginSpinner.getValue();
		double oldEnd = widget.oldEndSpinner.getValue();
		double newEnd = widget.newEndSpinner.getValue();
		sub = new WPS_DemoSubmit();
		//		if(startT > endT) {
		//			double tempT = endT;
		//			endT = startT;
		//			startT = tempT;
		//		}
		sub.setOldVideoTimes(oldBegin, oldEnd);
		sub.setNewVideoTimes(newBegin, newEnd);
		// Submit
		boolean submitOK = sub.invokeWPS();
		if(!submitOK) {
			System.err.println("Dialog saying we fail!");
		}
		MessageDialog.openInformation(widget.pollStatusBtn.getShell(), "WPS Status", "WPS Submission successful.  Status available at:\n" + 
				sub.statusUrl);
	}

	protected boolean listenToCanvas(boolean on) {
		if(on) {
			//  Scene can be NULL here IF no item yet sel'd from tree (happens when
			//  widget is insantiated before proj open and user selects PICJ 
			//  before selecting anything from tree).  FIX!
			if(scene == null) {
				System.err.println("WPSWidController.listenToCanvas():  Scene is null!");
				return false;
			}
			Composite canvas = scene.getRenderer().getCanvas();
			//			canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_CROSS));
			canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_IBEAM));
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
		return true;

	}

	protected void pickButtonPushed(int whichBtn){
		//  If mode IDLE, instigate picking sequnce...
		if(currentMode == Mode.IDLE) {
			currentMode = Mode.PICK;
			//  Check status of autoPick if needed
			//  Get current LAT/lON tf vals for restore	 on cancel
			//  change Button to CANCEL
			boolean listenOk = listenToCanvas(true);
			if(!listenOk) {
				currentMode = Mode.IDLE;
				return;
			}
			widget.pickBtn[whichBtn].setText(widget.pickCancelText);
			widget.pickBtn[whichBtn].setToolTipText(widget.pickCancelTooltipText);
			//  disable all other buttons just to simplify things
			//  Actually, there could still be bugs if user selects something outside WorldView window (like a sceneTree item)
			int otherBtn = (whichBtn == 0) ? 1 : 0;
			widget.pickBtn[otherBtn].setEnabled(false);
			widget.invokeBtn.setEnabled(false);
			widget.pollStatusBtn.setEnabled(false);
			currentPickBtn = whichBtn;
		} 
		//  If mode is already PICK,< can we assume cancel?
		else if(currentMode == Mode.PICK) {
			//			if(!widget.pickLLBtn.getText().equals(widget.pickCancelText)) {
			//				System.err.println("WPSWidCont.pickBtnPushed():  NO, we cannot assume cancel");
			//			}
			currentMode = Mode.IDLE;
			//  Check status of autoPick if needed
			// Reset LAT/lON tf vals for restorOnCancel
			//  change Button to CANCEL
			widget.pickBtn[whichBtn].setText(widget.pickText[whichBtn]);
			widget.pickBtn[whichBtn].setToolTipText(widget.pickTTT[whichBtn]);
			widget.enableAllButtons();
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

		if (control == widget.invokeBtn){
			System.err.println("WPSWidCont:  SUBMIT...");
			submitRequest();
		} else if (control == widget.pollStatusBtn) {
			pollWPS();
		} else if (control == widget.pickBtn[0]) {
			pickButtonPushed(0);
		} else if (control == widget.pickBtn[1]) {
			pickButtonPushed(1);
		} else {
			System.err.println(e.widget	);
		}
	}

	public void pollWPS(){
		if(sub == null)
			return;
		String [] times=null; 
		try {
			times = sub.pollStatus();
		} catch (IOException ex) {
			//  error dialog ex
			return;
		} catch (DOMHelperException ex) {
			//  error dialog ex
			return;
		}
		if(times == null) {
			System.err.println("WPS.poll(): No results");
			return;
		}
		// set Changed Video times
		boolean apply = MessageDialog.openQuestion(widget.pollStatusBtn.getShell(), "WPS Status", 
				"WPS Processing Complete.\n" + 
				"Changed Video Start Time: " + times[0] + "\n"  + 
				"Changed Video End Time: " + times[1] + "\n" + 
		"Apply these settings to SceneTime?");


		if(apply) {
			setChangedVideoTimes(times);
		}

		//		String piStr = widget.pollIntervalText.getText().trim();
		//		int pollInterval;
		//		try {
		//			pollInterval = Integer.parseInt(piStr);
		//		} catch (NumberFormatException e1) {
		//			// TODO Auto-generated catch block
		//			e1.printStackTrace();
		//			pollInterval = 60;
		//		}
		//		beginPolling(pollInterval);
	}

	//  TODO: support auto polling
	public void beginPolling(int pollInterval) {

	}


	public void setChangedVideoTimes(String [] times){
		//  Set in Changedvideo...
		STTTimeExtent ext = scene.getTimeExtent();
		double startTime = ext.getBaseTime();
		try {
			startTime = DateTimeFormat.parseIso(times[0]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//  and in Scene
		ext.setBaseTime(startTime);
		//  issue event?
		//  I would, but it seems to screw up next request of track item
//		ext.dispatchEvent(new STTEvent(this,	EventType.TIME_EXTENT_CHANGED), true);

	}

	public void mouseDoubleClick(MouseEvent arg0) {
	}

	//  Called when user selects point on Canvas
	//  Note: can move to mouse up if desired behavior is to not submit until btn is released
	public void mouseDown(MouseEvent e) {
		if(!(e.widget instanceof GLCanvas))
			return;
		if (e.button == 1 || e.button == 3) {  //  accept left or right button
			//  Set oldTrack and newTrack Item
			oldTrackItem = widget.getOldTrackItem();
			newTrackItem = widget.getNewTrackItem();

			//  get LatLon from WV
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			WorldView view = (WorldView)page.findView(WorldView.ID);
			int viewHeight = scene.getRenderer().getViewHeight();
			e.y = viewHeight - e.y;
			Vector3d new_llPos = view.getProjectedPosition(e.x, e.y);
			//  Disable mouse listeners and change cursor
			listenToCanvas(false);
			// PushButtonText
			//  change Button to PICK, mode to IDLE
			widget.pickBtn[currentPickBtn].setText(widget.pickText[currentPickBtn]);
			widget.pickBtn[currentPickBtn].setToolTipText(widget.pickTTT[currentPickBtn]);
			widget.enableAllButtons();
			currentMode= Mode.IDLE;
			double oldTime = lookupTimes(new_llPos, oldTrackItem);
			double newTime = lookupTimes(new_llPos, newTrackItem);
			//  Note: Need logic for case where endTime < beginTimes
			if(currentPickBtn == 0) {
				widget.oldBeginSpinner.setValue(oldTime);
				widget.newBeginSpinner.setValue(newTime);
			} else {
				widget.oldEndSpinner.setValue(oldTime);
				widget.newEndSpinner.setValue(newTime);
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
	}

	public void mouseUp(MouseEvent arg0) {
	}


	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	public static void main(String[] args) {
	}
}

