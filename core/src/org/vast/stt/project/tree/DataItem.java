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

package org.vast.stt.project.tree;


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
public class DataItem extends DataObject
{
    
    public DataItem()
	{
        super();        
	}
}
