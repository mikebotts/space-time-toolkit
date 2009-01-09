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

package org.vast.jpip.network;

import java.util.ArrayList;
import org.vast.jpip.cache.CacheDescriptor;
import org.vast.jpip.message.ChannelField;
import org.vast.jpip.message.JPIPRequestFields;
import org.vast.jpip.message.ViewWindowField;
import org.vast.jpip.message.ImageReturnTypes;


/**
 * This class is useful to build a JPIP string from the JPIP parameters.
 * <p>
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; set functions<br>
 * &nbsp; createRequest<br> 
 * &nbsp; getRequest<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0   2007/10/27
 */
public class JPIPRequestEncoder implements ImageReturnTypes
{

    /**
     * This attribute contains the JPIP request that will be used to build the
     * JPIP string.
     */
    JPIPRequestFields jpipRequestFields = null;


    /**
     * Constructor.
     */
    public JPIPRequestEncoder()
    {
        this.jpipRequestFields = new JPIPRequestFields();
    }
    
    
    public JPIPRequestEncoder(JPIPRequestFields jpipRequestFields)
    {
        this.jpipRequestFields = jpipRequestFields;
    }
    
    
    public JPIPRequestFields getJpipRequestFields()
    {
        return jpipRequestFields;
    }


    public void setJpipRequestFields(JPIPRequestFields jpipRequestFields)
    {
        this.jpipRequestFields = jpipRequestFields;
    }


    /**
     * Encoders the JPIP parameters as a URL string 
     * 
     * @param jpipRequestFields definition in {@link #jpipRequestFields}.
     * @return 
     */
    public String createGetRequest()
    {
        String request = "";

        // TARGET FIELD
        if (jpipRequestFields.channelField.cid == null)
        {
            if (jpipRequestFields.targetField.target != null)
            {
                request += "/" + jpipRequestFields.targetField.target;
            }
            else
            {
                assert (true);
            }
        }
        else
        {
            request += "/" + jpipRequestFields.channelField.path;
        }

        // ? character
        request += "?";

        // Subtarget
        int[] subtarget = jpipRequestFields.targetField.subtarget;
        if (subtarget != null)
        {
            if (subtarget[0] != -1)
            {
                request += "&subtarget=" + subtarget[0];
                if (subtarget[1] != -1)
                {
                    request += "-" + subtarget[1];
                }
            }
        }

        // Target ID
        String tid = jpipRequestFields.targetField.tid;
        if (tid != null)
        {
            request += "&tid=" + tid;
        }

        // CHANNEL FIELD

        // Channel ID
        String cid = jpipRequestFields.channelField.cid;
        if (cid != null)
        {
            request += "&cid=" + cid;
        }

        // New Channel
        int[] cnew = jpipRequestFields.channelField.cnew;
        if (cnew != null)
        {
            request += "&cnew=";
            for (int i = 0; i < cnew.length; i++)
            {
                switch (cnew[i])
                {
                case ChannelField.CHANNEL_HTTP:
                    request += "http";
                    break;
                case ChannelField.CHANNEL_HTTP_TCP:
                    request += "http-tcp";
                    break;
                default:
                    assert (true);
                }
                if (i < (cnew.length - 1))
                {
                    request += ",";
                }
            }
        }

        // Channel close
        String[] cclose = jpipRequestFields.channelField.cclose;
        if (cclose != null)
        {
            request += "&cclose=";
            for (int i = 0; i < cclose.length; i++)
            {
                request += cclose[i];
                if (i < (cclose.length - 1))
                {
                    request += ",";
                }
            }
        }

        // Request ID
        int qid = jpipRequestFields.channelField.qid;
        if (qid != -1)
        {
            request += "&qid=" + qid;
        }

        // VIEW WINDOW FIELD
        int[] fsiz = jpipRequestFields.viewWindowField.fsiz;
        if (fsiz[0] != 0 && fsiz[1] != 0)
        {
            // Frame size
            if (fsiz != null)
            {
                if ((fsiz[0] != -1) && (fsiz[1] != -1))
                {
                    request += "&fsiz=" + fsiz[0] + "," + fsiz[1];

                    if (fsiz[2] != -1)
                    {
                        switch (fsiz[2])
                        {
                        case ViewWindowField.ROUND_DOWN:
                            request += ",round-down";
                            break;
                        case ViewWindowField.ROUND_UP:
                            request += ",round-up";
                            break;
                        case ViewWindowField.CLOSEST:
                            request += ",closest";
                            break;
                        default:
                            assert (true);
                        }
                    }
                }
            }

            // Offset
            int[] roff = jpipRequestFields.viewWindowField.roff;
            if (roff != null)
            {
                if ((roff[0] != -1) && (roff[1] != -1))
                {
                    request += "&roff=" + roff[0] + "," + roff[1];
                }
            }

            // Region size
            int[] rsiz = jpipRequestFields.viewWindowField.rsiz;
            if (rsiz != null)
            {
                if ((rsiz[0] != -1) && (rsiz[1] != -1))
                {
                    request += "&rsiz=" + rsiz[0] + "," + rsiz[1];
                }
            }

            // Components
            int[][] comps = jpipRequestFields.viewWindowField.comps;
            if (comps != null)
            {
                request += "&comps=";
                for (int i = 0; i < comps.length; i++)
                {
                    request += comps[i][0];
                    if (comps[i][1] != -1)
                    {
                        request += "-" + comps[i][1];
                    }
                    if (i < (comps.length - 1))
                    {
                        request += ",";
                    }
                }
            }
        }

        // Stream
        int[][] stream = jpipRequestFields.viewWindowField.stream; // array of [][3], where 3 = {from, to, sampling-factor}

        if (stream != null)
        {
            request += "&stream=";
            for (int i = 0; i < stream.length; i++)
            {
                assert (stream[i][0] >= 0);
                request += stream[i][0]; // from
                if (stream[i][1] != -1)
                { // if "to" is present
                    assert ((stream[i][1] >= 0) && (stream[i][1] > stream[i][0]));
                    request += "-" + stream[i][1];
                    if (stream[i][2] != -1)
                    { // if "sampling-fctor" is present
                        assert (stream[i][2] >= 0);
                        request += ":" + stream[i][2];
                    }
                }
                if (i < (stream.length - 1))
                {
                    request += ",";
                }
            }
        }
        else
            request += "&stream=0";

        // Codestream Context

        // Sampling Rate
        float srate = jpipRequestFields.viewWindowField.srate;
        if (!(srate < 0))
        {
            request += "&srate=" + srate;
        }

        // Layers
        int layers = jpipRequestFields.viewWindowField.layers;
        if (layers != -1)
        {
            request += "&layers=" + layers;
        }

        // TODO write roi request field

        // Metadata
        String metareq = jpipRequestFields.metareq;
        if (metareq != null && !metareq.equals(""))
        {
            request += "&metareq=" + metareq;
        }

        // TODO write data limit request field

        // len
        int len = jpipRequestFields.dataLimitField.len;
        if (len != -1)
        {
            request += "&len=" + len;
        }

        // Quality
        int quality = jpipRequestFields.dataLimitField.quality;
        if (quality != -1)
        {
            request += "&quality=" + quality;
        }

        String[] type = jpipRequestFields.serverControlField.type;
        if (type != null)
        {
            request += "&type=";
            request += type[0];
            for (int i = 1; i < type.length; i++)
            {
                request += "," + type[i];
            }
        }

        // Delivery Rate
        float drate = jpipRequestFields.serverControlField.drate;
        if (!(drate < 0))
        {
            request += "&drate=" + drate;
        }

        // CACHE MANAGEMENT FIELD

        // Model		
        ArrayList model = jpipRequestFields.cacheManagementField.model;
        if ((model != null) && (model.size() > 0))
        {
            request += "&model=";
            CacheDescriptor cacheDescriptor = null;

            for (int i = 0; i < model.size(); i++)
            {
                cacheDescriptor = (CacheDescriptor) model.get(i);

                // Check whether it is aditive or sustractive
                if (!cacheDescriptor.aditive)
                {
                    request += "-";
                }

                switch (cacheDescriptor.Class)
                {
                case CacheDescriptor.PRECINCT:
                    if (cacheDescriptor.explicitForm)
                    {
                        // In class identifier
                        request += "P" + ((cacheDescriptor.InClassIdentifier != -1) ? cacheDescriptor.InClassIdentifier : "*");
                    }
                    else
                    {
                        // Tile identifier
                        if (cacheDescriptor.tileRange[0] < cacheDescriptor.tileRange[1])
                        {
                            request += "t" + cacheDescriptor.tileRange[0] + "-" + cacheDescriptor.tileRange[1];
                        }
                        else
                        {
                            if (cacheDescriptor.tileRange[0] == cacheDescriptor.tileRange[1])
                            {
                                request += "t" + cacheDescriptor.tileRange[0];
                            }
                            else
                            {
                                request += "t*";
                            }
                        }
                        // Component identifier
                        if (cacheDescriptor.componentRange[0] < cacheDescriptor.componentRange[1])
                        {
                            request += "c" + cacheDescriptor.componentRange[0] + "-" + cacheDescriptor.componentRange[1];
                        }
                        else
                        {
                            if (cacheDescriptor.componentRange[0] == cacheDescriptor.componentRange[1])
                            {
                                request += "c" + cacheDescriptor.componentRange[0];
                            }
                            else
                            {
                                request += "c*";
                            }
                        }
                        // Resolution level identifier
                        if (cacheDescriptor.resolutionLevelRange[0] < cacheDescriptor.resolutionLevelRange[1])
                        {
                            request += "r" + cacheDescriptor.resolutionLevelRange[0] + "-" + cacheDescriptor.resolutionLevelRange[1];
                        }
                        else
                        {
                            if (cacheDescriptor.resolutionLevelRange[0] == cacheDescriptor.resolutionLevelRange[1])
                            {
                                request += "r" + cacheDescriptor.resolutionLevelRange[0];
                            }
                            else
                            {
                                request += "r*";
                            }
                        }
                        // Precinct identifier
                        if (cacheDescriptor.precinctRange[0] < cacheDescriptor.precinctRange[1])
                        {
                            request += "p" + cacheDescriptor.precinctRange[0] + "-" + cacheDescriptor.precinctRange[1];
                        }
                        else
                        {
                            if (cacheDescriptor.precinctRange[0] == cacheDescriptor.precinctRange[1])
                            {
                                request += "p" + cacheDescriptor.precinctRange[0];
                            }
                            else
                            {
                                request += "p*";
                            }
                        }
                    }

                    // Qualifier
                    if (cacheDescriptor.numberOfLayers != -1)
                    {
                        request += ":L" + cacheDescriptor.numberOfLayers;
                    }
                    else
                    {
                        if (cacheDescriptor.numberOfBytes != -1)
                        {
                            request += ":" + cacheDescriptor.numberOfBytes;
                        }
                        else
                        {
                            assert (true);
                        }
                    }

                    break;

                case CacheDescriptor.TILE_HEADER:
                    break;

                case CacheDescriptor.TILE_DATA:
                    break;

                case CacheDescriptor.MAIN_HEADER:
                    if (cacheDescriptor.explicitForm)
                    {
                        request += "Hm";
                    }
                    else
                    {
                        assert (true);
                    }
                    break;

                case CacheDescriptor.METADATA:
                    break;
                }

                if (i < model.size() - 1)
                {
                    request += ",";
                }
            }
        }

        // TODO write UPLOAD FIELD

        // TODO write CLIENT CAPABILITY PREFERENCE FIELD

        request = request.replaceFirst("&", "");

        return request;
    }  

}