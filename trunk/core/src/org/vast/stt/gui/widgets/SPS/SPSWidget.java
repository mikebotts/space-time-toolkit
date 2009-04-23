package org.vast.stt.gui.widgets.SPS;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;


/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class SPSWidget
{
	Text lonText;
	Text latText;
	Text altText;
	Spinner zoomSpinner;
//	enum Mode { PICK, CANCEL};
	//Mode currentMode = Mode.PICK;
	double defaultLat = 34.725, defaultLon = -86.645, defaultAlt = 300;
	Button submitNowBtn;
	private Group group2;
	Button pickLLBtn;
	private Label latLabel;
	Button autoSubmitBtn;
	private Label zoomLabel;
	private Label altLabel;
	private Label lonLabel;
	private Group group1;
	private ScrolledComposite scrolledComposite1;
	//	private double oldLat = defaultLat, oldLon=defaultLon; 
	final String pickText = "Pick Lat/Lon";
	final String pickTooltipText = "Interactively select Lat/Lon from World View";
	final String pickCancelText = "Cancel Pick";
	final String pickCancelTooltipText ="Cancel Interactive Lat/Lon Picking Mode";
	//  Scene hnadle needed to allow picking
	WorldScene scene;
	SPSWidgetController controller;

	public SPSWidget(Composite parent)
	{
		initGui(parent);
		controller = new SPSWidgetController(this);
		addListeners(controller);
	}


	/**  FIX!!! **/
	public void setDataItem(DataItem item){
	}
	
    public void setScene(WorldScene scene){
		 controller.setScene(scene);
	     pickLLBtn.setEnabled(true);
	     submitNowBtn.setEnabled(true);
    }
	
	public void addListeners(SPSWidgetController cont){
		// zoomSp
		autoSubmitBtn.addSelectionListener(cont);
		pickLLBtn.addSelectionListener(cont);
		submitNowBtn.addSelectionListener(cont);
	}

	public void initGui(Composite parent)
	{
		{
			scrolledComposite1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			FillLayout scrolledComposite1Layout = new FillLayout(org.eclipse.swt.SWT.VERTICAL);
			scrolledComposite1Layout.type = SWT.VERTICAL;
			scrolledComposite1.setLayout(scrolledComposite1Layout);
			scrolledComposite1.setExpandHorizontal(true);
			scrolledComposite1.setExpandVertical(true);
			//			scrolledComposite.setContent(scrolledComposite1);
			scrolledComposite1.setSize(438, 190);
			{
				group1 = new Group(scrolledComposite1, SWT.NONE);
				scrolledComposite1.setContent(group1);
				GridLayout group1Layout = new GridLayout();
				group1Layout.numColumns = 4;
				group1.setLayout(group1Layout);
				group1.setText("SPS Camera Controller");
				{
					latLabel = new Label(group1, SWT.NONE);
					GridData latLabelLData = new GridData();
					latLabelLData.widthHint = 33;
					latLabelLData.verticalAlignment = GridData.FILL;
					latLabel.setLayoutData(latLabelLData);
					latLabel.setText("Lat:");
				}
				{
					latText = new Text(group1, SWT.NONE);
					latText.setText("      " + defaultLat);
				}
				{
					lonLabel = new Label(group1, SWT.NONE);
					GridData lonLabelLData = new GridData();
					lonLabelLData.horizontalIndent = 29;
					lonLabel.setLayoutData(lonLabelLData);
					lonLabel.setText("Lon:");
				}
				{
					lonText = new Text(group1, SWT.NONE);
					GridData text2LData = new GridData();
					text2LData.horizontalAlignment = GridData.CENTER;
					lonText.setText("      " + defaultLon);
				}
				{
					altLabel = new Label(group1, SWT.NONE);
					altLabel.setText("Alt:");
				}
				{
					altText = new Text(group1, SWT.NONE);
					altText.setText("      " + defaultAlt);
				}
				{
					zoomLabel = new Label(group1, SWT.NONE);
					GridData zoomLabelLData = new GridData();
					zoomLabelLData.horizontalIndent = 29;
					zoomLabel.setLayoutData(zoomLabelLData);
					zoomLabel.setText("Zoom:");
				}
				{
					zoomSpinner = new Spinner(group1, 0x0);
					//					gd = new GridData();
					//					gd.horizontalAlignment = SWT.BEGINNING;
					//					gd.verticalAlignment = SWT.CENTER;
					GridData zoomSpData = new GridData();
					zoomSpData.horizontalAlignment = GridData.END;
					zoomSpinner.setMinimum(1);
					zoomSpinner.setMinimum(70);
					zoomSpinner.setValues(10, 5, 70, 0, 1, 10);
					GridData zoomSpinnerLData = new GridData();
					zoomSpinnerLData.horizontalAlignment = GridData.CENTER;
					zoomSpinner.setSelection(55);
				}
				{
					group2 = new Group(group1, SWT.NONE);
					GridLayout group2Layout = new GridLayout();
					group2Layout.makeColumnsEqualWidth = true;
					group2Layout.numColumns = 3;
					group2.setLayout(group2Layout);
					GridData group2LData = new GridData();
					group2LData.horizontalSpan = 4;
					group2LData.widthHint = 325;
					group2LData.heightHint = 47;
					group2.setLayoutData(group2LData);
					group2.setText("Submit Controls");
					{
						pickLLBtn = new Button(group2, SWT.PUSH | SWT.CENTER);
						GridData pickLLBtnLData = new GridData();
						pickLLBtn.setLayoutData(pickLLBtnLData);
						pickLLBtn.setText(pickText);
						pickLLBtn.setToolTipText(pickTooltipText);
					}
					{
						submitNowBtn = new Button(group2, SWT.PUSH | SWT.CENTER);
						GridData submitNowBtnLData = new GridData();
						submitNowBtnLData.horizontalAlignment = GridData.CENTER;
						GridData submitNowBtnLData1 = new GridData();
						submitNowBtnLData1.horizontalAlignment = GridData.END;
						submitNowBtnLData1.horizontalIndent = 5;
						submitNowBtn.setLayoutData(submitNowBtnLData1);
						submitNowBtn.setText("Submit Now");
						submitNowBtn.setToolTipText("Submit SPS request with currently chosen parameters");
					}
					{
						autoSubmitBtn = new Button(group2, SWT.CHECK | SWT.LEFT);
						GridData autoSubmitButtonLData = new GridData();
						autoSubmitButtonLData.horizontalAlignment = GridData.CENTER;
						GridData autoSubmitButtonLData1 = new GridData();
						autoSubmitButtonLData1.horizontalAlignment = GridData.END;
						autoSubmitButtonLData1.horizontalIndent = 11;
						autoSubmitBtn.setLayoutData(autoSubmitButtonLData1);
						autoSubmitBtn.setText("Auto Submit");
						autoSubmitBtn.setToolTipText("Submit request automatically when choosing Lat/Lon from World View");
						//  suspend for now until WOrldView mouse interaction fixed
//						autoSubmitBtn.setEnabled(false);
					}
				}
			}
		}

	}
}
