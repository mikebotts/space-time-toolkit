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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 21, 2005
 * @version 1.0
 */
public interface StylerVisitor
{
	public void visit(PointStyler styler);
	
	public void visit(LineStyler styler);
	
	public void visit(PolygonStyler styler);
    
    public void visit(VectorStyler styler);
    
    public void visit(LabelStyler styler);
	
    public void visit(GridMeshStyler styler);
    
    public void visit(GridFillStyler styler);
    
    public void visit(GridBorderStyler styler);
    
	public void visit(RasterStyler styler);    
    
    public void visit(TextureStyler styler);
}
