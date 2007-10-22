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

package org.vast.stt.provider;

import org.vast.stt.data.DataException;
import org.vast.stt.data.DataNode;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventProducer;
import org.vast.stt.project.Resource;


/**
 * <p><b>Title:</b><br/>
 * Data Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Common interface for all data providers.
 * This allows updating and access to the DataNode, associating the
 * Provider to a given resource (file or service), as well as providing
 * feedback and control over the update operation.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 21, 2005
 * @version 1.0
 */
public interface DataProvider extends Resource, STTEventListener, STTEventProducer
{
    
    public void startUpdate(boolean force);
    
    
    public void cancelUpdate();
    
    
    public void init() throws DataException;
    
    
    public void updateData() throws DataException;
	
	
	public void clearData();
    
    
	public boolean isUpdating();
    
    
    public boolean hasError();
    
    
    public void setError(boolean error);
    
    
    public boolean isEnabled();
    
    
    public void setEnabled(boolean enabled);


	public DataNode getDataNode();
	
	
	public STTSpatialExtent getSpatialExtent();
	
	
	public void setSpatialExtent(STTSpatialExtent spatialExtent);
	
  
	public STTTimeExtent getTimeExtent();
	
	
	public void setTimeExtent(STTTimeExtent timeExtent);
	
	
	public STTSpatialExtent getMaxSpatialExtent();
	
	
	public STTTimeExtent getMaxTimeExtent();
	
	
	public boolean isSpatialSubsetSupported();
	
	
	public boolean isTimeSubsetSupported();
}