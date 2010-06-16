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

package org.vast.stt.apps;

import org.eclipse.ui.*;
import org.vast.stt.gui.views.*;
import org.vast.sttx.gui.views.WPS_DemoView;


public class Perspective implements IPerspectiveFactory
{

	public void createInitialLayout(IPageLayout layout)
	{
		String resFolderID = "STT.ResourceFolder";
		String sceneFolderID = "STT.SceneFolder";
        String resOptFolderID = "STT.CustomizerFolder";
        String sceneOptFolderID = "STT.SceneOptionsFolder";
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);

		IFolderLayout resFolder = layout.createFolder(resFolderID, IPageLayout.LEFT, 1.0f, layout.getEditorArea());
		IFolderLayout sceneFolder = layout.createFolder(sceneFolderID, IPageLayout.RIGHT, 0.2f, resFolderID);
        IFolderLayout resOptFolder = layout.createFolder(resOptFolderID, IPageLayout.BOTTOM, 0.5f, resFolderID);
        IFolderLayout sceneOptFolder = layout.createFolder(sceneOptFolderID, IPageLayout.BOTTOM, 0.75f, sceneFolderID);
        
		resFolder.addView(SceneTreeView.ID);
        resFolder.addView(SceneItemsView.ID);

        resOptFolder.addView(SymbolizerView.ID);
        resOptFolder.addView(TimeExtentView.ID);
        resOptFolder.addView(SpatialExtentView.ID);

        sceneFolder.addView(WorldView.ID);
        sceneFolder.addPlaceholder(TableView.ID + ":*");
        sceneFolder.addPlaceholder(ChartView.ID + ":*");
        sceneFolder.addPlaceholder(CapabilitiesView.ID);
        sceneFolder.addPlaceholder(CatalogView.ID);
        
        sceneOptFolder.addView(SceneTimeView.ID);
        sceneOptFolder.addView(ImageView.ID);
        sceneOptFolder.addView(WPS_DemoView.ID);

        sceneOptFolder.addView("org.eclipse.ui.views.ProgressView");
	}

}
