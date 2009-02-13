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

package org.vast.stt.project.chart;

import org.vast.stt.project.scene.Scene;
import org.vast.stt.style.DataStyler;


/**
 * <p><b>Title:</b><br/>
 * Chart Scene Descriptor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Encapsulate the current state of a chart scene.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class ChartScene extends Scene
{
    //  variables added for finer control of axis behavior
    protected boolean domainAxisAutoRange = false;
    protected boolean rangeAxisAutoRange = false;
    protected double rangeMin;
	protected double rangeMax;
    protected double domainMin;
    protected double domainMax;

    public ChartScene()
    {
        super();
    }
    
    @Override
    protected void prepareStyler(DataStyler styler)
    {
       
    }

    public double getRangeMin() {
		return rangeMin;
	}

	public void setRangeMin(double val) {
		this.rangeMin = val;
	}

	public double getRangeMax() {
		return rangeMax;
	}

	public void setRangeMax(double val) {
		this.rangeMax = val;
	}

	public double getDomainMin() {
		return domainMin;
	}

	public void setDomainMin(double val) {
		this.domainMin = val;
	}

	public double getDomainMax() {
		return domainMax;
	}

	public void setDomainMax(double val) {
		this.domainMax = val;
	}	
}
