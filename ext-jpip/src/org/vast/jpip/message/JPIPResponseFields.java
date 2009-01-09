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


/**
 * This class contains the values of the JPIP response header.
 * <p>
 * Further information, see ISO/IEC 15444-9 section C.10
 * 
 * @author Group on Interactive Coding of Images (GICI)
 * @verion 1.0   2007/10/26
 */
public class JPIPResponseFields
{

    /**
     * JPIP-tid = "JPIP-tid" ":" LWSP target-id
     * <p>
     * The server shall send this response header if the server's unique target identifier differs in any way from the identifier
     * supplied with a Target ID request field, or if the client did not specify a Target ID request field. The target-id is
     * an arbitrary, server-assigned string, not exceeding 255 characters in length. If the Target ID request field specifies a
     * value of "0", the server is obliged to include a Target ID response header, indicating the actual target-id. If the server is
     * unable to assign unique identifiers to the requested logical target, and hence cannot guarantee its integrity between
     * multiple requests or sessions, then the Target ID response header shall specify a value of 0. If the server supplies a
     * target-id which is different from that specified in the request, it shall disregard all model, tpmodel, need
     * and tpneed request fields when responding to this request.
     */
    public String tid = null;

    /**
     *      JPIP-cnew = "JPIP-cnew" ":" LWSP "cid" "=" channel-id
     *                   ["," 1#(transport-param "=" TOKEN)]
     * transport-param = TOKEN
     * The server shall send this response header if, and only if, it assigns a new channel in response to a New Channel request
     * field. The value string consists of a comma-separated list of name=value pairs, the first of which identifies the new
     * channel's channel-id token.
     * The following transport-param tokens are defined by this Recommendation | International Standard (see next Table).
     * 
     *  <p>
     * <table border=1 cellspacing=0 cellpadding=2 >
     * 	<tr align=center>
     * 		<td> <b> Value </b> </td>
     * 		<td> <b> Meaning </b> </td>
     * 	</tr>
     * 	<tr>
     * 		<td align=center> <b> "transport" </b> </td>
     * 		<td> This parameter shall be assigned one of the values in the list of acceptable transport names supplied in 
     *					the New Channel request field. If multiple transport names were supplied in the request field, the
     *					response header shall identify the actual transport that will be used with the channel.
     * 
     *			</td>
     *		</tr>
     *		<tr>
     *			<td align=center> <b> "host" </b> </td>
     *			<td> This parameter identifies the name or IP address of the host for the JPIP server that is managing the
     *					new channel. The parameter need not be returned unless the host differs from that to which the request
     *					was actually sent.
     *			</td>
     *		</tr>
     *		<tr>
     *			<td align=center> <b> "path" </b> </td>
     *			<td> This parameter identifies the path component of the URL to be used in constructing future requests
     *					with this channel. The parameter need not be returned unless the path name differs from that used in
     *					the request which was actually sent.
     *			</td>
     *		</tr>
     *		<tr>
     *			<td align=center> <b> "port" </b> </td>
     *			<td> This parameter identifies the numerical port number (decimal) at which the JPIP server that is
     *					managing the new channel is listening for requests. The parameter need not be returned if the host and
     *					port number are identical to those to which the original request was sent. The parameter also need not
     *					be returned if the host differs from that to which the request was sent and the default port number
     *					associated with the relevant transport is to be used
     *			</td>
     *		</tr> 
     *		<tr>
     *			<td align=center> <b> "auxport" </b> </td>
     *			<td> This parameter is used with transports requiring a second physical channel. If the "http-tcp" transport
     *					is used, the auxiliary port is used to connect the auxiliary TCP channel.The parameter need not be
     *					returned if the original request involved a channel that also
     *					employed an auxiliary channel, having the same auxiliary port number. Otherwise, the parameter need
     *					be returned only if the auxiliary port number differs from the default value associated with the selected
     *					transport.
     *			</td>
     *		</tr>  
     * </table>
     * 
     */
    public String cid = null;
    public int transport = -1;
    public String host = null;
    public String path = null;
    public int port = -1;
    public int auxport = -1;

    /**
     * Allowed values for the transport attribute.
     */
    public static final int TRANSPORT_HTTP = 1;
    public static final int TRANSPORT_HTTP_TCP = 2;

    /**
     *	JPIP-qid = "JPIP-qid" ":" LWSP UINT
     * <p>
     * The server shall send this response header if the client's request included a Request ID qid. The value of JPIP-qid
     * shall be identical to qid. The server shall not include a Request ID response header when the respective client request
     * did not include a Request ID.
     * NOTE - The server's Request ID, JPIP-qid, shall always be identical to the client's Request ID. Thus the Request ID is
     * distinctive in that this response header is sent when the client has used the Request ID, not when the server modifies the value. 
     */
    public int qid = -1; // Only positive values are allowed

    /**
     * JPIP-fsiz = "JPIP-fsiz" ":" LWSP fx "," fy
     * The server should send this response header if the frame size for which response data will be served differs from that
     * requested via the Frame Size request field.
     */
    public int[] fsiz = null;

    /**
     * JPIP-rsiz = "JPIP-rsiz" ":" LWSP sx "," sy
     * The server should send this response header if the size of the region for which response data will be served differs from
     * that requested.
     */
    public int[] rsiz = null;

    /**
     * JPIP-roff = "JPIP-roff" ":" LWSP ox "," oy
     * The server should send this response header if the offset of the region for which response data will be served differs
     * from that requested.
     */
    public int[] roff = null;

    /**
     * JPIP-comps = "JPIP-comps" ":" LWSP 1#UINT-RANGE
     * The server should send this response header if the components for which it will serve data differ from those requested
     * via the Components request field. It is not obliged to send this response header if requested image components do not
     * exist within any of the requested codestreams.
     */
    public int[][] comps = null;

    /**
     * 
     */
    public int[][] stream = null;

    /**
     * 
     */
    public String context = null;

    /**
     * 
     */
    public String roi = null;

    /**
     * JPIP-layers = "JPIP-layers" ":" LWSP UINT
     * The server should send this response header if the number of layers for which it will serve is smaller than the value
     * specified by the layers request field. Since the view-window is typically served in quality progressive fashion, the server
     * is not obliged (and indeed may not be able) to determine the number of layers which are spanned by the response data it
     * delivers. However, if the requested number of layers exceeds the number of layers available from any codestreams in
     * the view-window, the server should at least identify the maximum number of available layers. Any server that accepts
     * an Alignment request field (see C.7.1) shall provide a JPIP-layers response if the number of layers for which it will
     * serve is smaller than the value specified by the layers request field.
     */
    public int layers = -1;

    /**
     * JPIP-srate = "JPIP-srate" ":" LWSP UFLOAT
     * The server should send this response header if the average sampling rate of the codestreams which it will send to the
     * client is expected to differ from that requested via a Sampling Rate request field and the sampling rate is known. If the
     * source codestreams have no timing information, this response header should not be sent.
     */
    public float srate = -1.0F;

    /**
     * JPIP-metareq = "JPIP-metareq" ":" LWSP
     *                1#( "[" 1$(req-box-prop) "]" [root-bin] [max-depth] )
     *                [metadata-only]
     * req-box-prop = box-type [limit] [metareq-qualifier] [priority]
     * The server should send this response header if it is modifying the max-depth, limit, metareq-qualifier
     * or priority value provided in a Metadata Request request field.
     */
    public String metareq = null;

    /**
     * JPIP-len = "JPIP-len" ":" LWSP UINT
     * The server should send this response header if the byte limit specified in a Maximum Response Length request field
     * was too small to allow a non-empty response unless the byte limit was equal to zero. If returned, JPIP-len shall be a
     * value that informs the client of a suitable maximum response length, len, for subsequent requests. If len=0, the
     * server should respond to the request with response headers and no response data.
     */
    public int len = -1;

    /**
     * JPIP-quality = "JPIP-quality" ":" LWSP (1*2DIGIT / "100" / "-1")
     * The server may send this response header to inform the client of the quality value that will be associated with the image
     * data returned once this request has been completed. If the request is interrupted by another request (not having
     * "wait=yes"), this quality value may not be accurate. The quality value refers only to the view-window requested, and
     * has the same interpretation as the Quality request field. If the server ignored the client's request, a value "-1" shall be
     * returned.
     */
    public int quality = -1; // values in [0,100]

    /**
     * JPIP-type = "JPIP-type" ":" LWSP image-return-type
     * The server should include this response header unless another mechanism identifies the MIME subtype of the return
     * image data. Examples of other mechanisms include:<br>
     * 	- an HTTP "Content-Type:" header,<br>
     * 	- Responses to requests that are associated with a session whose return image type has already been signalled.
     */
    public int type = -1;

    /**
     * JPIP-mset = "JPIP-mset" ":" LWSP 1#sampled-range
     * The server should include this response header if the client's request contains a Model Set request field, and the
     * collection of codestreams identified by the client's Model Set request field differ in any way from the collection of
     * codestreams for which the server is actually prepared to maintain cache model information. The set of codestreams for
     * which the server maintains cache model information should include all codestreams which are associated with the
     * server's response data (either those identified in the client's request, or those identified by the server's Codestream
     * response header, if any). Apart from those codestreams, the server's "mset" may be no larger than that identified by the
     * client's Model Set request field.
     */
    public String mset = null;

    /**
     * JPIP-cap = "JPIP-cap" ":" LWSP 1#capability-code
     * This response header specifies that the client shall support a particular feature in order to interpret the logical target in a 
     * conformant manner. Legal capabilities are the same as those defined for the Capability request field in {@see ClientCapPrefField}

     */
    public String cap = null;

    /**
     * JPIP-pref = "JPIP-pref" ":" LWSP 1#related-pref-set
     * This response header should be provided if and only if a Client Preferences request field contained a related-
     * pref-set with the "/r" modifier (required), which the server was unwilling to support. In this case, an error value
     * should also be returned for the response status code. The value string consists of one or more of the related-
     * pref-sets that could not be supported, repeated in exactly the same form as they appeared in the Client Preferences
     * request.
     * Although desirable, it is not necessary for this response header to list all of the required related-pref-sets that
     * cannot be supported. Thus, it is permissible for a server to walk into the Client Preferences request field only until it
     * encounters a related-pref-set which specifies "/r" and cannot be supported.
     */
    String pref = null;


    /**************************************************************************/
    /**                          PRINCIPAL METHODS                           **/
    /**************************************************************************/

    /**
     * Constructor.
     */
    public JPIPResponseFields()
    {
        fsiz = new int[3];
        fsiz[0] = fsiz[1] = -1;

        rsiz = new int[2];
        rsiz[0] = rsiz[1] = -1;

        roff = new int[2];
        roff[0] = roff[1] = -1;
    }


    /**
     * 
     *
     */
    public void reset()
    {
        tid = null;
        cid = null;
        transport = -1;
        host = null;
        path = null;
        port = -1;
        auxport = -1;
        qid = -1;
        fsiz[0] = fsiz[1] = -1;
        rsiz[0] = rsiz[1] = -1;
        roff[0] = roff[1] = -1;
        comps = null;
        layers = -1;
        srate = -1.0F;
        len = -1;
        quality = -1;
        type = -1;
    }


    /**
     * Returns the parameters related with the View Window as a ViewWindow class.
     * 
     * @return
     */
    public ViewWindowField getViewWindow()
    {
        return (new ViewWindowField(fsiz, roff, rsiz, comps, stream, context, srate, roi, layers));
    }


    /**
     * 
     */
    public void setViewWindow(ViewWindowField viewWindow)
    {
        if (viewWindow.fsiz[0] != -1)
        {
            fsiz[0] = viewWindow.fsiz[0];
            fsiz[1] = viewWindow.fsiz[1];
        }
        if (viewWindow.roff[0] != -1)
        {
            roff[0] = viewWindow.roff[0];
            roff[1] = viewWindow.roff[1];
        }
        if (viewWindow.rsiz[0] != -1)
        {
            rsiz[0] = viewWindow.rsiz[0];
            rsiz[1] = viewWindow.rsiz[1];
        }
        if (viewWindow.comps != null)
            comps = viewWindow.comps;
        if (viewWindow.stream != null)
            stream = viewWindow.stream;
        if (viewWindow.context != null)
            context = viewWindow.context;
        srate = viewWindow.srate;
        if (viewWindow.roi != null)
            roi = viewWindow.roi;

        if (viewWindow.layers != -1)
        {
            layers = viewWindow.layers;
        }
    }


    /**
     * For debugging purposes.
     */
    public String toString()
    {

        String str = "";

        str = getClass().getName() + " [";

        if (tid != null)
        {
            str += "tid=" + tid;
        }

        if (cid != null)
        {
            str += " cid=" + cid;
        }

        // Channel new
        if (transport != -1)
        {
            str += " transport=";
            if (transport == TRANSPORT_HTTP)
            {
                str += "http";
            }
            else
            {
                if (transport == TRANSPORT_HTTP_TCP)
                {
                    str += "http-tcp";
                }
                else
                {
                    str += "unavailable transport mode";
                }
            }
        }

        if (host != null)
        {
            str += " host=" + host;
        }
        if (path != null)
        {
            str += " path=" + path;
        }
        if (port != -1)
        {
            str += " port=" + port;
        }
        if (auxport != -1)
        {
            str += " auxport: " + auxport;
        }

        if (qid != -1)
        {
            str += " quid=" + qid;
        }

        // VIEW WINDOW FIELD
        if (fsiz[0] != -1)
        {
            str += " fsiz=[ " + fsiz[0] + " , " + fsiz[1] + " ]";
        }

        if (rsiz[0] != -1)
        {
            str += " rsiz=[ " + rsiz[0] + " , " + rsiz[1] + " ]";
        }

        if (roff[0] != -1)
        {
            str += " roff=[ " + roff[0] + " , " + roff[1] + " ]";
        }

        if (comps != null)
        {
            str += " comps=";
            for (int i = 0; i < comps.length; i++)
            {
                str += comps[i][0] + "-" + comps[i][1] + ",";
            }
        }

        if (stream != null)
        {
            str += " streams=";
            for (int i = 0; i < stream.length; i++)
            {
                str += stream[i][0] + "-" + stream[i][1] + ",";
            }
        }

        if (context != null)
        {
            str += " context=" + context;
        }

        if (roi != null)
        {
            str += "roi=" + roi;
        }

        if (layers != -1)
        {
            str += " layers=" + layers;
        }

        if (srate >= 0)
        {
            str += " srate=" + srate;
        }

        // METADATA FIELD
        if (metareq != null)
        {
            str += " metareq=" + metareq;
        }

        // DATA LIMIT FIELD

        if (len != -1)
        {
            str += " len=" + len;
        }

        if (quality != -1)
        {
            str += " quality = " + quality;
        }

        // SERVER CONTROL FIELD	
        if (type != -1)
        {
            str += "type=";
            if (type == ImageReturnTypes.JPP_STREAM)
                str += "jpp-stream";
            if (type == ImageReturnTypes.JPT_STREAM)
                str += "jpt-stream";
            if (type == ImageReturnTypes.RAW)
                str += "raw";
        }

        // CACHE MANAGEMENT FIELD
        if (mset != null)
        {
            str += " mset=" + mset;
        }

        // UPLOAD FIELD

        //CLIENT CAPABILITIES PREFERENCES FIELD
        if (cap != null)
        {
            str += " cap=" + cap;
        }

        if (pref != null)
        {
            str += " pref=" + pref;
        }

        str += " ]";

        return str;
    }


    /**
     * Prints this JPIPResponseFields out to the specified output stream. This
     * method is useful for debugging.
     * 
     * @param out an output stream.
     */
    public void list(PrintStream out)
    {

        out.println("\n-- JPIP Response Fields --");

        if (tid != null)
            out.println("tid: " + tid);

        if (cid != null)
            out.println("cid: " + cid);

        // Channel new
        if (transport != -1)
        {
            out.print("transport = ");
            if (transport == TRANSPORT_HTTP)
            {
                out.println("http");
            }
            else
            {
                if (transport == TRANSPORT_HTTP_TCP)
                {
                    out.println("http-tcp");
                }
                else
                {
                    out.println("unavailable transport mode");
                }
            }
        }

        if (host != null)
            out.println("host: " + host);
        if (path != null)
            out.println("path: " + path);
        if (port != -1)
            out.println("port: " + port);
        if (auxport != -1)
            out.println("auxport: " + auxport);

        if (qid != -1)
            out.println("quid: " + qid);

        // VIEW WINDOW FIELD
        if (fsiz[0] != -1)
            out.println("fsiz: [ " + fsiz[0] + " , " + fsiz[1] + " ]");

        if (rsiz[0] != -1)
            out.println("rsiz: [ " + rsiz[0] + " , " + rsiz[1] + " ]");

        if (roff[0] != -1)
            out.println("roff: [ " + roff[0] + " , " + roff[1] + " ]");

        if (comps != null)
        {
            out.print("comps: ");
            for (int i = 0; i < comps.length; i++)
            {
                out.print(comps[i][0] + "-" + comps[i][1] + ",");
            }
            out.println();
        }

        if (stream != null)
        {
            out.print("streams: ");
            for (int i = 0; i < stream.length; i++)
            {
                out.print(stream[i][0] + "-" + stream[i][1] + ",");
            }
            out.println();
        }

        if (context != null)
            out.println("context: " + context);

        if (roi != null)
            out.println("roi: " + roi);

        if (layers != -1)
            out.println("layers: " + layers);

        if (srate >= 0)
            out.println("srate: " + srate);

        // METADATA FIELD
        if (metareq != null)
            out.println("metare:= " + metareq);

        // DATA LIMIT FIELD

        if (len != -1)
            out.println("len: " + len);

        if (quality != -1)
            out.println("quality: " + quality);

        // SERVER CONTROL FIELD	
        if (type != -1)
        {
            out.print("type: ");
            if (type == ImageReturnTypes.JPP_STREAM)
                out.println("jpp-stream");
            if (type == ImageReturnTypes.JPT_STREAM)
                out.println("jpt-stream");
            if (type == ImageReturnTypes.RAW)
                out.println("raw");
        }

        // CACHE MANAGEMENT FIELD
        if (mset != null)
            out.println("mset: " + mset);

        // UPLOAD FIELD

        //CLIENT CAPABILITIES PREFERENCES FIELD
        if (cap != null)
            out.println("cap: " + cap);

        if (pref != null)
            out.println("pref: " + pref);
    }

}