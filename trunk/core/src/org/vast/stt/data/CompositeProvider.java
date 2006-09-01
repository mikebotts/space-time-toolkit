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

package org.vast.stt.data;

import java.util.ArrayList;
import java.util.List;

import org.vast.stt.project.DataProvider;
import org.vast.stt.project.Resource;
import org.vast.stt.project.SpatialExtent;
import org.vast.stt.project.STTTimeExtent;


/**
 * <p><b>Title:</b><br/>
 * Composite Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This abstract Composite Provider class is the skeleton
 * for all composite providers. Composite Providers are
 * getting data from several child Data Providers and are
 * usually responsible for synchronization and merging.
 * If the behavior of certain methods is not appropriate
 * for a derived provider, they should be overidden.
 * (getMaxspatialExtent, getMaxTimeExtent, ...)
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 21, 2005
 * @version 1.0
 */
public abstract class CompositeProvider implements DataProvider
{
    protected List<DataProvider> providerList;


    public abstract DataNode getDataNode();
    public abstract void setDataNode(DataNode dataNode);


    public CompositeProvider()
    {
        providerList = new ArrayList<DataProvider>(2);
    }


    public void updateData() throws DataException
    {
        for (int i = 0; i < providerList.size(); i++)
            providerList.get(i).updateData();
    }


    public boolean isUpdating()
    {
        for (int i = 0; i < providerList.size(); i++)
            if (providerList.get(i).isUpdating())
                return true;

        return false;
    }


    public void cancelUpdate()
    {
        for (int i = 0; i < providerList.size(); i++)
            providerList.get(i).cancelUpdate();
    }


    public void clearData()
    {
        for (int i = 0; i < providerList.size(); i++)
            providerList.get(i).clearData();
    }


    public SpatialExtent getSpatialExtent()
    {
        return providerList.get(0).getSpatialExtent();
    }


    public void setSpatialExtent(SpatialExtent spatialExtent)
    {
        for (int i = 0; i < providerList.size(); i++)
            providerList.get(i).setSpatialExtent(spatialExtent);
    }


    public STTTimeExtent getTimeExtent()
    {
        return providerList.get(0).getTimeExtent();
    }


    public void setTimeExtent(STTTimeExtent timeExtent)
    {
        for (int i = 0; i < providerList.size(); i++)
            providerList.get(i).setTimeExtent(timeExtent);
    }
    
    
    public SpatialExtent getMaxSpatialExtent()
    {
        SpatialExtent bbox = new SpatialExtent();
        
        // get intersection of spatial extents of all children
        for (int i=0; i<providerList.size(); i++)
        {
            DataProvider nextProvider = providerList.get(i);
            SpatialExtent childBox =  nextProvider.getMaxSpatialExtent();
            
            if (i == 0)
                bbox = childBox.copy();
            else
                bbox.intersect(childBox);
        }
        
        return bbox;
    }


    public STTTimeExtent getMaxTimeExtent()
    {
        //TODO same as getMaxSpatialExtent
        return null;
    }


    public boolean isSpatialSubsetSupported()
    {
        for (int i = 0; i < providerList.size(); i++)
            if (!providerList.get(i).isSpatialSubsetSupported())
                return false;

        return true;
    }
    
    
    public boolean isTimeSubsetSupported()
    {
        for (int i = 0; i < providerList.size(); i++)
            if (!providerList.get(i).isTimeSubsetSupported())
                return false;

        return true;
    }
    
    
    public List<DataProvider> getProviderList()
    {
        return providerList;
    }


    public void setProviderList(List<DataProvider> providerList)
    {
        this.providerList = providerList;
    }
    
    
    public Resource getResource()
    {
        return null;
    }


    public void setResource(Resource resource)
    {
    }
}
