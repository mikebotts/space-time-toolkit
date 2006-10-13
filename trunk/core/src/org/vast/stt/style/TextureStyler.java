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
import org.vast.ows.sld.RasterChannel;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sld.TextureSymbolizer;
import org.vast.stt.data.BlockListItem;


/**
 * <p><b>Title:</b><br/>
 * Texture Mapping Styler
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
public class TextureStyler extends AbstractStyler
{
	protected TextureSymbolizer symbolizer;
    protected TexturePatchGraphic patch;
    protected RasterPixelGraphic pixel;
    protected GridPointGraphic point;
    protected ListInfo gridBlocks, texBlocks;
    protected int[] gridIndex = new int[4];
    protected int[] texIndex = new int[4];
    
    
	public TextureStyler()
	{
        pixel = new RasterPixelGraphic();
        point = new GridPointGraphic();
        patch = new TexturePatchGraphic();
        point.a = 255;
	}
    
    
    public TexturePatchGraphic nextTile()
    {
        // if no more items one of the lists, just return null
        if (!(gridBlocks.blockIterator.hasNext() && texBlocks.blockIterator.hasNext()))
        {
            clearBlockData();
            return null;
        }
        
        // get grid block for next tile
        BlockListItem nextGrid = gridBlocks.blockIterator.next();
        
        // setup grid indexer with new data
        AbstractDataBlock nextGridBlock = nextGrid.getData();
        gridBlocks.blockIndexer.setData(nextGridBlock);
        gridBlocks.blockIndexer.reset();
        gridBlocks.blockIndexer.next(); // to make sure we visit array size data
        
        // if texture is coming from a different list
        // get next texture block
        BlockListItem nextTexture;
        if (gridBlocks == texBlocks)
            nextTexture = nextGrid;
        else
        {
            nextTexture = texBlocks.blockIterator.next();
            
            // setup texture indexer with new data 
            AbstractDataBlock nextTexBlock = nextTexture.getData();
            texBlocks.blockIndexer.setData(nextTexBlock);
            texBlocks.blockIndexer.reset();
            texBlocks.blockIndexer.next(); // to make sure we visit array size data
        }
        
        // copy current item in the patch object
        patch.grid.block = nextGrid;
        patch.texture.block = nextTexture;
        
        // see what's needed on this block
        prepareBlock(nextGrid);
        
        return patch;
    }
	   
    
    public RasterPixelGraphic getPixel(int x, int y)
    {
        texIndex[2] = x;
        texIndex[3] = y;
        texBlocks.blockIndexer.getData(texIndex);
        return pixel;
    }
    
    
    public GridPointGraphic getGridPoint(int u, int v, float uScale, float vScale, boolean normalize)
    {
        point.x = point.y = point.z = 0.0;
        gridIndex[0] = u;
        gridIndex[1] = v;
        gridBlocks.blockIndexer.getData(gridIndex);
        
        // compute texture coordinates
        if (normalize)
        {
            point.tx = (float)u / (float)(patch.grid.width-1) * uScale;
            point.ty = (float)v / (float)(patch.grid.length-1) * vScale;
        }
        else
        {
            point.tx = (float)u / (float)(patch.grid.width-1) * ((float)patch.texture.width-1);
            point.ty = (float)v / (float)(patch.grid.length-1) * ((float)patch.texture.height-1);
        }
        
        // adjust geometry to fit projection
        projection.adjust(geometryCrs, point);
        
        // add point to bbox if needed
        addToExtent(currentBlockInfo, point);
        
        return point;
    }
    
    
    private BlockListItem nextGridBlock()
    {
        ListInfo listInfo = dataLists[0];
        
        // if no more items in the list, just return null
        if (!listInfo.blockIterator.hasNext())
            return null;
        
        // otherwise get the next item
        BlockListItem nextItem = listInfo.blockIterator.next();

        // setup indexer with new data 
        AbstractDataBlock nextBlock = nextItem.getData();
        listInfo.blockIndexer.setData(nextBlock);
        listInfo.blockIndexer.reset();
        //listInfo.blockIndexer.getData(0,0,0);
        
        // see what's needed on this block
        prepareBlock(nextItem);
        
        return nextItem;
    }
    
    
    private GridPointGraphic nextPoint()
    {
        if (gridBlocks.blockIndexer.hasNext())
        {
            point.x = point.y = point.z = 0.0;            
            dataLists[0].blockIndexer.next();
            
            // adjust geometry to fit projection
            projection.adjust(geometryCrs, point);

            // add point to bbox if needed            
            addToExtent(currentBlockInfo, point);
            
            return point;
        }
        
        return null;
    }
    
    
    @Override
    public void computeBoundingBox()
    {
        this.wantComputeExtent = true;
        this.resetIterators();
                
        while (nextGridBlock() != null)
            while (nextPoint() != null);
    }


    @Override
	public void updateDataMappings()
	{
        boolean colors = false;
        ScalarParameter param;
        RasterChannel channel;
        String propertyName = null;   
        
        // reset all parameters
        pixel = new RasterPixelGraphic();
        point = new GridPointGraphic();
        this.clearAllMappers();
        
        // grid width array
        propertyName = this.symbolizer.getGridDimensions().get("width");
        if (propertyName != null)
        {
            addPropertyMapper(propertyName, new GridWidthMapper(0, patch.getGrid(), null));
        }
        
        // grid length array
        propertyName = this.symbolizer.getGridDimensions().get("length");
        if (propertyName != null)
        {
            addPropertyMapper(propertyName, new GridLengthMapper(1, patch.getGrid(), null));
        }
        
        // grid geometry X
        param = this.symbolizer.getGeometry().getX();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericXMapper(point, param.getMappingFunction()));
            }
        }
        
        // grid geometry Y
        param = this.symbolizer.getGeometry().getY();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericYMapper(point, param.getMappingFunction()));
            }
        }
        
        // grid geometry Z
        param = this.symbolizer.getGeometry().getZ();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericZMapper(point, param.getMappingFunction()));
            }
        }
                
        // make sure we keep a handle to the block list containing grid data
        gridBlocks = dataLists[dataLists.length-1];
        
        // raster width array
        propertyName = this.symbolizer.getRasterDimensions().get("width");
        if (propertyName != null)
        {
            addPropertyMapper(propertyName, new RasterWidthMapper(2, patch.getTexture(), null));
        }
        
        // raster height array
        propertyName = this.symbolizer.getRasterDimensions().get("height");
        if (propertyName != null)
        {
            addPropertyMapper(propertyName, new RasterHeightMapper(3, patch.getTexture(), null));
        }
        
        // global texture opacity
        param = this.symbolizer.getOpacity();
        if (param != null)
        {
            if (param.isConstant())
            {
                Object value = param.getConstantValue();
                pixel.a = (Float)value * 255;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericAlphaMapper(pixel, param.getMappingFunction()));
                }
            }
        }
        
        // pixel red
        channel = this.symbolizer.getRedChannel();
        if (channel != null)       
        {
            colors = true;
            propertyName = channel.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericRedMapper(pixel, channel.getMappingFunction()));
            }
        }
        
        // pixel green
        channel = this.symbolizer.getGreenChannel();
        if (channel != null)
        {
            colors = true;
            propertyName = channel.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericGreenMapper(pixel, channel.getMappingFunction()));
            }
        }
        
        // pixel blue
        channel = this.symbolizer.getBlueChannel();
        if (channel != null)
        {
            colors = true;
            propertyName = channel.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericBlueMapper(pixel, channel.getMappingFunction()));
            }
        }
        
        // pixel alpha
        channel = this.symbolizer.getAlphaChannel();
        if (channel != null)
        {
            propertyName = channel.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericAlphaMapper(pixel, channel.getMappingFunction()));
            }
        }
        
        // pixel gray
        if (!colors)
        {
            channel = this.symbolizer.getGrayChannel();
            if (channel != null)
            {
                propertyName = channel.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericGrayMapper(pixel, channel.getMappingFunction()));
                }
            }
        }
                
        // make sure we keep a handle to the block list containing image data
        texBlocks = dataLists[dataLists.length-1];
	}
	
	
	public TextureSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (TextureSymbolizer)sym;
	}


	public void accept(StylerVisitor visitor)
	{
        dataNode = dataItem.getDataProvider().getDataNode();

        if (dataNode.isNodeStructureReady())
        {
            if (dataLists.length == 0)
                updateDataMappings();
                
            visitor.visit(this);
        }		
	}
}
