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
import org.vast.stt.project.DataItem;
import org.vast.stt.project.DataStyler;


/**
 * <p><b>Title:</b><br/>
 * Styler Factory
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Creates the right Styler object based on the symbolizer used.
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


    public DataStyler createStyler(DataItem dataItem, Symbolizer sym)
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

        else if (sym instanceof GridSymbolizer)
            styler = new GridStyler();

        else if (sym instanceof RasterSymbolizer)
            styler = new RasterStyler();

        else if (sym instanceof TextureSymbolizer)
            styler = new TextureMappingStyler();

        if (styler != null)
        {
            styler.setSymbolizer(sym);
            styler.setDataItem(dataItem);
        }

        return styler;
    }


    /**
     * Construct a new styler with the given name, stylerType, and DataProvider     * 
     * @param stylerName
     * @param stylerType
     * @param provider
     * @return the newly created styler
     */
    public static DataStyler createDefaultStyler(String stylerName, StylerType stylerType, DataItem dataItem)
    {
        DataStyler newStyler = null;
        switch (stylerType)
        {
        case point:
            newStyler = StylerFactory.createDefaultPointStyler(dataItem);
            break;
        case line:
            newStyler = StylerFactory.createDefaultLineStyler(dataItem);
            break;
        default:
            System.err.println("StylerType not supported in createNewStyler()");
            break;
        }
        return newStyler;
    }


    /**
     * Convenience method for constructing a new PointStyler with a default 
     * size and color, and the geometry settings of the input DataProvider     * 
     * @param provider - the dataProvider to use for the new Styler
     * @return PointStyler
     */
    static private PointStyler createDefaultPointStyler(DataItem dataItem)
    {
        PointStyler styler = new PointStyler();
        styler.setDataItem(dataItem);

        PointSymbolizer symbolizer = new PointSymbolizer();
        styler.setSymbolizer(symbolizer);

        //  size
        Graphic graphic = new Graphic();
        ScalarParameter size = new ScalarParameter();
        size.setConstantValue(new Float(2.0));
        graphic.setSize(size);

        //  color
        GraphicMark gm = new GraphicMark();
        Fill fill = new Fill();
        fill.setColor(new Color(1.0f, 0.0f, 0.0f, 1.0f));
        gm.setFill(fill);
        graphic.getGlyphs().add(gm);

        symbolizer.setGraphic(graphic);

        return styler;
    }


    /**
     * Convenience method for constructing a new LineStyler with a default 
     * size and color, and the geometry settings of the input DataProvider     * 
     * @param provider - the dataProvider to use for the new Styler
     * @return new LineStyler
     */
    static private LineStyler createDefaultLineStyler(DataItem dataItem)
    {
        LineStyler styler = new LineStyler();
        styler.setDataItem(dataItem);

        LineSymbolizer symbolizer = new LineSymbolizer();
        styler.setSymbolizer(symbolizer);
        Stroke stroke = new Stroke();

        //  width
        symbolizer.setStroke(stroke);
        ScalarParameter width = new ScalarParameter();
        width.setConstantValue(new Float(2.0));
        stroke.setWidth(width);

        //color
        stroke.setColor(new Color(1.0f, 0.0f, 0.0f, 1.0f));

        return styler;
    }

}
