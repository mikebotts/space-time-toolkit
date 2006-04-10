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

import org.ogc.cdm.common.DataBlock;
import org.vast.ows.sld.RasterSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;


/**
 * <p><b>Title:</b><br/>
 * Raster Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Renders rasters (i.e. mapped texture, grid data)
 * based on data obtained from a Data Provider and 
 * style info given by a Raster Symbolizer.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class RasterStyler extends AbstractStyler
{
	protected RasterSymbolizer symbolizer;
    protected RasterImageGraphic image;
    protected RasterPixelGraphic pixel;
    protected RasterGridGraphic grid;
    protected GridRowGraphic gridRow;
    
	
	public RasterStyler()
	{
        image = new RasterImageGraphic();
        pixel = new RasterPixelGraphic();
        grid = new RasterGridGraphic();
        gridRow = new GridRowGraphic();
	}
    
    
    /**
     * Returns the number of tiles, that is the number of
     * image/grid pairs to use for texture mapping.
     * @return
     */
    public int getTileCount()
    {
        return 1;
    }
	
    
    public RasterImageGraphic getImage(int index)
    {
//        if (image.data == null)
//        {
//            DataBlock data = dataNode.getComponent(0).getData();
//            int arraySize = data.getAtomCount();
//            byte[] array = new byte[arraySize];
//            image.data = array;       
//
//            for (int i=0; i<arraySize; i++)
//                array[i] = dataNode.getData().getByteValue(i);
//
//            image.width = 512;
//            image.height = 256;
//            image.updated = true;
//        }
        
        return image;
    }
    
    
    public RasterPixelGraphic getPixel(int x, int y, int z)
    {
        return pixel;
    }
    
    
    public RasterGridGraphic getGrid(int gridIndex)
    {
        grid.width = 10;
        grid.length = 10;
        return grid;
    }
    
    
    public GridRowGraphic nextGridRow()
    {
        return null;
    }
    
    
    public void updateBoundingBox()
	{
		// TODO Auto-generated method stub
	}


	public void updateDataMappings()
	{
        ScalarParameter param;
        String propertyName = null;   
        
        // generate indexing rules for geometry components        
        
        // geometry X
        param = this.symbolizer.getGeometry().getX();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericXMapper(pixel, null));
            }
        }
        
        //geometry Y
        param = this.symbolizer.getGeometry().getY();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericYMapper(pixel, null));
            }
        }
        
        // geometry Z
        param = this.symbolizer.getGeometry().getZ();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericZMapper(pixel, null));
            }
        }
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
                        
            visitor.visit(this);
        }		
	}
}
