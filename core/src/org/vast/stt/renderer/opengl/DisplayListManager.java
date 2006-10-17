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
import org.vast.stt.data.BlockListItem;
import org.vast.stt.project.scene.Projection;
import org.vast.stt.project.scene.Projection_ECEF;
import org.vast.stt.project.scene.Projection_LLA;
import org.vast.stt.style.DataStyler;


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
    protected static Hashtable<HashKey, GLDisplayListTable> DLTables
               = new Hashtable<HashKey, GLDisplayListTable>();
    protected GL gl;
    protected GLU glu;
    
    
    class HashKey
    {
        int hashCode;
        
        public HashKey(DataStyler styler)
        {
            hashCode = styler.getSymbolizer().hashCode();
            
            Projection projection = styler.getProjection();
            if (projection instanceof Projection_ECEF)
                hashCode *= 1;
            else if (projection instanceof Projection_LLA)
                hashCode *= 2;
        }
        
        @Override
        public int hashCode()
        {
            return hashCode;
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if (((HashKey)obj).hashCode == hashCode)
                return true;
            else
                return false;
        }
    }
    
    
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
        synchronized (DLTables)
        {
            // wrap styler with our own hashKey
            HashKey hashKey = new HashKey(styler);
            
            // try to find table for this symbolizer
            GLDisplayListTable mainTable = DLTables.get(hashKey);
            
            // if list doesn't exist, create it
            if (mainTable == null)
            {
                mainTable = new GLDisplayListTable();
                DLTables.put(hashKey, mainTable);
            }
            
            // try to find display list for this block
            GLDisplayList dlInfo = mainTable.get(block);
            
            // instantiate and add to table if it doesn't exist
            if (dlInfo == null)
            {
                dlInfo = new GLDisplayList();
                mainTable.put(block, dlInfo);
            }
            
            // create new display list only if it needs update
            if (dlInfo.needsUpdate)
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
    public void clearDisplayLists(DataStyler styler)
    {
        synchronized (DLTables)
        {
            // wrap styler with our own hashKey
            HashKey hashKey = new HashKey(styler);
            
            // delete all sub display lists
            GLDisplayListTable dlTable = DLTables.get(hashKey);
            if (dlTable != null)
            {
                Enumeration<GLDisplayList> subLists = dlTable.elements();
                while (subLists.hasMoreElements())
                {
                    GLDisplayList nextDL = subLists.nextElement();
                    if (nextDL.id > 0)
                    {
                        // System.out.println("DL# " + nextDL.id + " " + (gl.glIsList(nextDL.id) ? "on" : "off"));
                        gl.glDeleteLists(nextDL.id, 1);
                        // System.out.println("DL# " + nextDL.id + " " + (gl.glIsList(nextDL.id) ? "on" : "off"));
                        dlTable.remove(nextDL);
                    }
                }
                
                DLTables.remove(hashKey);
            }            
        }
    }
    
    
    /**
     * Clears display lists used by this symbolizer and
     * associated by the given objects.
     * @param sym
     */
    public void clearDisplayLists(DataStyler styler, Object[] objects)
    {
        synchronized (DLTables)
        {
            // wrap styler with our own hashKey
            HashKey hashKey = new HashKey(styler);
            
            // delete all sub display lists
            GLDisplayListTable dlTable = DLTables.get(hashKey);
            if (dlTable != null)
            {
                for (int i=0; i<objects.length; i++)
                {
                    GLDisplayList nextDL = dlTable.get(objects[i]);
                    if (nextDL != null && nextDL.id > 0)
                    {
                        // System.out.println("DL# " + nextDL.id + " " + (gl.glIsList(nextDL.id) ? "on" : "off"));
                        gl.glDeleteLists(nextDL.id, 1);
                        // System.out.println("DL# " + nextDL.id + " " + (gl.glIsList(nextDL.id) ? "on" : "off"));
                        dlTable.remove(nextDL);
                    }
                }
            }            
        }
    }
}