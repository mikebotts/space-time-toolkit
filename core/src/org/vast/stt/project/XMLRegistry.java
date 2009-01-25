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

package org.vast.stt.project;

import java.util.Hashtable;


public class XMLRegistry
{
    private static Hashtable<String, Class<?>> registeredReaders = new Hashtable<String, Class<?>>(); 
    private static Hashtable<String, Class<?>> registeredWriters = new Hashtable<String, Class<?>>();
    
    
    public static XMLModuleReader createReader(String ns, String eltName)
    {
        Class<?> readerClass = registeredReaders.get(ns + eltName);
        return (XMLModuleReader)createInstance(readerClass);
    }
    
    
    public static XMLModuleReader createReader(String eltName)
    {
        Class<?> readerClass = registeredReaders.get(eltName);
        if (readerClass == null)
            return null;
        else
            return (XMLModuleReader)createInstance(readerClass);
    }
    
    
    public static XMLModuleWriter createWriter(String ns, String eltName)
    {
        Class<?> writerClass = registeredWriters.get(ns + eltName);
        return (XMLModuleWriter)createInstance(writerClass);
    }
    
    
    public static XMLModuleWriter createWriter(String eltName)
    {
        Class<?> writerClass = registeredWriters.get(eltName);
        return (XMLModuleWriter)createInstance(writerClass);
    }
    
    
    public static void registerReader(String ns, String eltName, Class<?> readerClass)
    {
        registeredReaders.put(ns + eltName, readerClass);
    }
    
    
    public static void registerReader(String eltName, Class<?> readerClass)
    {
        registeredReaders.put(eltName, readerClass);
    }
    
    
    public static void registerWriter(String ns, String eltName, Class<?> writerClass)
    {
        registeredWriters.put(ns + eltName, writerClass);
    }
    
    
    public static void registerWriter(String eltName, Class<?> writerClass)
    {
        registeredWriters.put(eltName, writerClass);
    }
    
    
    private static Object createInstance(Class<?> objClass)
    {
        try
        {
            return objClass.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new IllegalStateException();
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalStateException();
        }
    }
}
