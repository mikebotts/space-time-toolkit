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
 * This interface defines the image media types that the server is able to send
 * this image media types. Therefore, a client must be include some t 
 * 
 * a client can include in
 * the request and the server is able to sent the  can be sent from the
 * server to a client in the response.
 * 
 * See ISO/IEC 15444-9 section C.7.3
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0   2007/10/27
 */
public interface ImageReturnTypes {

	/**
	 * Indicates the maximum number of image media type supported.
	 * <p>
	 * NOTICE: this attribute must not be used because it is desappearing
	 * in the next release.
	 */
	public static final int MAX_IMAGE_RETURN_TYPES_ALLOWED = 3;
	
	/**
	 * A JPP-Stream as defined in the ISO/IEC 15444-9 annex A. 
	 */
	public static final int JPP_STREAM = 0;
	
	/**
	 * A JPT-Stream as defined in the ISO/IEC 15444-9 annex A.
	 */
	public static final int JPT_STREAM = 1;
	
	/**
	 * The client is requesting the entire sequence of bytes in the logical
	 * target to be delivered unchanged.
	 */
	public static final int RAW = 2;
		
}
