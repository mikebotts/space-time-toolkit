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

package org.vast.stt.gui.widgets.image;


import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vast.cdm.common.DataBlock;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataBlockMixed;
import org.vast.ows.sld.Dimensions;
import org.vast.ows.sld.RasterChannel;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sld.TextureSymbolizer;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.BlockListIterator;
import org.vast.stt.data.DataNode;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.provider.DataProvider;

public class ImagePopup // implements SelectionListener, ModifyListener
{
	Display display;
	private Composite mainComp;
	private int defaultWidth = 704/2, defaultHeight = 480/2;
	static int popupCount = 0 ;
	private ImageCanvas canvas;
	private DataItem dataItem;
	
	public ImagePopup(Display display){
		this.display = display;
		initGui(display);
		popupCount++;
	}

	public void setDataItem(DataItem item){
		this.dataItem = item;
		DataProvider prov = item.getDataProvider();
		DataNode node = prov.getDataNode();
		node.getClass();
		BlockList list = node.getList("frameData");
		BlockListIterator it = list.getIterator();
		list.get(0);
			BlockListItem bli = it.next();
			DataBlock block = bli.getData();
			int height = block.getIntValue(2);
			int width = block.getIntValue(3);
			DataBlockMixed dbm = (DataBlockMixed)block;
			AbstractDataBlock [] adb = dbm.getUnderlyingObject();
			byte [] data = (byte [])adb[4].getUnderlyingObject();
		canvas.createImage(width, height, data);
	}

	protected void initGui(Display display) {
		Shell shell = new Shell(display,  SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.MODELESS); //,SWT.TITLE | SWT.RESIZE| SWT.TOOL);
		FillLayout layout = new FillLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		shell.setLayout(layout);
		Rectangle area = display.getClientArea();
		int xOr = area.width - defaultWidth - 20;
		int yOr = (popupCount * defaultHeight+10) + 50;
		shell.setLocation(xOr, yOr);
		shell.setSize(defaultWidth, defaultHeight);
		mainComp = new Composite(shell, 0x0);
		GridLayout gl = new GridLayout(1,true);
		mainComp.setLayout(gl);
		canvas = new ImageCanvas(mainComp, 0x0);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_RED));
		GridData gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
		gd.widthHint = defaultWidth;
		gd.heightHint = defaultHeight;
		canvas.setLayoutData(gd);
		
		shell.pack();
		shell.open();
	}

}