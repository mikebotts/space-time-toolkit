/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Initial Developer of the Original Code is Sensia Software LLC.
 Portions created by the Initial Developer are Copyright (C) 2008
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <alex.robin@sensiasoftware.com>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.provider.cache;

import java.io.File;
import org.vast.cdm.common.DataComponent;
import org.vast.stt.data.DataException;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.AbstractProvider;
import org.vast.stt.provider.CachedProvider;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.STTTimeExtent;
import com.sleepycat.je.*;


/**
 * <p><b>Title:</b>
 * TimeCache
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO TimeCache type description
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Nov 21, 2008
 * @version 1.0
 */
public class TimeCache extends AbstractProvider implements CachedProvider
{
    protected String timeData;
    protected DataComponent timeDataComponent;
    protected double tileSize; // in seconds
    protected int maxCacheSize; // in MB
    protected int maxLifeTime; // in minutes
    protected DataProvider subProvider;
    
    
    @Override
    public void init() throws DataException
    {
        try
        {
            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setAllowCreate(true);
            //envConfig.setSharedCache(true);
            String dbDir = System.getProperty("user.dir");
            Environment env = new Environment(new File(dbDir + "/stt/db"), envConfig);
            
            // Open the database. Create it if it does not already
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            Database db = env.openDatabase(null, subProvider.getName(), dbConfig);
            
            
            
            
        }
        catch (DatabaseException dbe)
        {
            // Exception handling goes here
        }        
    }
    

    @Override
    public void updateData() throws DataException
    {
        // TODO Auto-generated method stub
        
    }
    
    
    public String getTimeData()
    {
        return timeData;
    }


    public void setTimeData(String timeData)
    {
        this.timeData = timeData;
    }


    public DataComponent getTimeDataComponent()
    {
        return timeDataComponent;
    }


    public void setTimeDataComponent(DataComponent timeDataComponent)
    {
        this.timeDataComponent = timeDataComponent;
    }


    public double getTileSize()
    {
        return tileSize;
    }


    public void setTileSize(double tileSize)
    {
        this.tileSize = tileSize;
    }


    public int getMaxCacheSize()
    {
        return maxCacheSize;
    }


    public void setMaxCacheSize(int maxCacheSize)
    {
        this.maxCacheSize = maxCacheSize;
    }


    public int getMaxLifeTime()
    {
        return maxLifeTime;
    }


    public void setMaxLifeTime(int maxLifeTime)
    {
        this.maxLifeTime = maxLifeTime;
    }
    
    
    @Override
    public String getDescription()
    {
        return subProvider.getDescription();
    }


    @Override
    public String getName()
    {
        return subProvider.getName();
    }


    @Override
    public void setDescription(String description)
    {
        subProvider.setDescription(description);
    }


    @Override
    public void setName(String name)
    {
        subProvider.setName(name);
    }


    @Override
    public boolean isSpatialSubsetSupported()
    {
        return subProvider.isSpatialSubsetSupported();
    }
    

    @Override
    public boolean isTimeSubsetSupported()
    {
        return subProvider.isTimeSubsetSupported();
    }
    
    
    @Override
    public STTSpatialExtent getMaxSpatialExtent()
    {
        return subProvider.getMaxSpatialExtent();
    }


    @Override
    public STTTimeExtent getMaxTimeExtent()
    {
        return subProvider.getMaxTimeExtent();
    }


    public DataProvider getSubProvider()
    {
        return subProvider;
    }


    public void setSubProvider(DataProvider subProvider)
    {
        if (this.subProvider != subProvider)
        {
            if (this.subProvider != null)
                this.subProvider.removeListener(this);
            
            this.subProvider = subProvider;
            
            if (this.subProvider != null)
                this.subProvider.addListener(this);
        }
    }
    
    
    @Override
    public void handleEvent(STTEvent event)
    {
        switch(event.type)
        {
            case PROVIDER_DATA_CHANGED:
            case PROVIDER_DATA_CLEARED:
            case PROVIDER_DATA_REMOVED:
                //dispatchEvent(event.copy());
                break;
                
            case PROVIDER_ERROR:
                this.enabled = false;
                //dispatchEvent(event.copy());
                break;
        }        
    }  
}
