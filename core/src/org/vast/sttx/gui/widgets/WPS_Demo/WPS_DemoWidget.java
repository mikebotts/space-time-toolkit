package org.vast.sttx.gui.widgets.WPS_Demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.vast.stt.gui.widgets.time.CalendarSpinner;
import org.vast.stt.gui.widgets.time.TimeSpinner;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;



/**
 * <p>Title: WPS_DemoWidget.java</p>
 * <p>Description:  </p>
 * @author Tony Cook
 * @since Jun 6, 2010
 */

public class WPS_DemoWidget
{
	String oldVideoSrc = "urn:ogc:object:sensor:BOTTS-INC:bottsCam0";
	String newVideoSrc = "urn:ogc:object:sensor:BOTTS-INC:bottsCam0";
	final String pickText = "Pick Lat/Lon";
	final String pickTooltipText = "Interactively select Lat/Lon from World View";
	final String pickCancelText = "Cancel Pick";
	final String pickCancelTooltipText ="Cancel Interactive Lat/Lon Picking Mode";
	final String invokeText = "Invoke WPS";
	final String invokeTooltipText = "Invoke WPS with the currently selected parameters";
	final String pollStatusText = "Poll WPS";
	final String pollStatusTTT = "Poll Status URL provided by WPS";
	final String retrieveVideoText = "";
	final String retrieveVidTTT = "";
	protected Button pickLLBtn;
	protected Button invokeBtn;
	private WPSWidgetController controller;
	protected TimeSpinner newBeginSpinner;
	protected TimeSpinner oldBeginSpinner;
	protected CalendarSpinner oldEndSpinner;
	protected CalendarSpinner newEndSpinner;
	
	public WPS_DemoWidget(Composite parent)
	{
		initGui(parent);
		controller = new WPSWidgetController(this);
		addListeners(controller);
	}

	public void addListeners(WPSWidgetController cont){
		// zoomSp
		pickLLBtn.addSelectionListener(cont);
		invokeBtn.addSelectionListener(cont);
	}

	/**  FIX!!! **/
	public void setDataItem(DataItem item){
		controller.setDataItem(item);
	}

	public void setScene(WorldScene scene){
		 controller.setScene(scene);
	     pickLLBtn.setEnabled(true);
	}

	public void addListeners(){
	}

	public void initGui(Composite parent)
	{
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
		sc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
//		FillLayout scl = new FillLayout(org.eclipse.swt.SWT.VERTICAL);
//		scl.type = SWT.VERTICAL;
//		sc.setLayout(scl);
		
		Composite mainComp = new Composite(sc, 0x0);
		sc.setContent(mainComp);

		GridLayout gl = new GridLayout();
		gl.numColumns = 3;
		mainComp.setLayout(gl);
		
		// Old Group
		Group oldGroup = new Group(mainComp, 0x0);
		GridData gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, true);
		oldGroup.setLayoutData(gd);
		gl = new GridLayout();
		gl.numColumns = 2;
		gl.makeColumnsEqualWidth = false;
		oldGroup.setLayout(gl);
		oldGroup.setText("Old Video Source: " + oldVideoSrc);
		
		Label oldBeginLabel = new Label(oldGroup, 0x0);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		oldBeginLabel.setLayoutData(gd);
		oldBeginLabel.setText("Old Begin Time: ");
		oldBeginSpinner = new CalendarSpinner(oldGroup, "Begin Time");
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
		oldBeginSpinner.setLayoutData(gd);
		
		Label oldEndLabel = new Label(oldGroup, 0x0);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		oldEndLabel.setLayoutData(gd);
		oldEndLabel.setText("Old End Time: ");
		oldEndSpinner = new CalendarSpinner(oldGroup, "End Time");
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
		oldEndSpinner.setLayoutData(gd);
		
		// New Group
		Group newGroup = new Group(mainComp, 0x0);
		gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, true);
		newGroup.setLayoutData(gd);
		gl = new GridLayout();
		gl.numColumns = 2;
		gl.makeColumnsEqualWidth = false;
		newGroup.setLayout(gl);
		newGroup.setText("New Video Source: " + newVideoSrc);
		
		//  label on left, options on right
		Label newBeginLabel = new Label(newGroup, 0x0);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		newBeginLabel.setLayoutData(gd);
		newBeginLabel.setText("New Begin Time: ");
		newBeginSpinner = new CalendarSpinner(newGroup, "Begin Time");
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
		newBeginSpinner.setLayoutData(gd);
		
		Label newEndLabel = new Label(newGroup, 0x0);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		newEndLabel.setLayoutData(gd);
		newEndLabel.setText("New End Time: ");
		newEndSpinner = new CalendarSpinner(newGroup, "End Time");
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
		newEndSpinner.setLayoutData(gd);
		
		//  Buttons
		Composite btnComp = new Composite(mainComp, 0x0);
		gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
		btnComp.setLayoutData(gd);
		gl = new GridLayout(1, true);
		btnComp.setLayout(gl);
		
		pickLLBtn = new Button(btnComp, SWT.PUSH | SWT.CENTER);
		gd = new GridData(SWT.BEGINNING, SWT.END, false, true);
		pickLLBtn.setLayoutData(gd);
		pickLLBtn.setText(pickText);
		pickLLBtn.setToolTipText(pickTooltipText);		
		
		invokeBtn = new Button(btnComp, SWT.PUSH | SWT.CENTER);
		gd = new GridData(SWT.BEGINNING, SWT.END, false, false);
		invokeBtn.setLayoutData(gd);
		invokeBtn.setText(invokeText);
		invokeBtn.setToolTipText(invokeTooltipText);		
		
		Button pollStatusBtn = new Button(btnComp, SWT.PUSH | SWT.CENTER);
		gd = new GridData(SWT.BEGINNING, SWT.END, false, false);
		pollStatusBtn.setLayoutData(gd);
		pollStatusBtn.setText(pollStatusText);
		pollStatusBtn.setToolTipText(pollStatusTTT);		
		
		// size scroller
		sc.setMinSize(mainComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
}
