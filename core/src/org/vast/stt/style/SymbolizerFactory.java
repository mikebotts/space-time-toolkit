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

import java.util.Enumeration;
import org.vast.xml.DOMHelper;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.Fill;
import org.vast.ows.sld.Graphic;
import org.vast.ows.sld.GraphicMark;
import org.vast.ows.sld.LineSymbolizer;
import org.vast.ows.sld.PointSymbolizer;
import org.vast.ows.sld.SLDReader;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Stroke;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sld.TextureSymbolizer;
import org.vast.stt.apps.STTPlugin;
import org.vast.util.ExceptionSystem;


/**
 * <p><b>Title:</b>
 * 	SymbolizerFactory
 * </p>
 *
 * <p><b>Description:</b><br/>
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Nov 28, 2006
 * @version 1.0
 */
public class SymbolizerFactory {

	public enum SymbolizerType
    {
        point("point"), line("line"), grid("grid"), polygon("polygon"), raster("raster"), 
        texture("texture"), label("label");
        
        String typeStr;
        
        private SymbolizerType(String type){
        	this.typeStr = type;
        }
    };
	
   
    public static Symbolizer createDefaultSymbolizer(String symName, String symTypeStr){
    	SymbolizerType symType = SymbolizerType.valueOf(symTypeStr);
    	return createDefaultSymbolizer(symName, symType);
    }
    
    public static Symbolizer createDefaultSymbolizer(String symName, SymbolizerType symType)
    {
        Symbolizer newSymbolizer = null;
        switch (symType)
        {
        case point:
        	newSymbolizer = SymbolizerFactory.createDefaultPointSymbolizer();
            break;
        case line:
        	newSymbolizer = SymbolizerFactory.createDefaultLineSymbolizer();
            break;
//        case texture:
//            newStyler = StylerFactory.createDefaultTextureStyler();
//            break;
        default:
            System.err.println("SymbolizerFactory not supported in createNewSym()");
            return null;
        }
        if(newSymbolizer == null) {
            System.err.println("SymbolizerFactory.createNewSym failed for: " + symType);
            return null;
        }
        newSymbolizer.setName(symName);
        
        return newSymbolizer;
    }

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
        DOMHelper dom;
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

			dom = new DOMHelper(fileLocation, false);
			sym = sldReader.readTexture(dom, dom.getRootElement());
			return sym;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
  	
	public static String[] getSymbolizerTypes(){
		SymbolizerType [] symbolizerTypes = SymbolizerType.values();
		String [] symbolizerTypeStr = new String[symbolizerTypes.length];
		int i=0;
		for(SymbolizerType st : symbolizerTypes){
			symbolizerTypeStr[i++] = st.toString();
		}
		
		return symbolizerTypeStr;
	}
}

