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

import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sld.VectorSymbolizer;


/**
 * <p><b>Title:</b><br/>
 * Vector Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Converts source data to a sequence of VectorGraphic objects
 * that the renderer can access and render sequentially. 
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Apr 12, 2007
 * @version 1.0
 */
public class VectorStyler extends AbstractStyler
{
    protected VectorGraphic vector;
    protected LinePointGraphic point;
    protected VectorSymbolizer symbolizer;
    protected int[] pointIndex = new int[1];
    protected boolean useIcons;
    protected double vectorLength = 1.0;
    protected float steps;
    protected double currentStep = 0;
	
    
	public VectorStyler()
	{
        vector = new VectorGraphic();
        point = new LinePointGraphic();
	}
    
    
    @Override
    public void resetIterators()
    {
        super.resetIterators();
        currentStep = 0;
    }
    
    
    public LinePointGraphic nextPoint()
    {
        if (currentStep == 0)
        {
            // add break at beginning of vector
            point.graphBreak = true;
            for (int i=0; i<dataLists.length; i++)
            {
                if (dataLists[i].blockIndexer.hasNext())
                    dataLists[i].blockIndexer.next();
                else
                    return null;
            }
        }
        
        LinePointGraphic p1 = vector.point1;
        LinePointGraphic p2 = vector.point2;
        
        steps = ((Float)this.symbolizer.getNumberOfSteps().getConstantValue());
        double ratio = currentStep / (steps - 1.0);
        point.x = p1.x + (p2.x - p1.x)*ratio;
        point.y = p1.y + (p2.y - p1.y)*ratio;
        point.z = p1.z + (p2.z - p1.z)*ratio;
        
        // adjust geometry to fit projection
        if (projection != null)
            projection.adjust(geometryCrs, point);
        
        // increment/reset currentStep
        currentStep++;        
        if (currentStep >= steps)
            currentStep = 0;
        
        return point;
    }
        
    
    protected void transformPoint2()
    {
        LinePointGraphic p1 = vector.point1;
        LinePointGraphic p2 = vector.point2;
        
        switch (this.symbolizer.getDirectionType())
        {
            case ABS:
                break;
            
            case DIFF:
                p2.x = p1.x + p2.x * vectorLength;
                p2.y = p1.y + p2.y * vectorLength;
                p2.z = p1.z + p2.z * vectorLength;
                break;
            
            case ROT:
                // TODO handle ROT case in VectorStyler
                break;
        }
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
        vector = new VectorGraphic();
        this.clearAllMappers();
        
        // X,Y,Z are initialized to 0 by default
        vector.point1.x = vector.point1.y = vector.point1.z = 0.0;
        vector.point2.x = vector.point2.y = vector.point2.z = 0.0;
               
        // geometry X1
        param = this.symbolizer.getGeometry().getX();
        updateMappingX(vector.point1, param);
        
        //geometry Y1
        param = this.symbolizer.getGeometry().getY();
        updateMappingY(vector.point1, param);
        
        // geometry Z1
        param = this.symbolizer.getGeometry().getZ();
        updateMappingZ(vector.point1, param);
        
        // geometry X2
        param = this.symbolizer.getDirection().getX();
        updateMappingX(vector.point2, param);
        
        // geometry Y2
        param = this.symbolizer.getDirection().getY();
        updateMappingY(vector.point2, param);
        
        // geometry Z2
        param = this.symbolizer.getDirection().getZ();
        updateMappingZ(vector.point2, param);
        
        // geometry T
        param = this.symbolizer.getGeometry().getT();
        updateMappingT(point, param);
        
        // stroke color - red 
        param = this.symbolizer.getStroke().getColor().getRed();
        updateMappingRed(point, param);
        
        // stroke color - green 
        param = this.symbolizer.getStroke().getColor().getGreen();
        updateMappingGreen(point, param);
        
        // stroke color - blue 
        param = this.symbolizer.getStroke().getColor().getBlue();
        updateMappingBlue(point, param);
        
        // stroke color - alpha 
        param = this.symbolizer.getStroke().getColor().getAlpha();
        updateMappingAlpha(point, param);
        
        // geometry length
        param = this.symbolizer.getLength();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                vectorLength = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();          
                if (propertyName != null)
                {
                    //addPropertyMapper(propertyName, new GenericZMapper(vector.point2, param.getMappingFunction()));                
                }
            }
        }
        
        // stroke width 
        param = this.symbolizer.getStroke().getWidth();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                point.width = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new LineWidthMapper(point, param.getMappingFunction()));              
                }
            }
        }
        
        dataLists[0].indexOffset = 0;
        mappingsUpdated = true;
	}
	
	
	public VectorSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (VectorSymbolizer)sym;
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


    public boolean useIcons()
    {
        return useIcons;
    }
}