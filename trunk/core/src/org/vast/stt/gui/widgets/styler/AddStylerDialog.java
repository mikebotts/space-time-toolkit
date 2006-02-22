package org.vast.stt.gui.widgets.styler;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class AddStylerDialog implements SelectionListener {

	Shell shell;
	StyleWidget styleWidget;
	String [] stylerTypes = { "point", "line", "raster", "label", "polygon" };
	private Combo typeCombo;
	private Text nameText;
	private Button okBtn;
	private Button cancelBtn;
	
	public AddStylerDialog(StyleWidget sw) {
		this.styleWidget = sw;
		init(PlatformUI.getWorkbench().getDisplay().getActiveShell());
	}
	
	private void init(Shell parent){
		GridData gridData;
		shell = 
			new Shell(parent, SWT.APPLICATION_MODAL | SWT.RESIZE | SWT.TITLE | SWT.BORDER | SWT.CLOSE);
		shell.setText("Add Styler Dialog");
		shell.setSize(170,150);
		shell.setMinimumSize(170,150);
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 14;
		shell.setLayout(layout);
		
		//  Type Combo
		Label typeLabel = new Label(shell, SWT.LEFT);
		typeLabel.setText("Styler Type:");
		
		typeCombo = new Combo(shell, SWT.READ_ONLY);
		typeCombo.setItems(stylerTypes);
		typeCombo.select(0);
		
		//  Name textField
		Label nameLabel = new Label(shell, SWT.LEFT);
		nameLabel.setText("Name");
		
		nameText = new Text(shell, SWT.RIGHT);
		nameText.setText("     styler");
		nameText.selectAll();
		gridData = new GridData();
		gridData.minimumHeight = 20;
		gridData.minimumWidth = 60;
		nameText.setLayoutData(gridData);
		
		okBtn = new Button(shell, SWT.PUSH);
		okBtn.setText("OK");
		okBtn.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.widthHint = 55;
		okBtn.setLayoutData(gridData);
		
		cancelBtn = new Button(shell, SWT.PUSH);
		cancelBtn.setText("Cancel");
		cancelBtn.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.widthHint = 55;
		cancelBtn.setLayoutData(gridData);
		
		shell.open();
	}

	public int getStylerType(){
		return typeCombo.getSelectionIndex();
	}
	
	public String getStylerName(){
		return nameText.getText().trim();
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if(e.getSource() == okBtn) { 
			styleWidget.createNewStyler(getStylerName(), getStylerType());
		}  //else if(e.getSource() == cancelBtn)
		
		shell.dispose();
	}

}
