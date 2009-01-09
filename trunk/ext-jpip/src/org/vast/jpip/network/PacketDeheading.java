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


/**
 * This class decodes the packet headings
 * <p/>
 * Usage: example:<br>
 * &nbsp; construct <br>
 * &nbsp; run <br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0.1 2008/01/29
 */
public class PacketDeheading {
	
	/**
	 * Number of zero bit planes for each block. Indices means: <br>
	 * &nbsp; z: image component <br>
	 * &nbsp; resolutionLevel: 0 is the LL subband, and 1, 2, ... represents next starting with the little one <br>
	 * &nbsp; precinct: precinct in the resolution level <br>
	 * &nbsp; subband: 0 - HL, 1 - LH, 2 - HH (ifresolutionLevel == 0 --> 0 - LL) <br>
	 * &nbsp; yBlock: block row in the subband <br>
	 * &nbsp; xBlock: block column in the subband <br>
	 * <p>
	 * Only positive values allowed (0 value is possible too. If 0 --> block has not empty/0 bit planes).
	 */
	private int[][][][][][] mostBitPlanesNull = null;
	
	/**
	 * First layer in which the block is included. Indices means: <br>
	 * &nbsp; z: image component <br>
	 * &nbsp; resolutionLevel: 0 is the LL subband, and 1, 2, ... represents next starting with the little one <br>
	 * &nbsp; precinct: precinct in the resolution level <br>
	 * &nbsp; subband: 0 - HL, 1 - LH, 2 - HH (ifresolutionLevel == 0 --> 0 - LL) <br>
	 * &nbsp; yBlock: block row in the subband <br>
	 * &nbsp; xBlock: block column in the subband <br>
	 * <p>
	 * Only positive values allowed (0 value is possible too. If 0 --> block is included in first quality layer).
	 */
	private int[][][][][][] firstLayer = null;
	
	/**
	 * 
	 */
	private PacketInputStream packetInputStream;
	
	/**
	 * Indicates whether the Start of Packet marker is in the codestream and it
	 * must be read.
	 */
	private boolean sop = false; 
	
	/**
	 * Indicates whether the End of Packet marker is in the codestream and it
	 * must be read.
	 */
	private boolean eph = false;
	
	
	// INTERNAL VARIABLES
	
	/**
	 * Tag Tree where is the first layer which a packet is included
	 */
	private TagTreeDecoder[][][][] TTInclusionInformation = null;
	
	/**
	 * Tag Tree with the number of missing most significant bit planes for each codeblock
	 */
	private TagTreeDecoder[][][][] TTZeroBitPlanes = null;
		
	/**
	 * Code-block state variable.
	 * <p>
	 * Value 0 means that the packet has not been incluyed in any layer
	 */
	private int [][][][][][] lBlock = null;
	
	/**
	 * 
	 */
	int Nsop = 0;
	int NsopInHeader;
	int NsopInHeader_BITS = 16;
	
	
	/**************************************************************************/
	/**                     PRINCIPAL METHODS                                **/ 
	/**************************************************************************/
	
    public PacketDeheading()
    {        
    }
    
    
	/**
	 * Constructor
	 * @param mostBitPlanesNull definition in {@link BOI.BOICoder.FileWrite.PrecinctBuild#PBMostBitPlanesNull}
	 * @param firstLayer definition in {@link BOI.BOICoder.FileWrite.PrecinctBuild#PBFirstLayer}
	 * @param sop
	 * @param eph
	 *
	 * @throws ErrorException when the PBMostBitPlanesNull or PBFirstLayer structures are incorrect
	 */
	public PacketDeheading(int[][][][][][] mostBitPlanesNull, int  [][][][][][] firstLayer, boolean sop, boolean eph) {
		this.mostBitPlanesNull = mostBitPlanesNull;
		this.firstLayer = firstLayer;
		this.sop = sop;
		this.eph = eph;
		
		//PHDataInputStream = new PacketInputStream(null);
		Nsop = 0;
		
		// Initialise  TTInclusionInformation, TTZeroBitPlanes, lBlock
		InitialiseVariables();
	}
	
	/**
	 * Sets the internal attributes (states) to its initial values.
	 * 
	 * @throws ErrorException when the internal attributes cannot be reseted.
	 */
	public void reset() {
		for (int z = 0; z < firstLayer.length; z++) {
			for (int rLevel = 0; rLevel < firstLayer[z].length; rLevel++) {
				for (int precinct = 0; precinct < firstLayer[z][rLevel].length; precinct++) {
					for (int subband = 0; subband < firstLayer[z][rLevel][precinct].length; subband++) {
						if(firstLayer[z][rLevel][precinct][subband].length > 0){
							if(firstLayer[z][rLevel][precinct][subband][0].length > 0){
								//int numYBlocks = PBFirstLayer[z][rLevel][precinct][subband].length;
								//int numXBlocks = PBFirstLayer[z][rLevel][precinct][subband][0].length;
								//TTInclusionInformation[z][rLevel][precinct][subband] = new TagTreeDecoder(numYBlocks, numXBlocks);
								//TTZeroBitPlanes[z][rLevel][precinct][subband] = new TagTreeDecoder(numYBlocks, numXBlocks);
								TTInclusionInformation[z][rLevel][precinct][subband].reset();
								TTZeroBitPlanes[z][rLevel][precinct][subband].reset();
								for (int yBlock = 0; yBlock < firstLayer[z][rLevel][precinct][subband].length; yBlock++) {
									for (int xBlock = 0; xBlock < firstLayer[z][rLevel][precinct][subband][yBlock].length; xBlock++) {
										lBlock[z][rLevel][precinct][subband][yBlock][xBlock] = 0;
									}
								}
							}
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * Decode  the packet header for a precinct of a given layer, z, rLevel, precinct
	 * @param fileRead     FileRead class that contains the getTagBit function
	 * @param getTagBit    Function contained in the FileRead class to read bit by bit the file.
	 * @param layer        Layer
	 * @param z            Component
	 * @param rLevel       Resolution level
	 * @param precinct     Precinct
	 * @return             A vector which will contain the data length for a given subband, yBlock, xBlock and coding pass
	 *                     The index are: [subband][yBlock][xBlock][codingPasses] = data length
	 *
	 * @throws ErrorException when the packet header is corrupted
	 */
	public int[][][][] packetHeaderDecoding(PacketInputStream packetInputStream, int layer, int z, int rLevel, int precinct) throws IOException
    {
		this.packetInputStream = packetInputStream;
        packetInputStream.resetGetTagBit();
		
		int subband, yBlock, xBlock, lblock, codingPasses, wmsb, wmsb_temp;
		int [][][][] precinctData = null;
		boolean includeBlock=true;
		
		// Read Start of Packet Marker
		if( sop ) {
			readSOP(packetInputStream);
		}
		
		//System.out.println("PACKET HEADER DECODING");
		//System.out.println("Layer: "+ layer+" Component: "+z+" Resol. Level: "+rLevel+" Precinct: "+precinct);
		
		//System.out.println("Decodificando cabecera...");
		
		//System.out.print("Packet header:");
		// Zero/non-zero packet length
		
		if (getBit() == 0) {
			// Faltaria actualizar las variables de salida
			//System.out.println(" Packet empty");
			return (precinctData);
		}
		//System.out.println(" Packet non empty");
		
		precinctData = new int[firstLayer[z][rLevel][precinct].length][][][];
		// Loop subbands in resolution level
		for (subband = 0; subband < firstLayer[z][rLevel][precinct].length; subband++) {
			//System.out.println("--> Subband: " + subband);
			precinctData[subband] = new int [firstLayer[z][rLevel][precinct][subband].length][][];			
			for (yBlock = 0; yBlock < firstLayer[z][rLevel][precinct][subband].length; yBlock++) {
				precinctData[subband][yBlock] = new int [firstLayer[z][rLevel][precinct][subband][yBlock].length][];
				//System.out.println("--> yBlock="+ yBlock);
				for (xBlock = 0; xBlock < firstLayer[z][rLevel][precinct][subband][yBlock].length; xBlock++) {
					//System.out.println("--> xBlock="+ xBlock);
					//System.out.println("Layer: "+ layer+" Component: "+z+" Resol. Level: "+rLevel+" Precinct: "+precinct + " Subband: "+subband + " yBlock: " + yBlock+" xBlock: " +xBlock);
					
					includeBlock = true;
					// Code-block inclusion information
					//System.out.print("Inclusion Information:");
					
					// First inclusion of the code block
					if (lBlock[z][rLevel][precinct][subband][yBlock][xBlock] == 0) {
						//System.out.println(TTInclusionInformation[z][rLevel][precinct][subband].toString());
						firstLayer[z][rLevel][precinct][subband][yBlock][xBlock] =
							TTInclusionInformation[z][rLevel][precinct][subband].Decoder(layer + 1, yBlock, xBlock, packetInputStream);
						//System.out.print("Tag Tree");
						includeBlock = false;
						//System.out.println("First Layer=" + PBFirstLayer[z][rLevel][precinct][subband][yBlock][xBlock]);
						if (firstLayer[z][rLevel][precinct][subband][yBlock][xBlock] <= layer ) {
							
							if (firstLayer[z][rLevel][precinct][subband][yBlock][xBlock] == layer) {
								//System.out.println(" --> Se incluye en este paquete por primera vez");
								lBlock[z][rLevel][precinct][subband][yBlock][xBlock]=3;
								
								wmsb=wmsb_temp=1;
								while (wmsb >= wmsb_temp) {
									wmsb = TTZeroBitPlanes[z][rLevel][precinct][subband].Decoder(++wmsb_temp, yBlock, xBlock, packetInputStream);
								}
								mostBitPlanesNull[z][rLevel][precinct][subband][yBlock][xBlock] = wmsb;
								//System.out.println("z="+z+" rLevel="+ rLevel+" precinct="+precinct+" subband="+subband+" yBlock="+yBlock+" xBlock="+xBlock);
								//System.out.println("Bit Planes Null: "+wmsb);
								includeBlock = true;
							}
						}
						else {
							includeBlock = false;
						}
					}
					else if (getBit() == 0) {
						//System.out.println("No se incluye en este layer: ");
						includeBlock = false;
					}
					
					if ( includeBlock ) {
						//System.out.println("-> Se incluye en este layer: ");
						
						// Decode number of coding passes
						codingPasses = DecodeCodingPasses();
						precinctData[subband][yBlock][xBlock] = new int [codingPasses];
						//System.out.println("Coding Passes: " + codingPasses);
						
						// Decode LBlock
						lblock = DecodeLblock();
						lBlock[z][rLevel][precinct][subband][yBlock][xBlock] += lblock;
						//System.out.println("Lblock: " + lBlock[z][rLevel][precinct][subband][yBlock][xBlock]);
						//System.out.println("z="+z+" rLevel="+rLevel+" precinct="+precinct+" subband="+subband+" yBlock="+yBlock+" xBlock="+xBlock+" ->LBlock= " +lBlock[z][rLevel][precinct][subband][yBlock][xBlock]);
						
						// Decode length of codeword segment
						precinctData[subband][yBlock][xBlock] = DecodeLengths(lBlock[z][rLevel][precinct][subband][yBlock][xBlock], codingPasses);
						
						//for (int i=0; i< precinctData[subband][yBlock][xBlock].length; i++) System.out.println("Coding Pass: "+ i+" ->Length Encoding: " +precinctData[subband][yBlock][xBlock][i]);
						//for (int i=0; i< precinctData[subband][yBlock][xBlock].length; i++) {
						//System.out.println("z="+z+" rLevel="+rLevel+" precinct="+precinct+" subband="+subband+" yBlock="+yBlock+" xBlock="+xBlock+" CP="+ i+" ->Length Encoding: " +precinctData[subband][yBlock][xBlock][i]);
						//}
						
						//System.out.println();
					}
				} // xBlock
			} // yBlock
		} // Subband
		
		
		//	Read End of Packet Marker
		if( eph ){
			readEPH(packetInputStream);
		}
		
		return (precinctData);
	}
	
	/**
	 * @return PBMostBitPlanesNull definition in {@link BOI.BOICoder.FileWrite.PrecinctBuild#PBMostBitPlanesNull}
	 */
	public int [][][][][][] getBitPlanesNull(){
		return mostBitPlanesNull;
	}
	
	/**
	 * @return PBFirstLayer definition in {@link BOI.BOICoder.FileWrite.PrecinctBuild#PBFirstLayer}
	 */
	public int [][][][][][] getFirstLayer(){
		return firstLayer;
	}
	
	
	/**************************************************************************/
	/**                     AUXILIARY METHODS                                **/ 
	/**************************************************************************/
	
	/**
	 * Initilise the variables: TTInclusionInformation, TTZeroBitPlanes, lBlock
	 *
	 * @throws ErrorException when the PBMostBitPlanesNull or PBFirstLayer structures are incorrect
	 */
	private void InitialiseVariables() {
		
		int z, rLevel, precinct, subband, yBlock, xBlock;
		int numYBlocks, numXBlocks;
		
		TTInclusionInformation = new TagTreeDecoder[firstLayer.length][][][];
		TTZeroBitPlanes = new TagTreeDecoder[firstLayer.length][][][];
		lBlock = new int[firstLayer.length][][][][][];
		
		for (z = 0; z < firstLayer.length; z++) {
			TTInclusionInformation[z] = new TagTreeDecoder[firstLayer[z].length][][];
			TTZeroBitPlanes[z] = new TagTreeDecoder[firstLayer[z].length][][];
			lBlock[z] = new int[firstLayer[z].length][][][][];
			for (rLevel = 0; rLevel < firstLayer[z].length; rLevel++) {
				TTInclusionInformation[z][rLevel] = new TagTreeDecoder[firstLayer[z][rLevel].length][];
				TTZeroBitPlanes[z][rLevel] = new TagTreeDecoder[firstLayer[z][rLevel].length][];
				lBlock[z][rLevel] = new int[firstLayer[z][rLevel].length][][][];
				for (precinct = 0; precinct < firstLayer[z][rLevel].length; precinct++) {
					TTInclusionInformation[z][rLevel][precinct] = new TagTreeDecoder[firstLayer[z][rLevel][precinct].length];
					TTZeroBitPlanes[z][rLevel][precinct] = new TagTreeDecoder[firstLayer[z][rLevel][precinct].length];
					lBlock[z][rLevel][precinct] = new int [firstLayer[z][rLevel][precinct].length][][];
					for (subband = 0; subband < firstLayer[z][rLevel][precinct].length; subband++) {
						lBlock[z][rLevel][precinct][subband] = new int [firstLayer[z][rLevel][precinct][subband].length][];
						if(firstLayer[z][rLevel][precinct][subband].length > 0){
							if(firstLayer[z][rLevel][precinct][subband][0].length > 0){
								numYBlocks = firstLayer[z][rLevel][precinct][subband].length;
								numXBlocks = firstLayer[z][rLevel][precinct][subband][0].length;
								TTInclusionInformation[z][rLevel][precinct][subband] = new TagTreeDecoder(numYBlocks, numXBlocks);
								TTZeroBitPlanes[z][rLevel][precinct][subband] = new TagTreeDecoder(numYBlocks, numXBlocks);
								for (yBlock = 0; yBlock < firstLayer[z][rLevel][precinct][subband].length; yBlock++) {
									lBlock[z][rLevel][precinct][subband][yBlock] = new int [firstLayer[z][rLevel][precinct][subband][yBlock].length];
									for (xBlock = 0; xBlock < firstLayer[z][rLevel][precinct][subband][yBlock].length; xBlock++) {
										lBlock[z][rLevel][precinct][subband][yBlock][xBlock] = 0;
									}
								}
							}
						}
					}
				}
			}
		}
		
	} // End of InitialiseVariables
	
	/**
	 * Decodify the Lblock value
	 *
	 * @return lblock value
	 *
	 * @throws ErrorException when the packet header is corrupted
	 */
	private int DecodeLblock() throws IOException {
		
		int LblockValue = 0;

		while (getBit() == 1) {
			LblockValue++;
		}

		return (LblockValue);
	}	
	
	/**
	 * Decodify the length of codeword segment
	 *
	 * @param lblock        current value
	 * @param codingPasses  coding passes number of the codeblock
	 * @return              length of code-block
	 *
	 * @throws ErrorException when the packet header is corrupted
	 */
	private int[] DecodeLengths(int lblock, int codingPasses) throws IOException {
		
		int numBits;
		int codingPassesAdded=1;	// OBS: codingPassesAdded=1 because codeword segment is terminated at each coding pass
		int []lengthInformation = new int[codingPasses];
	
		for (int cp=0; cp<codingPasses; cp++ ) {
			lengthInformation[cp]=0;
			numBits = lblock + (int) Math.floor(Math.log(codingPassesAdded) / Math.log(2D));
			for (int nb = numBits - 1; nb >= 0; nb--) {
				lengthInformation[cp] += (1 << nb) * getBit();
			}
		}
			
		return (lengthInformation);
	}
	
	/**
	 * Decodify the coding passes
	 *
	 * @return coding passes
	 *
	 * @throws ErrorException when the packet header is corrupted
	 */
	private int DecodeCodingPasses() throws IOException {
		
		int codingPasses = 0;
		
		if (getBit() == 0) {
			codingPasses=1;
		}
		else {
			if (getBit() == 0) {
				codingPasses=2;
			}
			else {

				// Will be more than 2
				if (getBit() == 0) {

					if (getBit() == 0) {
						codingPasses=3;
					}
					else {
						codingPasses=4;
					}
				}
				else {

					if (getBit() == 0){
						codingPasses=5;
					}
					else {
						// Will be more than 5
						for (int i = 4; i >= 0; i--) {							
							codingPasses += (1 << i) * getBit();
						}
						if (codingPasses <= 30) {
							codingPasses += 6;
						}
						else {
							// Will be more than 36
							codingPasses = 0;
							for (int i = 6; i >= 0; i--) {
								codingPasses += (1 << i) * getBit();
							}
							codingPasses += 37;
						}
					}
				}
			}
		}
		
		return (codingPasses);
	}
	
	/**
	 * To position the file pointer just before the first PacketHeader
	 *
	 * @throws ErrorException when the file cannot be load
	 */
	private void readSOP(DataInputStream PHDataInputStream) throws IOException {
		boolean markerFound = false;
		boolean SOPMarkerFound = false;
	
		// Read SOP marker
		if(Nsop == 0){
			while(!SOPMarkerFound){
				while(!markerFound){
					markerFound = (PHDataInputStream.read() == (int)0xFF);
				}
				markerFound = false;
				SOPMarkerFound = (PHDataInputStream.read() == (int)0x91);
			}
			SOPMarkerFound = false;
		}else{
			markerFound = (PHDataInputStream.read() == (int)0xFF);
			SOPMarkerFound = (PHDataInputStream.read() == (int)0x91);
			if((markerFound == false) || (SOPMarkerFound == false)){
				throw new IOException("Error reading CodeStream, expected SOP and it's not found.");
			}
		}
		// Read Lsop marker
		if((PHDataInputStream.read() == (int)0x00) == false){
			throw new IOException("Error reading CodeStream, expected SOP and it's not found.");
		}
		if((PHDataInputStream.read() == (int)0x04) == false){
			throw new IOException("Error reading CodeStream, expected SOP and it's not found.");
		}
		// Read Nsop marker
		NsopInHeader = (PHDataInputStream.read()<<8) | PHDataInputStream.read();
		if(NsopInHeader != Nsop){
			throw new IOException("Error reading CodeStream, expected SOP and it's not found.");
		}
		Nsop = Nsop==(int)0xFFFF ? 0: ++Nsop ;

	}
		
	/**
	 * To read EPH marker if it is used
	 *
	 * @throws ErrorException when the file cannot be load
	 */
	private void readEPH(DataInputStream PHDataInputStream) throws IOException {
	
		if((PHDataInputStream.read() == (int)0xFF) == false){
			throw new IOException("Error reading CodeStream, expected EPH and it's not found.");
		}
		if((PHDataInputStream.read() == (int)0x92) == false){
			throw new IOException("Error reading CodeStream, expected EPH and it's not found.");
		}
	}
	
	/**
	 * Returns the bit readed from the file.
	 *
	 * @return an integer that represents the bit readed from the file
	 */
	private int getBit() throws IOException {
		return packetInputStream.getTagBit();
	}
			
}