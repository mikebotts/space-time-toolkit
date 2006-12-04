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

import org.vast.ows.sld.*;
import org.vast.stt.project.table.TableSymbolizer;
import org.vast.stt.project.tree.DataItem;


/**
 * <p><b>Title:</b><br/>
 * Styler Factory
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Creates the right Styler object based on the given symbolizer.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
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
        
        else if (sym instanceof TableSymbolizer)
            styler = new TableStyler();

        if (styler != null)
        {
            styler.setSymbolizer(sym);            
        }

        return styler;
    }

}
