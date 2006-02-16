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
 *
 * 
 */
public class AdvancedGeometryTab extends Composite 
	implements SelectionListener
{
	Composite parent;
	String [] mapToLabel = {	"X Coordinate:",
							   	"Y Coordinate:",
							   	"Z Coordinate:",
								"t coordinate:",
								"break coordinate"};
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
	 *  TODO - support constants, LUTS-
	 */
	private void updateMappingCombos(){
		if(activeStyler == null){
			System.err.println("AdvancedGeomTab.updateMappings - null styler");
			return;
		}
		Geometry geom = activeStyler.getSymbolizer().getGeometry();
		ScalarParameter xparam = geom.getX();
		ScalarParameter yparam = geom.getY();
		ScalarParameter zparam = geom.getZ();
		ScalarParameter tparam = geom.getT();
		ScalarParameter breaksParam = geom.getBreaks();
		if(xparam != null) {
			String xName = xparam.getPropertyName();
			if(xName != null) {
				int xIndex = findName(mappableItems, xName);
				if(xIndex >=0)
					mapFromCombo[0].select(xIndex);
			}
		}
		if(yparam != null) {
			String name = yparam.getPropertyName();
			if(name != null) {
				int index = findName(mappableItems, name);
				if(index >=0)
					mapFromCombo[1].select(index);
			}
		}
		if(zparam != null) {
			String name = zparam.getPropertyName();
			if(name != null) {
				int index = findName(mappableItems, name);
				if(index >=0)
					mapFromCombo[2].select(index);
			}
		}
		if(tparam != null) {
			String name = tparam.getPropertyName();
			if(name != null) {
				int index = findName(mappableItems, name);
				if(index >=0)
					mapFromCombo[3].select(index);
			}
		}
		if(breaksParam != null) {
			String name = breaksParam.getPropertyName();
			if(name != null) {
				int index = findName(mappableItems, name);
				if(index >=0)
					mapFromCombo[4].select(index);
			}
		}
	}
	
	private int findName(String [] srcArr, String target){
		String stmp;
		int lastSlashIndex;
		for(int i=0; i<srcArr.length; i++){
			lastSlashIndex = target.lastIndexOf('/');
			stmp = (lastSlashIndex == -1) ? target 
			    : target.substring(lastSlashIndex+1);
					
			if(stmp.equalsIgnoreCase(srcArr[i]))
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
		case 4:
			geom.setBreaks(sp);
			break;
		default:
			break;
		}
		activeStyler.updateDataMappings();
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

	public void widgetDefaultSelected(SelectionEvent e) {
	}
}
