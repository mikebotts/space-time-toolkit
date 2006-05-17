package org.vast.stt.gui.widgets.time;

//import java.util.TimeZone;

import java.util.TimeZone;

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
    
    //  Create String array of GMT timeZone IDs.  Could use TimeZone.getAvailableIDs(),
    //  but that returns an exhaustive list (557 in my test).
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
    
    public TimeZone getTimeZone(){
    	TimeZone tz = TimeZone.getTimeZone(tzCombo.getText());
    	return tz;
    }
}