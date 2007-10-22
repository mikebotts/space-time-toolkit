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
    Alexandre Robin <robin@nsstc.uah.edu>    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.project.tree.DataItem;
import org.vast.ows.sld.*;


/**
 * <p><b>Title:</b><br/>
 * StyleOptionChooser
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	SymbolizerOptionChooser is a composite that holds label/control pairs for 
 *  selecting options for a particular Styler type.   
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Jan 18, 2006
 * @version 1.0
 * 
 * TODO  add chooser/mapping widget when user selects + (add) style
 * TODO  add support for other Stylers (Polygon, Raster) 
 * TODO  support advanced options      
 */
public class SymbolizerOptionChooser extends OptionChooser
{
    private DataItem item;

	public SymbolizerOptionChooser(Composite parent){
		super(parent);
	}
    
    public void setDataItem(DataItem item)
    {
        this.item = item;
    }

	public void buildControls(Object symObj){
		Symbolizer sym = (Symbolizer) symObj;
		removeOldControls();

		if(sym instanceof PointSymbolizer) {
			optionController = new BasicPointController(optComp, (PointSymbolizer)sym);
		} else if (sym instanceof LineSymbolizer) {
			optionController = new BasicLineController(optComp, (LineSymbolizer)sym);
        } else if (sym instanceof PolygonSymbolizer) {
            optionController = new BasicPolygonController(optComp, (PolygonSymbolizer)sym);
        } else if (sym instanceof VectorSymbolizer) {
            optionController = new BasicVectorController(optComp, (VectorSymbolizer)sym);
		} else if (sym instanceof GridMeshSymbolizer) {
			optionController = new BasicGridMeshController(optComp, (GridSymbolizer)sym);
		} else if (sym instanceof GridFillSymbolizer) {
			optionController = new BasicGridFillController(optComp, (GridSymbolizer)sym);
		} else if (sym instanceof GridBorderSymbolizer) {
			optionController = new BasicGridBorderController(optComp, (GridSymbolizer)sym);
		} else if (sym instanceof RasterSymbolizer) {
			optionController = new BasicRasterController(optComp, (RasterSymbolizer)sym);
		} else if (sym instanceof TextSymbolizer) {
			optionController = new BasicLabelController(optComp, (TextSymbolizer)sym);
		} else if (sym instanceof TextureSymbolizer) {
			optionController = new BasicTextureController(optComp, (TextureSymbolizer)sym);
		} else 
			System.err.println("Styler not supported yet: " + sym);
		
		if(optionController == null)
			return;
        optionController.setDataItem(item);

		optComp.layout(true);		
		optScr.setMinSize(optComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		optComp.redraw();
	}	

}
