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

import org.vast.stt.data.BlockInfo;

/**
 * <p><b>Title:</b><br/>
 * Styler Visitor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Interface for a Styler Visitor.
 * Renderer are typically styler visitors.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 21, 2005
 * @version 1.0
 */
public interface StylerVisitor
{
	public void visit(PointStyler styler);
	
	public void visit(LineStyler styler);
	
	public void visit(PolygonStyler styler);
    
    public void visit(LabelStyler styler);
	
    public void visit(GridMeshStyler styler);
    
    public void visit(GridFillStyler styler);
    
    public void visit(GridBorderStyler styler);
    
	public void visit(RasterStyler styler);    
    
    public void visit(TextureStyler styler);
    
    public boolean filterBlock(BlockInfo blockInfo);
}
