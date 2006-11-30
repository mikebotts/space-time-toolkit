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

import java.util.Enumeration;
import org.vast.io.xml.DOMReader;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.Fill;
import org.vast.ows.sld.Graphic;
import org.vast.ows.sld.GraphicMark;
import org.vast.ows.sld.LineSymbolizer;
import org.vast.ows.sld.PointSymbolizer;
import org.vast.ows.sld.SLDReader;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Stroke;
import org.vast.ows.sld.TextureSymbolizer;
import org.vast.stt.apps.STTPlugin;
import org.vast.util.ExceptionSystem;

/**
 * <p><b>Title:</b>
 *  TODO:  Add Title
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  TODO: Add Description
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Nov 28, 2006
 * @version 1.0
 */

public class SymbolizerFactory {

  	public static PointSymbolizer createDefaultPointSymbolizer(){
		PointSymbolizer symbolizer = new PointSymbolizer();

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

		return symbolizer;
	}
  	
  	public static LineSymbolizer createDefaultLineSymbolizer(){
        LineSymbolizer symbolizer = new LineSymbolizer();
        Stroke stroke = new Stroke();

        //  width
        symbolizer.setStroke(stroke);
        ScalarParameter width = new ScalarParameter();
        width.setConstantValue(new Float(2.0));
        stroke.setWidth(width);

        //color
        stroke.setColor(new Color(1.0f, 0.0f, 0.0f, 1.0f));

        return symbolizer;
  	}
  	
  	public static TextureSymbolizer createWMSTextureSymbolizer(){
  	    SLDReader sldReader = new SLDReader();
		DOMReader dom;
		TextureSymbolizer sym = null;
		try {
			String fileLocation = null;
			Enumeration e = STTPlugin.getDefault().getBundle().findEntries(
					"templates", "wms.xml", false);
			if (e.hasMoreElements())
				fileLocation = (String) e.nextElement().toString();

			if (fileLocation == null) {
				ExceptionSystem.display(new Exception(
						"STT error: Cannot find template\\wms.xml"));
				return null;
			}

			dom = new DOMReader(fileLocation, false);
			sym = sldReader.readTexture(dom, dom.getRootElement());
			return sym;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

