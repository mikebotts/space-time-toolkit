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

import org.vast.stt.data.BlockInfo;


/**
 * <p><b>Title:</b>
 * Block Filter
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Filter whole blocks based on time and bbox
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 12, 2006
 * @version 1.0
 */
public class BlockFilter
{
    protected GL gl;
    protected GLU glu;
    
    
    public BlockFilter(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }
    
    
    public boolean filterBlock(BlockInfo blockInfo)
    {
        return false;
    }
}
