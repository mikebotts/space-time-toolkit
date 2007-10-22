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

import org.vast.ows.sld.Symbolizer;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.Projection;
import org.vast.stt.provider.STTSpatialExtent;


/**
 * <p><b>Title:</b><br/>
 * Data Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * A styler transforms data into graphic objects (point, line, polygon, etc...).
 * This is done using the style info provided by the corresponding symbolizer.
 * Stylers are renderer independent and contains helper logic to find the data
 * within the data component structure and process data with mapping functions
 * when indicated by the symbolizer.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 21, 2005
 * @version 1.0
 */
public interface DataStyler
{

	public void setSymbolizer(Symbolizer sym);


	public Symbolizer getSymbolizer();


    public DataItem getDataItem();
    
    
	public void setDataItem(DataItem dataItem);
    
    
    public void setProjection(Projection projection);
    
    
    public Projection getProjection();

	
	public void updateDataMappings();
    
    
    public void skipBlocks(int blockCount);
    
    
    public boolean hasMoreBlocks();
    
    
    public void resetBoundingBox();
	
	
	public STTSpatialExtent getBoundingBox();
    
    
    public void accept(StylerVisitor visitor);
    
}
