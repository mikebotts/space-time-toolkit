/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.style;

import java.util.Hashtable;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataIndexer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.BlockListIterator;
import org.vast.stt.data.DataNode;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.Projection;
import org.vast.stt.project.world.Projection.Crs;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.STTTimeExtent;


/**
 * <p><b>Title:</b><br/>
 * Abstract Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Abstract base class for all stylers.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public abstract class AbstractStyler implements DataStyler
{
    protected class ListInfo
    {
        protected BlockListIterator blockIterator;
        protected DataIndexer blockIndexer;
        protected int indexOffset = -1; // use when grid/image dim should map along the list dim itself
        
        public ListInfo(BlockList blockList, DataIndexer dataIndexer)
        {
            this.blockIndexer = dataIndexer;
            this.blockIterator = blockList.getIterator();
        }
        
        public void getBlock(int index)
        {
            AbstractDataBlock dataBlock = blockIterator.getList().get(index);
            blockIndexer.setData(dataBlock);
            blockIndexer.reset();
            blockIndexer.next();
        }
        
        public void getData(int[] indexList)
        {
            if (indexOffset >= 0)
                getBlock(indexList[indexOffset] + blockOffset);
            blockIndexer.getData(indexList);
        }
    }
    
    
    protected DataItem dataItem;
    protected DataNode dataNode;
    protected Hashtable<String, IndexerTreeBuilder> treeBuilders;
    protected ListInfo[] dataLists;
    protected Projection projection;
    protected Crs geometryCrs;
    protected double constantX;
    protected double constantY;
    protected double constantZ;
    protected double constantT;
    protected boolean computeExtent = true;
    protected int[] indexList = new int[3];
    protected STTSpatialExtent bbox;
    protected STTTimeExtent timeExtent;
    protected int blockOffset = 0;
    protected boolean mappingsUpdated = false;
    protected boolean allConstant = false;
    
    
    public abstract void setSymbolizer(Symbolizer symbolizer);
    public abstract void updateDataMappings();
    protected abstract void computeBoundingBox();
        
    
    public AbstractStyler()
    {
        treeBuilders = new Hashtable<String, IndexerTreeBuilder>();
        dataLists = new ListInfo[0];
        geometryCrs = Crs.EPSG4329;
        bbox = new STTSpatialExtent();
    }
    
    
    public DataItem getDataItem()
    {
        return dataItem;
    }
    
        
    public void setDataItem(DataItem dataItem)
	{
		this.dataItem = dataItem;
	}
    
    
    public Projection getProjection()
    {
        return this.projection;
    }
    
    
    public void setProjection(Projection projection)
    {
        this.projection = projection;
        computeExtent = true;
    }
    
    
    /**
     * Adds a property mapper at the right spot in the indexer tree
     * This method will create a new ListInfo if not created yet
     * @param componentPath
     * @param newMapper
     */
    public void addPropertyMapper(String componentPath, PropertyMapper newMapper)
    {
        // extract list name from component path
        String listName = componentPath;
        if (componentPath.indexOf('/') != -1)
            listName = componentPath.substring(0, componentPath.indexOf('/'));
        
        // retrieve previously created builder or create a new one
        IndexerTreeBuilder builder = treeBuilders.get(listName);
        if (builder == null)
        {
            BlockList list = dataNode.getList(listName);
            if (list == null) return;
            builder = new IndexerTreeBuilder(list.getBlockStructure());
            treeBuilders.put(listName, builder);
            builder.addVisitor(componentPath, newMapper);
                       
            // resize indexer array
            ListInfo[] oldDataLists = dataLists;
            dataLists = new ListInfo[oldDataLists.length + 1];
            System.arraycopy(oldDataLists, 0, dataLists, 0, oldDataLists.length);
            dataLists[oldDataLists.length] = new ListInfo(list, builder.getRootIndexer());
        }
        else
        {
            builder.addVisitor(componentPath, newMapper);
        }
    }
    
    
    /**
     * Reset the block counter used for the block iterator
     */
    public void resetIterators()
    {
        // reset all list iterators
        for (int i = 0; i < dataLists.length; i++)
        {
            ListInfo info = dataLists[i];
            info.blockIterator.reset();
        }
    }
    
    
    protected void setCrs(String crs)
    {
        if (crs == null)
            this.geometryCrs = Crs.EPSG4329;
        else if (crs.equalsIgnoreCase("epsg4329"))
            this.geometryCrs = Crs.EPSG4329;
        else if (crs.equalsIgnoreCase("ecef"))
            this.geometryCrs = Crs.ECEF;
        else if (crs.equalsIgnoreCase("eci"))
            this.geometryCrs = Crs.ECI;
    }
    
    
    public void clearAllMappers()
    {
        dataLists = new ListInfo[0];
        treeBuilders.clear();
        mappingsUpdated = false;
    }
    
    
    protected boolean checkTime(double time)
    {
        if (Double.isNaN(time))
            return true;
        else
        {
            return false;
        }
    }
    
    
    protected void prepareBlock(BlockListItem block)
    {

    }
    
    
    protected void clearBlockData()
    {
        for (int i = 0; i < dataLists.length; i++)
            dataLists[i].blockIndexer.clearData();
    }
    
    
    /**
     * Load data for next block so that it is updated
     * for all array mappers. Also update block property mappers.
     */
    public BlockListItem nextBlock()
    {
        BlockListItem nextItem = null;
        
        for (int i = 0; i < dataLists.length; i++)
        {
            ListInfo info = dataLists[i]; 
            DataIndexer nextIndexer = info.blockIndexer;
                        
            if (!info.blockIterator.hasNext())
            {
                clearBlockData();
                return null;
            }
            
            nextItem = info.blockIterator.next();
            AbstractDataBlock nextBlock = nextItem.getData();
            nextIndexer.reset();
            nextIndexer.setData(nextBlock);

            prepareBlock(nextItem);
        }           
                        
        return nextItem;
    }

    
    /**
     * Get the next item in all lists that still
     * have more data.
     * @return
     */
    public boolean nextItem()
    {
        boolean more = false;
        
        for (int i = 0; i < dataLists.length; i++)
        {
            ListInfo info = dataLists[i]; 
            DataIndexer nextIndexer = info.blockIndexer;
            
            if (nextIndexer.hasNext())
            {
                more = true;
                nextIndexer.next();
            }
        }
        
        return more;
    }
    
    
    /**
     * Skips the specified number of blocks from the lists
     */
    public void skipBlocks(int blockCount)
    {
        for (int i = 0; i < dataLists.length; i++)
        {
            ListInfo info = dataLists[i]; 
            
            int count = 0;
            while (info.blockIterator.hasNext() && count < blockCount)
            {
                info.blockIterator.next();
                count++;
            }
        }
    }
    
    
    public boolean hasMoreBlocks()
    {
        for (int i = 0; i < dataLists.length; i++)
        {
            if (!dataLists[i].blockIterator.hasNext())
                return false;
        }
        
        return true;
    }
    
    
    protected void addToExtent(PrimitiveGraphic point)
    {
        bbox.resizeToContain(point.x, point.y, point.z);
        if (!Double.isNaN(point.t))
            timeExtent.resizeToContain(point.t);
    }
    
    
    public void resetBoundingBox()
    {
        computeExtent = true;
    }
    
   
    public STTSpatialExtent getBoundingBox()
    {      
        if (computeExtent)
        {
            bbox.nullify();
            this.computeBoundingBox();
            computeExtent = false;
        }
            
        return bbox;
    }
    
    
    public STTTimeExtent getTimeRange()
    {
        if (computeExtent)
        {
            timeExtent.nullify();
            this.computeBoundingBox();
            computeExtent = false;
        }
            
        return timeExtent;
    }

    
    /**
     * Sets up mapping for geometry X property
     * @param point
     * @param param
     * @return
     */
    protected void updateMappingX(PrimitiveGraphic point, ScalarParameter param)
    {
        if (param != null)
        {
            if (param.isConstant())
            {
                Object value = param.getConstantValue();
                point.x = constantX = (Float)value;
            }
            else
            {
                String propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericXMapper(point, param.getMappingFunction()));
                    allConstant = false;
                }
            }
        }
    }
    
    
    /**
     * Sets up mapping for geometry Y property
     * @param point
     * @param param
     * @return
     */
    protected void updateMappingY(PrimitiveGraphic point, ScalarParameter param)
    {
        if (param != null)
        {
            if (param.isConstant())
            {
                Object value = param.getConstantValue();
                point.y = constantY = (Float)value;
            }
            else
            {
                String propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericYMapper(point, param.getMappingFunction()));
                    allConstant = false;
                }
            }
        }
    }
    
    
    /**
     * Sets up mapping for geometry Z property
     * @param point
     * @param param
     * @return
     */
    protected void updateMappingZ(PrimitiveGraphic point, ScalarParameter param)
    {
        if (param != null)
        {
            if (param.isConstant())
            {
                Object value = param.getConstantValue();
                point.z = constantZ = (Float)value;
            }
            else
            {
                String propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericZMapper(point, param.getMappingFunction()));
                    allConstant = false;
                }
            }
        }
    }
    
    
    /**
     * Sets up mapping for geometry T property
     * @param graphic
     * @param param
     * @return
     */
    protected void updateMappingT(TimeTaggedGraphic graphic, ScalarParameter param)
    {
        if (param != null)
        {
            if (param.isConstant())
            {
                Object value = param.getConstantValue();
                graphic.t = constantT = (Float)value;
            }
            else
            {
                String propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericTimeMapper(graphic, param.getMappingFunction()));
                    allConstant = false;
                }
            }
        }
    }
    
    
    /**
     * Sets up mapping for geometry red property
     * @param point
     * @param param
     */
    protected void updateMappingRed(PrimitiveGraphic point, ScalarParameter param)
    {
        if (param != null)
        {
            if (param.isConstant())
            {
                Object value = param.getConstantValue();
                point.r = (Float)value;
            }
            else
            {
                String propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericRedMapper(point, param.getMappingFunction()));
                }
            }
        }
    }
    
    
    /**
     * Sets up mapping for geometry green property
     * @param point
     * @param param
     */
    protected void updateMappingGreen(PrimitiveGraphic point, ScalarParameter param)
    {
        if (param != null)
        {
            if (param.isConstant())
            {
                Object value = param.getConstantValue();
                point.g = (Float)value;
            }
            else
            {
                String propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericGreenMapper(point, param.getMappingFunction()));
                }
            }
        }
    }
    
    
    /**
     * Sets up mapping for geometry blue property
     * @param point
     * @param param
     */
    protected void updateMappingBlue(PrimitiveGraphic point, ScalarParameter param)
    {
        if (param != null)
        {
            if (param.isConstant())
            {
                Object value = param.getConstantValue();
                point.b = (Float)value;
            }
            else
            {
                String propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericBlueMapper(point, param.getMappingFunction()));
                }
            }
        }
    }
    
    
    /**
     * Sets up mapping for geometry alpha property
     * @param point
     * @param param
     */
    protected void updateMappingAlpha(PrimitiveGraphic point, ScalarParameter param)
    {
        if (param != null)
        {
            if (param.isConstant())
            {
                Object value = param.getConstantValue();
                point.a = (Float)value;
            }
            else
            {
                String propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericAlphaMapper(point, param.getMappingFunction()));
                }
            }
        }
    }
}