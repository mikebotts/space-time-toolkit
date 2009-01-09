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
 * This class is used to save the data limit fields.
 * <p>
 * Further information, see ISO/IEC 15444-9 section C.6
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0   2007/10/26
 */
public class DataLimitField {

	/**
	 * len = "len" "=" UINT
	 * <p>
	 * This field specifies a restriction on the amount of data the client wants the server to send in response to this request. The
	 * unit shall be bytes. If not present, the server should send image data to the client until such point as all of the relevant
	 * data has been sent, a quality limit is reached (see C.6.2 of the ISO/IEC 15444-9:2005), or the response is interrupted by the arrival of a new request
	 * that does not include a Wait request field with a value of "yes" (see C.7.2 of the ISO/IEC 15444-9:2005). The client should use len=0 if it requires
	 * response headers and no response data.
	 */
	public int len = -1;
	
	/**
	 * quality = "quality" "=" (1*2DIGIT / "100") ; 0 to 100
	 * <p>
	 * This field may be used to limit data transmission to a quality level (between 0 for lowest quality and 100 for highest
	 * quality) associated with the image. Quality limits are difficult to formulate in a reliable manner, and the server may
	 * ignore this request by responding with a value "?1" (see D.2.16). Nevertheless, it is useful to allow the client to provide
	 * some indication of the maximum image quality that might be of interest. The quality factor may attempt to approximate
	 * the ad hoc Quality commonly used to control JPEG compression. The client should expect that the returned data size is
	 * monotonically non-decreasing with increasing quality, i.e., increasing the quality value generally corresponds to
	 * increasing the returned data size.
	 */
	public int quality = -1; // values into [0,100]
	
	
	/**************************************************************************/
	/**                         PRINCIPAL METHODS                            **/ 
	/**************************************************************************/
	
	/**
	 * Constructor.
	 */
	public DataLimitField() {
		reset();
	}
	
	/**
	 * Sets the attributes to its initial values.
	 */
	public void reset() {
		len = -1;
		quality = -1; // values into [0,100]
	}
	
	/**
	 * For debugging purposes. 
	 */
	public String toString() {
		String str = "";

		str = getClass().getName() + " [";
		
		str += "len=" + len;
		str += ", quality=" + quality;
		
		str += "]";
		return str;
	}
	
	/**
	 * Prints this Data limit fields out to the specified output stream.
	 * This method is useful for debugging.
	 * 
	 * @param out an output stream.
	 */
	public void list(PrintStream out) {

		out.println("-- Data limit fields --");
	
		out.println("len: " + len);
		out.println("quality: " + quality);
		
		out.flush();
	}	
			
}
