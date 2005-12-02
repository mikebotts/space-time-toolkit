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

package org.vast.stt.event;

public class STTEvent
{
	public enum Section
	{
		ALL,
		PROJECT,
		SERVICE,
		RESOURCE,
		SCENE,
		SCENE_VIEW,
		SCENE_TIME,
		DATAITEM,
		STYLE,
		DATA,
		OPTIONS
	};
	
	public enum Operation
	{
		ALL, ADD, REMOVE, CHANGE
	};


	public STTEvent(Object source, Section section)
	{
		this.section = section;
		this.source = source;
	}

	protected STTEvent next;
	protected Object source;
	protected Section section;
	protected Operation operation;


	public Operation getOperation()
	{
		return operation;
	}


	public Section getSection()
	{
		return section;
	}


	public Object getSource()
	{
		return source;
	}
	
	
	public String toString()
	{
		return "Event -> " + this.getSection();
	}
}
