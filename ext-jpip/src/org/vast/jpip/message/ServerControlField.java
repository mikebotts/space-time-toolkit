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
 * This class is used to store the server control fields.
 * <p>
 * Further information, see ISO/IEC 15444-9 section C.7	
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version beta 0.1   2007/10/26
 */
public class ServerControlField implements ImageReturnTypes {
	
	/**
	 * align = "align" "=" ("yes" / "no")
	 * <p>
	 *	This field specifies whether the server response data shall be aligned on natural boundaries. The default value is "no". If
	 *	the value is "yes", any JPT-stream or JPP-stream message delivered in response to this request which crosses any
	 *	"natural boundary" shall terminate at any subsequent "natural boundary." The natural boundaries for each data-bin type
	 *	are listed in the below table. A message is said to cross a natural boundary if it includes the last byte prior to the boundary,
	 *	and the first byte after the boundary. For example, a precinct data-bin crosses a natural boundary if it includes the last
	 *	byte of one packet and the first byte of the next packet. Note carefully that aligned response messages are not actually
	 *	required to terminate at a natural boundary unless they cross a boundary. This means, for example, that the response
	 *	may include partial packets from precincts, which may be necessary if a prevailing byte limit prevents the delivery of
	 *	complete packets.
	 *
	 * <p>
	 * <table border=1 cellspacing=0 cellpadding=2 >
	 * 	<tr align=center>
	 * 		<td> <b> Bin type </b> </td>
	 * 		<td> <b> Natural boundary </b> </td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td align=center> <b> Precinct data-bin </b> </td>
	 * 		<td> End of a packet (one boundary for each quality layer) </td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td align=center> <b> Tile data-bin </b> </td>
	 * 		<td> End of a tile-part (one boundary for each tile-part) </td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td align=center> <b> Tile header data-bin </b> </td>
	 * 		<td> End of the bin (only one boundary) </td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td align=center> <b> Main data-bin </b> </td>
	 * 		<td>  End of the bin (only one boundary)</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td align=center> <b> Metadata-bin </b> </td>
	 * 		<td> End of a box at the top level of the data-bin (one boundary for each box) </td>
	 * 	</tr>
	 * </table> 
	 */
	public boolean align = false;
	
	/**
	 * wait = "wait" "=" ("yes" / "no")
	 * <p>
	 *	This field is used to indicate whether the server shall complete a response to the previous request. If the value of the
	 *	field is "yes", the server shall completely respond to the previous request on the same channel resource specified
	 *	through the channel ID field before starting to respond to this request.
	 *	If the value of this field is "no", the server may gracefully terminate the processing of any previous request on the same
	 *	channel resource (specified through the Channel ID field) prior to completion and may start to respond to this new
	 *	request. In this context, "graceful termination" implies that the server shall at least complete the current message.
	 *	The default value of this field is "no"
	 */
	public boolean wait = false;
	
	/**
	 * type = "type" "=" 1#image-return-type <br>
	 *	image-return-type = media-type / reserved-image-return-type <br>
	 *	media-type = TOKEN "/" TOKEN *( ";" parameter ) <br>
	 *	reserved-image-return-type = TOKEN *( ";" parameter )<br>
	 *	parameter = attribute "=" value <br>
	 *	attribute = TOKEN <br>
	 *	value = TOKEN <br>
	 *	<p>
	 *	This field is used to indicate the type (or types) of the requested response data. A server unwilling to provide any of the
	 *	requested return types shall issue an error response.
	 * <p>
	 * The value of the Image Return Type request field shall be either a media type (defined in RFC 2046) or one of the
	 *	reserved image return types defined as:
	 * <p>
	 * <table border=1 cellspacing=0 cellpadding=2 >
	 * 	<tr align=center>
	 * 		<td> <b> Type </b> </td>
	 * 		<td> <b> Interpretation </b> </td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td align=center> <b> "jpp-stream" </b> </td>
	 * 		<td> A JPP-stream as defined in Annex A. "jpp-stream" may optionally be followed by
	 *					";ptype=ext", in which case the requested return type is one in which all precinct databin
	 *					message headers have the extended form. (see A.2.2) 
	 *			</td>
	 *		</tr>
	 *		<tr>
	 *			<td align=center> <b> "jpt-stream" </b> </td>
	 *			<td> A JPT-stream as defined in Annex A. "jpt-stream" may optionally be followed by
	 *					";ttype=ext", in which case the requested return type is one in which all tile data-bin
	 *					message headers have the extended form. (see A.2.2)
	 *			</td>
	 *		</tr>
	 *		<tr>
	 *			<td align=center> <b> "raw" </b> </td>
	 *			<td>The client is requesting the entire sequence of bytes in the logical target to be delivered
	 *				unchanged.
	 *			</td>
	 *		</tr>
 	 *		<tr>
	 *			<td align=center> <b> Other values </b> </td>
	 *			<td> Reserved for ISO use </td>
	 *		</tr> 
	 * </table>
	 * 
	 */
	public String[] type = null;
	
	/**
	 * drate = "drate" "=" rate-factor <br>
	 *	rate-factor = UFLOAT <br>
	 *	<p>
	 *	This field is used to specify the delivery rate of various codestreams. If this field is supplied, the server shall deliver
	 *	data belonging to the various codestreams in the view-window following a temporally sequenced schedule. The
	 *	codestreams which belong to the view-window are all those identified via the Codestream request field and the
	 *	Codestream Context request field, possibly subsampled in accordance with the Sampling Rate request field.
	 */
	public float drate; 
	
	
	/**************************************************************************/
	/**                         PRINCIPAL METHODS                            **/ 
	/**************************************************************************/

	
	/** 
	 * Constructor.
	 */
	public ServerControlField() {
		align=false;
		wait=false;
		type = null;
		drate = -1.0F;
	}
	
	/**
	 * Sets the attributes to its initial values.
	 */
	public void reset() {
		align=false;
		wait=false;
		type = null;
		drate = -1.0F; 
		
	}

	/**
	 * For debugging purposes. 
	 */
	public String toString() {
		String str = "";

		str = getClass().getName() + " [";

		str += "align=" + (align ? "true" : "false");
		
		str += ", wait=" + (wait ? "true" : "false");

		str += ", type=";
		if (type != null) {
			str +=  "(" + type[0];
			for (int i = 1; i < type.length; i++) {
				str += "," + type[i];
			}
			str += ")";
		} else {
			str += "<<< not defined >>>";
		}

		str += ", drate=" + drate;
		
		str += "]";
		return str;
	}
	
	/**
	 * Prints this Server Control Fields out to the specified output stream.
	 * This method is useful for debugging.
	 * 
	 * @param out an output stream.
	 */
	public void list(PrintStream out) {

		out.println("-- Server Control Fields --");
		
		out.println("align:" + (align ? "true" : "false"));
		
		out.println("wait:" + (wait ? "true" : "false"));

		out.print("type: ");
		if (type != null) {
			out.print(type[0]);
			for (int i = 1; i < type.length; i++) {
				out.print(", " + type[i]);
			}
			out.println();
		} else {
			out.println("<<< not defined >>>");
		}

		out.println("drate: " + drate);
		
		out.flush();
	}	

}
