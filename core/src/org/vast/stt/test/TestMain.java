package org.vast.stt.test;

//import org.ogc.cdm.common.DataBlock;
//import org.ogc.cdm.common.DataComponent;
//import org.vast.stt.data.DataNode;
//import org.vast.stt.data.DataProvider;
//import org.vast.stt.project.*;
//import org.vast.stt.scene.DataList;
//import org.vast.stt.scene.DataItem;
import org.ogc.cdm.common.DataType;
import org.vast.data.DataArray;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;


public class TestMain
{
	public static void main(String args[])
	{
//		long t0 = System.currentTimeMillis();
//		double a = 0;
//		double b = 0;
//		int c = 2;
//		for (int i=0; i<10000000; i++)
//		{
//			switch(c)
//			{
//				case 0:
//					a = cast(i);
//					break;
//					
//				case 1:
//					a = cast(i);
//					break;
//					
//				case 2:
//					a = cast(i);
//					break;
//			}
//		}
//		
//		long t1 = System.currentTimeMillis();
//		System.out.println(t1 - t0);
//		System.exit(0);
		
		try
		{
//			String url = "file:///D:/Projects/NSSTC/STT2/conf/SoCal.xml";
//			ProjectReader reader = new ProjectReader();
//			Project proj = reader.readProject(url);
//			
//			DataList itemList = (DataList)proj.getSceneList().get(0).getDataList().get(2);
//			DataProvider provider = ((DataItem)itemList.get(0)).getDataProvider();
//			provider.updateData();
//			DataNode node = provider.getDataNode();
//			
//			for (int i=0; i<node.getComponentCount(); i++)
//			{
//				node.getComponent(i).getComponent(0);
//				DataComponent pointArray = node.getComponent(i).getComponent(1);
//				
//				for (int j=0; j<pointArray.getComponentCount(); j++)
//				{
//					DataComponent point = pointArray.getComponent(j);
//					double x = point.getComponent(1).getData().getDoubleValue();
//					double y = point.getComponent(0).getData().getDoubleValue();
//					System.out.println("P" + j + ": " + x + "," + y);
//				}
//			}	
			
			// TEST GROUP WITH VARIABLE SIZE ARRAY
//			DataGroup group = new DataGroup();
//			DataValue sizeComponent = new DataValue(DataType.INT);
//			group.addComponent("arraySize", sizeComponent);			
//			DataArray array = new DataArray(sizeComponent);
//			array.addComponent("point", new DataValue(DataType.FLOAT));
//			group.addComponent("points", array);
//			group.addComponent("roll", new DataValue(DataType.FLOAT));
//			group.addComponent("yaw", new DataValue(DataType.FLOAT));
//			
//			group.assignNewDataBlock();			
//			
//			System.out.println(group + "\n");
//			System.out.println(group.getData() + "\n");
//			
//			group.getComponent(0).getData().setIntValue(20);
//			array.updateSize();			
//			
//			for (int i=0; i<20; i++)
//				array.getComponent(i).getData().setIntValue(i);
//			
//			System.out.println(group.getData() + "\n");
			
			
			// TEST ARRAY OF GROUP
//			DataGroup group2 = new DataGroup(3);
//			group2.addComponent("child1", new DataValue(DataType.INT));
//			group2.addComponent("child2", new DataValue(DataType.INT));
//			group2.addComponent("child3", new DataValue(DataType.INT));
//			
//			DataArray array2 = new DataArray(10);
//			array2.addComponent("child0", group2);
//			
//			System.out.println(array2 + "\n");			
//			array2.assignNewDataBlock();
//			System.out.println(array2.getData() + "\n");
//			
//			array2.getComponent(5).getComponent(0).getData().setIntValue(10);
//			System.out.println("OK");
			
			// TEST DOUBLE ARRAY OF GROUP
			DataGroup group = new DataGroup(3);
			group.addComponent("child1", new DataValue(DataType.DOUBLE));
			group.addComponent("child2", new DataValue(DataType.DOUBLE));
			group.addComponent("child3", new DataValue(DataType.INT));
			
			DataArray array1 = new DataArray(10);
			array1.addComponent(group);			
			
			DataArray array2 = new DataArray(10);
			array2.addComponent(array1);
			
			System.out.println(array2 + "\n");			
			array2.assignNewDataBlock();
			System.out.println(array2.getData() + "\n");
			
			for (int i=0; i<10; i++)
				array2.getComponent(i).getComponent(0).getComponent("child3").getData().setIntValue(i);
			
			System.out.print(array2.getData().getStringValue(92));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	protected static double cast(int i)
	{
		return i;
	}
}
