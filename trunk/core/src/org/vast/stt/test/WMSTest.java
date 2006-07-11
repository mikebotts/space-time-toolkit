package org.vast.stt.test;

import org.ogc.cdm.common.DataBlock;
import org.vast.data.DataArray;
import org.vast.data.DataBlockDouble;
import org.vast.io.xml.DOMReader;
import org.vast.process.DataProcess;
import org.vast.sensorML.reader.ProcessLoader;
import org.vast.sensorML.reader.ProcessReader;


public class WMSTest
{
	public static void main(String args[])
	{
        try
        {
            // load file and set up process reader
            String processFileUrl = WMSTest.class.getResource("WMSTest.xml").toString();
            DOMReader dom = new DOMReader(processFileUrl+"#WMS_PROCESS", false);
            ProcessReader processReader = new ProcessReader(dom);
            processReader.setReadMetadata(false);
            processReader.setCreateExecutableProcess(true);
            
            // load process map and parse process chain
            String processMapUrl = WMSTest.class.getResource("ProcessMap.xml").toString();
            ProcessLoader.reloadMaps(processMapUrl);
            DataProcess process = processReader.readProcess(dom.getBaseElement());
            
            // intitialize process and print out info
            process.init();
            System.out.println(process);
            
            // set bbox input values
            DataBlock bboxData = new DataBlockDouble(4);
            bboxData.setDoubleValue(1, -86.75);
            bboxData.setDoubleValue(0, 33.9);
            bboxData.setDoubleValue(3, -86.525);
            bboxData.setDoubleValue(2, 34.4);
            process.getInputList().getComponent("bbox").setData(bboxData);
            
            System.out.println("Image buffer will be: " + process.getOutputList().getComponent(0).getComponent(2).getData());
            System.out.println("Process Running...");
            process.execute();
            
            DataArray outputImage = (DataArray)process.getOutputList().getComponent(0).getComponent("image");
            DataBlock data = outputImage.getData();
            
            // print blue component values
            for (int i=0; i<data.getAtomCount(); i++)
            {
                System.out.print(data.getByteValue(i) + ",");
                
                if (i%outputImage.getComponentCount() == 0)
                    System.out.println("");                    
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
