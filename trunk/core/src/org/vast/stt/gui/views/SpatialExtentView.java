package org.vast.stt.gui.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.data.DataProvider;
import org.vast.stt.scene.DataEntry;
import org.vast.stt.scene.DataItem;
import org.vast.stt.util.SpatialExtent;

/**
 *  View Class for modding geographic region of a dataItem.
 * 
 * @author tcook
 * @date 12/13/05
 * 
 * TODO  Could support other formats (dd'mm"ss, dd'mm.mm")  like the old CompassPanel, but 
 *       I need to figure out how to enforce editor behavior on SWT Text Widgets to make this work.
 */

public class SpatialExtentView  extends ViewPart implements SelectionListener,  ISelectionListener
{
	public static final String ID = "STT.BboxView";
	LabelText nlatLt, slatLt, wlonLt, elonLt;
	Label itemLabel;
	Combo formatCombo;
	Button fitBtn;
	//  Not sure I really need a persistent pointer to the currently selected 
	//  LlFormat.  Can just get this from formatCombo.  Remove if it's not needed.
	static final int DEGREES = 0;
	static final int RADIANS = 1;
	int currentLlFormat; 
	
	public void createPartControl(Composite parent)
	{
		initView(parent);
		getSite().getPage().addPostSelectionListener(SceneTreeView.ID, this);
	}
	
	/**
	 * initView
	 * @param parent
	 */
	public void initView(Composite parent){
		//  Scrolled Composite to hold everything
		ScrolledComposite scroller = new ScrolledComposite(parent, SWT.SHADOW_ETCHED_OUT | SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setExpandHorizontal(true);
	    scroller.setExpandVertical(true);

	    //  Main Group Holds Label, CompassGroup, Format Combo, and Fit Button
		Group mainGroup = new Group(scroller, SWT.SHADOW_NONE);
		scroller.setContent(mainGroup);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 10;
		mainGroup.setLayout(layout);
		
		//  Create Label
		itemLabel = new Label(mainGroup, SWT.SHADOW_IN);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		itemLabel.setLayoutData(gridData);
		//  Create a Group for the compass Grid
		Group compassGroup = new Group(mainGroup, SWT.SHADOW_NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		compassGroup.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		compassGroup.setLayout(gridLayout);
		//  Create Buttons
		nlatLt = new LabelText(compassGroup, "northLat");
		wlonLt = new LabelText(compassGroup, "westLon");
		Label compassBtn = new Label(compassGroup, SWT.SHADOW_IN);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.CENTER;
		gridData.horizontalAlignment = SWT.CENTER;
		compassBtn.setLayoutData(gridData);
		ImageDescriptor descriptor = STTPlugin.getImageDescriptor("icons/compass1.gif");
		Image compImg = descriptor.createImage();
		compassBtn.setImage(compImg);
		//compassBtn.setImage(Image)
		elonLt = new LabelText(compassGroup, "eastLon");
		slatLt = new LabelText(compassGroup, "southLat");
		
		//  Layout Btns
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 3;
		nlatLt.setLayoutData(gridData);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		gridData.horizontalSpan = 1;
		gridData.verticalAlignment = SWT.CENTER;
		wlonLt.setLayoutData(gridData);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		gridData.horizontalSpan = 1;
		gridData.verticalAlignment = SWT.CENTER;
		elonLt.setLayoutData(gridData);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 3;

		slatLt.setLayoutData(gridData);
		
		//  Format combo
		Group formatGrp = new Group(mainGroup, SWT.SHADOW_NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
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
		currentLlFormat = DEGREES;  //  default to DEGREES 
		
		//  Fit Button
		fitBtn = new Button(mainGroup, SWT.PUSH);
		fitBtn.setText("Fit to Compass");
		fitBtn.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		fitBtn.setLayoutData(gridData);
		//  Set Default scroller size
		scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public SpatialExtent getSpatitalExtent(){
		double nlat = nlatLt.getValue();
		double slat = slatLt.getValue();
		double wlon = wlonLt.getValue();
		double elon = elonLt.getValue();
		SpatialExtent ext = new SpatialExtent();
		ext.setMinX(wlon);
		ext.setMinY(slat);
		ext.setMaxX(elon);
		ext.setMaxY(nlat);
		return ext;
	}
	
	//  Use org.vast.stt.util.SpatialExtent, or Bbox here? 
	public void setSpatialExtent(SpatialExtent bbox){
		nlatLt.setValue(bbox.getMaxY());
		slatLt.setValue(bbox.getMinY());
		wlonLt.setValue(bbox.getMinX());
		elonLt.setValue(bbox.getMaxX());
	}
	
	public void setFormat(int formatIndex){
		if(formatIndex == currentLlFormat)
			return;
		currentLlFormat = formatIndex;
		SpatialExtent bbox = this.getSpatitalExtent();
		double minX = bbox.getMinX();
		double maxX = bbox.getMaxX();
		double minY = bbox.getMinY();
		double maxY = bbox.getMaxY();
		double factor = 1.0;
		switch(currentLlFormat){
			case DEGREES:
				factor = 180.0/Math.PI;
				break;
			case RADIANS:
				factor = Math.PI/180.0;  //UnitConversion.getFactorToSI("deg");
				break;
			default: 
				break;
		}
		minX *= factor;
		maxX *= factor;
		minY *= factor;
		maxY *= factor;
		bbox.setMinX(minX);
		bbox.setMinY(minY);
		bbox.setMaxX(maxX);
		bbox.setMaxY(maxY);
		this.setSpatialExtent(bbox);
	}
	
	@Override
	public void setFocus()
	{
		
	}

	@Override
	public void dispose()
	{
		//getSite().getPage().removePostSelectionListener(this);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		//System.err.println(e);
		if(e.widget == fitBtn){
			double nlat = nlatLt.getValue();
			double slat = slatLt.getValue();
			double wlon = wlonLt.getValue();
			double elon = elonLt.getValue();
			System.out.println("Fit: " + wlon + ", " + slat + " ==> " + elon + ", " + nlat);
		} else if (e.widget == formatCombo){
			//System.err.println("Selection Index = " + formatCombo.getSelectionIndex());
			this.setFormat(formatCombo.getSelectionIndex());
		}
	}
	
	/**
	 *  A simple inline class that groups a label and
	 *  a text field in a vertical row 
	 */
	class LabelText {
		Group group;
		String labelString;
		Text text;
		
		public LabelText(Composite parent, String labelStr){
			this.labelString = labelStr;
			
			group = new Group(parent, SWT.SHADOW_NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			group.setLayout(layout);

			Label label = new Label(group, SWT.SHADOW_OUT | SWT.CENTER);
			GridData labelData = new GridData();
			labelData.horizontalAlignment = GridData.CENTER;
			label.setText(labelStr);
			label.setLayoutData(labelData);
			text = new Text(group, SWT.RIGHT);
			GridData textData = new GridData();
			textData.horizontalAlignment = GridData.FILL;
			textData.widthHint = 50;
			text.setLayoutData(textData);
		}
		
		public void setLayoutData(GridData ld){
			group.setLayoutData(ld);
		}

		public double getValue(){
			double value;
			try {
				value = Double.parseDouble(text.getText());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				System.err.println("Could not parse " + text.getText() + " in " + labelString + " field.");
				return 0.0;
			}

			return value;
		}
			
		public void setValue(double d){
			String dubStr = Double.toString(d);
			text.setText(dubStr);
		}
		
		public void setValue(double d, boolean b){
			String dubStr = Double.toString(d);
			// 	Hack to widen Text widget
			int strLen = dubStr.length();
			int maxLen = 12;
			int pad = maxLen - strLen;
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<pad; i++){
				sb.append(" ");
			}
			sb.append(dubStr);
			text.setText(sb.toString());
		}
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		if (part instanceof SceneTreeView)
		{
			DataEntry selectedItem = (DataEntry)((IStructuredSelection)selection).getFirstElement();
			if(selectedItem instanceof DataItem) {
				System.err.println("item " + selectedItem);
				itemLabel.setText(selectedItem.getName());
				DataProvider prov = ((DataItem)selectedItem).getDataProvider();
				//  If provider is null, this widget isn't supported.
				if(prov!=null) {
					SpatialExtent ext = prov.getSpatialExtent();
					this.setSpatialExtent(ext);
				}
			} else {
				//  TODO  support BBox for DataItemList
			}
		}		
	}

}
