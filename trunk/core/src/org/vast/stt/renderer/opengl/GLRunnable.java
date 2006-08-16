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

package org.vast.stt.renderer.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


/**
 * <p><b>Title:</b>
 * GLRunnable
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Abstract class for all GL rendering runnables.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 2, 2006
 * @version 1.0
 */
public abstract class GLRunnable implements Runnable
{
    protected GL gl;
    protected GLU glu;
    protected int blockCount;
    
    
    public abstract void run();
    
    
    public void setGL(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }
}
