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

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.vast.cdm.common.DataBlock;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataConstraint;
import org.vast.data.*;
import org.vast.stt.gui.views.DataItemView;
import org.vast.stt.provider.DataProvider;
import org.vast.sweCommon.IntervalConstraint;
import org.vast.sweCommon.EnumNumberConstraint;
import org.vast.sweCommon.SweConstants;
import org.vast.sweCommon.EnumTokenConstraint;
import org.vast.xml.QName;
import com.spotimage.stt.sps.SPSPlugin;
import com.spotimage.stt.sps.provider.SPSFeasibilityProvider;


public class SPSFeasibilityFormView extends DataItemView
{
	public static final String ID = "STT.SPSFeasibilityFormView";
	protected static final String POLYGON_URI = "urn:ogc:def:property:ISO-19107:Polygon";
	protected Composite requestComposite;
	protected ScrolledForm requestForm;	
	protected FormToolkit toolkit;	
	
	
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
		createRequestForm(parent);		
		super.createPartControl(parent);
	}
	
	
	@Override
	public void setFocus()
	{
		requestForm.setFocus();
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
		
		if (prov != null && prov instanceof SPSFeasibilityProvider)
		{
			if (requestForm.getBody().getChildren().length == 0)
				updateRequestForm();
		}		
	}
	
	
	protected void createRequestForm(Composite parent)
	{
		parent.setLayout(new FillLayout());
		requestComposite = parent;
		
		// request form
		toolkit = new FormToolkit(parent.getDisplay());
		requestForm = toolkit.createScrolledForm(parent);
		TableWrapLayout formLayout = new TableWrapLayout();
		formLayout.verticalSpacing = 3;
		requestForm.getBody().setLayout(formLayout);
		requestForm.setText("Feasibility Request");
	}
	
	
	protected void updateRequestForm()
	{		
		Composite body = requestForm.getBody();
		body.setVisible(false);
		
		// erase form
		Control[] children = body.getChildren();
		for (Control child: children)
			child.dispose();
		
		// reset and new buttons
		createNewButton(body);
		createResetButton(body);
		toolkit.createLabel(body, "");
		
		// update form if needed
		SPSFeasibilityProvider prov = (SPSFeasibilityProvider)item.getDataProvider();
		DataComponent taskingParams = prov.getTaskingParams();
		if (taskingParams != null)
		{
			int numGroups = taskingParams.getComponentCount();
			for (int i=0; i<numGroups; i++)
			{
				DataComponent component = taskingParams.getComponent(i);
				
				if (component instanceof DataGroup)
				{
					DataGroup dataGroup = (DataGroup)component;
					//Section section = toolkit.createSection(body, Section.TITLE_BAR|
					//		                  Section.TWISTIE|Section.EXPANDED|Section.CLIENT_INDENT);
					Section section = toolkit.createSection(body, Section.TREE_NODE|Section.EXPANDED);
					toolkit.createCompositeSeparator(section);
					
					TableWrapData layoutData = new TableWrapData();
					layoutData.grabHorizontal = true;
					layoutData.align = TableWrapData.FILL;
					section.setLayoutData(layoutData);
					section.setText(dataGroup.getName() + (isOptional(dataGroup) ? "" : " *"));
										
					Composite sectionClient = toolkit.createComposite(section);
					GridLayout layout = new GridLayout(1, false);
					layout.marginBottom = 10;
					layout.marginTop = 5;
					sectionClient.setLayout(layout);
					
					for (int c=0; c<dataGroup.getComponentCount(); c++)
						createForm(sectionClient, dataGroup.getComponent(c));
					
					section.setClient(sectionClient);
				}
				
				else if (component instanceof DataChoice)
				{
					DataChoice dataChoice = (DataChoice)component;
					//Section section = toolkit.createSection(body, Section.TITLE_BAR|
					//		                  Section.TWISTIE|Section.EXPANDED|Section.CLIENT_INDENT);
					Section section = toolkit.createSection(body, Section.TREE_NODE|Section.EXPANDED);
					toolkit.createCompositeSeparator(section);
					
					TableWrapData layoutData = new TableWrapData();
					layoutData.grabHorizontal = true;
					layoutData.align = TableWrapData.FILL;
					section.setLayoutData(layoutData);
					section.setText(dataChoice.getName() + (isOptional(dataChoice) ? "" : " *"));
					
					Composite sectionClient = toolkit.createComposite(section);
					GridLayout layout = new GridLayout(1, false);
					layout.marginBottom = 10;
					layout.marginTop = 5;
					sectionClient.setLayout(layout);
					
					dataChoice.setSelected(0);
					createForm(sectionClient, dataChoice.getComponent(0));
					
					section.setClient(sectionClient);
				}
				else
					createForm(body, component);
			}
			
			// send button
			createSendButton(body);
		}
		
		requestForm.reflow(true);
		body.setVisible(true);
	}
		
	
	protected Control createForm(Composite parent, DataComponent component)
	{
		Control control;
		
		if (component instanceof DataGroup)
			control = createDataRecordForm(parent, (DataGroup)component);
		else if (component instanceof DataChoice)
			control = createDataChoiceForm(parent, (DataChoice)component);
		else if (component instanceof DataArray)
			control = createDataArrayForm(parent, (DataArray)component);
		else
			control = createDataValueForm(parent, (DataValue)component);
		
		return control;
	}
	
	
	protected Control createDataRecordForm(Composite parent, DataGroup dataGroup)
	{
		// special case of polygon -> provide button!
		String defUri = (String)dataGroup.getProperty(SweConstants.DEF_URI);
		if (defUri != null && defUri.equals(POLYGON_URI))
		{
			Button selectBtn = toolkit.createButton(parent, "Select Polygon...", SWT.PUSH);
			return selectBtn;
		}
		
		Section subsection = toolkit.createSection(parent, Section.EXPANDED|Section.CLIENT_INDENT);
		subsection.setText(dataGroup.getName() + (isOptional(dataGroup) ? "" : " *"));
		
		Composite sectionClient = toolkit.createComposite(subsection);
		GridLayout gridLayout = new GridLayout(1, false);
		//gridLayout.verticalSpacing = 0;
		sectionClient.setLayout(gridLayout);
		
		for (int c=0; c<dataGroup.getComponentCount(); c++)
			createForm(sectionClient, dataGroup.getComponent(c));
		
		subsection.setClient(sectionClient);
		
		return subsection;
	}
	
	
	protected Control createDataChoiceForm(Composite parent, DataChoice dataChoice)
	{
		// TODO implement choice combo box
		
		dataChoice.setSelected(0);
		return createForm(parent, dataChoice.getComponent(0));
	}
	
	
	protected Composite createDataArrayForm(Composite parent, DataArray dataArray)
	{
		// if nothing selected, show list of choices
		
		// once selected, show children of selected item + back button
		
		return null;
	}
	
	
	protected Control createDataValueForm(Composite parent, DataValue dataValue)
	{
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		String name = dataValue.getName() + (isOptional(dataValue) ? "" : " *");
		String uom = (String)dataValue.getProperty(SweConstants.UOM_CODE);
		String label = name + " " + ((uom != null) ? ("(" + uom + ") ") : "") + ":  ";
		toolkit.createLabel(composite, label);
		
		// get component type and constraint
		QName compQName = (QName)dataValue.getProperty(SweConstants.COMP_QNAME);
		ConstraintList constraintList = dataValue.getConstraints();
		
		final DataBlock data = dataValue.getData();
		if (constraintList != null)
		{
			// deal only with first constraint for now!!
			DataConstraint firstConstraint = (DataConstraint)constraintList.get(0);
			
			if (firstConstraint instanceof EnumTokenConstraint)
			{
				Combo combo = createComboBox(composite, dataValue);
				String[] valueList = ((EnumTokenConstraint)firstConstraint).getValueList();
				combo.setItems(valueList);
				
				// select the right value
				if (data != null && data.getStringValue() != null)
				{
					for (int i=0; i<valueList.length; i++)
					{
						if (valueList[i].equals(data.getStringValue()))
						{
							combo.select(i);
							break;
						}
					}
				}
			}
			else if (firstConstraint instanceof EnumNumberConstraint)
			{
				Combo combo = createComboBox(composite, dataValue);
				double[] valueList = ((EnumNumberConstraint)firstConstraint).getValueList();
				for (int i=0; i<valueList.length; i++)
					combo.add(Double.toString(valueList[i]));
					
				// select the right value
				if (data != null)
				{
					for (int i=0; i<valueList.length; i++)
					{
						if (valueList[i] == dataValue.getData().getDoubleValue())
						{
							combo.select(i);
							break;
						}
					}
				}
			}
			else if ((firstConstraint instanceof IntervalConstraint))
			{
				Spinner spinner = new Spinner(composite, SWT.BORDER);
				spinner.setData(dataValue);
				
				double min = ((IntervalConstraint)firstConstraint).getMin();
				double max = ((IntervalConstraint)firstConstraint).getMax();
				
				int digits;
				if (compQName.getLocalName().equals("Count") || uom.equals("%"))
				{
					digits = 0;
					spinner.setIncrement(1);
					spinner.setPageIncrement(10);
				}
				else
				{
					digits = 1;
					spinner.setIncrement(1);
					spinner.setPageIncrement(10);
				}
				
				final int mul = (int)Math.pow(10, digits);
				spinner.setMinimum((int)(min * mul));
				spinner.setMaximum((int)(max * mul));
				spinner.setDigits(digits);

				if (data != null)
					spinner.setSelection((int)(dataValue.getData().getDoubleValue() * mul));
				
				// value changed listener
				spinner.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						Spinner spinner = (Spinner)e.widget;
						int val = spinner.getSelection();
						data.setDoubleValue((double)val / (double)mul);
					}
				});
			}
		}
		else
		{
			if (compQName.getLocalName().equals("Time"))
			{
				Composite group = toolkit.createComposite(composite);
				group.setLayout(new RowLayout(SWT.HORIZONTAL));
				final DateTime date = new DateTime(group, SWT.DATE | SWT.BORDER);
				final DateTime time = new DateTime(group, SWT.TIME | SWT.BORDER);
								
				// value changed listener
				SelectionAdapter listener = new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						GregorianCalendar cal = new GregorianCalendar();
						cal.set(Calendar.YEAR, date.getYear());
						cal.set(Calendar.MONTH, date.getMonth());
						cal.set(Calendar.DAY_OF_MONTH, date.getDay());
						cal.set(Calendar.HOUR_OF_DAY, time.getHours());
						cal.set(Calendar.MINUTE, time.getMinutes());
						cal.set(Calendar.SECOND, time.getSeconds());
						cal.set(Calendar.MILLISECOND, 0);						
						data.setDoubleValue(cal.getTimeInMillis() / 1000);
					}
				};
				
				date.addSelectionListener(listener);
				time.addSelectionListener(listener);				
				date.setVisible(true);
				time.setVisible(true);
				
				if (data.getDoubleValue() == 0)
					listener.widgetSelected(null);
			}
			else if (compQName.getLocalName().equals("Boolean"))
			{
				Combo combo = new Combo(composite, SWT.DROP_DOWN);
				combo.setData(dataValue);
				combo.add("Yes");
				combo.add("No");
				combo.select(1);
				
				// value changed listener
				combo.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						Combo combo = (Combo)e.widget;
						data.setBooleanValue(combo.getSelectionIndex() == 0 ? true: false);
					}
				});
			}
			else
			{
				Text text = new Text(composite, SWT.LEFT | SWT.BORDER);
				text.setData(dataValue);
				text.setSize(200, 18);
				
				if (data != null)
					text.setText(data.getStringValue());
				
				// value changed listener
				text.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						Text text = (Text)e.widget;
						data.setStringValue(text.getText());
					}
				});
			}
		}		
		
		return composite;
	}
	
	
	protected Combo createComboBox(Composite parent, DataValue dataValue)
	{
		Combo combo = new Combo(parent, SWT.DROP_DOWN);
		combo.setData(dataValue);
		
		combo.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent event)
			{
			}

			public void widgetSelected(SelectionEvent event)
			{
				Combo combo = ((Combo)event.widget);
				int index = combo.getSelectionIndex();
				String val = combo.getItem(index);
				((DataValue)combo.getData()).getData().setStringValue(val);
			}
			
		});
		
		return combo;
	}
	
	
	protected Control createResetButton(Composite parent)
	{
		ImageHyperlink resetBtn = toolkit.createImageHyperlink(parent, SWT.NONE);
		resetBtn.setText("Generate Form");
		resetBtn.setImage(SPSPlugin.getImageDescriptor("icons/generate_form.gif").createImage());
		resetBtn.addHyperlinkListener(new IHyperlinkListener()
		{
			public void linkActivated(HyperlinkEvent event)
			{
				try
				{
					Cursor oldCursor = requestForm.getCursor();
					Cursor cursor = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);
					requestForm.setCursor(cursor);
					
					updateRequestForm();
					
					requestForm.setCursor(oldCursor);
					cursor.dispose();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			public void linkEntered(HyperlinkEvent event)
			{
			}

			public void linkExited(HyperlinkEvent event)
			{
			}
		});
		
		return resetBtn;
	}
	
	
	protected Control createNewButton(Composite parent)
	{
		ImageHyperlink newBtn = toolkit.createImageHyperlink(parent, SWT.NONE);
		newBtn.setText("New Feasibility Study");
		newBtn.setImage(SPSPlugin.getImageDescriptor("icons/new_study.gif").createImage());
		newBtn.addHyperlinkListener(new IHyperlinkListener(){
			
			public void linkActivated(HyperlinkEvent event)
			{
				// create new feasibility item+provider in tree (next to parent item)
				
				// init dt request
				
				// select new item -> refresh view
			}

			public void linkEntered(HyperlinkEvent event)
			{
			}

			public void linkExited(HyperlinkEvent event)
			{
			}
		});
		
		return newBtn;
	}
	
	
	protected Control createSendButton(Composite parent)
	{
		Button sendBtn = toolkit.createButton(parent, "Send Request", SWT.PUSH);
		sendBtn.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent event)
			{
			}

			public void widgetSelected(SelectionEvent event)
			{
				// trigger update
				item.setEnabled(true);
				((SPSFeasibilityProvider)item.getDataProvider()).setCanUpdate(true);
				item.getDataProvider().clearData();
				item.getDataProvider().startUpdate(true);
			}
		});
		
		TableWrapData layoutData = new TableWrapData();
		layoutData.align = TableWrapData.CENTER;
		sendBtn.setLayoutData(layoutData);
		
		return sendBtn;
	}	
	
	
	protected boolean isOptional(DataComponent param)
	{
		Boolean optional = (Boolean)param.getProperty(SweConstants.OPTIONAL);
		if (optional == null)
			return false;
		else
			return optional;
	}
	
	
	protected String getComponentLabel(DataComponent component)
	{
		String name = (String)component.getProperty(SweConstants.NAME);
		if (name == null) name = component.getName();
		return name;
	}
	
	
	@Override
	public void clearView()
	{
	}


	@Override
	public void dispose()
	{
		super.dispose();
	}
}
