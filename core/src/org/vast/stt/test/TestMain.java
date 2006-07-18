package org.vast.stt.test;

import org.ogc.cdm.common.DataBlock;
import org.ogc.cdm.common.DataType;
import org.vast.data.DataArray;
import org.vast.data.DataBlockDouble;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.io.xml.DOMReader;
import org.vast.process.DataProcess;
import org.vast.sensorML.reader.ProcessLoader;
import org.vast.sensorML.reader.ProcessReader;
//import org.ogc.cdm.common.DataBlock;
//import org.ogc.cdm.common.DataComponent;
//import org.vast.stt.data.DataNode;
//import org.vast.stt.data.DataProvider;
//import org.vast.stt.project.*;
//import org.vast.stt.scene.DataList;
//import org.vast.stt.scene.DataItem;
//import org.ogc.cdm.common.DataType;
//import org.vast.data.DataArray;
//import org.vast.data.DataGroup;
//import org.vast.data.DataValue;


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
        
//        double[] array = new double[1000000];
//        double[] array2;
//        long t0, t1;
//        
//        t0 = System.currentTimeMillis();
//        for (int i=0; i<50; i++)
//            array2 = array.clone();        
//        t1 = System.currentTimeMillis();        
//        System.out.println(t1 - t0);
//        
//        t0 = System.currentTimeMillis();        
//        for (int i=0; i<50; i++)
//            array2 = new double[1000000];
//        t1 = System.currentTimeMillis();        
//        System.out.println(t1 - t0);
//        
//        System.exit(0);
        
        DataArray array1 = new DataArray(10);
        array1.setName("array1");
        DataArray array2 = new DataArray(10);
        array2.setName("array2");
        DataGroup group1 = new DataGroup(2);
        group1.setName("group1");
        DataGroup group2 = new DataGroup(3);
        group2.setName("group2");
        group2.addComponent("val1", new DataValue(DataType.DOUBLE));
        group2.addComponent("val2", new DataValue(DataType.DOUBLE));
        group2.addComponent("val3", new DataValue(DataType.DOUBLE));
        DataGroup group3 = new DataGroup(3);
        group3.setName("group3");
        group3.addComponent("val1", new DataValue(DataType.BYTE));
        group3.addComponent("val2", new DataValue(DataType.DOUBLE));
        group3.addComponent("val3", new DataValue(DataType.DOUBLE));
        group1.addComponent(group2);
        group1.addComponent(group3);
        array2.addComponent(group1);
        array1.addComponent(array2);
        array1.assignNewDataBlock();
        array1.getComponent(3);
        array2.getComponent(5);
        System.exit(0);
        
		try
		{
//            StaticDataSet resource = new StaticDataSet();
//            resource.setFormat("SWE");
//            resource.setSweDataUrl("http://vast.uah.edu/SWEData/coastlines/mapcil100.xml");
//            SWEProvider provider = new SWEProvider();
//            provider.setResource(resource);
//            provider.updateData();  
            
//            long t0, t1;
//            int var0 = 4;
//            int var = 0;
//            
//            for (int i = 0; i < 50000; i++)
//                var = obj.field1;
//            
//            t0 = System.currentTimeMillis();
//            for (int i = 0; i < 500000000; i++)
//                var = obj.field1;
//            t1 = System.currentTimeMillis();
//            System.out.println(var + ": " + (t1 - t0));
//
//            t0 = System.currentTimeMillis();
//            for (int i = 0; i < 500000000; i++)
//                var = obj.getField3();
//            t1 = System.currentTimeMillis();
//            System.out.println(var + ": " + (t1 - t0));
//            
//            t0 = System.currentTimeMillis();
//            for (int i = 0; i < 500000000; i++)
//                var = var0;
//            t1 = System.currentTimeMillis();
//            System.out.println(var + ": " + (t1 - t0));
            
            System.exit(1);
            
            DOMReader dom = new DOMReader("file:///D:/Projects/NSSTC/SensorML/examples/Process/TextureMappingProcess.xml#WMS_PROCESS", false);
            ProcessReader processReader = new ProcessReader(dom);
            processReader.setReadMetadata(false);
            processReader.setCreateExecutableProcess(true);
            ProcessLoader.reloadMaps("file:///d:/Projects/NSSTC/STT3/conf/ProcessMap.xml");
            DataProcess process = processReader.readProcess(dom.getBaseElement());
            process.init();
            System.out.println(process);
            
            DataBlock bboxData = new DataBlockDouble(4);
            bboxData.setDoubleValue(0, 1);
            bboxData.setDoubleValue(1, -1);
            bboxData.setDoubleValue(2, -1);
            bboxData.setDoubleValue(3, 1);
            process.getInputList().getComponent("bbox").setData(bboxData);
            
            System.out.println(process.getOutputList().getComponent(0).getComponent(2).getData());
            System.out.println("Process Running...");
            process.execute();
            
            DataArray outputImage = (DataArray)process.getOutputList().getComponent(0).getComponent("image");
            DataBlock data = outputImage.getData();
            
            for (int i=0; i<data.getAtomCount(); i++)
            {
                System.out.print(data.getByteValue(i) + ",");
                
                if (i%outputImage.getComponentCount() == 0)
                    System.out.println();                    
            }
            
//          ProcessChain process = (ProcessChain)systemReader.readProcess(dom.getBaseElement());
//          process.setChildrenThreadsOn(false);
//          process.init();
//          
//          process.getInputList().getComponent("lineIndex").getData().setIntValue(12000);
//          
//          for (int i=0; i<24000; i+=500)
//          {
//              process.getInputList().getComponent("pixelIndex").getData().setIntValue(i+1);
//              process.execute();
//              System.out.print(process.getOutputList().getComponent("sampleLocation").getComponent(0).getData().getDoubleValue() * 180.0 / Math.PI + ",");
//              System.out.print(process.getOutputList().getComponent("sampleLocation").getComponent(1).getData().getDoubleValue() * 180.0 / Math.PI - 360 + ",");
//              System.out.println(process.getOutputList().getComponent("sampleLocation").getComponent(2).getData().getDoubleValue());
//          }
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
