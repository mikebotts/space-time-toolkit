
package org.vast.stt.project;

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
    public String getName();


    public void setName(String name);


    public boolean isEnabled();


    public void setEnabled(boolean enabled);
}
