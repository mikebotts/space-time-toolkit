
package org.vast.stt.apps;

import org.eclipse.ui.*;
import org.vast.stt.gui.views.*;


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
		IFolderLayout sceneFolder = layout.createFolder(sceneFolderID, IPageLayout.RIGHT, 0.25f, resFolderID);
        IFolderLayout resOptFolder = layout.createFolder(resOptFolderID, IPageLayout.BOTTOM, 0.5f, resFolderID);
        IFolderLayout sceneOptFolder = layout.createFolder(sceneOptFolderID, IPageLayout.BOTTOM, 0.75f, sceneFolderID);
        
		resFolder.addView(SceneTreeView.ID);
        resFolder.addView(SceneItemsView.ID);
        resOptFolder.addView(DataProviderView.ID);        
        resOptFolder.addView(StyleView.ID);
        resOptFolder.addView(TimeExtentView.ID);
        resOptFolder.addView(SpatialExtentView.ID);

        //sceneFolder.addPlaceholder(WorldView.ID + ":*");
        sceneFolder.addView(WorldView.ID + ":000");
        sceneOptFolder.addView(MasterTimeView.ID);
	}

}
