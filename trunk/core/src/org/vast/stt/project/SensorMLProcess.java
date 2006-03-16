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

package org.vast.stt.project;

import org.vast.process.DataProcess;


/**
 * <p><b>Title:</b><br/>
 * SensorML Process Descriptor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Encapslate a SensorML ProcessModel or ProcessChain
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Feb 28, 2006
 * @version 1.0
 */
public class SensorMLProcess extends AbstractResource
{
    protected DataProcess internalProcess;


    public DataProcess getInternalProcess()
    {
        return internalProcess;
    }


    public void setInternalProcess(DataProcess internalProcess)
    {
        this.internalProcess = internalProcess;
    }

}
