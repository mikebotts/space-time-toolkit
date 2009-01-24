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

import org.vast.data.AbstractDataBlock;
import org.vast.ows.sld.GraphicImage;
import org.vast.ows.sld.GraphicMark;
import org.vast.ows.sld.GraphicSource;
import org.vast.ows.sld.PointSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.style.PointGraphic.ShapeType;
import org.vast.util.MessageSystem;


/**
 * <p><b>Title:</b><br/>
 * Point Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Converts source data to a sequence of PointGraphic objects
 * that the renderer can access and render sequentially. 
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class PointStyler extends AbstractStyler implements DataStyler1D
{
    protected PointGraphic point;    
    protected PointSymbolizer symbolizer;
    protected int[] pointIndex = new int[1];
    protected boolean useIcons;
    protected BlockListItem constantBlock;
    protected boolean returnConstantGraphic;
    protected boolean returnConstantBlock;
        
	
	public PointStyler()
	{
		point = new PointGraphic();
        constantBlock = new BlockListItem(null, null, null);
	}
    
    
    public PointGraphic nextPoint()
    {
        // reset point values
        point.x = constantX;
        point.y = constantY;
        point.z = constantZ;
        
        if (returnConstantGraphic || nextItem())
        {
            returnConstantGraphic = false;
            
            // adjust geometry to fit projection
            if (projection != null)
                projection.adjust(geometryCrs, point);
            
            return point;
        }
        
        return null;
    }
    
    
    public int getNumPoints()
    {
        if (allConstant)
            return 1;
        
        if (dataLists[0].indexOffset == 0)
            return dataLists[0].blockIterator.getList().getSize();
        
        return 0;
    }
    
    
    public PointGraphic getPoint(int u)
    {
        // reset point values
        point.x = constantX;
        point.y = constantY;
        point.z = constantZ;
        
        if (!allConstant)
        {    
            if (dataLists[0].indexOffset == 0)
            {
                AbstractDataBlock dataBlock = dataLists[0].blockIterator.getList().get(u);
                dataLists[0].blockIndexer.setData(dataBlock);
                dataLists[0].blockIndexer.reset();
                dataLists[0].blockIndexer.next();
            }
            else
            {
                pointIndex[0] = u;
                dataLists[0].blockIndexer.getData(pointIndex);
            }
        }
        
        // adjust geometry to fit projection
        if (projection != null)
            projection.adjust(geometryCrs, point);
        
        return point;
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
        String propertyName;
        Object value;
        
        // reset all parameters
        point = new PointGraphic();
        this.clearAllMappers();
        
        // constantPoint is true for now, it will be set to
        // false if at least one of X,Y,Z properies has a mapper
        allConstant = true;
        
        // X,Y,Z are initialized to 0 by default
        constantX = constantY = constantZ = 0.0;
               
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
        
        // simple graphic mark
        GraphicSource glyph = this.symbolizer.getGraphic().getGlyphs().get(0);
        if (glyph instanceof GraphicMark)
        {
            // color - red 
            param = ((GraphicMark)glyph).getFill().getColor().getRed();
            updateMappingRed(point, param);
            
            // color - green 
            param = ((GraphicMark)glyph).getFill().getColor().getGreen();
            updateMappingGreen(point, param);
            
            // color - blue 
            param = ((GraphicMark)glyph).getFill().getColor().getBlue();
            updateMappingBlue(point, param);
            
            // color - alpha 
            param = ((GraphicMark)glyph).getFill().getColor().getAlpha();
            updateMappingAlpha(point, param);
            
            // shape
            param = ((GraphicMark)glyph).getShape();
            if (param != null)
            {
                if (param.isConstant())
                {
                    String shapeName = (String)param.getValue();
                    if (shapeName.equalsIgnoreCase("square"))
                        point.shape = ShapeType.SQUARE;
                    else if (shapeName.equalsIgnoreCase("circle"))
                        point.shape = ShapeType.CIRCLE;
                    else if (shapeName.equalsIgnoreCase("triangle"))
                        point.shape = ShapeType.TRIANGLE;
                    else if (shapeName.equalsIgnoreCase("star"))
                        point.shape = ShapeType.STAR;
                    else
                        MessageSystem.display("Unknown shape: " + shapeName, true);
                }
//                else
//                {
//                    propertyName = param.getPropertyName();
//                    if (propertyName != null)
//                    {
//                        addPropertyMapper(propertyName, new GenericShapeMapper(point, param.getMappingFunction()));
//                        allConstant = false;
//                    }
//                }
            }
        }
        
        else if (glyph instanceof GraphicImage)
        {
            param = ((GraphicImage)glyph).getUrl();
            if (param != null)
            {
                if (param.isConstant())
                {
                    value = param.getConstantValue();
                    point.iconId = IconManager.getInstance().addIcon(((GraphicImage)glyph).getBaseFolder() + (String)value);
                }
                else
                {
                    propertyName = param.getPropertyName();
                    if (propertyName != null)
                    {
                        addPropertyMapper(propertyName, new IconIdMapper(point, param.getMappingFunction()));
                    }
                }
                
                useIcons = true;
            }
        }
        
        // point size
        param = this.symbolizer.getGraphic().getSize();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                point.size = ((Float)value).floatValue();
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new PointSizeMapper(point, param.getMappingFunction()));
                }
            }
        }
        
        // point orientation
        param = this.symbolizer.getGraphic().getRotation();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                point.orientation = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new PointOrientationMapper(point, param.getMappingFunction()));
                }
            }
        }
        
        if (dataLists.length > 0)
            dataLists[0].indexOffset = 0;
        
        mappingsUpdated = true;
	}
	
	
	public PointSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (PointSymbolizer)sym;
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
    
    
    @Override
    public void resetIterators()
    {
        super.resetIterators();
        returnConstantBlock = allConstant;
        returnConstantGraphic = allConstant;
    }
    
    
    @Override
    public BlockListItem nextBlock()
    {
        if (returnConstantBlock)
        {
            returnConstantBlock = false;
            return constantBlock;
        }
        
        return super.nextBlock();
    }


    public boolean useIcons()
    {
        return useIcons;
    }
}