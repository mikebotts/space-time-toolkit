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

package org.vast.stt.style;

import org.ogc.cdm.common.DataComponent;
import org.vast.data.DataArray;
import org.vast.stt.data.DataNode;
import org.vast.stt.data.DataProvider;
import org.vast.stt.util.SpatialExtent;


/**
 * <p><b>Title:</b><br/>
 * Abstract Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Abstract base class for all stylers.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public abstract class AbstractStyler implements DataStyler
{
	protected DataProvider dataProvider;
    protected SpatialExtent boundingBox;
    protected DataNode node;
	protected String name;
	protected boolean enabled;   
	
	
	public AbstractStyler()
	{		
	}
    
    
    /**
     * Compute index range array for the given component path
     * @param componentName
     * @return
     */
    protected int[][] computeIndices(String baseComponentName, String componentName)
    {
        int[][] indices;
        int indexNum = 0;
        DataComponent component;
        String[] names, baseNames;
        
        // starting from node root component
        if (baseComponentName == null)
        {
            names = componentName.split("/");
            indices = new int[names.length+1][2];
            component = node.getComponent(0);
            indices[0][0] = 0;
            indices[0][1] = node.getComponentCount();
            indexNum = 1;
        }
        else
        {
            componentName = componentName.substring(baseComponentName.length() + 1);
            baseNames = baseComponentName.split("/");
            names = componentName.split("/");
            
            component = node.getComponent(0);
            for (int i=0, j=1; i<baseNames.length; i++, j++)
                component = component.getComponent(baseNames[i]);
                       
            indices = new int[names.length][2];
        }
        
        
        for (int i=0; i<names.length; i++, indexNum++)
        {
            DataComponent parentComponent = component;
            component = component.getComponent(names[i]);
                        
            if (parentComponent instanceof DataArray)
            {
                int count = parentComponent.getComponentCount();
                indices[indexNum][0] = 0;
                indices[indexNum][1] = count;
            }
            else
            {
                int index = parentComponent.getComponentIndex(names[i]);
                indices[indexNum][0] = index;
                indices[indexNum][1] = index;
            }
        }
        
//        for (int i=0; i<indices.length; i++)
//            System.out.print("[" + indices[i][0] + "-" + indices[i][1] + "],");
        
        System.out.println();
        return indices;
    }
    
    
    /**
     * Get a component given the linear index
     * and the precomputed index range array
     * @param indexRangeArray
     * @param linearIndex
     * @return
     */
    protected DataComponent getComponent(DataComponent baseComponent, int[][] indexRangeArray, int linearIndex)
    {
        int[] indexArray = new int[indexRangeArray.length];
        
        for (int i=indexArray.length-1; i>=0; i--)
        {
            int min = indexRangeArray[i][0];
            int max = indexRangeArray[i][1];
            
            if (min != max)
            {                
                int size = max - min;
                indexArray[i] = linearIndex % size;
                linearIndex = linearIndex / size;
            }
            else
            {
                indexArray[i] = min;
            }
        }     
        
        // select the component
        DataComponent component = baseComponent;
        for (int i=0; i<indexArray.length; i++)
            component = component.getComponent(indexArray[i]);
        
        return component;
    }
    
    
    protected int getComponentCount(int[][] indexRangeArray)
    {
        int count = 0;
        
        for (int i=0; i<indexRangeArray.length; i++)
        {
            int min = indexRangeArray[i][0];
            int max = indexRangeArray[i][1];
            int size = max - min;
            count += size;
        }
        
        return count;
    }
    
    
	public void setDataProvider(DataProvider dataProvider)
	{
		this.dataProvider = dataProvider;
	}


	public boolean isEnabled()
	{
		return enabled;
	}


	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;		
	}
    
    
    public SpatialExtent getBoundingBox()
    {
        return boundingBox;
    }


    public double[] getCenterPoint()
    {
        double[] centerPoint = new double[3];
        
        centerPoint[0] = (boundingBox.getMaxX() - boundingBox.getMinX()) / 2;
        centerPoint[1] = (boundingBox.getMaxY() - boundingBox.getMinY()) / 2;
        centerPoint[2] = (boundingBox.getMaxZ() - boundingBox.getMinZ()) / 2;        
        
        return centerPoint;
    }
}
