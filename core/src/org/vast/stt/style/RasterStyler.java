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

import org.vast.ows.sld.RasterChannel;
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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class RasterStyler extends AbstractStyler
{
	protected RasterSymbolizer symbolizer;
    protected RasterTileGraphic tile;
    protected RasterPixelGraphic pixel;
    
	
	public RasterStyler()
	{
        pixel = new RasterPixelGraphic();
        tile = new RasterTileGraphic();
	}
    
    
    /**
     * Returns the number of tiles, that is the number of
     * image/grid pairs to use for texture mapping.
     * @return
     */
    public int getTileCount()
    {
        return 0;
    }
    
    
    public RasterTileGraphic nextTile()
    {
        return null;
    }
	   
    
    public RasterPixelGraphic getPixel(int x, int y, int z)
    {
        return pixel;
    }
    
    
    public void updateDataMappings()
	{
        ScalarParameter param;  
        RasterChannel channel;
        
        // reset all parameters
        pixel = new RasterPixelGraphic();
        this.clearAllMappers();
        
        // X,Y,Z are initialized to 0 by default
        constantX = constantY = constantZ = 0.0;
        
        // geometry X
        param = this.symbolizer.getGeometry().getX();
        updateMappingX(pixel, param);
        
        //geometry Y
        param = this.symbolizer.getGeometry().getY();
        updateMappingY(pixel, param);
        
        // geometry Z
        param = this.symbolizer.getGeometry().getZ();
        updateMappingZ(pixel, param);
        
        // geometry T
        param = this.symbolizer.getGeometry().getT();
        updateMappingT(tile, param);
        
        // pixel red
        channel = this.symbolizer.getRedChannel();
        updateMappingRed(pixel, param);
        
        // pixel green
        channel = this.symbolizer.getGreenChannel();
        updateMappingGreen(pixel, param);
        
        // pixel blue
        channel = this.symbolizer.getBlueChannel();
        updateMappingBlue(pixel, param);
        
        // pixel alpha
        channel = this.symbolizer.getAlphaChannel();
        updateMappingAlpha(pixel, param);
        
        // pixel gray
        channel = this.symbolizer.getGrayChannel();
        if (channel != null)
        {
            String propertyName = channel.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericGrayMapper(pixel, channel.getMappingFunction()));
            }
        }
        
        mappingsUpdated = true;
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
        dataNode = dataItem.getDataProvider().getDataNode();

        if (dataNode != null)
        {
            if (!mappingsUpdated)
                updateDataMappings();
                        
            visitor.visit(this);
        }		
	}
    
    
    @Override
    public void computeBoundingBox()
    {
        // NOT REALLY APPLICABLE HERE SINCE THERE IS NO GEOMETRY...
    }
}
