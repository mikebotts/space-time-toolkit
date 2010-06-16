package org.vast.stt.gui.widgets.image;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.vast.cdm.common.DataBlock;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataBlockMixed;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.BlockListIterator;
import org.vast.stt.data.DataNode;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.provider.DataProvider;
import org.vast.util.DateTimeFormat;



/**
 * <p>Title: ImageWidget.java</p>
 * <p>Description:  </p>
 * @author Tony Cook
 * @since Jun 13, 2010
 */

public class ImageWidget implements DisposeListener, STTEventListener
{
	//Display display;
	int defaultWidth = 256, defaultHeight = 256;
	int numImages = 3;
	Composite parent;
	int orientation = SWT.HORIZONTAL;  // may add support for vert or in future
	private ImageCanvas [] imCanvas;
	private Label [] timeLabels;
	private Label [] imLabels;
	private Composite [] imGroups;
	Map<DataItem, Integer> itemMap;

	public ImageWidget(Composite parent)
	{
		this.parent = parent;
		initGui(parent);
		itemMap = new HashMap<DataItem, Integer>(3);
	}

	public void addDataItem(DataItem dataItem){
		int size = itemMap.size();
		if(size>=3)
			return;
		int canvasNum = size;

		itemMap.put(dataItem, canvasNum);
		loadItem(dataItem, canvasNum);
		dataItem.addListener(this);
		dataItem.getDataProvider().getSpatialExtent().addListener(this);
	}

	//  TODO
	public void removeDataItem(DataItem dataItem) {
		dataItem.removeListener(this);
		//  Clear assocaited canvas, labels
	}

	public void loadItem(DataItem dataItem, int canvasNum) {
		imLabels[canvasNum].setText(dataItem.getName());
		if(dataItem.getName().contains("hanged"))
			getChangedImage(dataItem, canvasNum);
		else
			getImage(dataItem, canvasNum);
	}

	public void getImage(DataItem item, int canvasNum){
		DataProvider prov = item.getDataProvider();
		DataNode node = prov.getDataNode();
		node.getClass();
		BlockList list = node.getList("frameData");
		BlockListIterator it = list.getIterator();
		list.get(0);
		BlockListItem bli = it.next();
		DataBlock block = bli.getData();
		long time = block.getLongValue(0);
		int height = block.getIntValue(2);
		int width = block.getIntValue(3);
		DataBlockMixed dbm = (DataBlockMixed)block;
		AbstractDataBlock [] adb = dbm.getUnderlyingObject();
		byte [] data = (byte [])adb[4].getUnderlyingObject();
		imCanvas[canvasNum].createImage(width, height, data);
		//  setTime 
		String timeStr = DateTimeFormat.formatIso(time/1000.0, 0);
		timeLabels[canvasNum].setText(timeStr);
	}
	
	public void getChangedImage(DataItem item, int canvasNum){
		DataProvider prov = item.getDataProvider();
		DataNode node = prov.getDataNode();
		node.getClass();
		BlockList list = node.getList("frameData");
		BlockListIterator it = list.getIterator();
		list.get(0);
		BlockListItem bli = it.next();
		DataBlock block = bli.getData();
		DataBlockMixed dbm = (DataBlockMixed)block;
		AbstractDataBlock [] adb = dbm.getUnderlyingObject();
		byte [] data = (byte [])adb[1].getUnderlyingObject();
		int width = 704, height = 464;
		imCanvas[canvasNum].createImage(width, height, data);
	}

	public void initGui(Composite parent)
	{
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
		sc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

		Composite mainComp = new Composite(sc, 0x0);
		sc.setContent(mainComp);

		GridLayout gl = new GridLayout(numImages, true);
		mainComp.setLayout(gl);
		mainComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

		//  We can have up to 3 canvases- 3 for OWS7.but alter for 1,2 or more later
		//  Canvas, timeLabel, imLabel
		Display display= PlatformUI.getWorkbench().getDisplay();
		imGroups = new Composite[numImages];
		imCanvas = new ImageCanvas[numImages];
		timeLabels = new Label[numImages];
		imLabels = new Label[numImages];
		for(int i=0; i<numImages; i++ ) {
			imGroups[i] = new Composite(mainComp, 0x0);
			gl = new GridLayout(1,true);
			imGroups[i].setLayout(gl);
			GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, true);
			imGroups[i].setLayoutData(gd);

			imCanvas[i] = new ImageCanvas(imGroups[i], 0x0);
			imCanvas[i].setBackground(display.getSystemColor(SWT.COLOR_DARK_CYAN));
			gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
			gd.widthHint = defaultWidth;
			gd.heightHint = defaultHeight;
			imCanvas[i].setLayoutData(gd);

			timeLabels[i] = new Label(imGroups[i], 0x0);
			gd = new GridData(SWT.CENTER, SWT.CENTER, true, false);
			timeLabels[i].setLayoutData(gd);
			timeLabels[i].setText("2010-01-01T00:00:00.0000Z");

			imLabels[i] = new Label(imGroups[i], 0x0);
			gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
			imLabels[i].setLayoutData(gd);
			imLabels[i].setText("Image i label here");
		}

		// size scroller
		sc.setMinSize(mainComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	@Override
	public void widgetDisposed(DisposeEvent arg0) {
		// TODO Auto-generated method stub
		System.err.println("ImageWidget disp");

	}

	public void refreshViewAsync(DataItem item, int canvasNum)
	{
		UpdateCanvas uc = new UpdateCanvas(item, canvasNum);
		PlatformUI.getWorkbench().getDisplay().asyncExec(uc);
	}

	class UpdateCanvas implements Runnable
	{
		DataItem item;
		int canvasNum;
		
		UpdateCanvas(DataItem item, int canvasNum) {
			this.item = item;
			this.canvasNum = canvasNum;
		}
		
		public void run() {
//			refreshView();
			loadItem(item, canvasNum);
		}
	};

	/**
	 * handle data item events
	 */
	public void handleEvent(STTEvent e)
	{       
		System.err.println(e);
		if(!(e.producer instanceof DataItem)) {
			System.err.println("IW.hndleEvt:  producer not a D.I.?");
			return;
		}
		DataItem item = (DataItem)e.producer;
		Integer canvasNum = itemMap.get(item);
		if(canvasNum == null) {
			System.err.println("IW.hndleEvt:  producer not in itemMap?");
		}
		refreshViewAsync(item, canvasNum);
	}
}
