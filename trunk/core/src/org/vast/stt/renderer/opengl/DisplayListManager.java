/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.renderer.opengl;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.project.world.Projection;
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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Apr 13, 2006
 * @version 1.0
 */
public class DisplayListManager
{
    //protected Log log = LogFactory.getLog(DisplayListManager.class);
    
    protected static Hashtable<HashKey, GLDisplayListTable> DLTables
               = new Hashtable<HashKey, GLDisplayListTable>();
    protected GL gl;
    protected GLU glu;

    
    // use to build a unique hash code for a combination of symbolizer/projection
    class HashKey
    {
        int hashCode;
        
        public HashKey(DataStyler styler)
        {
            Projection projection = styler.getProjection();
            String className = projection.getClass().getSimpleName();
            hashCode = styler.getSymbolizer().hashCode();
            hashCode = (String.valueOf(hashCode) + className).hashCode();
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
    public void useDisplayList(DataStyler styler, BlockListItem block, GLRunnable renderRunnable, boolean force)
    {
        if (block == null)
        {
            renderRunnable.run();
            return;
        }
        
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
            if (dlInfo.needsUpdate || force)
            {
                dlInfo.needsUpdate = false;
                createDisplayList(dlInfo, renderRunnable);
                System.out.println("DL #" + dlInfo.id + " created for block " + block);
                logStatistics();
            }
            
            // otherwise just call existing one
            else
            {
                if (dlInfo.id > 0)
                {
                    gl.glCallList(dlInfo.id);
                    styler.skipBlocks(dlInfo.blockCount-1);
                    //log.debug("DL #" + dlInfo.id + " called");
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
        
        // delete previous list if needed
        if (oldID > 0)
        {
            gl.glDeleteLists(oldID, 1);
            System.out.println("DL #" + oldID + " deleted");
        }
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
                        gl.glDeleteLists(nextDL.id, 1);
                        System.out.println("DL #" + nextDL.id + " deleted for styler " + styler);
                    }
                    
                    dlTable.remove(nextDL);
                }
                
                DLTables.remove(hashKey);
            }
            
            logStatistics();
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
                    if (nextDL != null)
                    {
                        dlTable.remove(objects[i]);
                        
                        if (nextDL.id > 0)
                        {
                            gl.glDeleteLists(nextDL.id, 1);
                            System.out.println("DL #" + nextDL.id + " deleted for block " + objects[i]);
                        }
                    }
                    else
                    	System.out.println("DL not found for block " + objects[i]);
                }
            }
            
            logStatistics();
        }
    }
    
    
    private void logStatistics()
    {
        //if (log.isDebugEnabled())
        {
            int listCount = 0;
            
            for (int i=0; i<65535; i++)
                if (gl.glIsList(i))
                    listCount++;
            
            System.out.println("Num DL = " + listCount);
        }
    }
}