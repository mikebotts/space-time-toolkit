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
    protected static Hashtable<Symbolizer, GLDisplayList> symTable = new Hashtable<Symbolizer, GLDisplayList>();
    protected GL gl;
    protected GLU glu;
    protected int refreshPeriod = 500;
    
    
    public DisplayListManager(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }
    
    
    /**
     * Record new display list for the symbolizer attached with the styler
     * or simply call the one that was previously created.
     * @param sym
     * @return
     */
    public void useDisplayList(DataStyler styler, Runnable renderRunnable)
    {
        Symbolizer sym = styler.getSymbolizer();
        
        synchronized (symTable)
        {
            GLDisplayList dl = symTable.get(sym);
            
            // if it already exists, delete or use existing one
            if (dl != null && dl.id > 0)
            {
                // if style has changed, delete previous list
                long time = System.currentTimeMillis();
                if (time - dl.lastCompiled > refreshPeriod && styler.isUpdated())
                {
                    symTable.remove(sym);
                    gl.glDeleteLists(dl.id, 1);
                    //System.err.println("DL #" + dl.id + " deleted");
                }
               
                // otherwise simply call recorded list
                else if (dl.ready)
                {
                    gl.glCallList(dl.id);
                    //System.err.println("DL #" + dl.id + " called");
                    return;
                }
            }
            
            // otherwise record it        
            dl = new GLDisplayList();
            recordDisplayList(dl, renderRunnable);
            symTable.put(sym, dl);
            styler.setUpdated(false);
            //System.err.println("DL #" + dl.id + " created");
        }
    }
    
    
    /**
     * Record new display list for the symbolizer/block combination or
     * call simply the one that was previously created.
     * @param styler
     * @param block
     * @param renderRunnable
     */
    public void useDisplayList(DataStyler styler, BlockListItem block, Runnable renderRunnable)
    {
        Symbolizer sym = styler.getSymbolizer();
        
        synchronized (symTable)
        {
            GLDisplayList mainList = symTable.get(sym);
            
            // if list doesn't exist, create it
            if (mainList == null)
            {
                mainList = new GLDisplayList();
                symTable.put(sym, mainList);
            }
            
            // try to find sub list
            GLDisplayList subList = mainList.subDisplayLists.get(block);
            
            // if it already exists, delete or use existing one
            if (subList != null && subList.id > 0)
            {
                // if style has changed, delete previous list
                long time = System.currentTimeMillis();
                if (time - subList.lastCompiled > refreshPeriod && styler.isUpdated())
                {
                    subList.subDisplayLists.remove(block);
                    gl.glDeleteLists(subList.id, 1);
                    //System.err.println("DL #" + dl.id + " deleted");
                }
                
                // otherwise simply call recorded list
                else if (subList.ready)
                {
                    gl.glCallList(subList.id);
                    //System.err.println("DL #" + dl.id + " called");
                    return;
                }
            }
            
            // otherwise record it        
            subList = new GLDisplayList();
            mainList.subDisplayLists.put(block, subList);
            styler.setUpdated(false);        
            recordDisplayList(subList, renderRunnable);
            //System.err.println("DL #" + dl.id + " created");
        }
    }
    
    
    /**
     * Clears all display lists used by this symbolizer
     * @param sym
     */
    protected void clearDisplayLists(Symbolizer sym)
    {
        GLDisplayList dl = symTable.get(sym);
        
        if (dl != null)
        {
            symTable.remove(sym);
            if (dl.id > 0)
                gl.glDeleteLists(dl.id, 1);
            
            Enumeration<GLDisplayList> subLists = dl.subDisplayLists.elements();
            while (subLists.hasMoreElements())
            {
                GLDisplayList nextDL = subLists.nextElement();
                gl.glDeleteLists(nextDL.id, 1);
            }
        }
    }
    
    
    protected void recordDisplayList(GLDisplayList dl, Runnable renderRunnable)
    {
        dl.id = gl.glGenLists(1);
        gl.glNewList(dl.id, GL.GL_COMPILE_AND_EXECUTE);
        renderRunnable.run();
        gl.glEndList();
        dl.lastCompiled = System.currentTimeMillis();
        dl.ready = true;        
    }
}


class GLDisplayList
{
    protected int id = 0;
    protected boolean ready = false;
    protected long lastCompiled = 0;
    protected int blockCount = 1;
    protected Hashtable<Object, GLDisplayList> subDisplayLists;
    
    public GLDisplayList()
    {
        subDisplayLists = new Hashtable<Object, GLDisplayList>();
    }
}
