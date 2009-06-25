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
    Tony Cook
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.commands;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.PlatformUI;

/*
 * TODO  - add widget for controlling options
 *       - not sure this should be a command, since you can't undo it 
 */

public class CaptureScreen implements Command {
	
	int cnt = 1;
	
    public void testScreenCap() {
    	
    	try {
			Robot robot = new Robot();
			Rectangle appBounds = PlatformUI.getWorkbench().getDisplay().getActiveShell().getBounds();
			java.awt.Rectangle appBoundsAwt = new java.awt.Rectangle();
			appBoundsAwt.x = appBounds.x;
			appBoundsAwt.y = appBounds.y;
			appBoundsAwt.width = appBounds.width;
			appBoundsAwt.height = appBounds.height;
			
			BufferedImage screenShot = robot.createScreenCapture(appBoundsAwt);
			ImageIO.write(screenShot, "PNG", new File("C:/tcook/ows5/AMSR_anim/amsrE_" + cnt + ".png"));
			cnt++;
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isUndoAvailable() {
		return false;
	}

	@Override
	public void unexecute() {
	}
}

