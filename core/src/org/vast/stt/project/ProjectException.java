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


/**
 * <p><b>Title:</b>
 * Project Exception
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Exception thrown when encountering errors while parsing
 * project file.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 10, 2006
 * @version 1.0
 */
public class ProjectException extends Exception
{
    static final long serialVersionUID = 0;


    public ProjectException(String message)
    {
        super(message);
    }


    public ProjectException(Exception e)
    {
        super(e);
    }


    public ProjectException(String message, Exception e)
    {
        super(message, e);
    }
}
