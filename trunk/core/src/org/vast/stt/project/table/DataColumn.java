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

package org.vast.stt.project.table;

import org.vast.ows.sld.Symbolizer;


/**
 * <p><b>Title:</b>
 * Data Column
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Object containing a Table column style options
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Sep 12, 2006
 * @version 1.0
 */
public class DataColumn
{
    public final static int AUTO_SIZE = -1;
    protected String name;
    protected int width = AUTO_SIZE;
    protected Symbolizer symbolizer;
        
    
    public DataColumn()
    {
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }
    
    
    public int getWidth()
    {
        return width;
    }


    public void setWidth(int width)
    {
        this.width = width;
    }


    public Symbolizer getSymbolizer()
    {
        return symbolizer;
    }


    public void setSymbolizer(Symbolizer symbolizer)
    {
        this.symbolizer = symbolizer;
    }
}
