package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.sld.Geometry;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;


/**
 * 
 * <p><b>Title:</b>
 * Advanced Geometry Tab
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO AdvancedGeometryTab type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class AdvancedGeometryTab extends ScrolledComposite implements SelectionListener
{
	Composite mainGroup;
	String [] mapToLabel = {	"X Coordinate:",
							   	"Y Coordinate:",
							   	"Z Coordinate:",
								"t coordinate:",
								"break coordinate"};
	Combo [] mapFromCombo;
	Button[] lutButton;
    Symbolizer activeSymbolizer;
	String [] mappableItems;
	Display display = PlatformUI.getWorkbench().getDisplay();
	final Color WHITE = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE);
						
	public AdvancedGeometryTab(Composite parent){
		super(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		init();
	}
	
	public void init(){
		this.setExpandVertical(true);
		this.setExpandHorizontal(true);
		//  ??
		this.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		this.setBackground(WHITE);
		
	    mainGroup = new Composite(this, 0x0);
		this.setContent(mainGroup);
			
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		mainGroup.setLayout(gridLayout);
		mainGroup.setBackground(WHITE);
		addTopRow();
		addMappingRows();
		this.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	public void addTopRow(){
		//  Add Labels for top row
		Label toLabel = new Label(mainGroup, SWT.LEFT);
		Label fromLabel = new Label(mainGroup, SWT.LEFT);
		Label lutLabel = new Label(mainGroup, SWT.LEFT);
		toLabel.setText("Map To:");
		fromLabel.setText("Map From:");
		lutLabel.setText("Edit");
		toLabel.setBackground(WHITE);
		fromLabel.setBackground(WHITE);
		lutLabel.setBackground(WHITE);
	}
	
	public void addMappingRows(){
		mapFromCombo = new Combo[mapToLabel.length];
		lutButton = new Button[mapToLabel.length];
		for(int i=0; i<mapToLabel.length; i++){
			Label label = new Label(mainGroup, SWT.LEFT);
			label.setText(mapToLabel[i]);
			label.setBackground(WHITE);
			GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
			gd.minimumWidth = 100;
			//gd.widthHint = 120;
			label.setLayoutData(gd);
			mapFromCombo[i] = new Combo(mainGroup, SWT.READ_ONLY | SWT.LEFT);
			mapFromCombo[i].setBackground(WHITE);
			mapFromCombo[i].setTextLimit(20);
			mapFromCombo[i].addSelectionListener(this);
			gd = new GridData();
			//gd.widthHint = 30;
			mapFromCombo[i].setLayoutData(gd);

			//  set enabled state
			lutButton[i] = new Button(mainGroup, SWT.PUSH);
			lutButton[i].setText("Edit");
			lutButton[i].setBackground(WHITE);
			lutButton[i].addSelectionListener(this);
		}
	}

	// TODO:  load these based on actual mappable items	
	public void setMappableItems(String [] items){
		mappableItems = items;
		for (int i=0; i<mapFromCombo.length; i++) {
			mapFromCombo[i].setItems(items);
			mapFromCombo[i].setTextLimit(20);
		}
	}
	
	//  active Styler should NOT be a compositeStyler here  
	public void setActiveSymbolizer(Symbolizer symbolizer){
		this.activeSymbolizer = symbolizer;
		updateMappingCombos();
		//  recompute scroller minSize, as it may have changed
		this.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	/**
	 * sets the default state of all mapFromCombos
	 *
	 *  TODO - support constants, LUTS-
	 */
	private void updateMappingCombos(){
		if(activeSymbolizer == null){
			System.err.println("AdvancedGeomTab.updateMappings - null styler");
			return;
		}
		Geometry geom = activeSymbolizer.getGeometry();
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
		Geometry geom = activeSymbolizer.getGeometry();
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
		// TODO send event 
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
