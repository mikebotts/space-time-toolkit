
package org.vast.stt.gui.widgets.symbolizer;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.ogc.cdm.common.DataComponent;
import org.vast.data.DataArray;
import org.vast.data.DataGroup;
import org.vast.data.DataList;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.DataNode;


public class DataStructureTreeViewer
{

    TreeViewer treeViewer;


    public DataStructureTreeViewer(Composite parent, int style)
    {
        init(parent, style);
    }


    public void init(Composite parent, int style)
    {
        treeViewer = new TreeViewer(parent, style);
        DataStructureContentProvider contProv = new DataStructureContentProvider();
        DataStructureLabelProvider labelProv = new DataStructureLabelProvider();
        treeViewer.setContentProvider(contProv);
        treeViewer.setLabelProvider(labelProv);
        //final Tree tree = treeViewer.getTree();
        //tree.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
    }


    public void setInput(Object inputElement)
    {
        treeViewer.setInput(inputElement);
        treeViewer.expandAll();
    }
}


class DataStructureContentProvider implements ITreeContentProvider
{
    Viewer viewer;


    public Object[] getChildren(Object parentElement)
    {
        if (parentElement instanceof DataGroup)
        {
            DataGroup group = (DataGroup) parentElement;
            int numComps = group.getComponentCount();
            DataComponent[] children = new DataComponent[numComps];
            for (int i = 0; i < numComps; i++)
            {
                children[i] = group.getComponent(i);
            }
            return children;
        }
        else if (parentElement instanceof DataArray)
        {
            return new Object[] { ((DataComponent) parentElement).getComponent(0) };
        }
        else if (parentElement instanceof DataList)
        {
            return new Object[] { ((DataComponent) parentElement).getComponent(0) };
        }
        else
        {
            //System.err.println(parentElement);
            //return new Object[0];
            return null;
        }
    }


    public Object getParent(Object element)
    {
        return null;
    }


    public boolean hasChildren(Object element)
    {
        if (element instanceof DataGroup || element instanceof DataArray || element instanceof DataList)
            return true;
        else
            return false;
    }
    
    
    public Object[] getElements(Object inputElement)
    {
        if (inputElement instanceof DataNode)
        {
            ArrayList<BlockList> lists = ((DataNode)inputElement).getListArray();
            int listCount = lists.size();
            
            ArrayList<DataComponent> rootComponents = new ArrayList<DataComponent>(listCount);
            for (int i=0; i<listCount; i++)
            {
                rootComponents.add(lists.get(i).getBlockStructure());
            }
            return rootComponents.toArray();
        }
        return new Object[] {};
    }


    public void dispose()
    {
        // TODO Auto-generated method stub

    }


    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        System.err.println("inputChanged for " + viewer);
        this.viewer = viewer;
    }
}


class DataStructureLabelProvider extends LabelProvider
{

    public Image getImage(Object element)
    {
        return null;
    }


    public String getText(Object element)
    {
        if (element instanceof DataComponent)
        {
            //System.err.println("dataComp is " + element);
            String name = ((DataComponent) element).getName();
            if (element instanceof DataArray)
            {
                DataArray arr = (DataArray) element;
                if (arr.isVariableSize())
                    return name + "[?]";
                return name + "[" + arr.getComponentCount() + "]";
            }
            else if (element instanceof DataList)
            {
                DataList list = (DataList) element;
                return name + "[" + list.getComponentCount() + "]";
            }
            else
                return name;
        }
        return "??? - NOT a component";
    }
}
