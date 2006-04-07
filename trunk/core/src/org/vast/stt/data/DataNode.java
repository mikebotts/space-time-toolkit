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
import org.ogc.cdm.common.DataBlock;
import org.ogc.cdm.common.DataComponent;
import org.vast.data.DataArray;
import org.vast.data.DataGroup;
import org.vast.data.DataList;
import org.vast.data.DataValue;


/**
 * <p><b>Title:</b><br/>
 * Data Node
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO DataNode type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 1, 2006
 * @version 1.0
 */
public class DataNode extends DataGroup
{
    protected List<String> possibleScalarMappings;
    protected List<String> possibleBlockMappings;
    
    
    public DataNode()
    {
        possibleScalarMappings = new ArrayList<String>();
        possibleBlockMappings = new ArrayList<String>();
    }
    
    
    public void createList(DataComponent component)
    {
        DataList newList = new DataList();
        newList.addComponent(component);
        super.addComponent(component.getName(), newList);
        rebuildMappings(component);
    }
    
    
    public DataList getList(String name)
    {
        return (DataList)getComponent(name);
    }
    
    
    public DataList getList(int index)
    {
        return (DataList)getComponent(index);        
    }
    
    
    public void addData(DataList dataList, DataBlock data)
    {
        dataList.addData(data);
    }
    
    
    public void rebuildMappings(DataComponent component)
    {
        possibleScalarMappings.clear();
        possibleBlockMappings.clear();
        buildMappers(component, component.getName());
    }
    
    
    private void buildMappers(DataComponent component, String componentPath)
    {
        // for each array, build an array mapper
        if (component instanceof DataArray)
        {
            possibleBlockMappings.add(componentPath);
            DataComponent childComponent = component.getComponent(0);
            String childPath = componentPath + '/' + childComponent.getName();
            buildMappers(childComponent, childPath);
        }
        
        // just descend into DataGroups
        else if (component instanceof DataGroup)
        {
            possibleBlockMappings.add(componentPath);
            for (int i = 0; i < component.getComponentCount(); i++)
            {
                DataComponent childComponent = component.getComponent(i);
                String childPath = componentPath + '/' + childComponent.getName();
                buildMappers(childComponent, childPath);
            }
        }
        
        // handle DataValues
        else if (component instanceof DataValue)
        {
            possibleScalarMappings.add(componentPath);
        }
    }


    public List<String> getPossibleBlockMappings()
    {
        return possibleBlockMappings;
    }


    public List<String> getPossibleScalarMappings()
    {
        return possibleScalarMappings;
    }

}
