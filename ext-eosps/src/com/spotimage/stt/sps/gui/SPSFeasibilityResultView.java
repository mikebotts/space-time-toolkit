/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit SPS Plugin".
 
 The Initial Developer of the Original Code is Spotimage S.A.
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Alexandre Robin <alexandre.robin@spotimage.fr> for more
 information.
 
 Contributor(s): 
    Alexandre Robin <alexandre.robin@spotimage.fr>
 
******************************* END LICENSE BLOCK ***************************/

package com.spotimage.stt.sps.gui;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Locale;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataType;
import org.vast.data.*;
import org.vast.ows.sps.DescribeTaskingRequest;
import org.vast.ows.sps.FeasibilityReport;
import org.vast.ows.sps.GetFeasibilityResponse;
import org.vast.ows.sps.StatusReport;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.BlockListIterator;
import org.vast.stt.data.DataException;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.dialogs.DataProviderJob;
import org.vast.stt.gui.views.DataItemView;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.gui.views.SceneTreeView;
import org.vast.stt.project.tree.DataFolder;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.tree.DataTreeReader;
import org.vast.stt.provider.DataProvider;
import org.vast.sweCommon.SweConstants;
import org.vast.util.DateTimeFormat;
import org.vast.util.ExceptionSystem;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.vast.xml.QName;
import com.spotimage.eosps.EOReportHelper;
import com.spotimage.stt.sps.provider.NotificationOptions;
import com.spotimage.stt.sps.provider.SPSFeasibilityProvider;
import com.spotimage.stt.sps.provider.SPSStatusProvider;


public class SPSFeasibilityResultView extends DataItemView
{
	public static final String ID = "STT.SPSFeasibilityResultView";
	protected static final String POLYGON_URI = "urn:ogc:def:property:ISO-19107:Polygon";
	protected TabFolder tabs;
	protected ScrolledForm reportForm;
	protected Composite cellsTab, segmentsTab;
	protected Table cellsTable, segmentsTable;
	protected FormToolkit toolkit;
	protected NumberFormat decimalFormatter;
	
	
	class DecimalNumberValidator implements ModifyListener
	{
		public DecimalNumberValidator()
		{
		}
		
		public void modifyText(ModifyEvent event)
		{
			Text text = (Text)event.widget;
			if (text.getText().matches("^[-+]?\\d*(\\.\\d+)?(e[-+]?\\d+)?$"))
				text.setForeground(event.display.getSystemColor(SWT.COLOR_BLACK));
			else
				text.setForeground(event.display.getSystemColor(SWT.COLOR_RED));
		}
	}
	

	@Override
	public void createPartControl(Composite parent)
	{
		parent.setLayout(new FillLayout());
		tabs = new TabFolder(parent, SWT.TOP);
		Composite composite;
		
		// report info
		TabItem reportTab = new TabItem(tabs, SWT.NONE);
		reportTab.setText("Report");
		composite = new Composite(tabs, SWT.NONE);
		reportTab.setControl(composite);
		createReportForm(composite);
		
		// cells table
		TabItem cellsTab = new TabItem(tabs, SWT.NONE);
		cellsTab.setText("Cells");
		composite = new Composite(tabs, SWT.NONE);
		cellsTab.setControl(composite);
		createCellsTab(composite);
		
		// segment table
		TabItem segmentsTab = new TabItem(tabs, SWT.NONE);
		segmentsTab.setText("Segments");
		composite = new Composite(tabs, SWT.NONE);
		segmentsTab.setControl(composite);
		createSegmentsTab(composite);
		
		super.createPartControl(parent);
	}
	
	
	@Override
	public void setFocus()
	{
		tabs.setFocus();
	}


	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		decimalFormatter = NumberFormat.getNumberInstance(Locale.US);
		decimalFormatter.setMaximumFractionDigits(2);
	}


	@Override
	public void updateView()
	{
		DataProvider prov = item.getDataProvider();
		
		if (prov != null && prov instanceof SPSFeasibilityProvider)
		{
			GetFeasibilityResponse response = ((SPSFeasibilityProvider)prov).getGetFeasibilityResponse();
			if (response != null)
			{
				tabs.setEnabled(true);
				updateReportForm();
				updateCellsTable();
				updateSegmentsTable();
			}
		}
		else
		{
			clearView();
		}
	}
	
	
	@Override
	public void clearView()
	{
		if (cellsTable != null)
			cellsTable.removeAll();
		
		if (segmentsTable != null)
			segmentsTable.removeAll();
		
		tabs.setEnabled(false);
	}
	
	
	protected void createReportForm(Composite parent)
	{
		parent.setLayout(new FillLayout());
		toolkit = new FormToolkit(parent.getDisplay());
		reportForm = toolkit.createScrolledForm(parent);
		TableWrapLayout formLayout = new TableWrapLayout();
		formLayout.verticalSpacing = 0;
		reportForm.getBody().setLayout(formLayout);
		reportForm.setText("Feasibility Results");
	}
	
	
	protected void createCellsTab(Composite parent)
	{
		parent.setLayout(new FillLayout());
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		cellsTab = new Composite(sc, SWT.NONE);
		cellsTab.setLayout(new FillLayout());	
		sc.setContent(cellsTab);		
	    sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);
	}
	
	
	protected void createSegmentsTab(Composite parent)
	{
		parent.setLayout(new FillLayout());
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		segmentsTab = new Composite(sc, SWT.NONE);
		segmentsTab.setLayout(new FillLayout());
		sc.setContent(segmentsTab);
		sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);
	}
	
	
	protected void addColumns(Table table, DataComponent component)
	{
		if (component instanceof DataGroup)
		{
			String defUri = (String)component.getProperty(SweConstants.DEF_URI);		
			if (defUri != null && defUri.equals(POLYGON_URI))
				return;
			
			for (int i=0; i<component.getComponentCount(); i++)
				addColumns(table, component.getComponent(i));
		}
		else if (component instanceof DataArray)
		{
			// ?
		}
		else if (component instanceof DataChoice)
		{
			addColumns(table, ((DataChoice)component).getSelectedComponent());
		}
		else
		{
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(getComponentLabel(component));
			
			DataType type = ((DataValue)component).getDataType();
			if (type == DataType.FLOAT || type == DataType.DOUBLE ||
				type == DataType.INT || type == DataType.SHORT)
				column.setAlignment(SWT.RIGHT);

			column.pack();
		}
	}
	
	
	protected void updateReportForm()
	{
		Composite body = reportForm.getBody();
		body.setVisible(false);
		
		// erase form
		Control[] children = body.getChildren();
		for (Control child: children)
			child.dispose();
		
		// update form if needed
		SPSFeasibilityProvider prov = (SPSFeasibilityProvider)item.getDataProvider();
		GetFeasibilityResponse response = prov.getGetFeasibilityResponse();
		if (response != null)
		{
			// add estimated ToC and status code
			createHeaderHtml(body, response.getReport());
			toolkit.createLabel(body, "");
			
			// add submit button only if request was feasible
			if (response.getReport().getStatusCode().equals(FeasibilityReport.FEASIBLE))
				createSubmitButton(body);
		}
		
		reportForm.reflow(true);
		body.setVisible(true);
	}
	
	
	protected void createHeaderHtml(Composite parent, FeasibilityReport report)
	{
		FormText formText = toolkit.createFormText(parent, true);
		StringBuffer textBuf = new StringBuffer();
		textBuf.append("<form>");
		textBuf.append("<p><b>Report ID:</b> " + report.getTaskID() + "</p>");
		textBuf.append("<p><b>Feasibility:</b> " + report.getStatusCode() + "</p>");
		
		if (report.getExtendedData() != null)
		{
			EOReportHelper helper = new EOReportHelper(report);
			double cost = helper.getEstimatedCost();
			textBuf.append("<p><b>Estimated Cost:</b> " + cost + "</p>");		
		}
		
		textBuf.append("</form>");
		formText.setText(textBuf.toString(), true, true);
	}
		
		
	protected Control createSubmitButton(Composite parent)
	{
		final Button submitBtn = toolkit.createButton(parent, "Submit Request", SWT.PUSH);
		submitBtn.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent event)
			{
			}

			public void widgetSelected(SelectionEvent event)
			{
				// open notification wizard
				NotificationsWizard wizard = new NotificationsWizard();
				WizardDialog dialog = new WizardDialog(submitBtn.getShell(), wizard);
			    dialog.create();
			    dialog.open();
			    NotificationOptions notifOpts = wizard.getNotificationOptions();
			    				
				try
				{
					SPSFeasibilityProvider thisProv = (SPSFeasibilityProvider)item.getDataProvider();
					DescribeTaskingRequest dtRequest = thisProv.getDescribeTaskingRequest();
					String providerName = item.getName().substring(0, item.getName().indexOf("Feasibility")) + "Task Status";
					
					// create and init provider w/ dtRequest + taskID
					SPSStatusProvider newProvider = new SPSStatusProvider();
					newProvider.setServer(dtRequest.getGetServer(), dtRequest.getVersion());
					newProvider.setSensorID(dtRequest.getSensorID());
					newProvider.setName(providerName);
					new DataProviderJob(providerName, newProvider);
					
					// submit task and retrieve task ID
					StatusReport report = newProvider.submitTask(thisProv.getGetFeasibilityRequest().getParameters());
					newProvider.setTaskID(report.getTaskID());
					
					// subscribe or poll as necessary
					if (notifOpts.destUri != null)
						newProvider.subscribe(notifOpts, report.getTaskID());
					else if (notifOpts.pollAuto)
						newProvider.startPollingThread();
					
					// create status item from template
					DataItem newItem = null;
					try
					{
						InputStream is = getClass().getResourceAsStream("StatusItem.xml");
						DOMHelper dom = new DOMHelper(is, false);
						DataTreeReader itemReader = new DataTreeReader();
						newItem = (DataItem)itemReader.readDataEntry(dom, dom.getBaseElement());
					}
					catch (DOMHelperException e)
					{
						e.printStackTrace();
						return;
					}
					
					// insert in tree (next to parent item)
					newItem.setName(providerName);
					((DataFolder)item.getParent()).add(newItem);
					newItem.setDataProvider(newProvider);
					
					try
					{
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						
						// refresh scen tree view
						SceneTreeView view = (SceneTreeView)page.findView(SceneTreeView.ID);
						view.refreshView();
						ScenePageInput pageInput = (ScenePageInput)page.getInput();
						pageInput.getScene().setItemVisibility(newItem, true);
						pageInput.getScene().dispatchEvent(new STTEvent(this, EventType.ITEM_VISIBILITY_CHANGED), false);
						
						// pop up status view
						String viewID = SPSStatusView.ID;
						page.showView(viewID);					
					}
					catch (PartInitException e)
					{
						e.printStackTrace();
					}
				}
				catch (DataException e)
				{
					ExceptionSystem.display(e);
				}
			}
		});
		
		TableWrapData layoutData = new TableWrapData();
		layoutData.align = TableWrapData.LEFT;
		submitBtn.setLayoutData(layoutData);
		
		return submitBtn;
	}
	
	
	protected void updateCellsTable()
	{
		BlockList blockList = item.getDataProvider().getDataNode().getList("Cell");
		if (blockList == null)
			return;
		
		if (cellsTable == null)
		{
			cellsTable = new Table(cellsTab, SWT.BORDER);
			addColumns(cellsTable, blockList.getBlockStructure());
			cellsTable.setHeaderVisible(true);
		}
		else
			cellsTable.removeAll();
			
		updateTable(cellsTable, blockList);
	}
	
	
	protected void updateSegmentsTable()
	{
		BlockList blockList = item.getDataProvider().getDataNode().getList("Segment");
		if (blockList == null)
			return;
		
		if (segmentsTable == null)
		{
			segmentsTable = new Table(segmentsTab, SWT.BORDER);
			addColumns(segmentsTable, blockList.getBlockStructure());
			segmentsTable.setHeaderVisible(true);
		}
		else
			segmentsTable.removeAll();
		
		updateTable(segmentsTable, blockList);
	}
	
	
	protected void updateTable(Table table, BlockList blockList)
	{
		// get block list info
		DataComponent resultParams = blockList.getBlockStructure().copy();
		resultParams.assignNewDataBlock();
		BlockListIterator it = blockList.getIterator();
		
		// iterate through data blocks
		while (it.hasNext())
		{
			BlockListItem item = it.next();
			resultParams.setData(item.getData());
			
			TableItem tableItem = new TableItem(table, SWT.NONE);
			setTableItemData(tableItem, 0, resultParams);
		}
	}
	
	
	protected void setTableItemData(TableItem tableItem, int colIndex, DataComponent component)
	{
		if (component instanceof DataGroup)
		{
			String defUri = (String)component.getProperty(SweConstants.DEF_URI);		
			if (defUri != null && defUri.equals(POLYGON_URI))
				return;
			
			for (int i=0; i<component.getComponentCount(); i++)
				setTableItemData(tableItem, colIndex++, component.getComponent(i));
		}
		else if (component instanceof DataArray)
		{
			// ?
		}
		else if (component instanceof DataChoice)
		{
			setTableItemData(tableItem, colIndex, ((DataChoice)component).getSelectedComponent());
		}
		else if (component instanceof DataValue)
		{
			String text = getDataValueText((DataValue)component);
			tableItem.setText(colIndex, text);
		}
	}
	
	
	protected String getDataValueText(DataValue dataValue)
	{
		// get component type and constraint
		QName compQName = (QName)dataValue.getProperty(SweConstants.COMP_QNAME);
		String text;
		
		if (compQName.getLocalName().equals("Time"))
		{
			if (dataValue.getData().getDoubleValue() == 0)
				text = "NA";
			else
				text = DateTimeFormat.formatIso(dataValue.getData().getIntValue(), 0);
		}
		else
		{
			DataType dataType = dataValue.getDataType();
			
			if (dataType == DataType.BOOLEAN || dataType == DataType.UTF_STRING || dataType == DataType.ASCII_STRING)
			{
				text = dataValue.getData().getStringValue();
			}
			else
			{
				String uom = (String)dataValue.getProperty(SweConstants.UOM_CODE);								
				text = decimalFormatter.format(dataValue.getData().getDoubleValue());
				if (uom != null)
					text += (" " + uom);
			}
		}
		
		return text;
	}
	
	
	protected String getComponentLabel(DataComponent component)
	{
		String name = (String)component.getProperty(SweConstants.NAME);
		if (name == null) name = component.getName();
		
		while (name.length() < 8)
			name += " ";
		
		return name;
	}


	@Override
	public void dispose()
	{
		super.dispose();
	}
}
