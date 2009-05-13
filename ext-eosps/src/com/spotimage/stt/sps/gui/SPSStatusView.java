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
import java.util.Hashtable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.vast.ows.OWSReference;
import org.vast.ows.OWSReferenceGroup;
import org.vast.ows.sps.DescribeResultAccessResponse;
import org.vast.ows.sps.GetFeasibilityResponse;
import org.vast.ows.sps.GetStatusResponse;
import org.vast.ows.sps.StatusReport;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.gui.views.SceneTreeView;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.tree.DataFolder;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.tree.DataTreeReader;
import org.vast.stt.provider.DataProvider;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import com.spotimage.stt.sps.SPSPlugin;
import com.spotimage.stt.sps.provider.SPSFeasibilityProvider;
import com.spotimage.stt.sps.provider.SPSStatusProvider;


public class SPSStatusView extends SPSFeasibilityResultView
{
	public static final String ID = "STT.SPSStatusView";
	protected static final String PREVIEW_URI = "urn:ogc:def:data:OGC:picture";
	protected Composite resultsTab;
	protected Table resultsTable;
	

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		
		// result form
		TabItem resultsTab = new TabItem(tabs, SWT.NONE);
		resultsTab.setText("Task Results");
		Composite composite = new Composite(tabs, SWT.NONE);
		resultsTab.setControl(composite);
		createResultsTab(composite);
	}
	
	
	@Override
	protected void createReportForm(Composite parent)
	{
		parent.setLayout(new FillLayout());
		toolkit = new FormToolkit(parent.getDisplay());
		reportForm = toolkit.createScrolledForm(parent);
		TableWrapLayout formLayout = new TableWrapLayout();
		formLayout.verticalSpacing = 0;
		reportForm.getBody().setLayout(formLayout);
		reportForm.setText("Task Status");
	}
	
	
	protected void createResultsTab(Composite parent)
	{
		parent.setLayout(new FillLayout());
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		resultsTab = new Composite(sc, SWT.NONE);
		resultsTab.setLayout(new FillLayout());	
		sc.setContent(resultsTab);		
	    sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);
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
	}


	@Override
	public void updateView()
	{
		DataProvider prov = item.getDataProvider();
		
		if (prov != null && prov instanceof SPSStatusProvider)
		{
			GetStatusResponse response = ((SPSStatusProvider)prov).getGetStatusResponse();
			if (response != null)
			{
				tabs.setEnabled(true);
				updateReportForm();
				updateCellsTable();
				updateSegmentsTable();
				updateResultTable();
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
		if (resultsTable != null)
			resultsTable.removeAll();
		
		super.clearView();
	}
	
	
	@Override
	public void updateReportForm()
	{		
		Composite body = reportForm.getBody();
		body.setVisible(false);
		
		// erase form
		Control[] children = body.getChildren();
		for (Control child: children)
			child.dispose();
		
		// reset and new buttons
		createRefreshButton(body);
		toolkit.createLabel(body, "");
				
		// update form if needed
		SPSStatusProvider prov = (SPSStatusProvider)item.getDataProvider();
		GetStatusResponse response = prov.getGetStatusResponse();
		if (response != null)
			createHeaderXml(body, response.getReport());
		
		reportForm.reflow(true);
		body.setVisible(true);
	}
	
	
	public void updateResultTable()
	{
		/*Composite body = resultForm.getBody();
		body.setVisible(false);
		
		// erase form
		Control[] children = body.getChildren();
		for (Control child: children)
			child.dispose();
		
		// update form if needed
		SPSStatusProvider prov = (SPSStatusProvider)item.getDataProvider();
		DescribeResultAccessResponse draResponse = prov.getDescribeResultAccessResponse();
		if (draResponse != null)
		{
			String reasonCode = draResponse.getReasonCode();
			if (reasonCode != null)
			{
				FormText formText = toolkit.createFormText(body, true);
				StringBuffer textBuf = new StringBuffer();
				textBuf.append("<form>");
				textBuf.append("<p><b>Date Not Available</b></p>");
				textBuf.append("<p><b>Reason: </b> " + reasonCode + "</p>");
				textBuf.append("</form>");
				formText.setText(textBuf.toString(), true, true);
			}
			else
			{
				int numResults = draResponse.getResultGroups().size();
				for (int i=0; i<numResults; i++)
				{
					OWSReferenceGroup resGroup = draResponse.getResultGroups().get(i);
					
					Section section = toolkit.createSection(body,
							  Section.TITLE_BAR|Section.TWISTIE);
					
					//if (i == 0)
					//	section.setExpanded(true);
					
					TableWrapData layoutData = new TableWrapData();
					layoutData.grabHorizontal = true;
					layoutData.align = TableWrapData.FILL;
					section.setLayoutData(layoutData);
					section.setText("Scene " + (i+1));
					
					FormText formText = toolkit.createFormText(section, true);
					StringBuffer textBuf = new StringBuffer();
					textBuf.append("<form>");					
					textBuf.append("<p><b>Identifier: </b> " + resGroup.getIdentifier() + "<br/></p><p>");
					
					int numRef = resGroup.getReferenceList().size();
					for (int r=0; r<numRef; r++)
					{
						OWSReference ref = resGroup.getReferenceList().get(r);
						
						if (ref.getRole() != null)
							textBuf.append("<b>Document Type: </b> " + ref.getRole().replaceAll("&", "&amp;") + "<br/>");
						
						textBuf.append("<b>Format: </b> " + ref.getFormat() + "<br/>");
						textBuf.append("<b>Location: </b> " + ref.getHref().replaceAll("&", "&amp;") + "<br/><br/>");
					}
					
					textBuf.append("</p><p></p>");
					textBuf.append("</form>");
					formText.setText(textBuf.toString(), true, true);
										
					section.setClient(formText);
				}
			}			
		}
		
		resultForm.reflow(true);
		body.setVisible(true);*/
	}
	
	
	protected void createHeaderXml(Composite parent, StatusReport report)
	{
		FormText formText = toolkit.createFormText(parent, true);
		StringBuffer textBuf = new StringBuffer();
		textBuf.append("<form>");
		
		// ID
		textBuf.append("<p><b>Report ID:</b> " + report.getTaskID() + "</p>");
		
		// update time
		textBuf.append("<p><b>Last Updated:</b> " + report.getLastUpdate().formatIso(0) + "</p>");
		
		// estimated ToC
		if (report.getEstimatedToC() != null)
			textBuf.append("<p><b>Estimated ToC:</b> " + report.getEstimatedToC().formatIso(0) + "</p>");
		
		// task status
		String statusCode = report.getStatusCode();
		if (statusCode.startsWith("other:"))
			statusCode = statusCode.substring(6);
		textBuf.append("<p><b>Status:</b> " + statusCode + "</p>");
		
		textBuf.append("</form>");
		formText.setText(textBuf.toString(), true, true);
	}
	
	
	protected Control createRefreshButton(Composite parent)
	{
		ImageHyperlink refreshBtn = toolkit.createImageHyperlink(parent, SWT.NONE);
		refreshBtn.setText("Refresh Status");
		refreshBtn.setImage(SPSPlugin.getImageDescriptor("icons/refresh_status.gif").createImage());
		refreshBtn.addHyperlinkListener(new HyperlinkAdapter()
		{
			public void linkActivated(HyperlinkEvent event)
			{
				item.getDataProvider().clearData();
				item.getDataProvider().startUpdate(true);
			}
		});
		
		return refreshBtn;
	}
}
