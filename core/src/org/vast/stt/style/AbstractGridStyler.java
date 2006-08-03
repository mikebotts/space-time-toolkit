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

import org.vast.data.AbstractDataBlock;
import org.vast.math.Vector3d;
import org.vast.ows.sld.GridSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.data.BlockListItem;


/**
 * <p><b>Title:</b><br/>
 * Grid Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public abstract class AbstractGridStyler extends AbstractStyler
{
    protected GridSymbolizer symbolizer;
    protected GridPatchGraphic patch;
    protected GridPointGraphic point;
    
	
	public AbstractGridStyler()
	{
        patch = new GridPatchGraphic();
        point = new GridPointGraphic();
	}
    
    
    public GridPatchGraphic nextPatch()
    {
        ListInfo listInfo = dataLists[0]; 
        
        // if no more items in the list, just return null
        if (!listInfo.blockIterator.hasNext())
            return null;
        
        // otherwise get the next item
        BlockListItem nextItem = listInfo.blockIterator.next();
        
        // TODO implement block filtering here
        
        // setup indexer with new data 
        AbstractDataBlock nextBlock = nextItem.getData();
        listInfo.blockIndexer.setData(nextBlock);
        listInfo.blockIndexer.reset();
        listInfo.blockIndexer.getData(0,0,0);
        
        // TODO scan and compute block BBOX and Time Range
        
        // copy current item in the patch object
        patch.block = nextItem;
        
        return patch;
    }
    
    
    public GridPointGraphic getGridPoint(int u, int v)
    {
        dataLists[0].blockIndexer.getData(v, u, 0);
        
        if (computeExtents)
            bbox.resizeToContain(new Vector3d(point.x, point.y, point.z));
        
        return point;
    }


	public void updateDataMappings()
	{
        ScalarParameter param;
        String propertyName = null;
        
        // reset all parameters
        patch = new GridPatchGraphic();
        point = new GridPointGraphic();
        this.clearAllMappers();
        
        // geometry X
        param = this.symbolizer.getGeometry().getX();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericXMapper(point, param.getMappingFunction()));
            }
        }
        
        //geometry Y
        param = this.symbolizer.getGeometry().getY();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericYMapper(point, param.getMappingFunction()));
            }
        }
        
        // geometry Z
        param = this.symbolizer.getGeometry().getZ();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericZMapper(point, param.getMappingFunction()));
            }
        }
        
        // grid width
        param = this.symbolizer.getDimensions().getWidth();
        if (param != null)       
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GridWidthMapper(patch, param.getMappingFunction()));
            }
        }
        
        // grid length
        param = this.symbolizer.getDimensions().getLength();
        if (param != null)       
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GridLengthMapper(patch, param.getMappingFunction()));
            }
        }
        
        // grid depth
        param = this.symbolizer.getDimensions().getDepth();
        if (param != null)       
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GridDepthMapper(patch, param.getMappingFunction()));
            }
        }
	}
}
