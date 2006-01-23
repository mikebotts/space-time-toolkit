package org.vast.stt.gui.widgets;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * <p><b>Title:</b><br/>
 * AddStylerDialog
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	First cut of widget to select new styler type.  This widget will eventually be 
 *  a tabbedFolder dialog probably, with mapping controls on one tab     
 *
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 22, 2006
 * @version 1.0
 * 
 */
public class AddStylerDialog extends Dialog {

	private List list;
	int stylerType = -1;
	/**
	 * Create the dialog
	 * @param parentShell
	 */
	public AddStylerDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * Create contents of the dialog
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
//		container.setBackground(SWTResourceManager.getColor(192, 192, 192));
		list = new List(container, SWT.BORDER);
		list.setItems(new String[] {"Point Styler", "Line Styler"});
		list.setSelection(0);
		final GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		list.setLayoutData(gridData);
		//
		return container;
	}

	/**
	 * Create contents of the button bar
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(225, 207);
	}

	@Override
	protected void cancelPressed() {
		this.close();
		//  Any other cleanup needed?
	}

	@Override
	protected void okPressed() {
		stylerType = list.getSelectionIndex();
		this.setReturnCode(SWT.OK);
		this.close();
	}

	public int getStylerType(){
		return stylerType;
	}
}
