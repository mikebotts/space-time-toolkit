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
import java.util.List;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataIndexer;
import org.vast.ows.sld.RasterSymbolizer;
import org.vast.ows.sld.RasterChannel;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;


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
public class TextureMappingStyler extends AbstractStyler
{
	protected RasterSymbolizer symbolizer;
    protected List<TexturePatchInfo> tiles;
    protected TexturePatchInfo patch;
    protected RasterPixelGraphic pixel;
    protected GridPointGraphic point;
    protected ListInfo[] imageLists;
    protected ListInfo[] gridLists;
    protected int gridIndex1 = -1;
    protected int gridIndex2 = -1;
    protected DataIndexer blockIndexer1 = null;
    protected DataIndexer blockIndexer2 = null;
    protected BlockListItem[] gridBlocks = new BlockListItem[401];
    
	
	public TextureMappingStyler()
	{
        pixel = new RasterPixelGraphic();
        point = new GridPointGraphic();
        tiles = new ArrayList<TexturePatchInfo>();
        setName("Texture Styler");
	}
    
    
    public TexturePatchInfo nextTile()
    {
        if (patch == null)
        {
            patch = tiles.get(0);
            return patch;
        }
        
        return null;
    }
    
    
    @ Override
    public void reset()
    {
        super.reset();
        patch = null;
    }
	   
    
    public RasterPixelGraphic getPixel(int x, int y)
    {
        // figure out which block to read from
        // for now assume that blocks cover the whole grid width
        /*
        int blockIndex = y/patch.imageBlockHeight;
        int yLocal = y%patch.imageBlockHeight;
        
        AbstractDataBlock dataBlock = patch.imageBlocks[blockIndex].data;
        imageLists[0].blockIndexer.setData(dataBlock); //TODO optimize by keeping two mapper copies?
        imageLists[0].blockIndexer.getData(x, yLocal, 0);
        */
        pixel.r = (float)x / (float)patch.texture.width;
        pixel.g = (float)y / (float)patch.texture.height;
        
        return pixel;
    }
    
    
    public GridPointGraphic getGridPoint(int u, int v, boolean normalize)
    {
        // figure out which block to read from
        // for now assume that blocks cover the whole grid width
        int blockIndex = v/patch.gridBlockLength;
        int vLocal = v%patch.gridBlockLength;
        
        BlockListItem block = gridBlocks[blockIndex];
        //BlockListItem block = patch.firstGridBlock;
        //for (int i=0; i<blockIndex; i++)
        //    block = block.nextBlock;
        
        DataIndexer blockIndexer = null;
        if (blockIndex == gridIndex1)
        {
            blockIndexer = this.blockIndexer1;
        }
        else if (blockIndex == gridIndex2)
        {
            blockIndexer = this.blockIndexer2;
        }
        else
        {
            AbstractDataBlock dataBlock = block.data;
            
            if (u % 2 == 0)
            {
                blockIndexer = this.blockIndexer1;
                blockIndexer.setData(dataBlock);
                gridIndex1 = blockIndex;
            }
            else
            {
                blockIndexer = this.blockIndexer2;
                blockIndexer.setData(dataBlock);
                gridIndex2 = blockIndex;
            }
        }
        
        blockIndexer.getData(u, vLocal, 0);
        point.tx = (float)u / (float)patch.grid.width * (float)patch.texture.width;
        point.ty = (float)v / (float)patch.grid.length * (float)patch.texture.height;
        
        return point;
    }
    
    
    public void updateBoundingBox()
	{
		// TODO Auto-generated method stub
	}
    
    
    public void updateTiles()
    {
        // TODO scan new blocks and prepare new tiles
        
        // scan grid blocks
        // if list item has not been processed, generate tile for it
        
        TexturePatchInfo newPatch = new TexturePatchInfo();
        newPatch.grid.length = 401;
        newPatch.grid.width = 401;
        newPatch.gridBlockLength = 1;
        newPatch.gridBlockWidth = 401;
        newPatch.firstGridBlock = dataLists[0].blockList.firstBlock;
        newPatch.gridBlockCount = 401;
        
        newPatch.texture.height = 10;
        newPatch.texture.width = 10;
        
        BlockListItem block = newPatch.firstGridBlock;
        for (int i=0; i<401; i++)
        {
            gridBlocks[i] = block;
            block = block.nextBlock;
        }        
        
        tiles.add(newPatch);
    }


	public void updateDataMappings()
	{
        boolean colors = false;
        ScalarParameter param;
        RasterChannel channel;
        String propertyName = null;   
        
        // reset all parameters
        pixel = new RasterPixelGraphic();
        point = new GridPointGraphic();
        tiles.clear();
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
        
        // global texture opacity
        param = this.symbolizer.getOpacity();
        if (param != null)
        {
            if (param.isConstant())
            {
                Object value = param.getConstantValue();
                pixel.a = (Float)value;
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
            propertyName = channel.getChannelName().getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericRedMapper(pixel, param.getMappingFunction()));
            }
        }
        
        // pixel green
        channel = this.symbolizer.getGreenChannel();
        if (channel != null)
        {
            colors = true;
            propertyName = channel.getChannelName().getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericGreenMapper(pixel, param.getMappingFunction()));
            }
        }
        
        // pixel blue
        channel = this.symbolizer.getBlueChannel();
        if (channel != null)
        {
            colors = true;
            propertyName = channel.getChannelName().getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericBlueMapper(pixel, param.getMappingFunction()));
            }
        }
        
        // pixel alpha
        channel = this.symbolizer.getAlphaChannel();
        if (channel != null)
        {
            propertyName = channel.getChannelName().getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericAlphaMapper(pixel, param.getMappingFunction()));
            }
        }
        
        // pixel gray
        if (!colors)
        {
            channel = this.symbolizer.getGrayChannel();
            if (channel != null)
            {
                propertyName = channel.getChannelName().getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericGrayMapper(pixel, param.getMappingFunction()));
                }
            }
        }
        
        blockIndexer1 = dataLists[0].blockIndexer.copy();
        blockIndexer2 = dataLists[0].blockIndexer.copy();
	}
	
	
	public RasterSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (RasterSymbolizer)sym;
	}


	public void accept(StylerVisitor visitor)
	{
        dataNode = dataProvider.getDataNode();

        if (dataNode != null)
        {
            if (dataLists.length == 0)
                updateDataMappings();
                
            updateTiles();
            visitor.visit(this);
        }		
	}
}
