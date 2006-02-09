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
import org.vast.stt.data.DataProvider;


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
	
	public DataStyler createStyler(DataProvider provider, Symbolizer sym)
	{
		DataStyler styler = null;
		
		if (sym instanceof PointSymbolizer)
			styler = new PointStyler();
		
		else if (sym instanceof LineSymbolizer)
			styler = new LineStyler();
		
		else if (sym instanceof PolygonSymbolizer)
			styler = new LineStyler();
		
		if (styler != null)
		{
			styler.setSymbolizer(sym);
			styler.setDataProvider(provider);
		}
		
		return styler;
	}
	
	/**
	 * Convenience method for constructing a new PointStyler with a default 
	 * size and color, and the geometry settings of the input DataProvider
	 * 
	 * @param provider - the dataProvider to use for the new Styler
	 * @return PointStyler
	 */
	static public PointStyler createDefaultPointStyler(DataProvider provider){
		PointStyler styler = new PointStyler();
		styler.setDataProvider(provider);

		PointSymbolizer symbolizer = new PointSymbolizer();
		styler.setSymbolizer(symbolizer);

		Graphic graphic = new Graphic();
		ScalarParameter size = new ScalarParameter();
		size.setConstantValue(new Float(2.0));
		graphic.setSize(size);

		GraphicMark gm = new GraphicMark();
		ScalarParameter color = new ScalarParameter();
		color.setConstantValue(new Color(1.0f, 0.0f, 0.0f, 1.0f));
		Fill fill = new Fill();
		fill.setColor(color);
		gm.setFill(fill);
		graphic.addGlyph(gm);
		
		symbolizer.setGraphic(graphic);
		
		return styler;
	}
}
