package org.vast.stt.apps;

import java.util.Enumeration;
import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;
import org.vast.sensorML.SMLException;
import org.vast.sensorML.reader.ProcessLoader;
import org.vast.stt.gui.views.ExceptionPopup;


/**
 * The main plugin class to be used in the desktop.
 */
public class STTPlugin extends AbstractUIPlugin
{

	//The shared instance.
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
        try
        {
            // preload process map file
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.vast.STT", path);
	}
}
