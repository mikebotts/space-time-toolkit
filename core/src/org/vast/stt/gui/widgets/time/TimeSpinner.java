package org.vast.stt.gui.widgets.time;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;

/**
 * <p><b>Title:</b><br/>
 * TimeSpinner
 * </p>
 *
 * <p><b>Description:</b><br/>
 * GUI for holding a Text widget and two arrow buttons to allow time step/bias/lead/lag
 * selection.  Arrow buttons may be pressed and held to simulate spinner behavior.  
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Dec 19, 2005
 * @version 1.0
 */
public class TimeSpinner 
	implements TraverseListener, MouseListener, KeyListener, DisposeListener, FocusListener
{
	Group mainGroup;
	protected Composite spinnerGroup;
	StyledText text;
	int currentField;  // the currently selected Field in the StyledText widget
	private int currentCaretOffset = 0;
	Integer [] start, len;  //  Ts Model loads these, but doesn't need them subsequently
	Font entryFont;
	Button upBtn, downBtn;
	TimeSpinnerModel tsModel;
	boolean btnDown = false;  //  Used to indicate user is pressing and holding a spinner button
	// List timeListeners;
	final Color DARK_GRAY = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
	final Color GRAY = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_GRAY);
	Color activeBackground = null;
	List<TimeSpinnerListener> timeSpinnerListeners;
	protected final static int BTN_SIZE = 13;

	protected TimeSpinner(){
		//  Added so that CurrentTimeSpinner can extend this class
		initFont();
		timeSpinnerListeners = new ArrayList<TimeSpinnerListener>();
	}
	
	public TimeSpinner(Composite parent, String label) {
		this();
		tsModel = new TimeSpinnerModel("YYYY DDD HH:mm:SS");
		start = tsModel.getStart();
		len = tsModel.getLength();
		initGui(parent, label);
		addListeners();
		//  Should really generate initial data from formatStr, but I don't 
		//  think it will use this anyway.  In practice, setValue(double) will
		//  be called at initialization
		text.setText("0000 000 00:00:00");
		//text.setSize(300, 25);
		resetCaret();
	}
	
	public void addListeners(){
		text.addTraverseListener(this);
		text.addMouseListener(this);
		text.addKeyListener(this);
		text.addFocusListener(this);
		upBtn.addMouseListener(this);
    	upBtn.addFocusListener(this);
    	downBtn.addMouseListener(this);
    	downBtn.addFocusListener(this);
	}
	
	public void setLayoutData(GridData gridData){
		mainGroup.setLayoutData(gridData);
	}
	
	/**
	 * Fixed font (like courier) would look better, but it makes the text area
	 * too wide to fit in the TimeExtent widget without horizontal scrollbar.
	 * Variable width font causes width to vary when month is changing, also
	 * producing unwanted visual behavior.  Finally, hardwiring a font value in 
	 * like I have done here is system dependent.  We really need a FontRegistry
	 * in STT, but this won't fix the fixed vs. variable-width issue.  For that
	 */
	protected void initFont(){
		Display display = PlatformUI.getWorkbench().getDisplay();
		//entryFont = new Font(display, new FontData("lucida console", 9, SWT.NORMAL));
		entryFont = new Font(display, new FontData("arial", 9, SWT.NORMAL));
		
//		FontData [] fonts = display.getFontList(null, true);
//		for(int i=0; i<fonts.length; i++){
//			System.err.println(fonts[i]);
//		}
	}
	
	protected void initGui(Composite parent, String label){
		//  Create main group for text and Buttons
		mainGroup = new Group(parent, SWT.SHADOW_NONE);
		mainGroup.setText(label);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginTop = -10;
		gridLayout.marginBottom = -2;
		gridLayout.marginRight = -3;
		mainGroup.setLayout(gridLayout);
		mainGroup.addDisposeListener(this);
		//  Text widget		
		text = new StyledText(mainGroup, SWT.RIGHT | SWT.BORDER | SWT.READ_ONLY);
		GridData gridData = new GridData();
		//gridData.heightHint = 18;
		text.setLayoutData(gridData);
		text.setFont(entryFont);
		text.setToolTipText(tsModel.formatStr);
		activeBackground = text.getBackground();
		
		spinnerGroup = new Composite(mainGroup, SWT.SHADOW_NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 1;
		gridLayout.marginTop = -2;
		gridLayout.marginBottom = -5;
		spinnerGroup.setLayout(gridLayout);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.CENTER;
		spinnerGroup.setLayoutData(gridData);
		//  Spinner buttons
		upBtn = new Button(spinnerGroup, SWT.ARROW | SWT.UP);
		//upBtn.setSize(4,4);
		gridData = new GridData();
		gridData.heightHint = BTN_SIZE;
		gridData.widthHint = BTN_SIZE;
    	upBtn.setLayoutData(gridData);
    	
		downBtn = new Button(spinnerGroup, SWT.ARROW | SWT.DOWN);
		gridData = new GridData();
		gridData.heightHint = BTN_SIZE;
		gridData.widthHint = BTN_SIZE;
		downBtn.setLayoutData(gridData);
    	
	}

	public void setEnabled(boolean b){
		text.setEnabled(b);
		spinnerGroup.setEnabled(b);
		mainGroup.setEnabled(b);
		if(b)
			text.setBackground(activeBackground);
		else 
			text.setBackground(GRAY);
	}

	protected void timeUp(){
		int caretPos;
		if(text.isFocusControl())
			caretPos = text.getCaretOffset();
		else
			caretPos = currentCaretOffset;
		currentField = getCurrentField(caretPos);
		tsModel.increment(currentField);
		text.setText(tsModel.toString());
		text.setCaretOffset(caretPos); //  reset cursorPos after text has been refreshed
		hiliteField(currentField, true);
		publishTimeChanged();
	}
	
	protected void timeDown(){
		int caretPos;
		if(text.isFocusControl())
			caretPos = text.getCaretOffset();
		else
			caretPos = currentCaretOffset;
		currentField = getCurrentField(caretPos);
		tsModel.decrement(currentField);
		text.setText(tsModel.toString());
		text.setCaretOffset(caretPos);
		hiliteField(currentField, true);
		publishTimeChanged();
	}
	
	protected void publishTimeChanged(){
		TimeSpinnerListener tsTmp = null;
		double t = getValue();
		for(int i=0; i<timeSpinnerListeners.size(); i++){
			tsTmp = timeSpinnerListeners.get(i);
			tsTmp.timeChanged(this, t);
		}
	}

	public void addTimeSpinnerListener(TimeSpinnerListener tl){
		timeSpinnerListeners.add(tl);
	}
	
	public void removeTimeSpinnerListener(TimeSpinnerListener tl){
		timeSpinnerListeners.remove(tl);
	}
	
	Runnable spinUpThread = new Runnable(){
		public void run(){
			timeUp();
		}
	};
	
	Runnable spinDownThread = new Runnable(){
		public void run(){
			timeDown();
		}
	};

	private void startSpinUpThread(){
		if(btnDown)
			return;
		btnDown = true;
		timeUp();
		SpinThread spinThread = new SpinThread(spinUpThread);
		spinThread.start();
	}

	private void startSpinDownThread(){
		if(btnDown)
			return;
		btnDown = true;
		timeDown();
		SpinThread spinThread = new SpinThread(spinDownThread);
		spinThread.start();

	}

	class SpinThread extends Thread {
		Runnable childThread;
		
		SpinThread(Runnable child){
			childThread = child;
		}
		
		public  void run(){
			try {		
				Thread.sleep(300l);  //  make this less sensitive (mouseEvents seem to be getting queued here)
				while(btnDown){
					Thread.sleep(60l);
					if(!text.isDisposed()) {
						text.getDisplay().asyncExec(childThread);
					}
				}	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.err.println("");
		}
	}
	
	private void stopSpinThread(){
		btnDown = false;
		text.setFocus();
	}

	public void setValue(double value){
		tsModel.setValue(new Double(value));
		text.setText(tsModel.toString());
		hiliteField(currentField, true);
	}
	
	public double getValue(){
		Double val = (Double)tsModel.getValue();
		return val.doubleValue();
	}
	
	//  Just figure out the currentField based on caretPos
	//  Don't do any hi-liting
	public int getCurrentField(int caretPos){
        for(int i=0; i<start.length; i++){
            if(caretPos <= (start[i] + len[i])) 
            	return i;
        }
        
        System.err.println("TimeSpinnerModel.selectField():  field pos not found");
        return 0;  //  Default to 0th field, but this is really an error
	}

	protected void hiliteField(int field, boolean onOff){
		//  Set new field to hilite colors
        Display display = text.getDisplay();
        Color fgColor, bgColor;
        if(onOff) {
        	fgColor = new Color(display, 228,228,228);
        	bgColor = new Color(display,0,0,128);
        } else {
        	fgColor = text.getForeground();
        	bgColor = text.getBackground();
        }
        	
		StyleRange range = new StyleRange(start[field],len[field],fgColor, bgColor);
		text.setStyleRange(range);
	}

	/** 
     * Convenience method to hilite the minute field when spinner is refreshed
     * @param text
     */
    public void resetCaret(){
    	currentField = start.length - 2;
    	currentCaretOffset = start[currentField];
    	text.setCaretOffset(currentCaretOffset);
    	hiliteField(currentField, true);
    }
    
    /********************************************************/
    ////////   All listener methods from here down  /////////
    /********************************************************/
    /**
	 * Override behavior of up-down-left-right arrow keys
	 */
	public void keyTraversed(TraverseEvent e) {
		if(e.keyCode == SWT.ARROW_DOWN) {
			timeDown();
		} else if (e.keyCode == SWT.ARROW_UP) {
			timeUp();
		} else {
			hiliteField(currentField, false);
			currentField = getCurrentField(text.getCaretOffset());
			hiliteField(currentField, true);
		}
	}
	
	public void mouseDoubleClick(MouseEvent e) {
		//  do nothing...
	}

	public void mouseDown(MouseEvent e) {
		if(e.widget == upBtn) {
			startSpinUpThread();
		} else if(e.widget == downBtn) {
			startSpinDownThread();
		} else if(e.widget == text){
			hiliteField(currentField, false);
			currentField = getCurrentField(text.getCaretOffset());
			hiliteField(currentField, true);
		}
	}

	public void mouseUp(MouseEvent e) {
		btnDown = false;
		if(e.widget == upBtn || e.widget == downBtn) {
			stopSpinThread();
		} else  // e.widget == text
			;//selectField();
	}
	
	public void keyPressed(KeyEvent e) {
		if((e.keyCode<48 || e.keyCode > 57)) { // && e.keyCode!=0) {
			//System.err.println("NOT A NUMBER");
			//e.doit = false;
			return;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	/**
	 * verifyText - only allow numeric entry for this
	 */
	public void verifyText(VerifyEvent e) {
		System.err.println("** Verified Text is " + e);
		e.doit = false;
		if((e.keyCode<48 || e.keyCode > 57) && e.keyCode!=0) {
			System.err.println("NOT A NUMBER");
			e.doit = false;
		}
	}

	/**
	 *  Added dispose listener to dispose Font I use for the text fields.  If we 
	 *  implement a FontRegistry, I can get rid of this.  TC 
	 * @param e
	 */
	public void widgetDisposed(DisposeEvent e) {
		entryFont.dispose();
	}

	public void focusGained(FocusEvent e) {
		if(e.widget == text) {
			text.setCaretOffset(currentCaretOffset);
		}
	}

	public void focusLost(FocusEvent e) {
		if(e.widget == upBtn || e.widget == downBtn)
			stopSpinThread();
		else if(e.widget == text) {
			currentCaretOffset = text.getCaretOffset();
		}
	}
}
