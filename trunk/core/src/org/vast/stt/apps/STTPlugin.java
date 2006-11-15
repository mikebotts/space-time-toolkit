package org.vast.stt.apps;

import java.util.Enumeration;
import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;
import org.vast.sensorML.SMLException;
import org.vast.sensorML.reader.ProcessLoader;
import org.vast.stt.gui.views.ExceptionPopup;
import org.vast.stt.project.XMLRegistry;
import org.vast.stt.project.chart.ChartItemReader;
import org.vast.stt.project.scene.SceneReader;
import org.vast.stt.project.table.TableItemReader;
import org.vast.stt.provider.google.GoogleMapProviderReader;
import org.vast.stt.provider.ows.OWSProviderReader;
import org.vast.stt.provider.sml.SMLProviderReader;
import org.vast.stt.provider.swe.SWEProviderReader;
import org.vast.stt.provider.ve.VirtualEarthProviderReader;
import org.vast.sttx.provider.worldwind.WorldwindMapProviderReader;


/**
 * The main plugin class to be used in the desktop.
 */
public class STTPlugin extends AbstractUIPlugin
{
    public static String ID;
    private static STTPlugin plugin;


	/**
	 * The constructor.
	 */
	public STTPlugin()
	{
		plugin = this;
	}


	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
        STTPlugin.ID = context.getBundle().getSymbolicName();
        
		// preload process map file
        try
        {
            String fileLocation = null;
            Enumeration e = context.getBundle().findEntries("conf", "ProcessMap.xml", false);
            if (e.hasMoreElements())
                fileLocation = (String)e.nextElement().toString();
            ProcessLoader.reloadMaps(fileLocation);
            new ExceptionPopup();
        }
        catch (SMLException e)
        {
            e.printStackTrace();
        }
        
        // register basic data providers reader/writers            
        XMLRegistry.registerReader("SWEDataProvider", SWEProviderReader.class);
        XMLRegistry.registerReader("OWSDataProvider", OWSProviderReader.class);
        XMLRegistry.registerReader("SensorMLProvider", SMLProviderReader.class);
        XMLRegistry.registerReader("VirtualEarthProvider", VirtualEarthProviderReader.class);
        XMLRegistry.registerReader("GoogleMapProvider", GoogleMapProviderReader.class);
        XMLRegistry.registerReader("WorldwindProvider", WorldwindMapProviderReader.class);
        
        // register basic display type reader/writers
        XMLRegistry.registerReader("Scene", SceneReader.class);
        XMLRegistry.registerReader("TableItem", TableItemReader.class);
        XMLRegistry.registerReader("ChartItem", ChartItemReader.class);
	}


	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception
	{
		super.stop(context);
		plugin = null;
	}


	/**
	 * Returns the shared instance.
	 */
	public static STTPlugin getDefault()
	{
		return plugin;
	}


	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(STTPlugin.ID, path);
	}
}
