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

import java.io.IOException;
import java.net.ProtocolException;
import java.util.List;
import java.util.Map;
import org.vast.jpip.message.JPIPResponseFields;
import org.vast.jpip.message.ImageReturnTypes;


/**
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0 2007/12/14
 */
public class JPIPResponseFieldsParser
{

    private JPIPResponseFields jpipResponseFields = null;
    private boolean[] jpipHeaderFieldsFound = null;

    /**
     * ISO/IEC 15444-9 Annex D
     */
    private static String[] jpipResponseHeaderFields = { "JPIP-tid", "JPIP-cnew", "JPIP-qid", "JPIP-fsiz", "JPIP-rsiz", "JPIP-roff", "JPIP-comps", "JPIP-stream", "JPIP-context", "JPIP-roi",
            "JPIP-layers", "JPIP-srate", "JPIP-metareq", "JPIP-len", "JPIP-quality", "JPIP-type", "JPIP-mset", "JPIP-cap", "JPIP-pref" };

    private static String[] cnewTransportParamFields = { "transport", "host", "path", "port", "auxport" };


    /**************************************************************************/
    /**                      PRINCIPAL METHODS                               **/
    /**************************************************************************/

    /**
     * Constructor.
     */
    public JPIPResponseFieldsParser()
    {
        jpipHeaderFieldsFound = new boolean[jpipResponseHeaderFields.length];
    }


    /**
     * Parses the JPIP response fields
     * 
     * @throws 
     */
    public void parse(Map<String, List<String>> headers) throws IOException, ProtocolException
    {
        reset();
        jpipResponseFields = new JPIPResponseFields();

        // Check response headers
        int index = 0;

        // look for all possible jpip header fields
        for (index = 0; index < jpipResponseHeaderFields.length; index++)
        {
            String key = jpipResponseHeaderFields[index];
            List<String> values = headers.get(key);
                        
            if (values != null)
            {
                if (jpipHeaderFieldsFound[index])
                {
                    throw new IOException("Wrong server response: The " + jpipResponseHeaderFields[index] + " field is repeated");
                }
                
                else
                {
                    jpipHeaderFieldsFound[index] = true;
                    String value = values.get(0);
                    
                    switch (index)
                    {
                        case 0:
                            targetIDParser(value);
                            break;
                        case 1:
                            channelNewParser(value);
                            break;
                        case 2:
                            requestIDParser(value);
                            break;
                        case 3:
                            fsizParser(value);
                            break;
                        case 4:
                            rsizParser(value);
                            break;
                        case 5:
                            roffParser(value);
                            break;
                        case 6:
                            compsParser(value);
                            break;
                        case 7:
                            streamParser(value);
                            break;
                        case 8:
                            contextParser(value);
                            break;
                        case 9:
                            roiParser(value);
                            break;
                        case 10:
                            layersParser(value);
                            break;
                        case 11:
                            srateParser(value);
                            break;
                        case 12:
                            throw new IOException("The request parameter (" + key + ") is not supported");
                            //metareqParser(value);
                            //break;
                        case 13:
                            lenParser(value);
                            break;
                        case 14:
                            qualityParser(value);
                            break;
                        case 15:
                            typeParser(value);
                            break;
                        case 16:
                            throw new IOException("The request parameter (" + key + ") is not supported");
                            //msetParser(value);
                            //break;
                        case 17:
                            throw new IOException("The request parameter (" + key + ") is not supported");
                            //capParser(value);
                            //break;
                        case 18:
                            throw new IOException("The request parameter (" + key + ") is not supported");
                            //prefParser(value);
                            //break;                                    
                    }
                }
            }
        }
    }


    /**
     * Sets the class atributes to initial values
     *
     */
    public void reset()
    {
        for (int i = 0; i < jpipHeaderFieldsFound.length; i++)
        {
            jpipHeaderFieldsFound[i] = false;
        }
    }


    public JPIPResponseFields getJPIPResponseFields()
    {
        return jpipResponseFields;
    }


    /**************************************************************************/
    /**                      AUXILARY  METHODS                               **/
    /**************************************************************************/

    // TARGET FIELD PARSERS
    /**
     * 
     * @param tid definition in {@link CADI.Common.WOI#tid}
     * @throws Exception when the request element is wrong
     */
    private void targetIDParser(String tid) throws IOException
    {
        if (tid.length() > 256)
        {
            throw new IOException();
        }
        jpipResponseFields.tid = tid; //.substring(1,tid.length());		
    }


    // CHANNEL FIELD PARSERS

    /**
     * 
     * @param cnew definition in {@link CADI.Common.WOI#cnew}
     * 
     * @throws IOException when the request element is wrong
     */
    private void channelNewParser(String cnew) throws IOException
    {
        //String[] temp = null;
        String[] cnewParams = cnew.split(",");
        String paramName;
        String paramValue;
        int equalIndex;

        // temp = cnewParams[0].split("=");
        equalIndex = cnewParams[0].indexOf('=');
        paramName = cnewParams[0].substring(0, equalIndex);
        paramValue = cnewParams[0].substring(equalIndex + 1);
        if (paramName.compareTo("cid") == 0)
        {
            jpipResponseFields.cid = paramValue;
        }
        else
        {
            throw new IOException();
        }

        // If transport-parameters are defined
        boolean[] transportParamFound = new boolean[cnewTransportParamFields.length];
        for (int i = 0; i < transportParamFound.length; i++)
        {
            transportParamFound[i] = false;
        }
        if (cnewParams.length > 1)
        {
            for (int i = 1; i < cnewParams.length; i++)
            {
                //temp =cnewParams[i].split("=");  // ITT puts = in the parameter itself so split does not work!! - ARO
                equalIndex = cnewParams[i].indexOf('=');
                paramName = cnewParams[i].substring(0, equalIndex);
                paramValue = cnewParams[i].substring(equalIndex + 1);

                // Loop on transport params possibilities
                for (int index = 0; index < cnewTransportParamFields.length; index++)
                {
                    if (paramName.compareTo(cnewTransportParamFields[index]) == 0)
                    {
                        if (transportParamFound[index])
                        {
                            throw new IOException();
                        }
                        transportParamFound[index] = true;
                        switch (index)
                        {
                        case 0: // transport
                            if (paramValue.compareTo("http") == 0)
                            {
                                jpipResponseFields.transport = JPIPResponseFields.TRANSPORT_HTTP;
                            }
                            else
                            {
                                if (paramValue.compareTo("http-tcp") == 0)
                                {
                                    jpipResponseFields.transport = JPIPResponseFields.TRANSPORT_HTTP_TCP;
                                }
                                else
                                {
                                    throw new IOException();
                                }
                            }
                            break;
                        case 1: // host
                            jpipResponseFields.host = paramValue;
                            break;
                        case 2: // path
                            jpipResponseFields.path = paramValue;
                            break;
                        case 3: // port
                            jpipResponseFields.port = Integer.parseInt(paramValue);
                            break;
                        case 4: //auxport
                            jpipResponseFields.auxport = Integer.parseInt(paramValue);
                            break;
                        default:
                            throw new IOException();
                        }
                    }
                }
            }
        }
    }


    /** 
     * @param qid defined in {@link CADI.Common.WOI#qid}
     * 
     * @throws IOException when the request element is wrong
     */
    private void requestIDParser(String qid) throws IOException
    {
        // read comps values 
        try
        {
            jpipResponseFields.qid = Integer.parseInt(qid);
        }
        catch (Exception e)
        {
            throw new IOException();
        }
        if (jpipResponseFields.qid < 0)
        {
            throw new IOException();
        }
    }


    // VIEW WINDOW FIELD PARSERS

    /** 
     * @param fsiz defined in {@link CADI.Common.WOI#fx}, {@link CADI.Common.WOI#fy},
     *  and {@link CADI.Common.WOI#roundDirection} 
     * 
     * @throws IOException when the request element is wrong
     */
    private void fsizParser(String fsiz) throws IOException
    {
        String[] fsizString = fsiz.split(",");

        if ((fsizString.length > 3) || (fsizString.length == 0))
        {
            throw new IOException();
        }

        // read fx & fy values 
        try
        {
            jpipResponseFields.fsiz[0] = Integer.parseInt(fsizString[0]);
            jpipResponseFields.fsiz[1] = Integer.parseInt(fsizString[1]);
        }
        catch (Exception e)
        {
            throw new IOException();
        }
        if ((jpipResponseFields.fsiz[0] < 0) || (jpipResponseFields.fsiz[1] < 0))
        {
            throw new IOException();
        }
    }


    /**
     * @param roff defined in {@link CADI.Common.WOI#ox} and {@link CADI.Common.WOI#oy} 
     * 
     * @throws IOException when the request element is wrong
     */
    private void roffParser(String roff) throws IOException
    {
        String[] roffString = roff.split(",");

        if ((roffString.length > 2) || (roffString.length == 0))
        {
            throw new IOException();
        }

        // read ox and oy values 
        try
        {
            jpipResponseFields.roff[0] = Integer.parseInt(roffString[0]);
            jpipResponseFields.roff[1] = Integer.parseInt(roffString[1]);
        }
        catch (Exception e)
        {
            throw new IOException();
        }
        if ((jpipResponseFields.roff[0] < 0) || (jpipResponseFields.roff[1] < 0))
        {
            throw new IOException();
        }

    }


    /**
     * @param rsiz defined in {@link CADI.Common.WOI#sx} and {@link CADI.Common.WOI#sy} 
     * 
     * @throws IOException when the request element is wrong
     */
    private void rsizParser(String rsiz) throws IOException
    {
        String[] rsizString = rsiz.split(",");

        if ((rsizString.length > 2) || (rsizString.length == 0))
        {
            throw new IOException();
        }

        // read sx & sy values 
        try
        {
            jpipResponseFields.rsiz[0] = Integer.parseInt(rsizString[0]);
            jpipResponseFields.rsiz[1] = Integer.parseInt(rsizString[1]);
        }
        catch (Exception e)
        {
            throw new IOException();
        }

        if ((jpipResponseFields.rsiz[0] < 0) || (jpipResponseFields.rsiz[1] < 0))
        {
            throw new IOException();
        }
    }


    /**
     * @param comps definition in {@link CADI.Common.WOI#comps}
     * 
     * @throws IOException when the request element is wrong
     */
    private void compsParser(String comps) throws IOException
    {
        String[] temp1 = null, compsString = null;

        try
        {
            compsString = comps.split(",");
            jpipResponseFields.comps = new int[compsString.length][2];
            for (int i = 0; i < compsString.length; i++)
            {
                jpipResponseFields.comps[i][0] = -1;
                jpipResponseFields.comps[i][1] = -1;

                temp1 = compsString[i].split("-");
                if (temp1.length == 1)
                { // case UINT -
                    jpipResponseFields.comps[i][0] = Integer.parseInt(temp1[0]);
                }
                else
                {
                    if (temp1.length == 2)
                    { // UINT-UINT
                        jpipResponseFields.comps[i][0] = Integer.parseInt(temp1[0]);
                        jpipResponseFields.comps[i][1] = Integer.parseInt(temp1[1]);
                    }
                    else
                    {
                        throw new IOException();
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new IOException();
        }
    }


    /**
     * @param stream defined in {@link CADI.Common.WOI#stream}
     *  
     * @throws IOException when the request element is wrong
     */
    private void streamParser(String stream) throws IOException
    {
        // stream = "stream" "=" 1#sampled-range
        // sampled-range = UINT-RANGE [":" sampling-factor]
        // sampling-factor = UINT

        int from, to, samplingFactor;
        String[] streamString = null;
        String[] temp1 = null, temp2 = null;
        int[][] streamValues = null;

        try
        {
            streamString = stream.split(",");
            streamValues = new int[streamString.length][3];
            for (int i = 0; i < streamString.length; i++)
            {
                from = -1;
                to = -1;
                samplingFactor = -1;

                //Possibilities:
                // 1.- stream=from
                //	2.- stream=from-to
                //	3.- stream=from-to:samplingFactor
                // 4.- stream=from:samplingFactor

                temp1 = streamString[i].split("-");
                if (temp1.length == 2)
                { //case 2 or 3					
                    from = Integer.parseInt(temp1[0]);
                    temp2 = temp1[1].split(":");
                    if (temp2.length == 1)
                    { // case 2
                        to = Integer.parseInt(temp2[0]);
                    }
                    else if (temp2.length == 2)
                    { // case 3						
                        to = Integer.parseInt(temp2[0]);
                        samplingFactor = Integer.parseInt(temp2[1]);
                    }
                    else
                    {
                        throw new IOException();
                    }
                }
                else if (temp1.length == 1)
                { // case 1 or 4					
                    temp2 = temp1[0].split(":");
                    if (temp2.length == 1)
                    { // case 1
                        from = Integer.parseInt(temp2[0]);
                    }
                    else if (temp2.length == 2)
                    {
                        from = Integer.parseInt(temp2[0]);
                        samplingFactor = Integer.parseInt(temp2[1]);
                    }
                    else
                    {
                        throw new IOException();
                    }
                }
                else
                {
                    throw new IOException();
                }

                streamValues[i][0] = from;
                streamValues[i][1] = to;
                streamValues[i][2] = samplingFactor;

            } // 	for (int i=0; i<streamString.length; i++)

        }
        catch (Exception e)
        {
            throw new IOException();
        }
    }


    /**
     * 
     * @param string
     * @throws IOException when the request element is wrong
     */
    private void contextParser(String string) throws IOException
    {

    }


    protected void roiParser(String string)
    {

    }


    /**
     * @param layers defined in {@link CADI.Common.WOI#layers}
     * 
     * @throws IOException when the request element is wrong
     */
    private void layersParser(String layers) throws IOException
    {
        // read layers value 
        try
        {
            jpipResponseFields.layers = Integer.parseInt(layers);
        }
        catch (Exception e)
        {
            throw new IOException();
        }
        if (jpipResponseFields.layers < 0)
        {
            throw new IOException();
        }
    }


    /**
     * @param srate defined in {@link CADI.Common.WOI#srate}
     * 
     * @throws IOException
     */
    private void srateParser(String srate) throws IOException
    {
        // read comps values 
        try
        {
            jpipResponseFields.srate = Float.parseFloat(srate);
        }
        catch (Exception e)
        {
            throw new IOException();
        }
        if (jpipResponseFields.srate < 0)
        {
            throw new IOException();
        }
    }


    // METADATA FIELD PARSERS	
    /**
     * 
     */
    private void metareqParser(String string)
    {
        // TODO parse metadata request
    }


    // DATA LIMIT FIELD PARSERS

    /**
     * @param len defined in {@link CADI.Common.WOI#len}
     * 
     * @throws IOException when the request element is wrong
     */

    private void lenParser(String len) throws IOException
    {
        try
        {
            jpipResponseFields.len = Integer.parseInt(len);
        }
        catch (Exception e)
        {
            throw new IOException();
        }

        if (jpipResponseFields.len < 0)
        {
            throw new IOException();
        }
    }


    /**
     * @param quality defined in {@link CADI.Common.WOI#quality}
     * 
     * @throws IOException when the request element is wrong
     */
    private void qualityParser(String quality) throws IOException
    {
        try
        {
            jpipResponseFields.quality = Integer.parseInt(quality);
        }
        catch (Exception e)
        {
            throw new IOException();
        }

        if ((jpipResponseFields.quality < -1) || (jpipResponseFields.quality > 100))
        {
            throw new IOException();
        }
    }


    /**
     * 
     * @param type
     * @throws IOException
     */
    // SERVER CONTROL FIELD PARSERS
    private void typeParser(String type) throws IOException
    {
        if (type.equals("jpp-stream"))
        {
            jpipResponseFields.type = ImageReturnTypes.JPP_STREAM;
        }
        else
        {
            if (type.equals("jpt-stream"))
            {
                jpipResponseFields.type = ImageReturnTypes.JPT_STREAM;
            }
            else
            {
                if (type.equals("raw"))
                {
                    jpipResponseFields.type = ImageReturnTypes.RAW;
                }
                else
                {
                    throw new IOException();
                }
            }
        }
    }


    // CACHE MANAGEMENT FIELDS PARSERS	
    private void msetParser(String string)
    {
        // TODO parse cache management fields
    }


    // UPLOAD FIELD PARSERS

    // CLIENT CAPABILITIES PREFERENCES FIELD PARSERS	
    private void capParser(String string)
    {

    }


    protected void prefParser(String string)
    {

    }

}