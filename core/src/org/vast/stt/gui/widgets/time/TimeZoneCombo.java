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

package org.vast.stt.gui.widgets.time;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class TimeZoneCombo {
    Combo tzCombo;
    //  String [] for timeZones as offsets from GMT.  Could make this prettier by adding
    //  some specific tz's. likt CST, PST.  See Javadoc for TimeZone class and notes on
    //  3 letter timeZone issues with internationalization
    String [] gmtTimeZones;

    public TimeZoneCombo(Composite mainGroup){
    	tzCombo = new Combo(mainGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
    	tzCombo.setTextLimit(6);
    	gmtTimeZones = getTimeZones();
    	tzCombo.setItems(gmtTimeZones);
    	tzCombo.select(14);
    }

    public void addSelectionListener(SelectionListener sl){
    	tzCombo.addSelectionListener(sl);
    }
    
    private String [] getTimeZones(){
        String [] tz = new String[27];
        String sign;
        for(int i=-14, j=0;i<=12;i++,j++) {
            sign = (i>0) ? "+" : "";
            if(i==0)
                tz[j] = "GMT";
            else
                tz[j] = "GMT" + sign + i;
        }
        return tz;
    }
    
    public void setLayoutData(Object ld){
    	tzCombo.setLayoutData(ld);
    }
    
    public void setBackground(Color bg){
    	tzCombo.setBackground(bg);
    }
    
    public Combo getCombo(){
    	return tzCombo;
    }
    
    public int getZoneOffset(){
    	int sel = tzCombo.getSelectionIndex();
    	return sel - 14;  
    }
}