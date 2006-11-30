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
    public static enum StylerType
    {
        point, line, grid, polygon, raster, texture, label
    };


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


    /**
     * Construct a new styler with the given name, stylerType, and DataProvider
     * @param stylerName
     * @param stylerType
     * @param dataItem
     * @return the newly created styler
     */
    public static DataStyler createDefaultStyler(String stylerName, StylerType stylerType)
    {
        DataStyler newStyler = null;
        switch (stylerType)
        {
        case point:
            newStyler = StylerFactory.createDefaultPointStyler();
            break;
        case line:
            newStyler = StylerFactory.createDefaultLineStyler();
            break;
//        case texture:
//            newStyler = StylerFactory.createDefaultTextureStyler();
//            break;
        default:
            System.err.println("StylerType not supported in createNewStyler()");
            return null;
        }
        if(newStyler == null) {
            System.err.println("StylerFactory.createNewStyler failed for: " + stylerType);
            return null;
        }
        newStyler.getSymbolizer().setName(stylerName);
        
        return newStyler;
    }


    /**
     * Convenience method for constructing a new PointStyler with a default 
     * size and color, and the geometry settings of the input DataProvider 
     * @param provider - the dataProvider to use for the new Styler
     * @return PointStyler
     */
    private static PointStyler createDefaultPointStyler()
    {
        PointStyler styler = new PointStyler();

        PointSymbolizer symbolizer = SymbolizerFactory.createDefaultPointSymbolizer();
        styler.setSymbolizer(symbolizer);

        return styler;
    }


    /**
     * Convenience method for constructing a new LineStyler with a default 
     * size and color, and the geometry settings of the input DataProvider
     * @param provider - the dataProvider to use for the new Styler
     * @return new LineStyler
     */
    private static LineStyler createDefaultLineStyler()
    {
        LineStyler styler = new LineStyler();

        LineSymbolizer symbolizer = SymbolizerFactory.createDefaultLineSymbolizer();
        styler.setSymbolizer(symbolizer);

        return styler;
    }

    /**
    * @param provider - the dataProvider to use for the new Styler
    * @return new TextureStyler
    */
   public static TextureStyler createWMSTextureStyler(DataItem dataItem)
   {
	   TextureStyler styler = new TextureStyler();
	   styler.setDataItem(dataItem);
	   
	   TextureSymbolizer sym = SymbolizerFactory.createWMSTextureSymbolizer();
	   
	   styler.setSymbolizer(sym);
	   styler.setDataItem(dataItem);
	   
	   return styler;
   }
}
