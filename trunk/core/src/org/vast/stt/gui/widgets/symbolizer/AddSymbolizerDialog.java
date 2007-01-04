package org.vast.stt.gui.widgets.symbolizer;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vast.stt.style.SymbolizerFactory.SymbolizerType;

public class AddSymbolizerDialog extends Dialog {
	private SymbolizerType[] symbolizerTypes;  
	private String [] symbolizerTypeStr;
	private Combo typeCombo;
	private Text nameText;
	private SymbolizerType type;
	private String name;
	
	public AddSymbolizerDialog(Shell parent){
		super(parent);
		initSymbolizerTypes();
		this.open();
	}

	//  Generate string [] for types 
	private void initSymbolizerTypes(){
		symbolizerTypes = SymbolizerType.values();
		symbolizerTypeStr = new String[symbolizerTypes.length];
		int i=0;
		for(SymbolizerType st : symbolizerTypes){
			symbolizerTypeStr[i++] = st.toString();
		}
	}
	
	protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Add Graphic");
    }

	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite)super.createDialogArea(parent);
		GridLayout gl = new GridLayout(2, false);
		gl.verticalSpacing = 14;
		comp.setLayout(gl);

		//  Type Combo
		Label typeLabel = new Label(comp, SWT.RIGHT);
		GridData gridData = new GridData();
		typeLabel.setText("Graphic Type:");
		
		typeCombo = new Combo(comp, SWT.READ_ONLY | SWT.BORDER);
		typeCombo.setItems(symbolizerTypeStr);
		typeCombo.select(0);
		gridData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
		typeCombo.setLayoutData(gridData);
		
		//  Name textField
		Label nameLabel = new Label(comp, SWT.RIGHT);
		nameLabel.setText("Name:");
		
		nameText = new Text(comp, SWT.RIGHT | SWT.BORDER);
		String str = "   New Graphic";
		nameText.setText(str);
		gridData = new GridData();
		//gridData.minimumHeight = 20;
		//gridData.minimumWidth = 60;
		nameText.setLayoutData(gridData);
		//  No matter where I put selectAll(), by the time the actual dialog is opened
		//  the text is NOT selected.  Grrr!!!
//		nameText.selectAll();
		return comp;
	}

	protected void okPressed() {
		type = symbolizerTypes[typeCombo.getSelectionIndex()];
		name = nameText.getText();
		super.okPressed();
	}
	
	public SymbolizerType getSymbolizerType(){
		return type;
	}
	
	public String getStylerName(){
		return name;
	}
}