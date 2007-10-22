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

package org.vast.stt.gui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;


public class ProjectEditor extends EditorPart
{
	protected static Image folderImg, fileImg, infoImg, inImg, outImg, linkImg, itemImg, gearImg, refreshImg;
	protected Tree tree;
	
	
    public ProjectEditor()
    {
    	gearImg = this.getTitleImage();
    }
    
    
    static
    {
    	folderImg = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
    	fileImg = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
    	inImg = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_FORWARD);
    	outImg = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_BACK);
    	linkImg = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_REDO);
    	infoImg = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
    	itemImg = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
    }


    public void createPartControl(Composite parent)
    {
    	tree = new Tree(parent, SWT.SINGLE);
    	tree.setHeaderVisible(true);
    	    	    	
    	TreeColumn col1 = new TreeColumn(tree, SWT.LEFT, 0);
    	col1.setText("Name");
    	col1.setWidth(300);
    	
    	TreeColumn col2 = new TreeColumn(tree, SWT.LEFT, 1);
    	col2.setText("Value");
    	col2.setWidth(250);
    	
    	TreeColumn col3 = new TreeColumn(tree, SWT.LEFT, 2);
    	col3.setText("Unit");
    	col3.setWidth(100);
    	
    	TreeColumn col4 = new TreeColumn(tree, SWT.LEFT, 3);
    	col4.setText("Definition");
    	col4.setWidth(200);
    	
    	TreeItem root = new TreeItem(tree, SWT.NONE);
    	root.setText("Root");
    }
    
    
   	@Override
	public void doSave(IProgressMonitor monitor)
	{
			
	}


	@Override
	public void doSaveAs()
	{
				
	}


	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		this.setSite(site);
		this.setInput(input);
	}
	
	
	/**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus()
    {
    	if (tree != null)
    		tree.setFocus();
    }


	@Override
	public boolean isDirty()
	{
		return false;
	}


	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}
}