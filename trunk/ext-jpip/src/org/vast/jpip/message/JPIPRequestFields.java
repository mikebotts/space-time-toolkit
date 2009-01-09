/*
 * CADI Software - a JPIP Client/Server framework
 * Copyright (C) 2007  Group on Interactive Coding of Images (GICI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Group on Interactive Coding of Images (GICI)
 * Department of Information and Communication Engineering
 * Autonomous University of Barcelona
 * 08193 - Bellaterra - Cerdanyola del Valles (Barcelona)
 * Spain
 *
 * http://gici.uab.es
 * gici-info@deic.uab.es
 */

package org.vast.jpip.message;

import java.io.PrintStream;
import java.util.ArrayList;

import org.vast.jpip.cache.CacheDescriptor;


/**
 * This class is used to group all the JPIP request fields.
 * <p>
 * Further information, see ISO/IEC 15444-9 section C.1
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0   2007/10/26
 */
public class JPIPRequestFields
{

    /**
     * Definition in {@link CADI.Common.Network.JPIP.TargetField}.
     */
    public TargetField targetField = null;

    /**
     * Definition in {@link CADI.Common.Network.JPIP.ChannelField}.
     */
    public ChannelField channelField = null;

    /**
     * Definition in {@link CADI.Common.Network.JPIP.ViewWindowField}.
     */
    public ViewWindowField viewWindowField = null;

    /**
     * 
     */
    public String metareq = null;

    /**
     * Definition in {@link CADI.Common.Network.JPIP.DataLimitField}.
     */
    public DataLimitField dataLimitField = null;

    /**
     * Definition in {@link CADI.Common.Network.JPIP.ServerControlField}.
     */
    public ServerControlField serverControlField = null;

    /**
     * Definition in {@link CADI.Common.Network.JPIP.CacheManagementField}.
     */
    public CacheManagementField cacheManagementField = null;

    /**
     * 
     */
    public String upload = null;

    /**
     * Definition in {@link CADI.Common.Network.JPIP.ClientCapPrefField}.
     */
    public ClientCapPrefField clientCapPrefField = null;


    /**************************************************************************/
    /**                     PRINCIPAL  METHODS                               **/
    /**************************************************************************/

    /**
     * Constructor.
     */
    public JPIPRequestFields()
    {
        targetField = new TargetField();
        channelField = new ChannelField();
        viewWindowField = new ViewWindowField();
        metareq = null;
        dataLimitField = new DataLimitField();
        serverControlField = new ServerControlField();
        cacheManagementField = new CacheManagementField();
        upload = null;
        clientCapPrefField = new ClientCapPrefField();
    }


    /**
     * Sets all attributes to its initial values.
     */
    public void reset()
    {
        targetField.reset();
        channelField.reset();
        viewWindowField.reset();
        metareq = null;
        dataLimitField.reset();
        serverControlField.reset();
        cacheManagementField.reset();
        upload = null;
        clientCapPrefField.reset();
    }
    
    
    //  TARGET

    /**
     * 
     * @param target
     */
    public void setTarget(String target)
    {
        targetField.target = target;
    }


    /**
     * 
     * @param tid
     */
    public void setTID(String tid)
    {
        targetField.tid = tid;
    }


    // CHANNEL

    public void setCID(String cid)
    {
        channelField.cid = cid;
    }


    public void setPath(String path)
    {
        channelField.path = path;
    }


    public void setCNew(boolean httpChannel, boolean httpTcpChannel)
    {
        if (httpChannel && httpTcpChannel)
        {
            channelField.cnew = new int[2];
            channelField.cnew[0] = ChannelField.CHANNEL_HTTP;
            channelField.cnew[1] = ChannelField.CHANNEL_HTTP_TCP;
        }
        else if (httpChannel)
        {
            channelField.cnew = new int[1];
            channelField.cnew[0] = ChannelField.CHANNEL_HTTP;
        }
        else if (httpTcpChannel)
        {
            channelField.cnew = new int[1];
            channelField.cnew[0] = ChannelField.CHANNEL_HTTP_TCP;
        }
    }


    public void setCClose(String cid)
    {
        int pos = 0;
        
        if (channelField.cclose == null)
        {
            channelField.cclose = new String[1];
        }
        else
        {
            String[] temp = new String[channelField.cclose.length + 1];
            System.arraycopy(channelField.cclose, 0, temp, 0, temp.length - 1);
            channelField.cclose = null;
            temp = channelField.cclose;
        }

        channelField.cclose[pos] = cid;
    }


    // VIEW WINDOW

    public void setFSiz(int[] fsiz)
    {
        viewWindowField.fsiz[0] = fsiz[0];
        viewWindowField.fsiz[1] = fsiz[1];
        viewWindowField.fsiz[2] = fsiz[2];
    }


    public void setROff(int[] roff)
    {
        viewWindowField.roff[0] = roff[0];
        viewWindowField.roff[1] = roff[1];
    }


    public void setRSiz(int[] rsiz)
    {
        viewWindowField.rsiz[0] = rsiz[0];
        viewWindowField.rsiz[1] = rsiz[1];
    }


    public void setComps(int[][] comps)
    {
        viewWindowField.comps = new int[comps.length][2];
        for (int i = 0; i < comps.length; i++)
        {
            viewWindowField.comps[i][0] = comps[i][0];
            viewWindowField.comps[i][1] = comps[i][1];
        }
    }


    public void setLayers(int layers)
    {
        viewWindowField.layers = layers;
    }


    // METADATA

    // DATA LIMIT

    public void setLen(int len)
    {
        dataLimitField.len = len;
    }


    // SERVER CONTROL

    public void setType(int type)
    {
        if (serverControlField.type == null)
        {
            serverControlField.type = new String[1];
        }
        else
        {
            String[] tmp = new String[serverControlField.type.length + 1];
            System.arraycopy(serverControlField.type, 0, tmp, 0, tmp.length - 1);
            serverControlField.type = null;
            serverControlField.type = tmp;
        }

        String returnType = null;
        switch (type)
        {
            case ImageReturnTypes.JPP_STREAM:
                returnType = "jpp-stream";
                break;
            case ImageReturnTypes.JPT_STREAM:
                returnType = "jpt-stream";
                break;
            case ImageReturnTypes.RAW:
                returnType = "raw";
                break;
            default:
                assert (true);
        }
        
        serverControlField.type[serverControlField.type.length - 1] = returnType;
    }


    // CACHE MANAGEMENT

    public void setModel(ArrayList<CacheDescriptor> cacheDescriptor)
    {
        cacheManagementField.model = cacheDescriptor;
    }


    /**
     * For debugging purposes. 
     */
    public String toString()
    {
        String str = "";

        str = getClass().getName() + " [\n";

        str += targetField.toString() + "\n";
        str += channelField.toString() + "\n";
        str += viewWindowField.toString() + "\n";

        //str += "METADATA";

        str += dataLimitField.toString() + "\n";
        str += serverControlField.toString() + "\n";
        str += cacheManagementField.toString() + "\n";

        //str += " UPLOAD";

        str += clientCapPrefField.toString() + "\n";

        str += "]";
        return str;
    }


    /**
     * Prints this JPIP Request fields out to the specified output stream.
     * This method is useful for debugging.
     * 
     * @param out an output stream.
     */
    public void list(PrintStream out)
    {
        out.println("\n-- JPIP Request Fields --");

        targetField.list(out);
        channelField.list(out);
        viewWindowField.list(out);

        //str += "METADATA";

        dataLimitField.list(out);
        serverControlField.list(out);
        cacheManagementField.list(out);

        //str += " UPLOAD";

        clientCapPrefField.list(out);

        out.flush();
    }

}