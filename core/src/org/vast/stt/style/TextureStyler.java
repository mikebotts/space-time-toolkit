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
    
	
	public TextureStyler()
	{
        pixel = new RasterPixelGraphic();
        point = new GridPointGraphic();
        patch = new TexturePatchGraphic();
	}
    
    
    public TexturePatchGraphic nextTile()
    {
        ListInfo gridBlocks = dataLists[0];
        ListInfo texBlocks = dataLists[1];
        
        // if no more items one of the lists, just return null
        if (!(gridBlocks.blockIterator.hasNext() && texBlocks.blockIterator.hasNext()))
            return null;
        
        // otherwise get blocks for next tile
        BlockListItem nextGrid = gridBlocks.blockIterator.next();
        BlockListItem nextTexture = texBlocks.blockIterator.next();
        
        // TODO implement block filtering here
        
        // setup grid indexer with new data 
        AbstractDataBlock nextGridBlock = nextGrid.getData();
        gridBlocks.blockIndexer.setData(nextGridBlock);
        gridBlocks.blockIndexer.reset();
        gridBlocks.blockIndexer.getData(0,0,0);
        
        // setup texture indexer with new data 
        AbstractDataBlock nextTexBlock = nextTexture.getData();
        texBlocks.blockIndexer.setData(nextTexBlock);
        texBlocks.blockIndexer.reset();
        texBlocks.blockIndexer.getData(0,0,0);
        
        // copy current item in the patch object
        patch.grid.block = nextGrid;
        patch.texture.block = nextTexture;
        
        // TODO scan and compute block BBOX and Time Range
        
        return patch;
    }
	   
    
    public RasterPixelGraphic getPixel(int x, int y)
    {
        //pixel.r = (float)x / (float)patch.texture.width;
        //pixel.g = (float)y / (float)patch.texture.height;
        dataLists[1].blockIndexer.getData(y, x, 0);
        return pixel;
    }
    
    
    public GridPointGraphic getGridPoint(int u, int v, float uScale, float vScale, boolean normalize)
    {
        dataLists[0].blockIndexer.getData(v, u, 0);
        
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
        
        if (computeExtents)
            bbox.resizeToContain(new Vector3d(point.x, point.y, point.z));
        
        return point;
    }
    
    
    public void updateBoundingBox()
	{
		// TODO Auto-generated method stub
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
        this.clearAllMappers();
        
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
        
        // grid width
        param = this.symbolizer.getGridDimensions().getWidth();
        if (param != null)       
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GridWidthMapper(patch.grid, param.getMappingFunction()));
            }
        }
        
        // grid length
        param = this.symbolizer.getGridDimensions().getLength();
        if (param != null)       
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GridLengthMapper(patch.grid, param.getMappingFunction()));
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
        
        // image width
        param = this.symbolizer.getRasterDimensions().getWidth();
        if (param != null)       
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new RasterWidthMapper(patch.texture, param.getMappingFunction()));
            }
        }
        
        // image height
        param = this.symbolizer.getRasterDimensions().getLength();
        if (param != null)       
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new RasterHeightMapper(patch.texture, param.getMappingFunction()));
            }
        }
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
