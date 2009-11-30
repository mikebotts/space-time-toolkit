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

package org.vast.stt.gui.widgets.bbox;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.STTSpatialExtent;


public class BboxWidget implements SelectionListener, KeyListener
{
    WorldScene scene;
    DataItem dataItem;
	Text nlatText, slatText, wlonText, elonText;
    Text latTilesText, lonTilesText;
	Combo formatCombo;
	Button fitBtn, updateBtn;
	//  Not sure I really need a persistent pointer to the currently selected 
	//  LlFormat.  Can just get this from formatCombo.  Remove if it's not needed.
	static final int DEGREES = 0;
	static final int RADIANS = 1;
	int currentLlFormat;
	private Group mainGroup; 
	
	public BboxWidget(Composite parent){
		init(parent);
	}
	
	public void init(Composite parent){
		//  Scroller
		final ScrolledComposite scroller = 
			new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		
		scroller.setExpandVertical(true);
		scroller.setExpandHorizontal(true);
		scroller.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

	    mainGroup = new Group(scroller, 0x0);
		mainGroup.setText("itemName");
		scroller.setContent(mainGroup);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 10;
		mainGroup.setLayout(layout);
		
		//  Create a Group for the compass Grid
		Composite compassGroup = new Composite(mainGroup, SWT.SHADOW_NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
        gridData.horizontalSpan = 2;
		compassGroup.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		compassGroup.setLayout(gridLayout);
		//  Create Buttons
		Group nlatGroup = new Group(compassGroup, 0x0);
		nlatGroup.setText("northLat");
		FillLayout fl = new FillLayout();
		fl.marginHeight = 4;
		fl.marginWidth = 4;
		nlatGroup.setLayout(fl);
		nlatText = new Text(nlatGroup, SWT.RIGHT);
		Group wlonGroup = new Group(compassGroup, 0x0);
		wlonGroup.setText("westLon");
		fl = new FillLayout();
		fl.marginHeight = 4;
		fl.marginWidth = 4;
		wlonGroup.setLayout(fl);
		wlonText = new Text(wlonGroup, SWT.RIGHT);
		Label compassBtn = new Label(compassGroup, SWT.SHADOW_IN);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.CENTER;
		gridData.horizontalAlignment = SWT.CENTER;
		compassBtn.setLayoutData(gridData);
		ImageDescriptor descriptor = STTPlugin.getImageDescriptor("icons/compass1.gif");
		Image compImg = descriptor.createImage();
		compassBtn.setImage(compImg);
		//compassBtn.setImage(Image)
		Group elonGroup = new Group(compassGroup, 0x0);
		elonGroup.setText("eastLon");
		fl = new FillLayout();
		fl.marginHeight = 4;
		fl.marginWidth = 4;
		elonGroup.setLayout(fl);
		elonText = new Text(elonGroup, SWT.RIGHT);
		Group slatGroup = new Group(compassGroup, 0x0);
		slatGroup.setText("southLat");
		fl = new FillLayout();
		fl.marginHeight = 4;
		fl.marginWidth = 4;
		slatGroup.setLayout(fl);
		slatText = new Text(slatGroup, SWT.RIGHT);
		//  restrict text fields to numeric input
		wlonText.addKeyListener(this);
		elonText.addKeyListener(this);
		nlatText.addKeyListener(this);
		slatText.addKeyListener(this);
		
		//  Layout Btns
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 3;
		nlatGroup.setLayoutData(gridData);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		gridData.horizontalSpan = 1;
		gridData.verticalAlignment = SWT.CENTER;
		wlonGroup.setLayoutData(gridData);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		gridData.horizontalSpan = 1;
		gridData.verticalAlignment = SWT.CENTER;
		elonGroup.setLayoutData(gridData);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 3;
		slatGroup.setLayoutData(gridData);
		
		//  Format combo
		Composite formatGrp = new Composite(mainGroup, 0x0);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
        gridData.horizontalSpan = 2;
		formatGrp.setLayoutData(gridData);
		RowLayout formatLayout = new RowLayout(SWT.HORIZONTAL);
		formatGrp.setLayout(formatLayout);
		Label formatLbl = new Label(formatGrp, SWT.CENTER);
		formatLbl.setText("Format:");
		formatCombo = new Combo(formatGrp, SWT.DROP_DOWN | SWT.READ_ONLY);
		formatCombo.addSelectionListener(this);
		//  May add options for dd'mm"ss later
		formatCombo.add("degrees");
		formatCombo.add("radians");
		formatCombo.select(0);
		currentLlFormat = DEGREES;  //  default to DEGREES 
		
        // tiles numbers
        Group latTilesGroup = new Group(mainGroup, 0);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.CENTER;
        latTilesGroup.setLayoutData(gridData);
        latTilesGroup.setText("Lat Tiles");        
        fl = new FillLayout();
        fl.marginHeight = 4;
        fl.marginWidth = 4;
        latTilesGroup.setLayout(fl);
        latTilesText = new Text(latTilesGroup, SWT.RIGHT);        
        Group lonTilesGroup = new Group(mainGroup, 0);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.CENTER;
        lonTilesGroup.setLayoutData(gridData);
        lonTilesGroup.setText("Lon Tiles");
        fl = new FillLayout();
        fl.marginHeight = 4;
        fl.marginWidth = 4;
        lonTilesGroup.setLayout(fl);        
        lonTilesText = new Text(lonTilesGroup, SWT.RIGHT);
        
		// Update Button
        updateBtn = new Button(mainGroup, SWT.PUSH);
        updateBtn.setText("Update Item");
        updateBtn.addSelectionListener(this);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.CENTER;
        updateBtn.setLayoutData(gridData);
        
		//  Fit Button
		fitBtn = new Button(mainGroup, SWT.PUSH);
		fitBtn.setText("Fit Item to View");
		fitBtn.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		fitBtn.setLayoutData(gridData);
        
		//  Set Default scroller size
		scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public STTSpatialExtent getSpatialExtent(){
		double nlat = getValue(nlatText);
		double slat = getValue(slatText);
		double wlon = getValue(wlonText);
		double elon = getValue(elonText);
		STTSpatialExtent ext = new STTSpatialExtent();
		ext.setMinX(wlon);
		ext.setMinY(slat);
		ext.setMaxX(elon);
		ext.setMaxY(nlat);
		return ext;
	}
	
	//  Use org.vast.stt.util.SpatialExtent, or Bbox here? 
	public void setSpatialExtent(STTSpatialExtent bbox){
		setValue(nlatText, bbox.getMaxY());
		setValue(slatText, bbox.getMinY());
		setValue(wlonText, bbox.getMinX());
		setValue(elonText, bbox.getMaxX());
        setValue(latTilesText, bbox.getYTiles());
        setValue(lonTilesText, bbox.getXTiles());
	}
	
	public void setFormat(int formatIndex){
		if(formatIndex == currentLlFormat)
			return;
		currentLlFormat = formatIndex;
		STTSpatialExtent bbox = this.getSpatialExtent();
		double minX = bbox.getMinX();
		double maxX = bbox.getMaxX();
		double minY = bbox.getMinY();
		double maxY = bbox.getMaxY();
		double factor = 1.0;
		switch(currentLlFormat){
			case DEGREES:
				factor = 180.0/Math.PI;
				break;
			case RADIANS:
				factor = Math.PI/180.0;  //UnitConversion.getFactorToSI("deg");
				break;
			default: 
				break;
		}
		minX *= factor;
		maxX *= factor;
		minY *= factor;
		maxY *= factor;
		bbox.setMinX(minX);
		bbox.setMinY(minY);
		bbox.setMaxX(maxX);
		bbox.setMaxY(maxY);
		this.setSpatialExtent(bbox);
	}
	
	public void setDataItem(DataItem item){
		this.dataItem = item;
		mainGroup.setText(item.getName());
		DataProvider prov = item.getDataProvider();
		//  If provider is null, this widget isn't supported.
		if(prov!=null) {
			STTSpatialExtent ext = prov.getSpatialExtent();
			this.setSpatialExtent(ext);
		}
	}
    
    public void setScene(WorldScene scene){
        this.scene = scene;
    }
	
	public double getValue(Text text){
		double value;
		try {
			value = Double.parseDouble(text.getText());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.err.println("ParseError: " + text.getText());
			return 0.0;
		}

		return value;
	}
		
	public void setValue(Text text, double d){
		String dubStr = Double.toString(d);
		text.setText(dubStr);
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
        if(e.widget == updateBtn){
            double nlat = getValue(nlatText);
            double slat = getValue(slatText);
            double wlon = getValue(wlonText);
            double elon = getValue(elonText);
            int latTiles = (int)getValue(latTilesText);
            int lonTiles = (int)getValue(lonTilesText);
            STTSpatialExtent bbox = this.dataItem.getDataProvider().getSpatialExtent();
            bbox.setMaxX(elon);
            bbox.setMinX(wlon);
            bbox.setMaxY(nlat);
            bbox.setMinY(slat);
            bbox.setXTiles(lonTiles);
            bbox.setYTiles(latTiles);
            bbox.dispatchEvent(new STTEvent(this, EventType.SPATIAL_EXTENT_CHANGED), false);            
        }
        else if(e.widget == fitBtn){
            STTSpatialExtent bbox = this.dataItem.getDataProvider().getSpatialExtent();
            scene.getViewSettings().getProjection().fitBboxToView(bbox, scene);
            setSpatialExtent(bbox);
            bbox.dispatchEvent(new STTEvent(this, EventType.SPATIAL_EXTENT_CHANGED), false);
		} else if (e.widget == formatCombo){
			//System.err.println("Selection Index = " + formatCombo.getSelectionIndex());
			this.setFormat(formatCombo.getSelectionIndex());
		}
	}
	
	public void keyPressed(KeyEvent e) {
		//  accept only numbers, decimal point, and '-' sign
		e.doit = ( (e.keyCode >=48 && e.keyCode <= 57) || e.keyCode == 46 || e.keyCode == 45 );
		//  and bkspace/delete/LFT/RT ARROW
		e.doit = e.doit || e.keyCode == 8 || e.keyCode == 127 || e.keyCode == 16777219 || e.keyCode == 16777220;
	}

	public void keyReleased(KeyEvent e) {
	}
}
