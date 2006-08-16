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
import java.util.Hashtable;
import java.util.List;
import org.ogc.cdm.common.DataComponent;
import org.vast.data.DataArray;
import org.vast.data.DataGroup;
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
public class DataNode
{
    protected List<String> possibleScalarMappings;
    protected List<String> possibleBlockMappings;
    protected Hashtable<String, BlockList> listMap;
    protected ArrayList<BlockList> listArray;
    protected boolean nodeStructureReady;
    
    
    public DataNode()
    {
        possibleScalarMappings = new ArrayList<String>();
        possibleBlockMappings = new ArrayList<String>();
        listMap = new Hashtable<String, BlockList>(1);
        listArray = new ArrayList<BlockList>(1);
    }
    
    
    public ArrayList<BlockList> getListArray()
    {
        return listArray;
    }
    
    
    public BlockList createList(DataComponent component)
    {
        BlockList newList = new BlockList();
        newList.setBlockStructure(component);
        listMap.put(component.getName(), newList);
        listArray.add(newList);
        rebuildMappings(component);
        return newList;
    }
    
    
    public BlockList getList(String name)
    {
        return listMap.get(name);
    }
    
    
    public void removeList(String name)
    {
        BlockList list = listMap.remove(name);
        listArray.remove(list);
    }
    
    
    public void clearList(String name)
    {
        listMap.get(name).clear();
    }
    
    
    public void clearAll()
    {
        for (int i=0; i<listArray.size(); i++)
            listArray.get(i).clear();
    }
    
    
    public void rebuildMappings(DataComponent component)
    {
        possibleScalarMappings.clear();
        possibleBlockMappings.clear();
        findPossibleMappings(component, component.getName());
    }
    
    
    private void findPossibleMappings(DataComponent component, String componentPath)
    {
        // for each array, build an array mapper
        if (component instanceof DataArray)
        {
            possibleBlockMappings.add(componentPath);
            DataComponent childComponent = component.getComponent(0);
            String childPath = componentPath + '/' + childComponent.getName();
            findPossibleMappings(childComponent, childPath);
        }
        
        // just descend into DataGroups
        else if (component instanceof DataGroup)
        {
            possibleBlockMappings.add(componentPath);
            for (int i = 0; i < component.getComponentCount(); i++)
            {
                DataComponent childComponent = component.getComponent(i);
                String childPath = componentPath + '/' + childComponent.getName();
                findPossibleMappings(childComponent, childPath);
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


    public boolean isNodeStructureReady()
    {
        return nodeStructureReady;
    }


    public void setNodeStructureReady(boolean nodeStructureReady)
    {
        this.nodeStructureReady = nodeStructureReady;
    }
}
