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
 * This class is used to store the client capabilities and preferences fields.
 * <p>
 * Further information, see ISO/IEC 15444-9 section C.10
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version beta 0.1 2007/10/26
 */
public class ClientCapPrefField {

	/**
	 * 
	 */
	public String cap = null;
	
	/**
	 * 
	 */
	public String pref = null;
	
	/**
	 * 
	 */
	public String csf = null;


	/**************************************************************************/
	/**                         PRINCIPAL METHODS                            **/ 
	/**************************************************************************/
	
	/**
	 * Constructor.
	 */
	public ClientCapPrefField() {
		reset();
	}
	
	/**
	 * Sets the attributes to its initial values.
	 */
	public void reset() {
		cap = null;
		pref = null;
		csf = null;
	}
	
	/**
	 * For debugging purposes. 
	 */
	public String toString() {
		String str = "";

		str = getClass().getName() + " [";

		str += "<<< Not implemented yet >>> ";

		str += "]";
		return str;
	}
	
	/**
	 * Prints this Client Capabilities and Preferences fields out to the
	 * specified output stream. This method is useful for debugging.
	 * 
	 * @param out an output stream.
	 */
	public void list(PrintStream out) {

		out.println("-- Client Capabilities and Preferences --");		
		
		out.println("<<< Not implemented yet >>> ");
						
		out.flush();
	}
	
}
