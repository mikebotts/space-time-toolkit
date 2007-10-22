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

package org.vast.stt.project.tree;

import java.util.ArrayList;


/**
 * <p><b>Title:</b><br/>
 * Data List
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Represents a list of data items in a scene
 * DataList can be nested recursively, this is why the
 * DataEntry interface is used her
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 17, 2005
 * @version 1.0
 */
public class DataFolder extends ArrayList<DataEntry> implements DataEntry
{
	static final long serialVersionUID = 0;
	protected String name;
	protected DataItemIterator iterator;
	
	
	public DataFolder()
	{
		iterator = new DataItemIterator(this);
	}
	
	
	public DataFolder(int listSize)
	{
		super(listSize);
		iterator = new DataItemIterator(this);
	}
	

	public String getName()
	{
		return name;
	}

	
	public void setName(String name)
	{
		this.name = name;
	}
	
	
	public boolean isEnabled()
	{
		return true;
	}


	public void setEnabled(boolean enabled)
	{
		iterator.reset();
		while (iterator.hasNext())
			iterator.next().setEnabled(enabled);
	}
	
	
	public DataItemIterator getItemIterator()
	{
		iterator.reset();
		return iterator;
	}
    
    
    /**
     * Looks for the given entry recursively into this folder
     * @param item
     * @return true if entry was found in this folder or its sub-folders
     */
    public boolean containsRecursively(DataEntry item)
    {
        for (int i=0; i<size(); i++)
        {
            DataEntry nextEntry = get(i);
            
            if (nextEntry == item)
                return true;
            
            else if (nextEntry instanceof DataFolder)
            {
                boolean found = ((DataFolder)nextEntry).containsRecursively(item);
                if (found)
                    return true;
            }
            
            else if (nextEntry instanceof DataItem)
            {
                boolean found = ((DataItem)nextEntry).getMasks().contains(item);
                if (found)
                    return true;
            }
        }
        
        return false;
    }
}
