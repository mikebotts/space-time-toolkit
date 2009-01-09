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
 * This class contains the values of the JPIP message header.
 * <p>
 * Further information, see ISO/IEC 15444-9 section A.2
 * 
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0 2007/10/26
 */
public class JPIPMessageHeader
{

    /**
     *	Bin-ID = [BinIdIndicator, completeDataBin, InClassIdentifier]
     * 
     * Bin-ID format:<BR>
     *  
     *  <samp>
     *  MSB                                                 LSB 
     *   7 6 5 4 3 2 1 0    7 6 5 4 3 2 1 0    7 6 5 4 3 2 1 0  
     *  -----------------  -----------------  -----------------
     *  |a|b b|c|d d d d|  |a|d d d d d d d|  |a|d d d d d d d| .....
     *  -----------------  -----------------  -----------------
     *  </samp>
     * <P>
     *  Bits 6 and 5 of the first, BinIDIndicator, byte indicate whether the
     *  Class and CSn VBASs are present in the message header. Next table defines
     *  the bit values and its meaning
     *  <P>
     *  <table border=1 cellspacing=0 cellpadding=2>
     *  	<tr align=center>
     *  		<td> <b> Indicator </b> </td>
     *  		<td> <b> Meaning </b> </td>
     *  	</tr>
     *  	<tr>
     *  		<td> 0 </td>
     *  		<td> Prohibited </td>
     *  	</tr>
     *  	<tr>
     * 	 	<td> 1 </td>
     *  		<td> No Class or CSn VBAS is present in message header </td>
     *  	</tr>    
     *  	<tr>
     *  		<td> 2 </td>
     *  		<td> Class VBAS is present but CSn is no present in message header </td>
     *  	</tr>  
     *  	<tr>
     *  		<td> 3 </td>
     *  		<td> Class and CSn VBAS are both present in the message header </td>
     *  	</tr>  
     *  </table>
     *  <P>
     *  Bit 4, completeDataBin, of the first byte of the Bin-ID indicates
     *  whether or not this message contains the last byte in the asociated
     *  data-bin: '0' means it is not the last byte in the data-bin; '1'
     *  inidates tat it is the last byte in the data-bin.
     *  <P>
     *  The remaining 4 bits of the first byte and the 7 low order bits of
     *  any remaining bytes in the Bin-ID VBAS form an "in-class identifier".
     *  <P>
     */
    public boolean isLastByte = true; // completeDataBin better???
    public long inClassIdentifier = -1;

    /**
     * If present, provides a message class identifier. The message class
     * identifier is a non-negative integer, formed by concatenating the
     * least significant 7 bits of each byte of the VBAS in big-endian order.
     * If no present, the message class identifier is unchanged form that
     * associated with the previous message. If the Class VBAS is not present
     * and there is no previous message, the message class identifier is 0.
     * <P>
     * <table border=1 cellspacing=0 cellpadding=2>
     *  	<tr align=center>
     *  		<td> <b> Class identifier </b> </td>
     *  		<td> <b> Message class </b> </td>
     *  		<td> <b> Data-bin class </b> </td>
     *  		<td> <b> Stream type </b> </td>
     *  	</tr>
     *		<tr>
     *  		<td> 0 </td>
     *  		<td> Precinct data-bin message </td>
     *  	 	<td> Precinct data-bin</td>
     *    	<td> JPP-stream only </td>
     *  	</tr>
     *   	<tr>
     *  		<td> 1 </td>
     *  		<td> Extended precinct data-bin message </td>
     *  	 	<td> Precinct data-bin</td>
     *    	<td> JPP-stream only </td>
     *  	</tr>	
     *   	<tr>
     *  		<td> 2 </td>
     *  		<td> Tile header data-bin message </td>
     *  	 	<td> Tile header data-bin</td>
     *    	<td> JPP-stream only </td>
     *  	</tr>
     *  	<tr>
     *  		<td> 4 </td>
     *  		<td> Tile data-bin message </td>
     *  	 	<td> Tile data-bin</td>
     *    	<td> JPT-stream only </td>
     *  	</tr>
     *   	<tr>
     *  		<td> 5 </td>
     *  		<td> Extended precinct data-bin message </td>
     *  	 	<td> Tile data-bin</td>
     *    	<td> JPT-stream only </td>
     *  	</tr>
     *   	<tr>
     *  		<td> 6 </td>
     *  		<td> Main header data-bin message </td>
     *  	 	<td> Main heaer data-bin</td>
     *    	<td> JPP- and JPT-stream </td>
     *  	</tr>
     *  	<tr>
     *  		<td> 8 </td>
     *  		<td> Metadata-bin message </td>
     *  	 	<td> Metadata-bin</td>
     *    	<td> JPP and JPT-stream </td>
     *  	</tr>
     *  </table>
     */
    public int classIdentifier = -1;

    /**
     * Allowed values for Class attribute.
     */
    public static final int PRECINCT = 0;
    public static final int EXTENDED_PRECINCT = 1;
    public static final int TILE_HEADER = 2;
    public static final int TILE = 4;
    public static final int EXTENDED_TILE = 5;
    public static final int MAIN_HEADER = 6;
    public static final int METADATA = 8;

    /**
     * If present, identifies the index (stating from 0) of the codestream to
     * which the data-bin belongs. The codestream index is formed by concatenating
     * the leas significant 7 bits of each byte of the VBAS in big-endian order.
     * If the CSn VBAS is no present, the codestream index is unchanged from the
     * previous message. If CSn VBAS is no present and there is no previous
     * message, the codestream index is 0.
     */
    public int CSn = -1;

    /**
     * Identifies the offset of the data in the message from the start of the
     * data-bin. It is a non-negative integer value, formed by concatenating the
     * least significant 7 bits of each byte in the VBAS in big-endian order.
     */
    public long msgOffset = -1;

    /**
     * Identifies the total number of bytes in the body of the message. It is a
     * non-negative integer value, formed by concatenating the least significant
     * 7 bits of each byte in the VBAS in big-endian order.
     */
    public long msgLength = -1;

    /**
     * If present, it represents a non-negative integer value, formed by
     * concatenating the leas significant 7 bits of each byte in the VBAS in
     * big-endian order.
     *	Its presence and meaning if present, is determined by the message class
     * identifier foun within the Bin-ID VBAS, see {@link CADI.Server.Core#BinID}.
     */
    public int Aux = -1;

    /**
     * Indicates if this JPIP Message is an End of Response Message.
     */
    public boolean isEOR = false;

    /**
     * @see CADI.Common.Communication.JPIP.EORCodes
     * ISO/IEC 15444-9 sect. D.3
     */
    public int EORCode = -1; // <<<<<<<<<< NOTICE: the inClassIdentifier could be used as a EORCode >>>>>>>>>>


    /**************************************************************************/
    /**                        PRINCIPAL METHODS                             **/
    /**************************************************************************/

    /**
     * Default constructor.
     */
    public JPIPMessageHeader()
    {
    }


    /**
     * Constructor.
     * 
     * @param isLastByte definition in {@link #isLastByte}
     * @param inClassIdentifier definition in {@link #inClassIdentifier}
     * @param classIdentifier definition in {@link #classIdentifier}
     * @param CSn definition in {@link #CSn} 
     * @param msgOffset definition in {@link #msgOffset}
     * @param msgLength definition in {@link #msgLength}
     * @param Aux definition in {@link #Aux}
     */
    public JPIPMessageHeader(int CSn, int classIdentifier, long inClassIdentifier, long msgOffset, long msgLength, boolean isLastByte, int Aux)
    {
        this.isEOR = false;
        this.CSn = CSn;
        this.classIdentifier = classIdentifier;
        this.inClassIdentifier = inClassIdentifier;
        this.msgOffset = msgOffset;
        this.msgLength = msgLength;
        this.isLastByte = isLastByte;
        this.Aux = Aux;
    }


    /**
     * Constructor for EOR message header. 
     * 
     * @param EORCode
     * @param MsgLength
     */
    public JPIPMessageHeader(int EORCode, int MsgLength)
    {
        this.isEOR = true;
        this.EORCode = EORCode;
        this.msgLength = MsgLength;
    }


    /** 
     * Sets the attributes to its initial vaules.
     */
    public void reset()
    {
        isLastByte = true;
        inClassIdentifier = -1;
        classIdentifier = -1;
        CSn = -1;
        msgOffset = -1;
        msgLength = -1;
        Aux = -1;
        isEOR = false;
    }


    /**
     * For debugging purposes.  
     */
    public String toString()
    {
        String str = "";

        str = getClass().getName() + " [";
        if (!isEOR)
        {
            str += " InClassIdentifier=" + inClassIdentifier;
            str += " ClassIdentifier=" + classIdentifier;
            str += " CSn=" + CSn;
            str += " MsgOffset=" + msgOffset;
            str += " MsgLength=" + msgLength;
            str += " Complete Data Bin: " + (isLastByte ? "TRUE" : "FALSE");
            str += " Aux=" + Aux;
        }
        else
        {
            str += "EOR Message:";
            str += " Code=" + EORCode;
            str += " MsgLength=" + msgLength;
        }

        str += "]";

        return str;
    }


    /**
     * Prints this JPIP Message Header fields out to the
     * specified output stream. This method is useful for debugging.
     * 
     * @param out an output stream.
     */
    public void list(PrintStream out)
    {

        out.println("-- JPIP Message Header --");

        if (!isEOR)
        {
            out.println("InClassIdentifier: " + inClassIdentifier);
            out.println("Class: " + classIdentifier);
            out.println("CSn: " + CSn);
            out.println("MsgOffset: " + msgOffset);
            out.println("MsgLength: " + msgLength);
            out.println("Complete Data Bin: " + (isLastByte ? "true" : "false"));
            out.println("Aux: " + Aux);
        }
        else
        {
            out.println("EOR Message");
            out.println("Code: " + EORCode);
            out.println("MsgLength: " + msgLength);
        }

        out.flush();
    }

}