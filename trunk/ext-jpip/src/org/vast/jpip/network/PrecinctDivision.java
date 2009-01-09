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

/**
 *
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; run<br>
 * &nbsp; [get functions]<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0.1 2008/01/28
 */
public class PrecinctDivision {

	/**
	 * Definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#zSize}
	 */
	private int zSize;

	/**
	 * Definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#ySize}
	 */
	private int ySize;

	/**
	 * Definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#xSize}
	 */
	private int xSize;

	/**
	 * Definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#WTLevels}
	 */
	private int[] WTLevels;

	/**
	 * Definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#BDBlockWidths}
	 */
	private int[] blockWidths;

	/**
	 * Definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#BDBlockHeights}
	 */
	private int[] blockHeights;

	/**
	 * Precinct width in the transformed domain for each component and resolution level. Index means [z][rLevel] (rLevel==0 is the LL subband, and 1, 2, ... represents next starting with the little one).
	 * <p>
	 * Values equal or greater than BDBlockWidth.
	 */
	private int[][] resolutionPrecinctWidths;

	/**
	 * Same as BDResolutionPrecinctWidths but for precinct heights.
	 * <p>
	 * Values equal or greater than BDBlockHeight.
	 */
	private int[][] resolutionPrecinctHeights;

	/**
	 * 	 * Number of blocks in each precinct for resolution level and component (width dimension). Index means [z][rLevel]. If the number of blocks does not fit with precincts, take upper left corner in the subband to start. For example, if a subband have 3x3 blocks:<br>
	 * -------------------<br>
	 * |  1  |  2  |  3  |<br>
	 * |-----------------|<br>
	 * |  4  |  5  |  6  |<br>
	 * |-----------------|<br>
	 * |  7  |  8  |  9  |<br>
	 * -------------------<br>
	 * and BDBlocksPerPrecinctWidths[z][rLevel]==2 then precincts will contain these blocks: {1,2,4,5},{3,6},{7,8},{9}.
	 * <p>
	 * Values greater than 1.
	 */
	private int[][] blocksPerPrecinctWidths = null;

	/**
	 * Same as BDResolutionPrecinctWidths but for precinct heights.
	 * <p>
	 * Values greater than 1.
	 */
	private int[][] blocksPerPrecinctHeights = null;

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
	 * Divides the image into precincts.
	 *
	 * @param zSize definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#zSize}
	 * @param ySize definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#ySize}
	 * @param xSize definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#xSize}
	 * @param WTLevels definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#WTLevels}
	 * @param blockWidths definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#BDBlockWidths}
	 * @param blockHeights definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#BDBlockHeights}
	 * @param resolutionPrecinctWidths definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#BDResolutionPrecinctWidths}
	 * @param resolutionPrecinctHeights definition in {@link CADI.Common.LogicalTarget.JPEG2000.JPCParameters#BDResolutionPrecinctHeights}
	 */
	public PrecinctDivision(
							int zSize,
							int ySize,
							int xSize,
							int[] WTLevels,
							int[] blockWidths,
							int[] blockHeights,
							int[][] resolutionPrecinctWidths,
							int[][] resolutionPrecinctHeights
							){
								//Data copy
								this.zSize = zSize;
								this.ySize = ySize;
								this.xSize = xSize;
								this.WTLevels = WTLevels;
								this.blockWidths = blockWidths;
								this.blockHeights = blockHeights;
								this.resolutionPrecinctWidths = resolutionPrecinctWidths;
								this.resolutionPrecinctHeights = resolutionPrecinctHeights;
							}

	/**
	 * Builds the layers.
	 */
	public void run(){

		//CONSTRUCTION OF PBByteStreamsLayers, PBMostBitPlanesNull, PBFirstLayer
		blocksPerPrecinctWidths = new int[zSize][];
		blocksPerPrecinctHeights = new int[zSize][];
		
		mostBitPlanesNull = new int[zSize][][][][][];
		firstLayer = new int[zSize][][][][][];
		for(int z = 0; z < zSize; z++){
			//Block sizes (updating with subband sizes - BDBlockWidths/BDBlockHeights is not updated)
			int blockWidth = blockWidths[z];
			int blockHeight = blockHeights[z];

			//Set level sizes
			int xSubbandSize = xSize;
			int ySubbandSize = ySize;

			//Memory allocation (resolution levels)
			mostBitPlanesNull[z] = new int[WTLevels[z]+1][][][][];
			firstLayer[z] = new int[WTLevels[z]+1][][][][];
			blocksPerPrecinctWidths[z] = new int[WTLevels[z]+1];
			blocksPerPrecinctHeights[z] = new int[WTLevels[z]+1];

			//Block division for each WT level
			for(int rLevel = WTLevels[z]; rLevel > 0; rLevel--){
				//Size setting for the level
				int xOdd = xSubbandSize % 2;
				int yOdd = ySubbandSize % 2;
				xSubbandSize = xSubbandSize / 2 + xOdd;
				ySubbandSize = ySubbandSize / 2 + yOdd;

				//Block sizes (reduced if subband is enough greater)
				if(Math.pow(2, blockWidth-1) >= xSubbandSize){
					blockWidth--;
				}
				if(Math.pow(2, blockHeight-1) >= ySubbandSize){
					blockHeight--;
				}

				//Estimated block width and height
				int width = (int) Math.pow(2, blockWidth);
				int height = (int) Math.pow(2, blockHeight);

				// LL HL
				// LH HH
				//HL, LH, HH subband
				int[] xBegin = {xSubbandSize, 0, xSubbandSize};
				int[] yBegin = {0, ySubbandSize, ySubbandSize};
				int[] xEnd = {xSubbandSize*2 - xOdd, xSubbandSize, xSubbandSize*2 - xOdd};
				int[] yEnd = {ySubbandSize, ySubbandSize*2 - yOdd, ySubbandSize*2 - yOdd};

				//Maximum number of blocks for this resolution level
				int maxWidth = xEnd[0] - xBegin[0];
				for(int subband = 1; subband < 3; subband++){
					if(maxWidth < xEnd[subband] - xBegin[subband]){
						maxWidth = xEnd[subband] - xBegin[subband];
					}
				}
				int xMaxNumBlocks = (int) Math.ceil(maxWidth / (float) width);
				int maxHeight = yEnd[0] - yBegin[0];
				for(int subband = 1; subband < 3; subband++){
					if(maxHeight < yEnd[subband] - yBegin[subband]){
						maxHeight = yEnd[subband] - yBegin[subband];
					}
				}
				int yMaxNumBlocks = (int) Math.ceil(maxHeight / (float) height);

				//Precinct sizes
				int precinctWidth = resolutionPrecinctWidths[z][rLevel];
				int precinctHeight = resolutionPrecinctHeights[z][rLevel];
				blocksPerPrecinctWidths[z][rLevel] = (int) Math.ceil(Math.pow(2, precinctWidth - blockWidth));
				blocksPerPrecinctHeights[z][rLevel] = (int) Math.ceil(Math.pow(2, precinctHeight - blockHeight));

				//Number of precincts for each resolution level
				int numPrecinctsWidth = (int) Math.ceil(((double) xMaxNumBlocks) / ((double) blocksPerPrecinctWidths[z][rLevel]));
				int numPrecinctsHeight = (int) Math.ceil(((double) yMaxNumBlocks) / ((double) blocksPerPrecinctHeights[z][rLevel]));
				int numPrecincts = numPrecinctsHeight * numPrecinctsWidth;

				//Memory allocation (precincts and subbands)
				mostBitPlanesNull[z][rLevel] = new int[numPrecincts][3][][];
				firstLayer[z][rLevel] = new int[numPrecincts][3][][];

				for(int precinct = 0; precinct < numPrecincts; precinct++){
				for(int subband = 0; subband < 3; subband++){

					//Number of blocks for this subband
					int xNumBlocks = (int) Math.ceil((xEnd[subband] - xBegin[subband]) / (float) width);
					int yNumBlocks = (int) Math.ceil((yEnd[subband] - yBegin[subband]) / (float) height);

					//Number of blocks for each precinct
					int numBlocksWidth = 1;
					if(blocksPerPrecinctWidths[z][rLevel] > 0){
						if(numPrecinctsWidth == 1){
							if(xNumBlocks % blocksPerPrecinctWidths[z][rLevel] != 0){
								numBlocksWidth = xNumBlocks % blocksPerPrecinctWidths[z][rLevel];
							}else{
								numBlocksWidth = blocksPerPrecinctWidths[z][rLevel];
							}
						}else{
							if((precinct+1) % numPrecinctsWidth == 0){
								if(xMaxNumBlocks == xNumBlocks){
									if(xNumBlocks % blocksPerPrecinctWidths[z][rLevel] != 0){
										numBlocksWidth = xNumBlocks % blocksPerPrecinctWidths[z][rLevel];
									}else{
										numBlocksWidth = blocksPerPrecinctWidths[z][rLevel];
									}
								}else{
									//Special cases when the precinct has 0 blocks in this subband but someone in the other subbands
									numBlocksWidth = 0;
								}
							}else{
								numBlocksWidth = blocksPerPrecinctWidths[z][rLevel];
							}
						}
					}

					int numBlocksHeight = 1;
					if(blocksPerPrecinctHeights[z][rLevel] > 0){
						if(numPrecinctsHeight == 1){
							if(yNumBlocks % blocksPerPrecinctHeights[z][rLevel] != 0){
								numBlocksHeight = yNumBlocks % blocksPerPrecinctHeights[z][rLevel];
							}else{
								numBlocksHeight = blocksPerPrecinctHeights[z][rLevel];
							}
						}else{
							if(precinct >= numPrecinctsWidth * (numPrecinctsHeight-1)){
								if(yMaxNumBlocks == yNumBlocks){
									if(yNumBlocks % blocksPerPrecinctHeights[z][rLevel] != 0){
										numBlocksHeight = yNumBlocks % blocksPerPrecinctHeights[z][rLevel];
									}else{
										numBlocksHeight = blocksPerPrecinctHeights[z][rLevel];
									}
								}else{
									//Special cases when the precinct has 0 blocks in this subband but someone in the other subbands
									numBlocksHeight = 0;
								}
							}else{
								numBlocksHeight = blocksPerPrecinctHeights[z][rLevel];
							}
						}
					}

					//To be coherent - only checking
					if(numBlocksWidth == 0){
						numBlocksHeight = 0;
					}else{
						if(numBlocksHeight == 0){
							numBlocksWidth = 0;
						}
					}

					//Memory allocation (num blocks)
					mostBitPlanesNull[z][rLevel][precinct][subband] = new int[numBlocksHeight][numBlocksWidth];
					firstLayer[z][rLevel][precinct][subband] = new int[numBlocksHeight][numBlocksWidth];
				}}
			}

			//LL SUBBAND

			//Estimated block width and height
			int width = (int) Math.pow(2, blockWidth);
			int height = (int) Math.pow(2, blockHeight);

			//Number of blocks set
			int xNumBlocks = (int) Math.ceil(xSubbandSize / (float) width);
			int yNumBlocks = (int) Math.ceil(ySubbandSize / (float) height);

			//Precinct sizes
			int precinctWidth = resolutionPrecinctWidths[z][0];
			int precinctHeight = resolutionPrecinctHeights[z][0];
			blocksPerPrecinctWidths[z][0] = (int) Math.ceil(Math.pow(2, precinctWidth - blockWidth));
			blocksPerPrecinctHeights[z][0] = (int) Math.ceil(Math.pow(2, precinctHeight - blockHeight));

			//Number of precincts for each resolution level
			int numPrecinctsWidth = (int) Math.ceil(((double) xNumBlocks) / ((double) blocksPerPrecinctWidths[z][0]));
			int numPrecinctsHeight = (int) Math.ceil(((double) yNumBlocks) / ((double) blocksPerPrecinctHeights[z][0]));
			int numPrecincts = numPrecinctsHeight * numPrecinctsWidth;

			//Memory allocation (precincts and subbands)
			mostBitPlanesNull[z][0] = new int[numPrecincts][1][][];
			firstLayer[z][0] = new int[numPrecincts][1][][];

			for(int precinct = 0; precinct < numPrecincts; precinct++){

				//Number of blocks for each precinct
				int numBlocksWidth = 1;
				if(blocksPerPrecinctWidths[z][0] > 0){
					if(((precinct+1) % numPrecinctsWidth == 0) && (xNumBlocks % blocksPerPrecinctWidths[z][0] != 0)){
						numBlocksWidth = xNumBlocks % blocksPerPrecinctWidths[z][0];
					}else{
						numBlocksWidth = blocksPerPrecinctWidths[z][0];
					}
				}

				int numBlocksHeight = 1;
				if(blocksPerPrecinctHeights[z][0] > 0){
					if((precinct >= numPrecinctsWidth * (numPrecinctsHeight-1)) && (yNumBlocks % blocksPerPrecinctHeights[z][0] != 0)){
						numBlocksHeight = yNumBlocks % blocksPerPrecinctHeights[z][0];
					}else{
						 numBlocksHeight = blocksPerPrecinctHeights[z][0];
					}
				}

				//Memory allocation (precincts and subbands)
				mostBitPlanesNull[z][0][precinct][0] = new int[numBlocksHeight][numBlocksWidth];
				firstLayer[z][0][precinct][0] = new int[numBlocksHeight][numBlocksWidth];
			}
		}
	}

	/**
	 * @return PBMostBitPlanesNull definition in {@link #mostBitPlanesNull}
	 */
	public int[][][][][][] getMostBitPlanesNull(){
		return(mostBitPlanesNull);
	}

	/**
	 * @return PBFirstLayer definition in {@link #firstLayer}
	 */
	public int[][][][][][] getFirstLayer(){
		return(firstLayer);
	}

	/**
	 * @return BDBlocksPerPrecinctWidths definition in {@link #blocksPerPrecinctWidths}
	 */
	public int[][] getBlocksPerPrecinctWidths(){
		return(blocksPerPrecinctWidths);
	}

	/**
	 * @return BDBlocksPerPrecinctHeights definition in {@link #blocksPerPrecinctHeights}
	 */
	public int[][] getBlocksPerPrecinctHeights(){
		return(blocksPerPrecinctHeights);
	}

	/**
	 * For debugging purpose.
	 */
	public String toString() {
		
		String str = "";
		
		str += "PB MOST BIT PLANES NULL\n";
		for (int z=0; z < mostBitPlanesNull.length; z++) {
			for (int rLevel=0; rLevel<mostBitPlanesNull[z].length; rLevel++) {
					for (int precinct=0; precinct<mostBitPlanesNull[z][rLevel].length; precinct++) {
						for (int subband=0;	subband<mostBitPlanesNull[z][rLevel][precinct].length; subband++) {
						for (int yBlock=0; yBlock<mostBitPlanesNull[z][rLevel][precinct][subband].length; yBlock++) {
							for (int xBlock=0; xBlock<mostBitPlanesNull[z][rLevel][precinct][subband][yBlock].length; xBlock++) {
								str += "z="+z +" rLevel="+rLevel+" precinct="+precinct+" subband="+subband+" yBlock="+yBlock+" xBlock="+xBlock+" PBMostBitPlanesNull=" +mostBitPlanesNull[z][rLevel][precinct][subband][yBlock][xBlock] + "\n";					
							}
						}
					}
				}
			}
		}
		
		str += "PB FIRST LAYER\n";
		for (int z=0; z < firstLayer.length; z++) {
			for (int rLevel=0; rLevel<firstLayer[z].length; rLevel++) {
					for (int precinct=0; precinct<firstLayer[z][rLevel].length; precinct++) {
						for (int subband=0;	subband<firstLayer[z][rLevel][precinct].length; subband++) {
						for (int yBlock=0; yBlock<firstLayer[z][rLevel][precinct][subband].length; yBlock++) {
							for (int xBlock=0; xBlock<firstLayer[z][rLevel][precinct][subband][yBlock].length; xBlock++) {
								str += "z="+z +" rLevel="+rLevel+" precinct="+precinct+" subband="+subband+" yBlock="+yBlock+" xBlock="+xBlock+" PBFirstLayer=" +firstLayer[z][rLevel][precinct][subband][yBlock][xBlock] + "\n";					
							}
						}
					}
				}
			}
		}
		
		
		return str;
	}
	
}