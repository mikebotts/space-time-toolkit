/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

package org.vast.stt.gui.widgets.catalog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * <p><b>Title:</b>
 *   Catalog Widget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *    Allow user to search OGC Catalog by keyword, service type, roi, or provider organization
 *    TODO ADD borders to all combos and text fields
 *    TODO add time
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class CatalogWidget implements SelectionListener 
{
	Composite mainGroup;
	//  TODO Add Other Catalog servers, possibly
	String [] catServers = {
//			"http://dev.ionicsoft.com:8082/ows4catalog/wrs/WRS"
			"Ionic"
	};
	
	public CatalogWidget(Composite parent, int style) {
		initGui(parent);
	}

	public void initGui(Composite parent){
		GridData gd;
		
		mainGroup = new Composite(parent, 0x0);
		mainGroup.setLayout(new GridLayout(2, false));
		mainGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false ));
		
//		Group topGroup = new Group(mainGroup, 0x0);
//		topGroup.setLayout(new GridLayout(2, false));
//		topGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false ));
//		topGroup.setText("Servers");

		//  Servers (Only one initially, but still making it a combo)
		Label serverLabel = new Label(mainGroup, SWT.LEFT);  
		serverLabel.setText("Server:");
		Combo serverCombo = new Combo(mainGroup, SWT.BORDER | SWT.READ_ONLY);
		serverCombo.setTextLimit(20);
		serverCombo.setItems(catServers);
		//typesCombo.setItems(new String [] {"WMS", "WCS", "WFS", "SOS"});
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		serverCombo.setLayoutData(gd);
		serverCombo.select(0);
		serverCombo.addSelectionListener(this);
		
		//  Search Criteria
		//  Service Type (Combo)
		
		//  Keyword (Text)
		Label kwLabel = new Label(mainGroup, SWT.LEFT);
		kwLabel.setText("Keywords:");
		kwLabel.setToolTipText("Space-Separated keyowrds");
		Text kwText = new Text(mainGroup, SWT.BORDER | SWT.LEFT);
		gd = new GridData(SWT.LEFT,SWT.CENTER, true, false);
		kwText.setLayoutData(gd);
		
		//  Provder (Text)
		
		//  ROI (text)
		
		//  Submit Btn (and potentially edit btn to add new Catalog)
		Button submitBtn = new Button(mainGroup, SWT.PUSH);
		submitBtn.setText("Submit");
		gd = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1);
		submitBtn.setLayoutData(gd);
	}	

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
