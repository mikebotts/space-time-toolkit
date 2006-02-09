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

package org.vast.stt.scene;

import java.util.*;

import org.vast.stt.data.DataProvider;
import org.vast.stt.style.CompositeStyler;
import org.vast.stt.style.DataStyler;


/**
 * <p><b>Title:</b><br/>
 * DataItem Descriptor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Represents a Data item present in a scene. It is typically
 * a subset (in time, space, ...) of a given resource data set, 
 * which data is obtained through a DataProvider.
 * It also contains one or more style helpers used to 
 * render/display the data.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 3, 2005
 * @version 1.0
 */
public class DataItem implements DataEntry
{
	protected String name;
	protected boolean enabled = true;
	protected Hashtable<String, Object> options;
	protected DataProvider dataProvider;
	protected DataStyler styler;

	
	public DataItem()
	{
	}
	

	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}
	
	
	public DataStyler getStyler()
	{
		return styler;
	}


	public void setStyler(DataStyler styler)
	{
		this.styler = styler;
	}
	
	//  Consider adding addStyler to CompositeStyler class- T, 2/8/06
	public void addStyler(DataStyler newStyler){
		if(this.styler instanceof CompositeStyler) {
			((CompositeStyler)styler).getStylerList().add(newStyler);
		} else {
			CompositeStyler compStyler = new CompositeStyler(2);
			compStyler.getStylerList().add(this.styler);
			compStyler.getStylerList().add(newStyler);
			this.styler = compStyler;
		}
	}


	public DataProvider getDataProvider()
	{
		return dataProvider;
	}


	public void setDataProvider(DataProvider dataProvider)
	{
		this.dataProvider = dataProvider;
	}


	public boolean isEnabled()
	{
		return enabled;
	}


	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}


	public Hashtable<String, Object> getOptions()
	{
		return options;
	}


	public void setOptions(Hashtable<String, Object> options)
	{
		this.options = options;
	}
	
	
	public String toString()
	{
		return this.name + " - " + super.toString();
	}
}
