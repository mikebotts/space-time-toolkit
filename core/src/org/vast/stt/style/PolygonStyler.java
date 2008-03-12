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

import org.vast.ows.sld.Dimensions;
import org.vast.ows.sld.PolygonSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;


/**
 * <p><b>Title:</b><br/>
 * Polygon Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Converts source data to a sequence of PolygonPointGraphic objects
 * that the renderer can access and render sequentially.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class PolygonStyler extends AbstractStyler
{
	protected PolygonGraphic poly;
	protected PolygonPointGraphic point;
    protected PolygonSymbolizer symbolizer;
	
	
	public PolygonStyler()
	{
        poly = new PolygonGraphic();
		point = new PolygonPointGraphic();
	}
	
	
	public int getNumPoints()
    {
        if (dataLists[0].indexOffset == 0)
            return dataLists[0].blockIterator.getList().getSize();
        else
            return poly.numPoints;
    }
    
    
    public PolygonPointGraphic nextPoint()
    {
        if (dataLists[0].blockIndexer.hasNext())
        {
            // reset point values
            point.x = constantX;
            point.y = constantY;
            point.z = constantZ;
            
            dataLists[0].blockIndexer.next();
            
            // adjust geometry to fit projection
            projection.adjust(geometryCrs, point);
            
            return point;
        }
        
        return null;
    }
    
    
    @Override
    public void computeBoundingBox()
    {
        this.resetIterators();
        PrimitiveGraphic point;
        
        while (nextBlock() != null)
            while ((point = nextPoint()) != null)
                addToExtent(point);
    }


    @Override
	public void updateDataMappings()
	{
        ScalarParameter param;
        String propertyName = null;
        
        // reset all parameters
        poly = new PolygonGraphic();
        point = new PolygonPointGraphic();
        this.clearAllMappers();
        
        // X,Y,Z are initialized to 0 by default
        constantX = constantY = constantZ = 0.0;
        
        // polygon size (dimension or breaks)
        Dimensions dims = this.symbolizer.getDimensions();
        if (dims != null)
        {
	        propertyName = dims.get("numPoints");
	        if (propertyName != null)
	        {
	            if (propertyName.equals("/"))
	                dataLists[0].indexOffset = 0;
	            else    
	                addPropertyMapper(propertyName, new PolySizeMapper(0, poly, null));
	        }
        }
        else
        {
	        // geometry breaks
	        param = this.symbolizer.getGeometry().getBreaks();
	        if (param != null)
	        {
	            propertyName = param.getPropertyName();
	            if (propertyName != null)
	            {
	                addPropertyMapper(propertyName, new GenericBreakMapper(point, param.getMappingFunction()));               
	            }
	        }
        }
        
        // geometry X
        param = this.symbolizer.getGeometry().getX();
        updateMappingX(point, param);
        
        //geometry Y
        param = this.symbolizer.getGeometry().getY();
        updateMappingY(point, param);
        
        // geometry Z
        param = this.symbolizer.getGeometry().getZ();
        updateMappingZ(point, param);
        
        // geometry T
        param = this.symbolizer.getGeometry().getT();
        updateMappingT(point, param);
        
        // color - red 
        param = this.symbolizer.getFill().getColor().getRed();
        updateMappingRed(point, param);
        
        // color - green 
        param = this.symbolizer.getFill().getColor().getGreen();
        updateMappingGreen(point, param);
        
        // color - blue 
        param = this.symbolizer.getFill().getColor().getBlue();
        updateMappingBlue(point, param);
        
        // color - alpha 
        param = this.symbolizer.getFill().getColor().getAlpha();
        updateMappingAlpha(point, param);
        
        mappingsUpdated = true;
	}
	
	
	public PolygonSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (PolygonSymbolizer)sym;
        this.setCrs(sym.getGeometry().getCrs());
	}


	public void accept(StylerVisitor visitor)
	{
        dataNode = dataItem.getDataProvider().getDataNode();
        
        if (dataNode.isNodeStructureReady())
        {
            if (!mappingsUpdated)
                updateDataMappings();
                        
            visitor.visit(this);
        }		
	}
}
