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

import org.vast.stt.style.BlockInfo;


/**
 * <p><b>Title:</b><br/>
 * Display List Manager
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Handle creation, call and deletion of display lists
 * for all rendering types.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 13, 2006
 * @version 1.0
 */
public class DisplayListManager
{
    protected GL gl;
    protected GLU glu;
    
    
    public DisplayListManager(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }
    
    
    /**
     * Manage display list by calling previously recorded one
     * or starting to record a new one
     * @param glInfo
     * @param blockInfo
     * @return true if a list was called, false if a new needs to be generated
     */
    protected boolean useDisplayList(BlockInfo blockInfo, OpenGLInfo glInfo)
    {
        if (glInfo != null)
        {
            // if block was updated delete previous display list
            if (blockInfo.updated)
            {
                if (glInfo.objectID != -1)
                    gl.glDeleteLists(glInfo.objectID, 1);
            }
            
            // otherwise just call display list
            else 
            {
                gl.glCallList(glInfo.objectID);
                return true;
            }
        }
        else
        {
            glInfo = new OpenGLInfo();
            blockInfo.rendererParams = glInfo;
        }
        
        // create and start list recording
        int listNum = gl.glGenLists(1);
        glInfo.objectID = listNum;
        blockInfo.updated = false;
        
        gl.glNewList(listNum, GL.GL_COMPILE_AND_EXECUTE);        
        
        return false;
    }
}
