package org.vast.stt.gui.widgets.styler;


import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.sld.Geometry;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.style.DataStyler;

/**
 * 
 * 
 * @author tcook
 *
 */
public class AdvancedGeometryTab extends Composite 
	implements SelectionListener
{

	Composite parent;
	String [] mapToLabel = {	"X Coordinate:",
							   	"Y Coordinate:",
							   	"Z Coordinate:",
								"t coordinate:" };
	Combo [] mapFromCombo;
	Button[] lutButton;
	DataStyler activeStyler;
	String [] mappableItems;
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
			mapFromCombo[i] = new Combo(this, SWT.READ_ONLY);
			mapFromCombo[i].setBackground(WHITE);
			mapFromCombo[i].addSelectionListener(this);
			lutButton[i] = new Button(this, SWT.PUSH);
			lutButton[i].setText("LUT");
			lutButton[i].setBackground(WHITE);
			lutButton[i].addSelectionListener(this);
		}
	}

	// TODO:  load these based on actual mappable items	
	public void setMappableItems(String [] items){
		mappableItems = items;
		for (int i=0; i<mapFromCombo.length; i++) {
			mapFromCombo[i].setItems(items);
			//mapFromCombo[i].select(0);
		}
	}
	
	//  active Styler should NOT be a compositeStyler here  
	public void setActiveStyler(DataStyler styler){
		this.activeStyler = styler;
		updateMappingCombos();
	}
	
	/**
	 * sets the default state of all mapFromCombos
	 *
	 */
	private void updateMappingCombos(){
		if(activeStyler == null){
			System.err.println("AdvancedGeomTab.updateMappings");
		}
		Geometry geom = activeStyler.getSymbolizer().getGeometry();
		String xName = geom.getX().getPropertyName();
		String yName = geom.getY().getPropertyName();
		String zName = geom.getZ().getPropertyName();
		String tName = geom.getT().getPropertyName();
		int xIndex = findName(mappableItems, xName);
		int yIndex = findName(mappableItems, yName);
		int zIndex = findName(mappableItems, zName);
		int tIndex = findName(mappableItems, tName);

		if(xIndex >=0)
			mapFromCombo[0].select(xIndex);
		if(yIndex >=0)
			mapFromCombo[1].select(yIndex);
		if(zIndex >=0)
			mapFromCombo[2].select(zIndex);
		if(tIndex >=0)
			mapFromCombo[3].select(tIndex);
	}
	
	private int findName(String [] srcArr, String target){
		for(int i=0; i<srcArr.length; i++){
			if(srcArr[i].equalsIgnoreCase(target))
				return i;
		}
		return -1;
	}
	
	public void close(){
	}

	private void doLut(int index){
		
	}
	
	private void doMapping(int index){
		Geometry geom = activeStyler.getSymbolizer().getGeometry();
		int selIndex = mapFromCombo[index].getSelectionIndex();
		ScalarParameter sp = new ScalarParameter();
		sp.setPropertyName(mappableItems[selIndex]);
		switch(index){
		case 0:
			geom.setX(sp);
			break;
		case 1:
			geom.setY(sp);
			break;
		case 2:
			geom.setZ(sp);
			break;
		case 3:
			geom.setT(sp);
			break;
		default:
			break;
		}
		activeStyler.updateDataMappings();
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control source = (Control)e.getSource();
		for(int i=0; i<lutButton.length; i++){
			if(source == lutButton[i]) {
				doLut(i);
				return;
			} 
			if(source == mapFromCombo[i]) {
				doMapping(i);
				return;
			}
		}
	}
}
