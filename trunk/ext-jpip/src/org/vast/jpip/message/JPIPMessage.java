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
 * The class <code>JPIPMessage</code> is useful to store JPIP messages. It
 * contains: the JPIP header that provides descriptive information to identify
 * the JPIP message in the data-bin, and the message body which is a segment
 * from a data-bin.
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0 2007/10/26
 */
public class JPIPMessage
{

    /**
     * Contains the JPIP message header. {@see JPIPMessageHeader}
     */
    public JPIPMessageHeader header = null;

    /**
     * Is the body of the JPIP message, i.e., it is data from a data-bin.
     */
    public byte[] messageBody = null;


    /**************************************************************************/
    /**                        PRINCIPAL METHODS                             **/
    /**************************************************************************/

    /**
     * Default constructor.
     */
    public JPIPMessage()
    {
        header = new JPIPMessageHeader();
    }


    /** 
     * Sets the attributes to its initial vaules.
     */
    public void reset()
    {
        header.reset();
        messageBody = null;
    }


    /**
     * For debugging purposes. 
     */
    public String toString()
    {
        String str = "";

        str = getClass().getName() + " [";
        if (!header.isEOR)
        {
            str += " InClassIdentifier=" + header.inClassIdentifier;
            str += " classIdentifier=" + header.classIdentifier;
            str += " CSn=" + header.CSn;
            str += " MsgOffset=" + header.msgOffset;
            str += " MsgLength=" + header.msgLength;
            str += " Complete Data Bin=" + (header.isLastByte ? "TRUE" : "FALSE");
            str += " Aux=" + header.Aux;
        }
        else
        {
            str += "EOR Message:";
            str += " Code=" + header.EORCode;
            str += " MsgLength=" + header.msgLength;
        }

        if (messageBody != null)
        {
            str += "\nMessageBody=";
            //str += " <<< Not visible >>>";
            for (int i = 0; i < messageBody.length; i++)
            {
                str += (char) messageBody[i];
            }
        }
        str += "]";

        return str;
    }


    /**
     * Prints this JPIPMessage out to the specified output stream. This method
     * is useful for debugging.
     * 
     * @param out an output stream.
     */
    public void list(PrintStream out)
    {

        out.println("\n-- JPIP Message --");

        if (!header.isEOR)
        {
            out.println("InClassIdentifier: " + header.inClassIdentifier);
            out.println("ClassIdentifier: " + header.classIdentifier);
            out.println("CSn: " + header.CSn);
            out.println("MsgOffset: " + header.msgOffset);
            out.println("MsgLength: " + header.msgLength);
            out.println("Complete Data-Bin: " + (header.isLastByte ? "TRUE" : "FALSE"));
            out.println("Aux: " + header.Aux);
        }
        else
        {
            out.println("EOR Message:");
            out.println(" Code: " + header.EORCode);
            out.println(" MsgLength: " + header.msgLength);
        }

        if (messageBody != null)
        {
            out.print("MessageBody: ");
            
            if (header.classIdentifier == JPIPMessageHeader.METADATA)
            {
                out.print(new String(messageBody));
            }
            else
            {
                for (int i = 0; i < messageBody.length; i++)
                {
                    if ((0xFF & messageBody[i]) < 16)
                        out.print("0");
    
                    out.print(Integer.toHexString(0xFF & messageBody[i]));
                }
            }
            
            out.println();
        }
        out.flush();
    }
}
