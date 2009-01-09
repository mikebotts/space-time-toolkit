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

/**
 * End Of Response (EOR) reason codes.
 * <p>
 * Further information, see ISO/IEC 154999-9, Annex D.3
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0   2007/10/26
 */
public interface EORCodes {
	
	/**
	 * The server has transferred all available image information (not just
	 * information relevant to the requested view-window) to the client. This
	 * reason code has a particular meaning to session-based requests. For a
	 * session-based request, this reason code implies that the client has
	 * received all data which could be sent in response to any session-based
	 * request associated with this logical target. With the possible exception
	 * of requests which include cache management requests fields, any subsequent
	 * session-based request will be responded with no response data and R=1 EOR.
	 */
	public static final int IMAGE_DONE = 1;
	
	/**
	 * The server has transferred all available information that is relevant to
	 * the requested viewwindow. This reason code has a particular meaning to
	 * session-based requests. For a sessionbased request, this reason code
	 * implies that the client has received all data which could be sent in
	 * response to this request and the response data was not limited by any
	 * data-limit-field (len or quality) in the request, or by the handling of a
	 * subsequent request. With the possible exception of requests which include
	 * cache management request fields, any subsequent repetition of the request
	 * will be responded with no response data and R=2 EOR.
	 */
	public static final int WINDOW_DONE = 2;
	
	/**
	 * The server is terminating its response in order to service a new request
	 * which does not specify Wait=yes.
	 */
	public static final int IMAGE_CHANGE = 3;
	
	/**
	 * The server is terminating its response because the byte limit specified
	 * in a Maximum Response Length request field has been reached.
	 */
	public static final int BYTE_LIMIT_REACHED = 4;
	
	/**
	 * The server is terminating its response because the quality limit specified
	 * in a Quality request field has been reached.
	 */
	public static final int QUALITY_LIMIT_REACHED = 5;
	
	/**
	 * The server is terminating its response because some limit on the session
	 * resources, e.g., a time limit, has been reached. No further request should
	 * be issued using a channel ID associated with that session.
	 */
	public static final int SESSION_LIMIT_REACHED = 6;
	
	/**
	 * The server is terminating its response because some limit, e.g., a time
	 * limit, has been reached. If the request is issued in a session, further
	 * requests can still be issued using a channel ID associated with that session.
	 */
	public static final int RESPONSE_LIMIT_REACHED = 7;
	
	/**
	 * The server is terminating its response for a reason that is not specified.
	 */
	public static final int NON_ESPECIFIED_REASON = 0xFF;
	
}
