package org.vast.stt.gui.widgets.styler;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.ogc.cdm.common.DataComponent;
import org.vast.data.DataArray;
import org.vast.data.DataGroup;
import org.vast.data.DataList;
import org.vast.data.DataValue;

public class DataStructureTreeViewer {

	TreeViewer treeViewer;
	
	public DataStructureTreeViewer(Composite parent, int style){
		init(parent, style);
	}
	
	
	public void init(Composite parent, int style){
		treeViewer = new TreeViewer(parent, style);
		DataStructureContentProvider contProv = new DataStructureContentProvider();
		DataStructureLabelProvider labelProv = new DataStructureLabelProvider();
		treeViewer.setContentProvider(contProv);
		treeViewer.setLabelProvider(labelProv);
		//final Tree tree = treeViewer.getTree();
		//tree.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	}
	
	public void setInput(Object inputElement){
		treeViewer.setInput(inputElement);
		treeViewer.expandAll();
	}
}

class DataStructureContentProvider implements ITreeContentProvider {

	Viewer viewer;
	
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		if(parentElement instanceof DataGroup){
			DataGroup group = (DataGroup)parentElement;
			int numComps = group.getComponentCount();
			DataComponent[] children = new DataComponent[numComps];			
			for(int i=0; i<numComps; i++){
				children[i] = group.getComponent(i);
			}
			return children;
		} else if (parentElement instanceof DataArray) {
			return new Object [] {((DataArray)parentElement).getComponent(0)};
		} else if (parentElement instanceof DataList) {
			return new Object[] {((DataComponent)parentElement).getComponent(0)};
		} else {
			//System.err.println(parentElement);
			//return new Object[0];
			return null;
		}
	}

	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		if (element instanceof DataGroup || element instanceof DataArray ||
			element instanceof DataList)
			return true;
		else 
			return false;
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		System.err.println("inputChanged for " + viewer);
		this.viewer = viewer;
	}

	public Object[] getElements(Object inputElement) {
		System.err.println("DataStructTV.getElements():  inputElt = " + inputElement);
		// TODO Auto-generated method stub
		if(inputElement instanceof DataList){
			DataList node = (DataList)inputElement;
			//int numComps = node.getComponentCount();
			//for(int i=0; i<numComps; i++){
			//	System.err.println("Component is " + node.getComponent(i));
			//}
			return new Object[]{node.getComponent(0)};
		}
		return new Object[]{};
	}

	
}

class DataStructureLabelProvider extends LabelProvider {

	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getText(Object element) {
		// TODO Auto-generated method stub
		if(element instanceof DataComponent) {
			//System.err.println("dataComp is " + element);
			String name = ((DataComponent)element).getName();
			if(name != null)
				return name;
			if(element instanceof DataGroup)
				return "DataGroup";
			if(element instanceof DataArray)
				return "DataArray";
			if(element instanceof DataList)
				return "DataList";
			if(element instanceof DataValue)
				return "DataValue";
			return "whatTheHell";
		}
		return "??? - NOT a component";
	}
}
