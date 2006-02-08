package org.vast.stt.gui.widgets.styler;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

public class AdvancedGeometryTab extends Composite implements SelectionListener{

	Composite parent;
	String [] mapToLabel = {	"X Coordinate:",
							   	"Y Coordinate:",
							   	"Z Coordinate:",
								"t coordinate:" };
	Combo [] mapFromCombo;
	Button[] lutButton;
	final Color WHITE = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE);
						
	public AdvancedGeometryTab(Composite parent){
		super(parent, SWT.BORDER);
		this.parent = parent;
		init();
	}
	
	public void init(){
		this.setBackground(WHITE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
//		gridLayout.horizontalSpacing = 10;
		this.setLayout(gridLayout);
		addTopRow();
		addMappingRows();
	}

	public void addTopRow(){
		//  Add Labels for top row
		Label toLabel = new Label(this, SWT.LEFT);
		Label fromLabel = new Label(this, SWT.LEFT);
		Label lutLabel = new Label(this, SWT.LEFT);
		toLabel.setText("Map To:");
		fromLabel.setText("MapFrom:");
		lutLabel.setText("");
		toLabel.setBackground(WHITE);
		fromLabel.setBackground(WHITE);
		lutLabel.setBackground(WHITE);
	}
	
	public void addMappingRows(){
		mapFromCombo = new Combo[mapToLabel.length];
		lutButton = new Button[mapToLabel.length];
		for(int i=0; i<mapToLabel.length; i++){
			Label label = new Label(this, SWT.LEFT);
			label.setText(mapToLabel[i]);
			label.setBackground(WHITE);
			GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gd.minimumWidth = 120;
			//gd.widthHint = 120;
			label.setLayoutData(gd);
			mapFromCombo[i] = new Combo(this, 0x0);
			mapFromCombo[i].setBackground(WHITE);
			mapFromCombo[i].addSelectionListener(this);
			lutButton[i] = new Button(this, SWT.PUSH);
			lutButton[i].setText("LUT");
			lutButton[i].setBackground(WHITE);
			lutButton[i].addSelectionListener(this);
		}
	}
	
	public void close(){
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		System.err.println(e);
	}
}
