/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.widgets.symbolizer;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vast.stt.style.SymbolizerFactory;
import org.vast.stt.style.SymbolizerFactory.SymbolizerType;

public class AddSymbolizerDialog extends Dialog implements SelectionListener, ModifyListener
{
	private SymbolizerType[] symbolizerTypes;  
	private String [] symbolizerTypeStr;
	private Combo typeCombo;
	private Text nameText;
	private SymbolizerType type;
	private String name;
	private boolean nameModded = false;
	
	public AddSymbolizerDialog(Shell parent){
		super(parent);
		symbolizerTypes = SymbolizerFactory.getSymbolizerTypes();
		symbolizerTypeStr = SymbolizerFactory.getSymbolizerTypeNames();
		this.open();
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
		GridData gridData = new GridData();
		gridData.widthHint = 200;
		comp.setLayoutData(gridData);

		//  Type Combo
		Label typeLabel = new Label(comp, SWT.RIGHT);
		typeLabel.setText("Graphic Type:");
		
		typeCombo = new Combo(comp, SWT.READ_ONLY | SWT.BORDER);
		typeCombo.setItems(symbolizerTypeStr);
		typeCombo.select(0);
		typeCombo.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.TRAIL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 70;
		typeCombo.setLayoutData(gridData);
		
		//  Name textField
		Label nameLabel = new Label(comp, SWT.RIGHT);
		nameLabel.setText("Name:");
		
		nameText = new Text(comp, SWT.RIGHT | SWT.BORDER);
		String str = "   New Point";
		nameText.setText(str);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.TRAIL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 70;
		nameText.setLayoutData(gridData);
		nameText.addModifyListener(this);
		//  No matter where I put selectAll(), by the time the actual dialog is opened
		//  the text is NOT selected.  Grrr!!!
//		nameText.selectAll();
		return comp;
	}

	protected void okPressed() {
		int selIndex = typeCombo.getSelectionIndex();
		type = symbolizerTypes[selIndex];
		name = nameText.getText();
		super.okPressed();
	}
	
	public SymbolizerType getSymbolizerType(){
		return type;
	}
	
	public String getStylerName(){
		return name;
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		//  Change the default name, if the user hasn't already changed it
		if(!nameModded) {
			nameText.setText("New " + symbolizerTypeStr[typeCombo.getSelectionIndex()]);
			nameModded = false;
		}
	}

	public void modifyText(ModifyEvent e) {
		nameModded = true;
	}
}