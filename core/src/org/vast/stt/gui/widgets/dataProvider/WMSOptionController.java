package org.vast.stt.gui.widgets.dataProvider;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vast.stt.data.WMSProvider;
import org.vast.stt.gui.widgets.OptionControl;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;

public class WMSOptionController 
	implements SelectionListener, ModifyListener, VerifyListener, KeyListener 
{
	private WMSProvider provider;
	private OptionControl[] optionControl;
	private Button checkButton;
	private Combo styleCombo;
	private Combo srsCombo;
	private Combo formatCombo;
	private Text heightText;
	private Text widthText;
	
	public WMSOptionController(Composite parent, WMSProvider provider){
		this.provider = provider;
	}

	public void buildAdvancedControls(Composite parent){
		
	}
	
	public void buildBasicControls(Composite parent) {
		// TODO Auto-generated method stub
		optionControl = new OptionControl[6];
		//  Image Width
		optionControl[0] = new OptionControl(parent);
		widthText = optionControl[0].createText("Width:", "500");
		widthText.addKeyListener(this);

		//  Image Height
		optionControl[1] = new OptionControl(parent);
		heightText = optionControl[1].createText("Height:", "500");
		heightText.addKeyListener(this);

		//  Formats
		//String [] formatOpts = (String [])provider.getLayerCapabilities().getFormatList().toArray(new String []{});
		optionControl[2] = new OptionControl(parent);
		String [] formatOpts = {"jpg","png","gif"};
		formatCombo = optionControl[2].createCombo("Format:", formatOpts);
		formatCombo.addSelectionListener(this);

		//  SRS  
		//
		optionControl[3] = new OptionControl(parent);
		String [] srsOpts = {"EPSG:4326","EPSG:4329"};
		srsCombo = optionControl[3].createCombo("SRS:", srsOpts);
		srsCombo.addSelectionListener(this);

		//  Styles  
		//
		optionControl[4] = new OptionControl(parent);
		String [] styleOpts = {"style1","style2"};
		styleCombo = optionControl[4].createCombo("Styles:", styleOpts);
		styleCombo.addSelectionListener(this);
		
		// Transparent Checkbox
		//
		optionControl[5] = new OptionControl(parent);
		boolean trans = true;
		checkButton = optionControl[5].createCheckbox("Transparency:", trans);
		checkButton.addSelectionListener(this);
		
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		System.err.println(e);
	}

	public void modifyText(ModifyEvent e) {
		// TODO Auto-generated method stub
		System.err.println(e);
	}

	public void verifyText(VerifyEvent e) {
		// TODO Auto-generated method stub
		System.err.println(e);
	}

	public void keyPressed(KeyEvent e) {
		//  if e.control == numericText
		e.doit = (e.keyCode >=48 && e.keyCode <= 57);
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}


}
