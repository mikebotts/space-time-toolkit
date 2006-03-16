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

import java.util.ArrayList;
import org.ogc.cdm.common.DataComponent;
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
    protected SpatialExtent bbox;
    protected DataComponent currentData;
	protected String name;
	protected boolean enabled;
    protected ArrayList<DataIndexer> dataHelpers;
    protected DataIndexer xData, yData, zData, tData;
    
	
	public AbstractStyler()
	{
        dataHelpers = new ArrayList<DataIndexer>();
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
        return bbox;
    }


    public double[] getCenterPoint()
    {
        double[] centerPoint = new double[3];
        
        centerPoint[0] = (bbox.getMaxX() - bbox.getMinX()) / 2;
        centerPoint[1] = (bbox.getMaxY() - bbox.getMinY()) / 2;
        centerPoint[2] = (bbox.getMaxZ() - bbox.getMinZ()) / 2;
        
        return centerPoint;
    }
    
    
    public void updateBoundingBox()
    {
        
    }
    
    
    /**
     * Reset all data helpers
     */
    public void reset()
    {
        for (int i=0; i<dataHelpers.size(); i++)
            dataHelpers.get(i).reset();
    }
}
    
    
//    protected int getComponentCount(IndexedData indexedData)
//    {
//        return 8000;
//        int blockCount = node.getComponentCount();
//        
//        int count = 1;
//        IndexRule[] indexRules = indexedData.indexRules;
//        for (int i=0; i<indexRules.length; i++)
//            count *= indexRules[i].arraySize;
//        
//        if (count > 0)
//            count *= blockCount;
//        else
//        {
//            count = 0;
//            DataBlockList nodeData = (DataBlockList)node.getData();
//            
//            for (int i=0; i<blockCount; i++)
//            {
//                DataBlock nextDataBlock = nodeData.get(i);
//                count += nextDataBlock.getIntValue(sizeDataOffset);
//            }
//        }
//        
//        return count;
//    }
    
    
//    protected int getDataIndex(IndexedData indexedData, int componentIndex)
//    {
//        int linearIndex = 0;
//        int blockIndex;
//        int currentIndex = componentIndex;
//        IndexRule[] indexRules = indexedData.indexRules;       
//        int totalInBlock = 0;
//        
//        if (componentIndex > indexedData.lastComponentIndex)
//        {
//            currentIndex -= indexedData.lastComponentIndex;
//            blockIndex = indexedData.lastBlockIndex;
//        }
//        else
//            blockIndex = -1;
//        
//        DataBlockList nodeData = (DataBlockList)node.getData();
//        DataBlock nextDataBlock;
//        
//        do
//        {
//            blockIndex++;
//            currentIndex -= totalInBlock;
//            nextDataBlock = nodeData.get(blockIndex);
//            totalInBlock = nextDataBlock.getIntValue(0);
//            //System.err.println("-> " + componentIndex + "/" + currentIndex + "/" + totalInBlock);
//        }
//        while (currentIndex >= totalInBlock);
//        //System.err.println("END");
//        indexedData.dataBlock = nextDataBlock;
//        indexedData.lastComponentIndex = componentIndex - currentIndex;
//        indexedData.lastBlockIndex = blockIndex-1;
//        
//        // then apply index rules
//        for (int i=0; i<indexRules.length; i++)
//        {
//            linearIndex += indexRules[i].contentSize*(currentIndex) + indexRules[i].offset;
//        }
//
//        return linearIndex;
//    }
    
    
//    protected IndexedData buildIndexRules(String baseComponentName, String componentName)
//    {
//        ArrayList<IndexRule> ruleList = new ArrayList<IndexRule>();
//        IndexedData indexedData = new IndexedData();
//        
//        // first find base component using '/' separated name
//        DataComponent baseComponent = node;        
//        if (baseComponentName != null)
//        {
//            String[] baseNames = baseComponentName.split("/");
//            for (int i=0; i<baseNames.length; i++)
//            {
//                baseComponent = baseComponent.getComponent(baseNames[i]);
//                
//                if (baseComponent == null)
//                    throw new IllegalStateException("Data Component " + baseComponentName + " was not found");
//            }
//            
//            // remove baseComponentName from componentName
//            componentName = componentName.substring(baseComponentName.length()+1);
//        }
//        
//        // build component tree using component '/' separated fullName
//        String[] names = componentName.split("/");
//        DataComponent[] componentTree = new DataComponent[names.length];        
//        DataComponent component = baseComponent;
//        for (int i=0; i<names.length; i++)
//        {
//            component = component.getComponent(names[i]);
//            componentTree[i] = component;
//            
//            if (component == null)
//                throw new IllegalStateException("Data Component " + componentName + " was not found");
//        }
//        
//        // loop through component tree and create appropriate rules
//        DataComponent parentComponent;      
//        int offset = 0;
//        for (int i=componentTree.length-1; i>=0; i--)
//        {
//            component = componentTree[i];
//            
//            if (i != 0)
//                parentComponent = componentTree[i-1];
//            else
//                parentComponent = baseComponent;            
//            
//            if (parentComponent instanceof DataGroup)
//            {
//                offset += parentComponent.getComponentIndex(names[i]);
//            }
//            else if (parentComponent instanceof DataArray)
//            {
//                IndexRule indexRule = new IndexRule();
//                
//                if (!((DataArray)parentComponent).isVariableSize())
//                    indexRule.arraySize = parentComponent.getComponentCount();
//                else
//                    indexRule.varSize = true;
//                
//                indexRule.contentSize = component.getData().getAtomCount();
//                indexRule.offset = offset;            
//                ruleList.add(indexRule);
//                offset = 0;
//            }
//            
//            // case of DataList (we should be at the top here)
//            else if (parentComponent instanceof DataList)
//            {
//                IndexRule indexRule;
//                int listSize = ruleList.size();
//                
//                if (listSize > 0)
//                    indexRule = ruleList.get(listSize - 1);
//                else
//                {
//                    indexRule = new IndexRule();
//                    indexRule.contentSize = 0;
//                    indexRule.arraySize = 0;
//                }
//                
//                indexRule.offset += offset;                 
//                offset = 0;
//            }
//        }
//
//        indexedData.dataBlock = baseComponent.getComponent(0).getData();
//        indexedData.indexRules = ruleList.toArray(new IndexRule[0]);
//        
//        return indexedData;
//    }


//class IndexedData
//{
//    public DataBlock dataBlock;
//    public IndexRule[] indexRules;
//    public int lastComponentIndex;
//    public int lastBlockIndex;
//    public int currentRuleIndex = 0;
//}
//
//
//class IndexRule
//{
//    public int arraySize = -1;
//    public int contentSize = -1;
//    public int offset = 0;    
//    public boolean varContent;
//    public boolean varSize;
//    public int sizeDataOffset = 0;
//    public IndexRule targetRule;
//    public IndexRule masterRule;
//    public IndexedData masterData;
//    public int currentIndex = -1;
//    public int currentLinearIndex = 0;
//    public boolean newItem = false;
//    public DataComponent dataComponent;
//}