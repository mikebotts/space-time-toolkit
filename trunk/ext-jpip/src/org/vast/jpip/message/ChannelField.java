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
 * This class is used to store the channel fields.
 * <p>
 * Further information, see ISO/IEC 15444-9 section C.3
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version beta 0.2   2007/10/26
 */
public class ChannelField {

	/**
	 * cid = "cid" "=" channel-id <br>
	 * channel-id = TOKEN
	 * <p>
	 * This field is used to associate the request with a particular JPIP channel, and hence the session to which the channel belongs.
	 */
	public String cid = null;
	
	/**
	 * {@see JPIPResponseFields#path}
	 */
	public String path = null;
	
	/**
	 * cnew = "cnew" "=" 1#transport-name <br>
	 * transport-name = TOKEN
	 * <p>
	 * This field is used to request a new JPIP channel. If no Channel ID request field is present, the request is for a new
	 * session. Otherwise, the request is for a new channel in the same session as the channel identified by the Channel ID
	 * request field.
	 * <p>
	 * The value string identifies the names of one or more transport protocols that the client is willing to accept. The
	 *	ISO/IEC 15444-9:2005 Recommendation | International Standard defines only the transport names, "http" and "http-tcp,"
	 *	although it is anticipated that other transports, such as "udp", may be defined elsewhere.
	 */
	public int[] cnew = null;	
	
	/**
	 * Allowed values for the <data>cnew</data> attribute.
	 */
	public static final int CHANNEL_HTTP = 1;
	public static final int CHANNEL_HTTP_TCP = 2;
	
	
	/**
	 * cclose = "cclose" "=" ("*" / 1#channel-id)
	 * <p>
	 * This field is used to close one or more open channels to a session. If the value field contains one or more channel-id
	 * tokens, they shall all belong to the same session. In this case, the Channel ID request field is not necessary, but if
	 * provided it shall also reference a channel belonging to the same session.
	 * If the value field is "*", all channels associated with the session will be closed. In this case, the session shall be
	 * identified by the inclusion of a Channel ID request field.
	 */
	public String[] cclose = null;
	
	/**
	 * qid = "qid" "=" UINT
	 * <p>
	 * This field is used to specify a Request ID value. Each channel has its own request queue, with its own Request ID
	 * counter. Requests which are received within any given channel (as indicated by the Channel ID value) shall be
	 * processed in the order of their Request ID values, where the Request ID field is used. The server may process requests
	 * which do not contain a Request ID field on a first-come-first-served basis. However, it shall not process a request which
	 * carrives with a Request ID value of n until it has processed all requests with a Request ID value less than n which is
	 * associated with the same channel, unless n=0. The client shall not issue a request which specifies the same Request ID
	 * value as any other request associated with the same channel, and shall not issue Request ID's that are smaller than any
	 * previously issued Request ID on this channel.
	 */
	public int qid=-1; // Only positive values are allowed
	 
	
	/**************************************************************************/
	/**                        PRINCIPAL METHODS                             **/ 
	/**************************************************************************/
	
	/**
	 * Constructor.
	 */
	public ChannelField() {		
		reset();
	}
	
	/**
	 * Sets the attributes to its initial values.
	 */
	public void reset() {
		cid = null;
		cnew = null;
		cclose = null;
		qid=-1;
	}
	
	/**
	 * For debugging purposes. 
	 */
	public String toString() {
		String str = "";

		str = getClass().getName() + " [";
			
		if (cid != null) str += "cid=" + cid;
		
		if (path != null) str += ", path=" + path;
			
		if (cnew != null) {
			str += ", cnew=";
			for (int i = 0; i < cnew.length; i++) {
				str += " " + cnew[i];
			}
		}
		if (cclose != null) {
			str +=  ", cclose=";
			for (int i = 0; i < cclose.length; i++) {
				str += " " + cclose[i];
			}
		}		
		str += "]";
		return str;
	}
	
	/**
	 * Prints this Channel fields out to the specified output stream.
	 * This method is useful for debugging.
	 * 
	 * @param out an output stream.
	 */
	public void list(PrintStream out) {

		out.println("-- Channel fields --");
		
		if (cid != null) out.println("cid: " + cid);
		
		if (path != null) out.println("path: " + path);
			
		if (cnew != null) {
			out.print("cnew: ");
			for (int i = 0; i < cnew.length; i++) {
				out.print(" " + cnew[i]);
			}
			out.println();
		}
		
		if (cclose != null) {
			out.print("cclose: ");
			for (int i = 0; i < cclose.length; i++) {
				out.print(" " + cclose[i]);
			}
			out.println();
		}		
		
		out.flush();
	}
}
