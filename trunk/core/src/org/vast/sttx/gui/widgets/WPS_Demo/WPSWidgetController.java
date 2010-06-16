package org.vast.sttx.gui.widgets.WPS_Demo;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

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
import org.vast.cdm.common.DataBlock;
import org.vast.math.Vector3d;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.BlockListIterator;
import org.vast.stt.data.DataNode;
import org.vast.stt.gui.views.WorldView;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.DataProvider;
import org.vast.util.SpatialExtent;

public class WPSWidgetController implements SelectionListener, MouseListener, MouseMoveListener
{
	DataItem dataItem;
	private enum Mode { PICK, IDLE};
	private Mode currentMode = Mode.PICK;
	private double defaultLat = 34.725, defaultLon = -86.645, defaultAlt = 300;
	private double oldLat = defaultLat, oldLon=defaultLon; 
	//  Temporary SPSSubmit class
	WPS_DemoWidget widget;
//  Scene hnadle needed to allow picking
	WorldScene scene;
	
	
	public WPSWidgetController(WPS_DemoWidget widget){
		this.widget = widget;
		currentMode = Mode.IDLE;
	}
	
	/**  Remove if we really end up not needing **/
	public void setDataItem(DataItem item){
		//  Make sure it is the track!!!  If it is not the track, don't change it here.
		this.dataItem = item;
	}
	
    public void setScene(WorldScene scene){
        this.scene = scene;
    }

	private void lookUpTimes(Vector3d lla){
		System.err.println("WPSWC.lookupTimes for " + lla.x + "  " + lla.y + " " + lla.z);
		DataProvider provider = dataItem.getDataProvider();
		SpatialExtent spatialExtent = provider.getSpatialExtent();
		DataNode dataNode = provider.getDataNode();
		BlockList blockList = dataNode.getList("time");
		if(blockList == null) {
			System.err.println("WPSWidgetController.lookupTImes():  Geoloc info not in this item!");
			return;
		}
		BlockListIterator it = blockList.getIterator();
		double time, lat, lon, alt;
		while(it.hasNext()){
			BlockListItem bli = it.next();
			DataBlock block = bli.getData();
			time = block.getDoubleValue(0);
			lat = block.getDoubleValue(1);
			lon = block.getDoubleValue(2);
			System.err.println("Data: t,ll: " + time + " " + lat + " " + lon);
		}
		
//		2010-04-14T17:12:26.300-05:00
		Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.set(2010, 3, 14, 22,12, 26);
		widget.newBeginSpinner.setValue(cal.getTimeInMillis()/1000.0);
		cal.set(2010, 3, 14, 22,12, 28);
		widget.newEndSpinner.setValue(cal.getTimeInMillis()/1000.0);
	}
	
	public void submitRequest() {
		//  Get Values from widgets
		
		//  Build request
		
		// Submit
		
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
	
	protected void pickButtonPushed(){
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
			widget.pickLLBtn.setText(widget.pickCancelText);
			widget.pickLLBtn.setToolTipText(widget.pickCancelTooltipText);
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

		if (control == widget.invokeBtn){
			System.err.println("WPSWidCont:  SUBMIT...");
			submitRequest();
		} else if (control == widget.pickLLBtn) {
			System.err.println("WPSWidCont:  PickLL...");
			pickButtonPushed();
		} else {
			System.err.println(e.widget	);
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
	        int viewHeight = scene.getRenderer().getViewHeight();
			e.y = viewHeight - e.y;
				Vector3d new_llPos = view.getProjectedPosition(e.x, e.y);
				lookUpTimes(new_llPos);
				//  Disable mouse listeners and change cursor
				listenToCanvas(false);
				// PushButtonText
			//  change Button to PICK, mode to IDLE
				widget.pickLLBtn.setText(widget.pickText);
				widget.pickLLBtn.setToolTipText(widget.pickTooltipText);
				currentMode= Mode.IDLE;
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
//		setLatLon(llPos.y, llPos.x);
	}
	
	public void mouseUp(MouseEvent arg0) {
	}


	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}
	
	public void findClosesetPoint(double x, double y){
		double [] testX = { 0.0, 1.0,2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0};
		double [] testY = { 0.0, 1.0,2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0};
		double delX, delY;
		double distance, minDistance = Double.MAX_VALUE;
		int closestIdx = -1;
		for(int i=0; i< testX.length; i++) {
			 delX = testX[i] - x;
			 delY = testY[i] - y;
//			 distance = Math.sqrt(delX*delX + delY*delY);
			 distance = Math.sqrt(delX*delX + delY*delY);
			 if (distance < minDistance) {
				 minDistance= distance;
				 closestIdx = i;
			 }
		}
		System.err.println("Closest Point: " + closestIdx + ": " + testX[closestIdx] + " " + testY[closestIdx]);
		System.err.println("Min distance: " + minDistance);
	}
	
	
	
	public static void main(String[] args) {
		WPSWidgetController wc = new WPSWidgetController(null);
		
		
		wc.findClosesetPoint(6.5 ,6.5);
	}
}

