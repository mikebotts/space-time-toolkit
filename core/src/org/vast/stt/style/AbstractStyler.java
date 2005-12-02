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

package org.vast.stt.style;

import org.ogc.cdm.common.DataComponent;
import org.vast.stt.data.DataProvider;


/**
 * <p><b>Title:</b><br/>
 * Abstract Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Abstract base class for all stylers.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public abstract class AbstractStyler implements DataStyler
{
	protected DataProvider dataProvider;
	protected boolean enabled;
	
	
	class DataPointer
	{
		public DataComponent  base; // parent to get DataBlock from
		public int startIndex;
		public int stepIndex;
		
		
		public DataPointer(DataComponent baseComponent, DataComponent repeatedComponent)
		{
			
		}
		
		
		public boolean hasNext()
		{
			return true;
		}
		
		
		public int nextIndex()
		{
			return 0;
		}
		
		
		public void reset()
		{
			
		}
	}
	
	
	public AbstractStyler()
	{
		
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
}
