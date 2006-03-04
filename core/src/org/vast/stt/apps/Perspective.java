
package org.vast.stt.apps;

import org.eclipse.ui.*;
import org.vast.stt.gui.views.*;


public class Perspective implements IPerspectiveFactory
{

	public void createInitialLayout(IPageLayout layout)
	{
		String resFolderID = "STT.ResourceFolder";
		String scenesFolderID = "STT.ScenesFolder";
        String optFolderID = "STT.CustomizerFolder";
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);

		IFolderLayout ResourceFolder = layout.createFolder(resFolderID, IPageLayout.LEFT, 1.0f, layout.getEditorArea());
		IFolderLayout SceneFolder = layout.createFolder(scenesFolderID, IPageLayout.RIGHT, 0.25f, resFolderID);
        IFolderLayout OptFolder = layout.createFolder(optFolderID, IPageLayout.BOTTOM, 0.5f, resFolderID);
        
		ResourceFolder.addView(SceneTreeView.ID);
        OptFolder.addView(StyleView.ID);
        OptFolder.addView(TimeExtentView.ID);
        OptFolder.addView(SpatialExtentView.ID);
        
		SceneFolder.addPlaceholder(SceneView.ID + ":*");
	}

}
