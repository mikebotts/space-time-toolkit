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
    protected DataIndexer redData, greenData, blueData, alphaData;
    
	
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
        if (image.data == null)
        {
            DataBlock data = currentData.getComponent(0).getData();
            int arraySize = data.getAtomCount();
            byte[] array = new byte[arraySize];
            image.data = array;       
        
            for (int i=0; i<arraySize; i++)
                array[i] = currentData.getData().getByteValue(i);
            
            image.width = 512;
            image.height = 256;
            image.updated = true;
        }
        
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
    
    
    public boolean hasMoreRows()
    {
        return xData.hasNext();
    }
    
    
    public GridRowGraphic nextGridRow()
    {
        if (grid.updated = true)
        {
            gridRow.gridPoints = new GridPointGraphic[grid.width];
            grid.updated = false;
        }
        
        for (int i=0; i<grid.width; i++)
        {
            int linearIndex;
            
            if (xData != null)
            {
                linearIndex = xData.nextIndex();
                gridRow.gridPoints[i].x = xData.dataBlock.getDoubleValue(linearIndex);
            }
            
            if (yData != null)
            {
                linearIndex = yData.nextIndex();
                gridRow.gridPoints[i].y = yData.dataBlock.getDoubleValue(linearIndex);
            }
            
            if (zData != null)
            {
                linearIndex = zData.nextIndex();
                gridRow.gridPoints[i].z = zData.dataBlock.getDoubleValue(linearIndex);
            }
            
            if (redData != null)
            {
                linearIndex = redData.nextIndex();
                gridRow.gridPoints[i].r = redData.dataBlock.getFloatValue(linearIndex);
            }
            
            if (greenData != null)
            {
                linearIndex = greenData.nextIndex();
                gridRow.gridPoints[i].g = greenData.dataBlock.getFloatValue(linearIndex);
            }
            
            if (blueData != null)
            {
                linearIndex = blueData.nextIndex();
                gridRow.gridPoints[i].b = blueData.dataBlock.getFloatValue(linearIndex);
            }
            
            if (alphaData != null)
            {
                linearIndex = alphaData.nextIndex();
                gridRow.gridPoints[i].a = alphaData.dataBlock.getFloatValue(linearIndex);
            }
            
            // also computes texture coordinates
            
        }
        
        return gridRow;
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
                xData = new DataIndexer(currentData, propertyName);
                dataHelpers.add(xData);                
            }
        }
        
        //geometry Y
        param = this.symbolizer.getGeometry().getY();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                yData = new DataIndexer(currentData, propertyName);
                dataHelpers.add(yData);                
            }
        }
        
        // geometry Z
        param = this.symbolizer.getGeometry().getZ();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                zData = new DataIndexer(currentData, propertyName);
                dataHelpers.add(zData);                
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
        currentData = dataProvider.getDataNode();

        if (currentData != null)
        {
            if ((xData == null) && (yData == null) && (zData == null))
                updateDataMappings();
            
            visitor.visit(this);
        }		
	}
}
