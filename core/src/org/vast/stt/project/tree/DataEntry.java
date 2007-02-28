
package org.vast.stt.project.tree;

/**
 * <p><b>Title:</b>
 * Data Entry
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Interface for all data objects in a scene
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 10, 2006
 * @version 1.0
 */
public interface DataEntry
{
    public final static String MASK = "mask";
    
    
    public String getName();


    public void setName(String name);
}
