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



/**
 * <p><b>Title:</b><br/>
 * Scene Descriptor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Encapsulate the current state of a scene (graphic view)
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class Scene
{
	protected String name;
	protected ViewSettings viewSettings;
	protected TimeSettings timeSettings;
	protected DataEntryList dataList;


	public DataEntryList getDataList()
	{
		return dataList;
	}


	public void setDataList(DataEntryList dataList)
	{
		this.dataList = dataList;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public ViewSettings getViewSettings()
	{
		return viewSettings;
	}


	public void setViewSettings(ViewSettings viewSettings)
	{
		this.viewSettings = viewSettings;
	}


	public TimeSettings getTimeSettings()
	{
		return timeSettings;
	}


	public void setTimeSettings(TimeSettings timeSettings)
	{
		this.timeSettings = timeSettings;
	}
}
