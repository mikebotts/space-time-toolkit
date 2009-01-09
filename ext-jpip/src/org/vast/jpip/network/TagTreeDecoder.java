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

import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0.1 2008/01/29
 */
public class TagTreeDecoder {
		
	/**
	 * Number of rows
	 * <br>
	 * Negative values are not allowed
	 */
	private int rows;
	
	/**
	 * Number of columns
	 * <br>
	 * Negative values are not allowed
	 */
	private int cols;
	
	/**
	 * 
	 */
	private PacketInputStream PHDataInputStream = null;
	
	
	// INTERNAL ATTRIBUTRES
	
	/**
	 * Number of levels
	 * <br>
	 * Negative values are not allowed
	 */
	private int levels;
	
	/**
	 * Tag Tree values. The first value index indicates the level of the tree,
	 * where the value 0 is the matrix to encoder, and value levels-1 is the root.
	 * The second index is the rows, and the third is the columns
	 * <br>
	 * Negative values are not allowed
	 */
	private int tagTree[][][];
	
	/**
	 * Tag Tree States.  The first value index indicates the level of the tree,
	 * where the value 0 is the matrix to encoder, and value levels-1 is the root.
	 * The second index is the rows, and the third is the columns
	 * <br>
	 * Negative values are not allowed
	 */
	private int states[][][];
		
	
	/**************************************************************************/
	/**                        PRINCIPAL METHODS                             **/ 
	/**************************************************************************/
	
	/**
	 * Constructor
	 *
	 * @param rows matrix rows
	 * @param cols matrix columns
	 *
	 * @throws IllegalArgumentException when a codeblock row or column are wrong
	 */
	TagTreeDecoder(int rows, int cols) {
		
		int r, c;
		
		// Check parameters
		if ((rows <= 0) || (cols <= 0)) {
			throw new IllegalArgumentException("Rows and columns must be positive.");
		}
		// Initialitate dimensions
		this.rows = rows;
		this.cols = cols;
		
		// Number of levels
		int levelsAux = Math.max(rows,cols);
		for ( levels=1; levelsAux>1; levels++) {
			levelsAux = (int)Math.ceil(levelsAux/2D);
		}
		
		// Initialitate tagTree and states array
		tagTree = new int[levels][][];
		states = new int[levels][][];
		
		int rowsAux=rows, colsAux=cols;
		for (int i = 0; i < levels; i++) {
			tagTree[i] = new int[rowsAux][colsAux];
			states[i] = new int[rowsAux][colsAux];
			// Initialitate the tagTree and states
			for (r = 0; r < rowsAux; r++){
				for (c = 0; c < colsAux; c++) {
					states[i][r][c] = (int) 0;
					tagTree[i][r][c] = (int) 0;//Integer.MAX_VALUE;
				}
			}
			rowsAux = (int) Math.ceil(rowsAux / 2.0);
			colsAux = (int) Math.ceil(colsAux / 2.0);	
		}
	}
	
	/**
	 * Decode a value of the matrix
	 *
	 * @param m coordintes of the value to codify
	 * @param n coordintes of the value to codify
	 * @param t threshold
	 * @param PHDataInputStream data input stream with the packet header.
	 * @return tag tree value at [m,n] position for theshold t
	 *
	 * @throws IllegalArgumentException when codeblock coordinates are wrong or the bitstream is insuficient for decoding the tag tree information
	 */
	public int Decoder(int t, int m, int n, PacketInputStream PHDataInputStream) {
		
		int tmin = 0;
		int mk, nk;
		int temp;
		this.PHDataInputStream = PHDataInputStream;
		
		// Check parameters
		if ((m < 0) || (m >= rows) || (n < 0) || (n >= cols) || (t < 0)) {
			throw new IllegalArgumentException("Wrong code-block coordinates.");
		}
		
		// Loop on levels
		for (int k = levels - 1; k >= 0; k--) {
			
			temp = (int) Math.pow(2, k);
			mk = (int) m / temp;
			nk = (int) n / temp;
			
			if (states[k][mk][nk] < tmin) {
				states[k][mk][nk] = tmin;
				tagTree[k][mk][nk] = tmin;
			}
			
			while ((tagTree[k][mk][nk] == states[k][mk][nk]) && (states[k][mk][nk] < t)) {
				
				states[k][mk][nk]++;
				if (getBit() == 0){  // A 0 bit means that the value is mayor
					tagTree[k][mk][nk]++;
				}
			}
			// Update tmin
			if (states[k][mk][nk] < tagTree[k][mk][nk]) {
				tmin = states[k][mk][nk];
			}
			else {
				tmin = tagTree[k][mk][nk];  //tmin = Math.min( states[k][mk][nk], tagTree[k][mk][nk]);
			}
		}
		
		return (tagTree[0][m][n]);
	}
	
	/**
	 * Sets the <code>state</code> and <code>tagTree</code> internal
	 * attributes to its initial values.
	 */
	public void reset() {
		for (int l = 0; l < states.length; l++) {
			for (int r = 0; r < states[l].length; r++){
				for (int c = 0; c < states[l][r].length; c++) {
					states[l][r][c] = (int) 0;
					tagTree[l][r][c] = (int) 0;
				}
			}			
		}
	}
		
	/**
	 * @param m coordinates of the value to codify
	 * @param n coordinates of the value to codify
	 *
	 * @throws IllegalArgumentException when the codeblock coordinates are wrong
	 */
	public int getValue(int m, int n) {
		
		// Check parameters
		if ((m < 0) || (m >= rows) || (n < 0) || (n >= cols)) {
			throw new IllegalArgumentException("Wrong code-block coordinates.");
		}
		
		return (tagTree[0][m][n]);
	}

	@Override
	public String toString() {
				
		int rowsAux=rows, colsAux=cols;		
		String str = "";

		str = getClass().getName() + " [";
		
		str += "rows=" + rows + "cols= " + cols + " levels=" + levels;
		for(int l = 0; l < levels; l++) {
			for(int r = 0; r < rowsAux; r++){
				for (int c = 0; c < colsAux; c++){
					str += tagTree[l][r][c] + " ";
				}
				str += "\t";
			}
			//Update rows and columns
			rowsAux = (int)Math.ceil(rowsAux/2.0);
			colsAux = (int)Math.ceil(colsAux/2.0);
		}
		str += "]";
		
		return str;
	}
		
	/**
	 * Prints this Tag Tree out to the specified output stream. This method is
	 * useful for debugging.
	 * 
	 * @param out an output stream.
	 */
	public void list(PrintStream out) {

		out.println("-- Tag Tree --");		
		
		int rowsAux=rows, colsAux=cols;
		out.println("rows: " + rows);
		out.println("cols: " + cols);
		out.println("levels: " + levels);
	
		for(int l = 0; l < levels; l++) {
			for(int r = 0; r < rowsAux; r++){
				for (int c = 0; c < colsAux; c++){
					out.println(tagTree[l][r][c] + " ");
				}
				out.println();
			}
			//Update rows and columns
			rowsAux = (int)Math.ceil(rowsAux/2.0);
			colsAux = (int)Math.ceil(colsAux/2.0);
		}

		out.flush();
	}
	
	
	/**************************************************************************/
	/**                       AUXILARY  METHODS                              **/ 
	/**************************************************************************/
	
	/**
	 * Returns the bit readed from the file.
	 *
	 * @return an integer that represents the bit readed from the file
	 */
	private int getBit() {
		try {
			return PHDataInputStream.getTagBit();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
}