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

import java.util.Enumeration;
import java.util.Hashtable;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.project.DataStyler;


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
    protected static Hashtable<Symbolizer, GLDisplayListTable> symDLTables
               = new Hashtable<Symbolizer, GLDisplayListTable>();
    protected GL gl;
    protected GLU glu;
    protected int refreshPeriod = 300;
    
    
    class GLDisplayList
    {
        protected int id = 0;
        protected boolean needsUpdate = true;
        protected long lastCompiled = 0;
        protected int blockCount;
    }
    
    
    class GLDisplayListTable extends Hashtable<Object, GLDisplayList>
    {
        private final static long serialVersionUID = 0;
    }
    
    
    public DisplayListManager(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }
    
    
    /**
     * Record new display list for the symbolizer/block combination or
     * call simply the one that was previously created.
     * @param styler
     * @param block
     * @param renderRunnable
     */
    public void useDisplayList(DataStyler styler, BlockListItem block, GLRunnable renderRunnable)
    {
        Symbolizer sym = styler.getSymbolizer();
        
        synchronized (symDLTables)
        {
            // try to find table for this symbolizer
            GLDisplayListTable mainTable = symDLTables.get(sym);
            
            // if list doesn't exist, create it
            if (mainTable == null)
            {
                mainTable = new GLDisplayListTable();
                symDLTables.put(sym, mainTable);
            }
            
            // try to find display list for this block
            GLDisplayList dlInfo = mainTable.get(block);
            
            // create if it doesn't exist
            if (dlInfo == null)
            {
                dlInfo = new GLDisplayList();
                mainTable.put(block, dlInfo);
            }
            
            // create new display list if it needs update
            long now = System.currentTimeMillis();
            if (dlInfo.needsUpdate && (now - dlInfo.lastCompiled > refreshPeriod))
            {
                dlInfo.needsUpdate = false;
                createDisplayList(dlInfo, renderRunnable);
            }
            
            // otherwise just call existing one
            else
            {
                if (dlInfo.id > 0)
                {
                    gl.glCallList(dlInfo.id);
                    styler.skipBlocks(dlInfo.blockCount-1);
                    //System.err.println("DL #" + dlInfo.id + " called");
                }
            }
        }
    }
    
    
    /**
     * Record a new display list with GL commands called in the given runnable
     * @param dlInfo
     * @param renderRunnable
     */
    protected void createDisplayList(GLDisplayList dlInfo, GLRunnable renderRunnable)
    {
        // create new DL name and record Gl commands in runnable
        int newID = gl.glGenLists(1);
        gl.glNewList(newID, GL.GL_COMPILE_AND_EXECUTE);
        renderRunnable.run();
        gl.glEndList();
        
        // set new id and compile time
        dlInfo.lastCompiled = System.currentTimeMillis();
        int oldID = dlInfo.id;
        dlInfo.id = newID;
        dlInfo.blockCount = renderRunnable.blockCount;
        
        // delete previous texture if needed
        if (oldID > 0)
        {
            gl.glDeleteLists(oldID, 1);
            //System.err.println("DL #" + oldID + " deleted");
        }
        
        //System.err.println("DL #" + dlInfo.id + " created");
    }
    
    
    /**
     * Clears all display lists used by this symbolizer
     * @param sym
     */
    public void clearDisplayLists(Symbolizer sym)
    {
        synchronized (symDLTables)
        {
            // delete all sub display lists
            GLDisplayListTable dlTable = symDLTables.get(sym);
            if (dlTable != null)
            {
                Enumeration<GLDisplayList> subLists = dlTable.elements();
                while (subLists.hasMoreElements())
                {
                    GLDisplayList nextDL = subLists.nextElement();
                    if (nextDL.id > 0)
                    {
                        gl.glDeleteLists(nextDL.id, 1);
                        //System.err.println("DL #" + nextDL.id + " deleted");
                    }
                }
                
                symDLTables.remove(sym);
            }            
        }
    }
}