package org.vast.stt.gui.widgets.styler;


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
import org.vast.stt.style.StylerFactory.StylerType;

public class AddStylerDialog extends Dialog {
	private StylerType[] stylerTypes;  
	private String [] stylerTypeStr; //= { "point", "line", "grid", "polygon", "raster", "texture", "label"};
	private Combo typeCombo;
	private Text nameText;
	private StylerType type;
	private String name;
	
	public AddStylerDialog(Shell parent){
		super(parent);
		initStylerTypes();
		this.open();
	}

	//  Generate string [] for types 
	private void initStylerTypes(){
		stylerTypes = StylerType.values();
		stylerTypeStr = new String[stylerTypes.length];
		int i=0;
		for(StylerType st : stylerTypes){
			stylerTypeStr[i++] = st.toString();
		}
	}
	
	protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Add Styler");
    }

	protected Control createDialogArea(Composite parent) {
		Composite comp = new Composite(parent, 0x0);
		comp.setLayout(new GridLayout(2, false));

		//  Type Combo
		Label typeLabel = new Label(comp, SWT.RIGHT);
		GridData gridData = new GridData();
		typeLabel.setText("Styler Type:");
		
		typeCombo = new Combo(comp, SWT.READ_ONLY);
		typeCombo.setItems(stylerTypeStr);
		typeCombo.select(0);
		
		//  Name textField
		Label nameLabel = new Label(comp, SWT.RIGHT);
		nameLabel.setText("Name:");
		
		nameText = new Text(comp, SWT.RIGHT);
		nameText.setText("     styler");
		nameText.selectAll();
		gridData = new GridData();
		//gridData.minimumHeight = 20;
		//gridData.minimumWidth = 60;
		nameText.setLayoutData(gridData);
	
		return comp;
	}

	protected void okPressed() {
		type = stylerTypes[typeCombo.getSelectionIndex()];
		name = nameText.getText();
		super.okPressed();
	}
	
	public StylerType getStylerType(){
		return type;
	}
	
	public String getStylerName(){
		return name;
	}
}