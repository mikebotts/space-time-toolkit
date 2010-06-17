package org.vast.sttx.gui.widgets.WPS_Demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.vast.stt.gui.widgets.time.CalendarSpinner;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.tree.DataItemIterator;
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
	final String [] pickText = new String [] { "Pick Begin Point", "Pick End Point" };
	final String [] pickTTT = new String [] { "Interactively select Beginning point from World View", 
											   "Interactively select BegEndinning point from World View"};
	final String pickCancelText = "Cancel Pick";
	final String pickCancelTooltipText ="Cancel Interactive Lat/Lon Picking Mode";
	final String invokeText = "Invoke WPS";
	final String invokeTooltipText = "Invoke WPS with the currently selected parameters";
	final String pollStatusText = "Poll WPS";
	final String pollStatusTTT = "Poll Status URL provided by WPS";
	final String retrieveVideoText = "";
	final String retrieveVidTTT = "";
	protected Button [] pickBtn;
	protected Button invokeBtn;
	private WPSWidgetController controller;
	protected CalendarSpinner newBeginSpinner;
	protected CalendarSpinner oldBeginSpinner;
	protected CalendarSpinner oldEndSpinner;
	protected CalendarSpinner newEndSpinner;
	protected Map<String,DataItem> possibleTracks;
	private Combo oldList;
	private Combo newList;
	protected Button pollStatusBtn;
	protected Text pollIntervalText;
	
	public WPS_DemoWidget(Composite parent)
	{
		initGui(parent);
		controller = new WPSWidgetController(this);
		addListeners(controller);
	}

	public void addListeners(WPSWidgetController cont){
		// zoomSp
		pickBtn[0].addSelectionListener(cont);
		pickBtn[1].addSelectionListener(cont);
		invokeBtn.addSelectionListener(cont);
		pollStatusBtn.addSelectionListener(cont);
	}

	public void enableAllButtons(){
		 pickBtn[0].setEnabled(true);
	     pickBtn[1].setEnabled(true);
	     invokeBtn.setEnabled(true);
	     pollStatusBtn.setEnabled(true);
	}
	
	public void setScene(WorldScene scene){
		 controller.setScene(scene);
		 DataItemIterator it = scene.getDataTree().getItemIterator();
		 DataItem itemTmp;
		 possibleTracks = new HashMap<String, DataItem>(10);
		 while(it.hasNext()) {
			 itemTmp = it.next();
			 if(itemTmp.getName().contains("Track"))
				 possibleTracks.put(itemTmp.getName(), itemTmp);
		 }
		 Set<String> names = possibleTracks.keySet();
		 String [] namesArr = names.toArray(new String[] {});
		 Arrays.sort(namesArr);
		 
		 oldList.setItems(namesArr);
		 newList.setItems(namesArr);
		 oldList.select(1);
		 newList.select(0);
		 enableAllButtons();
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
		oldGroup.setText("Old Video");
		
		Label oldSrcLabel = new Label(oldGroup, 0x0);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		oldSrcLabel.setLayoutData(gd);
		oldSrcLabel.setText("Old Source: ");
		
		oldList = new Combo(oldGroup, 0x0);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		oldList.setLayoutData(gd);
		
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
		newGroup.setText("New Video");
		
		Label newSrcLabel = new Label(newGroup, 0x0);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		newSrcLabel.setLayoutData(gd);
		newSrcLabel.setText("New Source: ");

		newList = new Combo(newGroup, 0x0);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		newList.setLayoutData(gd);
		
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
		gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, true);
		btnComp.setLayoutData(gd);
		gl = new GridLayout(2, false);
		gl.verticalSpacing = 12;
		btnComp.setLayout(gl);
		
		pickBtn = new Button[2];
		
		pickBtn[0] = new Button(btnComp, SWT.PUSH | SWT.CENTER);
		gd = new GridData(SWT.FILL, SWT.END, true, true);
		gd.horizontalSpan = 2;
		pickBtn[0].setLayoutData(gd);
		pickBtn[0].setText(pickText[0]);
		pickBtn[0].setToolTipText(pickTTT[0]);		
		pickBtn[0].setEnabled(false);

		pickBtn[1] = new Button(btnComp, SWT.PUSH | SWT.CENTER);
		gd = new GridData(SWT.FILL, SWT.END, true, true);
		gd.horizontalSpan = 2;
		pickBtn[1].setLayoutData(gd);
		pickBtn[1].setText(pickText[1]);
		pickBtn[1].setToolTipText(pickTTT[1]);		
		pickBtn[1].setEnabled(false);
		
		invokeBtn = new Button(btnComp, SWT.PUSH | SWT.CENTER);
		gd = new GridData(SWT.FILL, SWT.END, true, false);
		gd.horizontalSpan = 2;
		invokeBtn.setLayoutData(gd);
		invokeBtn.setText(invokeText);
		invokeBtn.setToolTipText(invokeTooltipText);		
		invokeBtn.setEnabled(false);
		
		pollStatusBtn = new Button(btnComp, SWT.PUSH | SWT.CENTER);
		gd = new GridData(SWT.FILL, SWT.END, true, false);
		gd.horizontalSpan = 2;
		pollStatusBtn.setLayoutData(gd);
		pollStatusBtn.setText(pollStatusText);
		pollStatusBtn.setToolTipText(pollStatusTTT);	
		pollStatusBtn.setEnabled(false);
		
		Label pollPeriodLabel = new Label(btnComp, 0x0);
		gd = new GridData(SWT.BEGINNING, SWT.END, true, false);
		pollPeriodLabel.setLayoutData(gd);
		pollPeriodLabel.setText("Poll Interval (s): ");
		pollPeriodLabel.setEnabled(false);
		
		pollIntervalText = new Text(btnComp, 0x0);
		gd = new GridData(SWT.BEGINNING, SWT.END, false, false);
		pollIntervalText.setLayoutData(gd);
		pollIntervalText.setText("   30");
		pollIntervalText.setEnabled(false);
		
		// size scroller
		sc.setMinSize(mainComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public DataItem getOldTrackItem(){
		String name = oldList.getText();
		
		DataItem oldItem = possibleTracks.get(name);
		return oldItem;
	}
	
	public DataItem getNewTrackItem(){
		String name = newList.getText();
		
		DataItem newItem = possibleTracks.get(name);
		return newItem;
	}
}
