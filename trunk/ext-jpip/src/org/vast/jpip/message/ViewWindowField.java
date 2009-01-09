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
 * This class is used to store the view window fields.
 * <p>
 * Further information, see ISO/IEC 15444-9 section C.4
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0   2007/10/26 
 */
public class ViewWindowField {

	/**
	 * fsiz = "fsiz" "=" fx "," fy ["," round-direction] <br>
	 * fx = UINT <br>
	 * fy = UINT <br>
	 * round-direction = "round-up" / "round-down" / "closest"
	 * <p>
	 * This field is used to identify the resolution associated with the requested view-window. The values fx and fy specify
	 * the dimensions of the desired image resolution. The round-direction value specifies how an available
	 * codestream image resolution shall be selected for each requested codestream, if the requested image resolution is not
	 * available within that codestream. The requested frame size is mapped to a codestream image resolution, following the
	 * procedure described in C.4.1 of the ISO/IEC 15444-9:2005, possibly with the addition of coordinate transformations requested via a Codestream
	 * Context request field. A client wishing to control the exact number of samples received for a particular
	 * image component may need to increase the requested frame size, as explained in C.4.1. The round-direction
	 * options defined ISO/IEC 15444-9:2005 Recommendation | International Standard are:
	 * 
	 * <p>
	 * <table border=1 cellspacing=0 cellpadding=2 >
	 * 	<tr align=center>
	 * 		<td> <b> Round-direction </b> </td>
	 * 		<td> <b> Meaning </b> </td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td align=center> <b> "round-up" </b> </td>
	 * 		<td>  For each requested codestream, the smallest codestream image resolution whose
	 *					width and height are both greater than or equal to the specified size shall be
	 *					selected. If there is none, then the largest available codestream image resolution
	 *					shall be used.
	 *			</td>
	 *		</tr>
	 *		<tr>
	 *			<td align=center> <b> "round-down" </b> </td>
	 *			<td> For each requested codestream, the largest codestream image resolution whose
	 *				width and height are both less than or equal to the specified size shall be selected.
	 *				This is the default value when the round-direction parameter is not
	 *				specified.
	 *			</td>
	 *		</tr>
	 *		<tr>
	 *			<td align=center> <b> "closest" </b> </td>
	 *			<td>For each requested codestream, the codestream image resolution that is closest
	 *				to the specified size in area (where area = fx x fy) shall be selected. Where two
	 *				codestream image resolutions have areas which are equidistant from fx x fy, the
	 *				larger of the two shall be selected.
	 *			</td>
	 *		</tr> 
	 * </table>
	 */	
	public int[] fsiz = {-1, -1, ROUND_DOWN};
	
	/**
	 * Allowed values for the round direction
	 */
	public static final int ROUND_DOWN = 1;
	public static final int ROUND_UP = 2;
	public static final int CLOSEST = 3;
	
	/**
	 * roff = "roff" "=" ox "," oy <br>
	 * ox = UINT <br>
	 * oy = UINT <br>
	 * <p>
	 * This field is used to identify the upper left hand corner (offset) of the spatial region associated with the requested viewwindow;
	 * if not present, the offsets default to 0. The actual displacement of a codestream image region from the upper
	 * left hand corner of the image, at the actual codestream image resolution selected by the server, is obtained following the
	 * procedure described in C.4.1 of the ISO/IEC 15444-9:2005, possibly with the addition of coordinate transformations requested via a Codestream
	 * Context request field.
	 * <p>
	 *	Use of the Offset field is valid only in conjunction with the Frame Size request field.
	 */
	public int[] roff = {-1, -1};	 
	
	/**
	 * rsiz = "rsiz" "=" sx "," sy <br>
	 * sx = UINT <br>
	 * sy = UINT <br>
	 * <p>
	 * This field is used to identify the horizontal and vertical extent (size) of the spatial region associated with the requested
	 * view-window; if not present, the region extends to the lower right hand corner of the image. The actual dimensions of a
	 *	codestream image region, at the actual codestream image resolution selected by the server, are computed following the
	 *	procedure described in C.4.1 of the ISO/IEC 15444-9:2005, possibly with the addition of coordinate transformations requested via a Codestream
	 *	Context request field. A requested codestream image region need not necessarily be fully contained within
	 *	the codestream, in which case the server simply takes the intersection between the available codestream image region
	 *	and the requested region.
	 * <p>
	 * Use of the Region Size request field is valid only in conjunction with the Frame Size request field.
	 */
	public int[] rsiz = {-1, -1};

	/**
	 * comps = "comps" "=" 1#UINT-RANGE
	 * <p>
	 * This field is used to identify the image components that are to be included in the requested view-window; if not present,
	 * the request is understood to include all available image components of all codestreams identified via the Codestream
	 * request field, and all relevant components of all codestreams requested via the Codestream Context request field.
	 */
	public int [][] comps = null;

	/**
	 * stream = "stream" "=" 1#sampled-range <br>
	 *	sampled-range = UINT-RANGE [":" sampling-factor] <br>
	 *	sampling-factor = UINT <br>
	 * <p>
	 *	This field is used to identify which codestream or codestreams belong to the requested view-window. If the field is
	 *	omitted and the codestream(s) cannot be determined by other means, the default is the single codestream with
	 *	identifier 0. Note that the Codestream Context request field (see C.4.7) provides an additional means for requesting
	 *	codestreams.
	 * <p>
	 *	Where a range of codestreams is identified, the absence of an upper bound means that the range extends to all
	 *	codestreams with larger identifiers. Where an upper bound is provided, the upper bound provides the absolute identifier
	 *	of the last codestream in the range.
	 * <p>
	 * Whether or not an upper bound is provided, a codestream range may be qualified by an additional samplingfactor.
	 *	The sampling-factor, if provided, shall be a strictly positive integer, F. The range then includes all
	 *	codestream identifiers L+Fk which lie within the unqualified range, where L is the identifier of the first codestream in
	 *	the range. The client's index of the codestreams of interest is k and k is a UINT.
	 */
	public int [][] stream = null; // array of [][3], where 3 = {from, to, sampling-factor}  <<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**
	 * 	
	 */
	public String context = null;

	/**
	 * srate = "srate" "=" streams-per-second <br>
	 * streams-per-second = UFLOAT <br>
	 * <p>
	 * If this field is supplied, the codestreams which belong to the view-window are obtained by subsampling those
	 * mentioned by the Codestream request field, in addition to those expanded from context-range values in the Codestream
	 * Context request field (see C.4.7), so as to achieve an average sampling rate no greater than the streams-per-second
	 *	value. This is possible only if the codestreams have associated timing information (e.g., if they belong to a logical target
	 *	conforming to the MJ2 file format).
	 * <p>
	 *	This request field serves only to determine which codestreams should be considered to belong to the view-window. The
	 *	server shall scan through all codestreams which would otherwise be included in the view-window, discarding
	 *	codestreams as required to ensure that the average separation between codestream source times is no less than the
	 *	reciprocal of the streams-per-second value. This Recommendation | International Standard does not prescribe an
	 * algorithm for subsampling, or a precise interpretation for the term "average separation."
	 * <p>
	 *	If no source timing information is available, the view-window will consist of all codestreams identified via the
	 *	Codestream request field and the Codestream Context request field, but this request field may nonetheless affect the
	 *	interpretation of a Delivery Rate request field, if present
	 */
	public float srate = -1.0F;

	/**
	 * 
	 */
	public String roi = null;

	/**
	 * layers = "layers" "=" UINT
	 * <p>
	 *	This field may be used to restrict the number of codestream quality layers that belong to the view-window request. By
	 *	default, all available layers are of interest. The value specifies the number of initial quality layers that are of interest.
	 *	The server should not attempt to augment any precinct data-bins beyond the relevant layer boundary. The server should
	 *	not attempt to augment any tile data-bins beyond the point at which all remaining contents lie beyond the relevant layer
	 *	boundary. Due to the order of data within a tile, it may be necessary for the server to return data beyond the boundary of
	 *	the requested layer for JPT-stream requests only.
	 */
	public int layers = -1;


	/**************************************************************************/
	/**                     PRINCIPAL  METHODS                               **/ 
	/**************************************************************************/
	
	/**
	 * Constructor.
	 */
	public ViewWindowField() {
		fsiz = new int[3];
		fsiz[0] = fsiz[1] = -1; 
		fsiz[2] = ROUND_DOWN;
		
		rsiz = new int[2];
		rsiz[0] = rsiz[1] = -1;
		
		roff = new int[2];
		roff[0] = roff[1] = -1;
		
		comps = null;
		layers = -1;
		srate = -1.0F;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param fsiz definition in {@link #fsiz}
	 * @param roff definition in {@link #roff}
	 * @param rsiz definition in {@link #rsiz}
	 * @param comps definition in {@link #comps}
	 * @param stream definition in {@link #stream}
	 * @param context definition in {@link #context}
	 * @param srate definition in {@link #srate}
	 * @param roi definition in {@link #roi}
	 * @param layers definition in {@link #layers} 
	 */
	public ViewWindowField(int[] fsiz, int[] roff, int[] rsiz, int[][] comps, int[][] stream, String context, float srate, String roi, int layers) {
		this.fsiz[0] = fsiz[0]; this.fsiz[1] = fsiz[1]; this.fsiz[2] = fsiz[2];
		this.roff[0] = roff[0]; this.roff[1] = roff[1];
		this.rsiz[0] = rsiz[0]; this.rsiz[1] = rsiz[1];
		this.comps = comps;
		this.stream = stream;
		this.context = context;
		this.srate = srate;
		this.roi = roi;
		this.layers = layers;
	}
	
	/**
	 * Copy attributes from viewWindow1 to viewWindow2. It is a deep copy, then
	 * attributes are copied one by one, and none reference is copied. 
	 * 
	 * @param viewWindow1 data to copy.
	 * @param viewWindow2 where data are copied.
	 */
	public static void deepCopy(ViewWindowField viewWindow1, ViewWindowField viewWindow2) {
		if ( (viewWindow1 == null ) || (viewWindow2 == null) ) {
			throw new NullPointerException();
		}
		viewWindow2.fsiz[0] = viewWindow1.fsiz[0];
		viewWindow2.fsiz[1] = viewWindow1.fsiz[1];
		viewWindow2.fsiz[2] = viewWindow1.fsiz[2];
		viewWindow2.roff[0] = viewWindow1.roff[0];
		viewWindow2.roff[1] = viewWindow1.roff[1];
		viewWindow2.rsiz[0] = viewWindow1.rsiz[0];
		viewWindow2.rsiz[1] = viewWindow1.rsiz[1];
		if (viewWindow1.comps != null) {
			viewWindow2.comps = new int[viewWindow1.comps.length][2];
			for (int i = 0; i < viewWindow1.comps.length; i++) {
				viewWindow2.comps[i][0] = viewWindow1.comps[i][0];
				viewWindow2.comps[i][1] = viewWindow1.comps[i][1];
			}
		}
		viewWindow2.layers  = viewWindow1.layers;
	}
	
	/**
	 * Sets the attributes to its initial values.
	 */
	public void reset() {
		fsiz[0] = fsiz[1] = -1;  fsiz[2] = ROUND_DOWN;
		rsiz[0] = rsiz[1] = -1;
		roff[0] = roff[1] = -1;
		comps = null;
		layers = -1;
		srate = -1.0F;
	}
		
	/**
	 * For debugging purpose
	 */
	public String toString() {

		String str = "";

		str = getClass().getName() + " [";

		// VIEW WINDOW FIELD
		if(fsiz[0] != -1) str += "fsiz=[ " + fsiz[0] + " , " +fsiz[1]+" ]";		
		if(roff[0] != -1) str += " roff=[ " + roff[0] + " , " +roff[1]+" ]";
		if(rsiz[0] != -1) str += " rsiz=[ " + rsiz[0] + " , " +rsiz[1]+" ]";
		
		if(comps != null) {
			str += " comps=";
			for (int i = 0; i < comps.length; i++) {
				str += "["+comps[i][0] + "-" + comps[i][1]+"] " ;
			}
		}

		if(stream != null) {			
			str += " streams=";				
			for (int i=0; i<stream.length; i++) {
				if (stream[i][0] != -1) {
					str += stream[i][0];
					if (stream[i][1] != -1) {
						str += "-" + stream[i][1];
					}
				}					
				str += "," ;	
			}	
		}

		if (context != null) str += " context=" + context;

		if (roi != null) str += " roi=" + roi;

		if (layers != -1) str += " layers=" + layers;

		if (srate >= 0) str += " srate=" + srate;

		str += " ]";

		return str;
	}

	/**
	 * Prints this ViewWindow out to the specified output stream. This method
	 * is useful for debugging.
	 * 
	 * @param out an output stream.
	 */
	public void list(PrintStream out) {
				
		out.println("-- View Window Fields --");
		
		// VIEW WINDOW FIELD
		if(fsiz[0] != -1) out.println("fsiz: [" + fsiz[0] + "," +fsiz[1]+"]");
		if(roff[0] != -1) out.println("roff: [" + roff[0] + "," +roff[1]+"]");
		if(rsiz[0] != -1) out.println("rsiz: [" + rsiz[0] + "," +rsiz[1]+"]");

		if(comps != null) {
			out.print("comps: ");
			for (int i = 0; i < comps.length; i++) {
				out.print("[" + comps[i][0] + "-" + comps[i][1]+"]") ;	
			}		
			out.println();
		}

		if(stream != null) {			
			out.print("streams: ");				
			for (int i=0; i<stream.length; i++) {
				if (stream[i][0] != -1) {
					out.print( stream[i][0]);
					if (stream[i][1] != -1) {
						out.print("-" + stream[i][1]);
					}
				}					
				out.print(",");	
			}
			out.println();			
		}

		if (context != null) out.println("context: " + context);

		if (roi != null) out.println("roi: " + roi);

		if (layers != -1) out.println("layers: " + layers);

		if (srate >= 0) out.println("srate: " + srate);

		out.flush();
	}

}