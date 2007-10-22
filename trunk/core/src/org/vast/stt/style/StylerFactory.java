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

import org.vast.ows.sld.*;


/**
 * <p><b>Title:</b><br/>
 * Styler Factory
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Creates the right Styler object based on the given symbolizer.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 21, 2005
 * @version 1.0
 */
public class StylerFactory
{
    public static DataStyler createStyler(Symbolizer sym)
    {
        DataStyler styler = null;

        if (sym instanceof PointSymbolizer)
            styler = new PointStyler();

        else if (sym instanceof LineSymbolizer)
            styler = new LineStyler();

        else if (sym instanceof PolygonSymbolizer)
            styler = new PolygonStyler();
        
        else if (sym instanceof VectorSymbolizer)
            styler = new VectorStyler();

        else if (sym instanceof TextSymbolizer)
            styler = new LabelStyler();

        else if (sym instanceof GridMeshSymbolizer)
            styler = new GridMeshStyler();
        
        else if (sym instanceof GridFillSymbolizer)
            styler = new GridFillStyler();
        
        else if (sym instanceof GridBorderSymbolizer)
            styler = new GridBorderStyler();

        else if (sym instanceof TextureSymbolizer)
            styler = new TextureStyler();
        
        else if (sym instanceof RasterSymbolizer)
            styler = new RasterStyler();

        if (styler != null)
        {
            styler.setSymbolizer(sym);            
        }

        return styler;
    }

}
