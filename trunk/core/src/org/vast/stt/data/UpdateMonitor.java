package org.vast.stt.data;


/**
 * <p><b>Title:</b><br/>
 * Update Monitor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO UpdateMonitor type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Mar 1, 2006
 * @version 1.0
 */
public interface UpdateMonitor
{
    public void dataUpdated(DataProvider provider);
    
    
    public void dataUpdated(DataProvider provider, float completionFactor);
}
