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

import java.util.ArrayList;
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
    protected ArrayList<RasterTileGraphic> tiles;
    protected RasterPixelGraphic pixel;
    
	
	public RasterStyler()
	{
        pixel = new RasterPixelGraphic();
        tiles = new ArrayList<RasterTileGraphic>();
	}
    
    
    /**
     * Returns the number of tiles, that is the number of
     * image/grid pairs to use for texture mapping.
     * @return
     */
    public int getTileCount()
    {
        return tiles.size();
    }
    
    
    public RasterTileGraphic nextTile()
    {
        return tiles.get(0);
    }
	   
    
    public RasterPixelGraphic getPixel(int x, int y, int z)
    {
        return pixel;
    }
    
    
    public void updateDataMappings()
	{
        ScalarParameter param;
        String propertyName = null;   

        // reset all parameters
        tiles.clear();
        pixel = new RasterPixelGraphic();
        this.clearAllMappers();
        
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
        dataNode = dataItem.getDataProvider().getDataNode();

        if (dataNode != null)
        {
            if (dataLists.length == 0)
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
