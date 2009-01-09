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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import org.vast.jpip.message.JPIPMessage;


/**
 * This class reads JPIP messages as they are defined in ISO/IEC 15444-9
 * section A.2. Each time the <code>{@link #readMessage()}</code>  method is
 * called, only one JPIP message is read. And the method who is calling must be
 * taken into account when a JPIP End Of Response message is received, then it
 * must not call the <code>{@link #readMessage()}</code> method. If it call
 * after the JPIP End Of Response message is received, a <code>IOException
 * </code> will be thrown.
 * <p>
 * This class needs a input stram where data are readed from. This class
 * only needs a <code>read</code> method to read a <code>byte</code> value and
 * a <code>readFully</code> method to read a byte array. 
 * <p>
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; readMessage<br> 
 * &nbsp; ....<br>
 * &nbsp; readMessage<br>
 * 
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0 2008/01/10
 */
public class JPIPMessageDecoder
{

    /**
     * It is the input stream where data are read from.
     */
    InputStream inputStream = null;

    // INTERNAL ATTRIBUTES

    /**
     * Contains the last Class value. It is a state variable used when dependent
     * form is used.
     */
    private int lastClass = 0;

    /**
     * Contains the last CSn value. It is a state variables used when dependent
     * form is used.
     */
    private int lastCSn = 0;


    /**************************************************************************/
    /**                        PRINCIPAL METHODS                             **/
    /**************************************************************************/

    /**
     * Constructor.
     */
    public JPIPMessageDecoder()
    {
    }


    /**
     * Sets the input stream where the data are read from. This method must be
     * called before another one is called.
     * 
     * @param inputStreamReader definition in {@link #inputStream}
     */
    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }


    /**
     * This method is used to read a JPIP messase. Data are read from the input
     * stream, they are decoded, and a <code>JPIPMessage</code> object is built. 
     * 
     * @return a <code>JPIPMessage</code> object.
     * @throws ProtocolException if received data can not be decoded correctly.
     * @throws IOException if data can not be read from the input stream. It may
     *  be because the link is broken, the server closed the connection, ...
     */
    public JPIPMessage readMessage() throws ProtocolException, IOException
    {
        int BinIDIndicator = 0;
        long inClassIdentifier = 0;
        boolean completeDataBin = false;
        int tempByte = 0;
        JPIPMessage jpipMessage = new JPIPMessage();

        //	Bin-ID
        tempByte = inputStream.read();

        if (tempByte == 0x00)
        { // EOR is reached
            jpipMessage = readEORMessage();
            return jpipMessage;
        }

        // b bits
        BinIDIndicator = (tempByte >>> 5) & 0x03;
        if ((BinIDIndicator < 1) || (BinIDIndicator > 3))
        {
            throw new ProtocolException("Wrong server response: impossible to decode it correctly");
        }

        // c bit
        completeDataBin = (tempByte & 0x10) == 0 ? false : true;

        // d bits (In-Class ID)
        inClassIdentifier = tempByte & 0x0F;
        if ((tempByte >>> 7) > 0)
        {
            int numBytesVBAS = 1;
            do
            {
                tempByte = inputStream.read();
                if (tempByte == -1)
                {
                    throw new EOFException("There is not data available to read the VBAS");
                }
                inClassIdentifier = (inClassIdentifier << 7) | (long) (tempByte & 0x7F);
                numBytesVBAS++;

                if (numBytesVBAS > 9)
                { // maximum long value is 2^63 - 1 => 9 bytes VBAS 
                    throw new ProtocolException("VBAS length is larger than 63 bits (which is the maximum of long)");
                }
            }
            while ((tempByte & 0x80) != 0);
        }

        jpipMessage.header.isLastByte = completeDataBin;
        jpipMessage.header.inClassIdentifier = (int) inClassIdentifier;

        // Class		
        if ((BinIDIndicator == 2) || (BinIDIndicator == 3))
        {
            jpipMessage.header.classIdentifier = (int) readVBAS();
            lastClass = jpipMessage.header.classIdentifier;

        }
        else
        {
            jpipMessage.header.classIdentifier = lastClass;
        }

        // CSn				
        if (BinIDIndicator == 3)
        {
            jpipMessage.header.CSn = (int) readVBAS();
            lastCSn = jpipMessage.header.CSn;

        }
        else
        {
            jpipMessage.header.CSn = lastCSn;
        }

        // Msg-Offset
        jpipMessage.header.msgOffset = (int) readVBAS();

        // Msg-Length						
        jpipMessage.header.msgLength = (int) readVBAS();

        // Aux
        if (((BinIDIndicator == 2) || (BinIDIndicator == 3)) && ((jpipMessage.header.classIdentifier % 2) == 1) && (jpipMessage.header.classIdentifier != 0))
        {
            jpipMessage.header.Aux = (int) readVBAS();
        }

        // Read jpip message body
        if (jpipMessage.header.msgLength > 0)
        {
            int msgLength = (int)jpipMessage.header.msgLength;
            jpipMessage.messageBody = new byte[msgLength];
            readFully(jpipMessage.messageBody, 0, msgLength);            
        }

        return jpipMessage;
    }


    /**************************************************************************/
    /**                        AUXILIARY METHODS                             **/
    /**************************************************************************/

    /**
     * 
     * @return
     * @throws IOException 
     */
    private long readVBAS() throws IOException
    {
        long value = 0;
        int tempByte;
        int numBytesVBAS = 0;

        do
        {
            tempByte = inputStream.read();

            if (tempByte == -1)
            {
                throw new EOFException("There is not data available to read the VBAS");
            }

            value = (value << 7) | (long) (tempByte & 0x7F);
            numBytesVBAS++;

            if (numBytesVBAS > 9)
            { // maximum long value is 2^63 - 1 => 9 bytes VBAS 
                throw new ProtocolException("VBAS length is larger than 63 bits (which is the maximum of long)");
            }

        }
        while ((tempByte & 0x80) != 0);

        return value;
    }


    /**
     * Reads the End of Response message.
     * ISO/IEC 15444-9 Annex D.3
     * 
     * @return
     * @throws IOException
     */
    private JPIPMessage readEORMessage() throws IOException
    {
        JPIPMessage jpipMessage = null;

        jpipMessage = new JPIPMessage();
        jpipMessage.header.isEOR = true;

        // Read EOR code		
        jpipMessage.header.EORCode = inputStream.read();

        // Read EOR body length
        int EORBodyLength = (int) readVBAS();
        jpipMessage.header.msgLength = EORBodyLength;

        // Read EOR body
        if (EORBodyLength > 0)
        {
            jpipMessage.messageBody = new byte[EORBodyLength];
            readFully(jpipMessage.messageBody, 0, EORBodyLength);
        }

        return jpipMessage;
    }
    
    
    private int readFully(byte[] buf, int offset, int length) throws IOException
    {
        int readBytes = 0;
        
        while (readBytes < length)
            readBytes += inputStream.read(buf, readBytes, length - readBytes);
        
        return readBytes;
    }

}