/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
 Alexandre Robin <robin@nsstc.uah.edu>
 
 ******************************* END LICENSE BLOCK ***************************/

package org.vast.sttx.jpip;

import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;
import org.vast.stt.project.XMLRegistry;
import org.vast.sttx.jp2k.JPEG2000ProviderReader;


/**
 * The main plugin class to be used in the desktop.
 */
public class JPIPPlugin extends AbstractUIPlugin
{
    public static String ID;
    private static JPIPPlugin plugin;


    /**
     * The constructor.
     */
    public JPIPPlugin()
    {
        plugin = this;
    }


    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        JPIPPlugin.ID = context.getBundle().getSymbolicName();
        
        // register provider readers
        XMLRegistry.registerReader("JPEG2000Provider", JPEG2000ProviderReader.class);
        XMLRegistry.registerReader("JPIPProvider", JPIPProviderReader.class);
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
    public static JPIPPlugin getDefault()
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
        return AbstractUIPlugin.imageDescriptorFromPlugin(JPIPPlugin.ID, path);
    }
}
