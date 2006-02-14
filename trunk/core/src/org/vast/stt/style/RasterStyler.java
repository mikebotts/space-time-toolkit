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
    protected ImageGraphic image;
    
	
	public RasterStyler()
	{
        image = new ImageGraphic();
	}
	
    
    public ImageGraphic getImage(int index)
    {
        if (image.data == null)
        {
            DataBlock data = node.getComponent(0).getData();
            int arraySize = data.getAtomCount();
            byte[] array = new byte[arraySize];
            image.data = array;       
        
            for (int i=0; i<arraySize; i++)
                array[i] = node.getData().getByteValue(i);
            
            image.width = 512;
            image.height = 256;
        }
        
        return image;
    }
	

	public void updateBoundingBox()
	{
		// TODO Auto-generated method stub
	}


	public void updateDataMappings()
	{
		node = dataProvider.getDataNode();
        
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
        updateDataMappings(); //TODO shouldn't be called here -> not too efficient !
        visitor.visit(this);		
	}
}
