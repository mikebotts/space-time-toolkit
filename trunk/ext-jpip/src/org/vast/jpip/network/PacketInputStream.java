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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 *
 */
public class PacketInputStream extends DataInputStream {
	
	// INTERNAL ATTRIBUTES
	
	/**
	 * Variable used for the getTagBit function, needed to get bit by bit the
	 * data input stream
	 * <p>
	 * Only positive values allowed.
	 */
	int numBit;
	
	/**
	 * Boolean that indicates if a 0xFF has been found in the bytestream.
	 * <p>
	 * True indicates that a 0xFF byte has been found.
	 */
	boolean foundFF;
		
	/**
	 * Variable used for the getTagBit function, needed to get bit by bit the
	 * packet data. This is the byte readed.
	 * <p>
	 * Byte from the packet data.
	 */
	byte readByte;
	
	/**
	 * Variable used for the getTagBit function, needed to get bit by bit the
	 * packet data. This variable is used to save the next byte readed when
	 * detected an 0xFF (stuffing).
	 * <p>
	 * Byte from the packet data.
	 */
	byte nextByte;
	
	
	/**************************************************************************/
	/**                        PRINCIPAL METHODS                             **/ 
	/**************************************************************************/


	/**
	 * Constructor
	 * 
	 * @param bufferedDataInputStream
	 */
	public PacketInputStream(InputStream is) {
		super(is);
		resetGetTagBit();
	}

    
	/**
	 * Retrieves a bit from the file. Function used in PacketDeheading.
	 *
	 * @return the readed bit
	 *
	 * @throws ErrorException when end of file is reached
	 */
	public int getTagBit() throws IOException{
		numBit--;
		if(numBit < 0){

			//Read next byte, stuffing is found
			if(foundFF){
				numBit = (byte) 6;
				readByte = nextByte;
			}else{
				numBit = (byte) 7;				
				readByte = readByte();				
			}

			//Check that the byte is an FF or not
			if(readByte == (byte)0xFF){	
				nextByte = readByte();
				foundFF = true;
			}else{
				foundFF = false;
			}
		}
		return((readByte & (byte)(1<<numBit)) == 0 ? 0 : 1);
	}
	
	/**
	 * Reset the getTagBit flags to reinitialize the reading.
	 */
	public void resetGetTagBit() {
		numBit = 0;
		readByte = 0;
		foundFF=false;
	}
	
	
	/**************************************************************************/
	/**                      AUXILARY  METHODS                               **/ 
	/**************************************************************************/
		
}
