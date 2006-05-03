package org.vast.stt.gui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.gui.widgets.time.MasterTimeWidget;
import org.vast.stt.gui.widgets.time.TimeExtentWidget;
import org.vast.stt.project.DataProvider;
import org.vast.stt.scene.DataEntry;
import org.vast.stt.scene.DataItem;

/**
 * <p><b>Title:</b><br/>
 * MasterTimeView
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	View for MasterTime Controller for a Scene/Workbench   
 *
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date May 1, 2006
 * @version 1.0
 * 
 */
public class MasterTimeView extends ViewPart // implements ISelectionListener
{	
	public static final String ID = "STT.MasterTimeView";
	private MasterTimeWidget masterTimeWidget;
	
	@Override
	public void createPartControl(Composite parent) {
		masterTimeWidget = new MasterTimeWidget(parent); 
		//getSite().getPage().addPostSelectionListener(SceneTreeView.ID, this);		
	}
	@Override
	public void setFocus() {
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
	}
}
