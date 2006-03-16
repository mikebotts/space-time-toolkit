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

package org.vast.stt.data;

import org.ogc.cdm.common.DataComponent;
import org.vast.data.DataList;


public class DataNode extends DataList
{
    public DataComponent getStructure()
    {
        return this.getComponent(0);
    }
    
    public void clear()
    {
        this.removeAllComponents();
        System.gc();
    }
}
