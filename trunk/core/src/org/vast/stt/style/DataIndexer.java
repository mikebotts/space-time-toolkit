package org.vast.stt.style;

import java.util.ArrayList;

import org.ogc.cdm.common.DataBlock;
import org.ogc.cdm.common.DataComponent;
import org.vast.data.DataArray;
import org.vast.data.DataBlockList;
import org.vast.data.DataGroup;
import org.vast.data.DataList;

/**
 * <p><b>Title:</b><br/>
 * Indexed Data
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Provides iterative and random access methods to data defined
 * by a Data Component structure.  
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Mar 15, 2006
 * @version 1.0
 */
public class DataIndexer
{
    public class IndexRule
    {
        public int arraySize = -1;
        public int contentSize = -1;
        public int offset = 0;    
        public boolean varSize;
        public IndexRule targetRule;
        public IndexRule masterRule;
        public DataIndexer masterData;
        public int currentIndex = -1;
        public int currentLinearIndex = 0;
        public boolean newItem = false;
        public boolean done = false;
        public DataComponent parentComponent;
    }
    
    public DataBlock dataBlock;
    public DataComponent baseComponent;
    public IndexRule[] indexRules;
    public int currentRuleIndex = 0;
    public int lastLinearIndex;
    public int lastComponentIndex;
    public int lastBlockIndex;
    
    
    /**
     * Constructs an Indexed Data helper object to access data of
     * the component referenced by the given path.
     * @param baseComponent
     * @param componentName
     */
    public DataIndexer(DataComponent baseComponent, String componentName)
    {
        ArrayList<IndexRule> ruleList = new ArrayList<IndexRule>();
        
        // build component tree using component '/' separated fullName
        String[] names = componentName.split("/");
        DataComponent[] componentTree = new DataComponent[names.length];        
        DataComponent component = baseComponent;
        for (int i=0; i<names.length; i++)
        {
            component = component.getComponent(names[i]);
            componentTree[i] = component;
            
            if (component == null)
                throw new IllegalStateException("Data Component " + componentName + " was not found");
        }
        
        // loop through component tree and create appropriate rules
        DataComponent parentComponent;      
        int offset = 0;
        for (int i=componentTree.length-1; i>=0; i--)
        {
            component = componentTree[i];
            
            if (i != 0)
                parentComponent = componentTree[i-1];
            else
                parentComponent = baseComponent;            
            
            if (parentComponent instanceof DataGroup)
            {
                offset += parentComponent.getComponentIndex(names[i]);
            }
            else if (parentComponent instanceof DataArray)
            {
                IndexRule indexRule = new IndexRule();
                indexRule.arraySize = parentComponent.getComponentCount();
                indexRule.contentSize = component.getData().getAtomCount();
                indexRule.offset = offset;    
                indexRule.parentComponent = parentComponent;
                ruleList.add(0, indexRule);
                offset = 0;
                
                // insert another rule if variable size
                if (((DataArray)parentComponent).isVariableSize())
                {
                    IndexRule varSizeRule = new IndexRule();
                    varSizeRule.varSize = true;
                    varSizeRule.targetRule = ruleList.get(0);
                    varSizeRule.offset = computeVarSizeIndex((DataArray)parentComponent);
                    ruleList.add(0, varSizeRule);
                }
            }
            else if (parentComponent instanceof DataList)
            {
                IndexRule indexRule = new IndexRule();
                indexRule.arraySize = parentComponent.getComponentCount();
                indexRule.contentSize = 0;
                indexRule.offset = offset;
                indexRule.parentComponent = parentComponent;
                ruleList.add(0, indexRule);
                offset = 0;
            }
        }

        this.dataBlock = baseComponent.getComponent(0).getData();
        this.indexRules = ruleList.toArray(new IndexRule[0]);
        this.baseComponent = baseComponent;
    }
    
    
    private int computeVarSizeIndex(DataArray arrayComponent)
    {
        // TODO implement computation of variable size component index
        return -1;
    }
    
    
    /**
     * Reset iterator to beginning of data
     * @param indexedData
     */
    public void reset()
    {
        currentRuleIndex = 0;
        
        for (int i=0; i<indexRules.length; i++)
        {
            indexRules[i].currentIndex = -1;
            indexRules[i].currentLinearIndex = 0;
        }
    }
    
    
    /**
     * Check if more data is available
     * @return
     */
    public boolean hasNext()
    {
        for (int i=0; i<indexRules.length; i++)
        {
            IndexRule rule = indexRules[i];
            if (rule.currentIndex < rule.arraySize-1)
                return true;
        }
        
        return false;
    }
    
    
    /**
     * Retrieve linear index of next data value of interest
     * @return
     */
    public int nextIndex()
    {
        int linearIndex = 0;
        IndexRule currentRule = indexRules[currentRuleIndex];
        
        // case of relative rule
        if (currentRule.masterRule != null)
        {
            dataBlock = currentRule.masterData.dataBlock;
            currentRule.currentLinearIndex = currentRule.masterRule.currentLinearIndex + currentRule.offset;
            return currentRule.currentLinearIndex;
        }
        
        // if this rule is a varSize rule
        if (currentRule.varSize)
        {
            if (currentRule.currentIndex == -1)
            {
                // get array size value
                int size = dataBlock.getIntValue(currentRule.currentLinearIndex + currentRule.offset);
                //System.err.println("size: " + size);
                
                // set array size
                currentRule.targetRule.arraySize = size;
                
                currentRuleIndex++;
                IndexRule nextRule = indexRules[currentRuleIndex];
                nextRule.currentLinearIndex = currentRule.currentLinearIndex;
                linearIndex = nextIndex();
                
                currentRule.currentIndex++;
            }
            else
            {
                currentRule.currentIndex = -1;
                currentRuleIndex--;
                linearIndex = nextIndex();          
            }
        }
        
        // otherwise, just an array rule
        else
        {           
            // make sure we update node size
            if (currentRuleIndex == 0)
                currentRule.arraySize = baseComponent.getComponentCount();
            
            // if end of array not reached yet
            if (currentRule.currentIndex < currentRule.arraySize-1)
            {
                currentRule.currentIndex++;
                currentRule.newItem = true;
                
                // increment linear index to go to next value
                if (currentRule.currentIndex == 0)
                    currentRule.currentLinearIndex += currentRule.offset;
                else
                    currentRule.currentLinearIndex += currentRule.contentSize;
                
                // assign next data block
                if (currentRuleIndex == 0)
                    dataBlock = ((DataBlockList)baseComponent.getData()).get(currentRule.currentIndex);
                
                // jump to next rule
                if (currentRuleIndex < indexRules.length-1)
                {
                    // get and initialize next rule
                    currentRuleIndex++;
                    IndexRule nextRule = indexRules[currentRuleIndex];
                    nextRule.currentLinearIndex = currentRule.currentLinearIndex;               
                
                    // call recursively
                    linearIndex = nextIndex();
                }
                else
                {
                    linearIndex = currentRule.currentLinearIndex;
                }
            }
            
            // if end of array, jump to previous rule
            else
            {            
                //System.err.println(currentRule.currentIndex+1 + "/" + currentRule.arraySize);
                currentRule.currentIndex = -1;
                //currentRule.currentLinearIndex = 0;
                currentRuleIndex--;
                linearIndex = nextIndex();
            }
        }
                
        return linearIndex;
    }
    
    
    /**
     * Optimize rules when both data component are in the same group.
     * In this case there exist a pure offset between them and data2
     * can be located relative to data1 using this offset. 
     * @param data1
     * @param data2
     */
    public void optimizeIndexRules(DataIndexer data1)
    {
        int ruleCount1 = data1.indexRules.length;
        int ruleCount2 = this.indexRules.length;
        
        if (ruleCount1 != ruleCount2)
            return;
        
        int ruleIndex = ruleCount1 - 1;
        IndexRule lastRule1 = data1.indexRules[ruleIndex];
        IndexRule lastRule2 = this.indexRules[ruleIndex];
        
        if (lastRule1.parentComponent == lastRule2.parentComponent)
        {
            int offset1 = lastRule1.offset;
            int offset2 = lastRule2.offset;
            
            IndexRule newIndexRule = new IndexRule();
            newIndexRule.varSize = false;
            newIndexRule.masterRule = lastRule1;
            newIndexRule.masterData = data1;
            newIndexRule.offset = offset2 - offset1;
            this.indexRules = new IndexRule[1];
            this.indexRules[0] = newIndexRule;            
        }
    }
}



