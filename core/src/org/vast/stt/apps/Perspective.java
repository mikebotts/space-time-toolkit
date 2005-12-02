
package org.vast.stt.apps;

import org.eclipse.ui.*;
import org.vast.stt.gui.views.*;


public class Perspective implements IPerspectiveFactory
{

	public void createInitialLayout(IPageLayout layout)
	{
		String resFolderID = "STT.ResourceFolder";
		String scenesFolderID = "STT.ScenesFolder";
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);

		IFolderLayout ResourceFolder = layout.createFolder(resFolderID, IPageLayout.LEFT, 1.0f, layout.getEditorArea());
		IFolderLayout SceneFolder = layout.createFolder(scenesFolderID, IPageLayout.RIGHT, 0.2f, resFolderID);
		layout.addView(CustomizerView.ID, IPageLayout.BOTTOM, 0.5f, resFolderID);
		
		//ResourceFolder.addView(ResourceTreeView.ID);
		ResourceFolder.addView(SceneTreeView.ID);
		//ResourceFolder.addView(ServiceTreeView.ID);
		
		SceneFolder.addPlaceholder(SceneView.ID + ":*");
	}

}
