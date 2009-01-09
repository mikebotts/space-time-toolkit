/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
 Alexandre Robin <robin@nsstc.uah.edu>
 
 ******************************* END LICENSE BLOCK ***************************/

package org.vast.jpip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.vast.jpip.message.JPIPMessage;
import org.vast.jpip.message.JPIPRequestFields;
import jj2000.j2k.codestream.HeaderInfo;
import jj2000.j2k.codestream.Markers;
import jj2000.j2k.codestream.ProgressionType;
import jj2000.j2k.codestream.reader.BitstreamReaderAgent;
import jj2000.j2k.codestream.reader.CBlkInfo;
import jj2000.j2k.codestream.reader.HeaderDecoder;
import jj2000.j2k.decoder.DecoderSpecs;
import jj2000.j2k.entropy.StdEntropyCoderOptions;
import jj2000.j2k.entropy.decoder.DecLyrdCBlk;
import jj2000.j2k.io.RandomAccessIO;
import jj2000.j2k.quantization.dequantizer.StdDequantizerParams;
import jj2000.j2k.util.ArrayUtil;
import jj2000.j2k.util.ParameterList;
import jj2000.j2k.wavelet.synthesis.SubbandSyn;
import jj2000.j2k.wavelet.synthesis.SynWTFilter;


/**
 * <p><b>Title:</b>
 * JPIPBitstreamReader
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This class reads data from a JPIP bitstream after requesting it
 * from a server compliant with the JPIP protocol.
 * Code blocks can be retrieved through the CodedCBlkDataSrcDec
 * interface and thus consumed by the JJ2000 entropy decoder.
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Feb 22, 2008
 * @version 1.0
 */
public class JPIPBitstreamReader extends BitstreamReaderAgent implements Markers, ProgressionType, StdEntropyCoderOptions
{
    private static final int X = 0;
    private static final int Y = 1;

    protected JPIPHttpClient jpipClient;
    protected PktDecoder pktDec;
    protected Thread receivingThread;
    protected CBlkInfo[][][][][] ccbInfo; // [comp][res][subband][y][x]
    protected byte[][][][][][] ccbData; // [comp][res][subband][y][x][bytes]
    protected JPIPListener listener;
    protected boolean cancelRequest = false;

    
    public static JPIPBitstreamReader createJpipReader(String url, String target, ParameterList params, boolean useSessions) throws IOException
    {
        JPIPHttpClient jpipClient = new JPIPHttpClient(url, useSessions);
        RandomAccessIO in = jpipClient.connect(target);
        HeaderInfo hi = new HeaderInfo();
        HeaderDecoder hd = new HeaderDecoder(in, params, hi);
        DecoderSpecs decSpec = hd.getDecoderSpecs();
        System.out.println(hi.toStringMainHeader());

        JPIPBitstreamReader jpipReader = new JPIPBitstreamReader(jpipClient, hd, decSpec);
        return jpipReader;
    }
    
    
    public JPIPBitstreamReader(JPIPHttpClient jpipClient, HeaderDecoder hd, DecoderSpecs decSpec) throws IOException
    {
        super(hd, decSpec);
        this.jpipClient = jpipClient;
        this.pktDec = new PktDecoder(decSpec, hd, this, false, 0);
        this.ctX = -1;
        this.ctY = -1;
    }
    
    
    public void getMetadata()
    {
        
    }


    /**
     * Launch a thread to retrieve data for the specified portion of the image
     */
    public void requestData(final int[] fsiz, final int[] rsiz, final int[] roff, final int[][] comps, final int numLayer)
    {
        setTile(0, 0);
        //newRequest = true;
        
        // thread to receive and decode jpip messages
        receivingThread = new Thread("Decoding")
        {
            public void run()
            {
                try
                {
                	cancelRequest = false;
                	
                	JPIPRequestFields request = jpipClient.getLastRequest();
                    request.viewWindowField.fsiz = fsiz;
                    request.viewWindowField.rsiz = rsiz;
                    request.viewWindowField.roff = roff;
                    request.viewWindowField.comps = comps;
                    request.viewWindowField.layers = numLayer;                    
                    jpipClient.sendRequest();
                    jpipClient.readResponseHeader();

                    ByteArrayOutputStream buffer = new ByteArrayOutputStream(65536);
                    long inClassId = -1;
                    long previousId = -1;

                    // loop until all messages are read (EOR)
                    JPIPMessage jpipMessage = null;
                    do
                    {
                    	if (cancelRequest)
                        	break;
                    	
                    	jpipMessage = jpipClient.readNextMessage();
                        byte[] msgBody = jpipMessage.messageBody;

                        // skip messages w/o body
                        if (msgBody == null)
                            continue;

                        // skip non precinct databins messages
                        if (!(jpipMessage.header.classIdentifier == 0))
                            continue;

                        // assemble all messages w/ same inClassId
                        previousId = inClassId;
                        inClassId = jpipMessage.header.inClassIdentifier;
                        if (inClassId == previousId || previousId == -1)
                        {
                            buffer.write(msgBody);
                            previousId = inClassId;
                            continue;
                        }

                        // wrap byte array w/ PacketInputStream
                        byte[] precinctData = buffer.toByteArray();
                        ByteArrayInputStream is = new ByteArrayInputStream(precinctData);
                        System.out.println("\nReading Packet #" + (int) previousId + " - Total bytes: " + precinctData.length);

                        // convert packet index to tcrp
                        int[] tcrp = InClassIdentifierToTCRP(previousId);
                        int res = tcrp[2];
                        int comp = tcrp[1];
                        int precinct = tcrp[3];

                        System.out.println("c=" + comp + ", r=" + res + ", p=" + precinct);
                        
                        // read all packets for this precinct
                        boolean eof = false;
                        for (int layer = 0; layer < numLayer && !eof; layer++)
                        {
                            eof = pktDec.readSOPMarker(precinct, comp, res);
                            
                            if (!eof)
                                eof = pktDec.readPktHead(is, layer, res, comp, precinct, ccbInfo[comp][res], null);
                            
                            if (cancelRequest)
                            	break;
                            
                            if (!eof)
                                pktDec.readPktData(is, layer, res, comp, precinct, ccbInfo[comp][res], ccbData[comp][res], null);

                            if (cancelRequest)
                            	break;
                            
                            if (is.available() == 0)
                                break;
                        }
                        
                        if (cancelRequest)
                        	break;
                        
                        if (listener != null)
                            listener.precinctReceived(comp, res, pktDec.getPrecInfo(comp, res, precinct));

                        // now add message for next pass                      
                        buffer.reset();
                        buffer.write(msgBody);
                    }
                    while (!jpipMessage.header.isEOR);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                	// release the connection so it can be reused
                	jpipClient.releaseConnection();
                }
            }
        };

        receivingThread.run();//.start();
    }
    
    
    public void cancelRequest()
    {
    	this.cancelRequest = true;
    }


    /**
     * Converts the unique precinct identifier (inClassIdentifier) to a
     * tile-component-resolution level-precinct identifiers. The precinct
     * identifer corresponds to precinct number within tile-component-
     * resolution level.
     * <p>
     * Returns a integer array where the indexes are:
     * tile
     * component
     * resolution level
     * precinct  
     * 
     * @param inClassIdentifier
     * @param maxTiles
     * @param zSize
     * @param xSize
     * @param ySize
     * @param XOsize
     * @param YOsize
     * @param WTLevels
     * @param BDResolutionPrecinctWidths
     * @param BDResolutionPrecinctHeights
     * @return
     */
    protected int[] InClassIdentifierToTCRP(long inClassIdentifier)
    {
        int maxTiles = hd.getNumTiles();
        int zSize = hd.getNumComps();
        int xSize = hd.getImgWidth();
        int ySize = hd.getImgHeight();
        int XOsize = hd.getImgULX();
        int YOsize = hd.getImgULY();
        int[] WTLevels = new int[] { 5, 5, 5, 5 };
        //int[][] BDResolutionPrecinctWidths;
        //int[][] BDResolutionPrecinctHeights;

        int[] tcrp = { -1, -1, -1, -1 };

        int[] tcp = InClassIdentifierToTCP(inClassIdentifier, maxTiles, zSize);
        int tile = tcp[0];
        int component = tcp[1];
        int precinct = tcp[2];

        // Available codestream frame size  (ISO/IEC 15444-9 sect. C.4)
        int[][][] frameSizes = availableFrameSizes(xSize, ySize, XOsize, YOsize, WTLevels);

        int numPrecincts = 0;
        for (int rLevel = 0; rLevel <= WTLevels[component]; rLevel++)
        { // <<<<<<<<<< NOTICE: This part of the function can be improved >>>>>>>>>>          
            //System.out.println("\n\n\ntile: " + tile+ " Comp: " + component+ " Res. Level: " + rLevel);   // DEBUG                    

            // Number of precincts for each resolution level
            int[] precinctSize = new int[2];
            precinctSize[X] = hd.getPPX(tile, component, rLevel);//(1 << BDResolutionPrecinctWidths[component][rLevel]);
            precinctSize[Y] = hd.getPPY(tile, component, rLevel);//(1 << BDResolutionPrecinctHeights[component][rLevel]);
            // MOD ARO
            //            if (rLevel != 0)
            //            {
            //                precinctSize[X] *= 2;
            //                precinctSize[Y] *= 2;
            //            }
            //System.out.println("Precinct Sizes: " + precinctSize[X] + " x " + precinctSize[Y]); // DEBUG

            int numPrecinctsWidth = (int) Math.ceil(((double) frameSizes[component][rLevel][X]) / ((double) (precinctSize[X])));
            int numPrecinctsHeight = (int) Math.ceil(((double) frameSizes[component][rLevel][Y]) / ((double) (precinctSize[Y])));

            if ((numPrecincts + numPrecinctsHeight * numPrecinctsWidth) > precinct)
            {
                tcrp[0] = tile;
                tcrp[1] = component;
                tcrp[2] = rLevel;
                tcrp[3] = (int) (precinct - (long) numPrecincts);
                //System.out.println("CONVERSION tile: " + tile + " component: " + component + " resolution level: " + rLevel + " precinct: " + tcrp[3]); //DEBUG
                return tcrp;
            }
            else
            {
                numPrecincts += numPrecinctsHeight * numPrecinctsWidth;
            }
        }

        return tcrp;
    }


    /**
     * Converts the inClassIdentifier precinct identifier to tile, component and
     * precinct format.
     * 
     * @param numTiles the maximum number of image tiles
     * @param numComponents the maximum number of image components
     * @param inClassIdentifier the precinct unique identifer within the
     *  codestream 
     * @return
     */
    protected int[] InClassIdentifierToTCP(long inClassIdentifier, int numTiles, int numComponents)
    {
        int[] tcp = new int[3];

        tcp[0] = (int) (inClassIdentifier % numTiles);
        int temp = (int) ((inClassIdentifier - tcp[0]) / (double) numTiles);
        tcp[1] = temp % numComponents;
        tcp[2] = (int) ((temp - tcp[1]) / (double) numComponents);

        return tcp;
    }


    /**
     * Calculates the frame sizes for each image components and resolution level
     * according to the image parameters that are passed to the function.
     * 
     * @param xSize
     * @param ySize
     * @param XOsize
     * @param YOsize
     * @param WTLevels
     * @return frame size for each component. First index is the image component,
     *  second is the resolution level, and the third index will be 0 for the 
     *  frame size width and 1 for the frame size height. 
     */
    protected int[][][] availableFrameSizes(int xSize, int ySize, int XOsize, int YOsize, int[] WTLevels)
    {

        assert ((xSize > 0) && (ySize > 0));
        assert ((XOsize >= 0) && (YOsize >= 0));
        assert (WTLevels != null);

        int numComponents = WTLevels.length;

        // Available codestream frame size  (ISO/IEC 15444-9 sect. C4)
        // First index indicates the component, an the second indicates the resolution level

        int[][][] frameSizes = new int[numComponents][][];

        for (int c = 0; c < numComponents; c++)
        {
            frameSizes[c] = new int[WTLevels[c] + 1][2];
            int D = WTLevels[c];
            for (int r = 0; r < WTLevels[c] + 1; r++)
            {
                frameSizes[c][r][X] = (int) Math.ceil((float) xSize / (1 << (D - r))) - (int) Math.ceil((float) XOsize / (1 << (D - r)));
                frameSizes[c][r][Y] = (int) Math.ceil((float) ySize / (1 << (D - r))) - (int) Math.ceil((float) YOsize / (1 << (D - r)));
            }
        }

        //DEBUG     
        /*System.out.println("Available codestream frame size");
         for(int c=0; c<numComponents; c++){
         for(int rLevel=0; rLevel<=WTLevels[c];rLevel++){
         System.out.println("Comp:" + c +" rLevel:" + rLevel + " --> width=" + frameSizes[c][rLevel][X] + " height=" + frameSizes[c][rLevel][Y]);
         }
         }*/
        //END DEBUG
        return frameSizes;
    }


    public DecLyrdCBlk getCodeBlock(int c, int m, int n, SubbandSyn sb, int fl, int nl, DecLyrdCBlk ccb)
    {
        //System.out.println("\nRequesting Code Block:");
        //System.out.println("c=" + c + ", r=" + sb.resLvl + ", m=" + m + ", n=" + n + ", s=" + sb.sbandIdx + ", fl=" + fl + ", nl=" + nl);

        int r = sb.resLvl; // Resolution level
        int s = sb.sbandIdx; // Subband index
        int t = this.getTileIdx();

        // Number of layers
        int numLayers = ((Integer) decSpec.nls.getTileDef(t)).intValue();
        int options = ((Integer) decSpec.ecopts.getTileCompVal(t, c)).intValue();
        if (nl < 0)
            nl = numLayers - fl + 1;

        // hack to fix to 1 layer
        fl = 1;
        //nl = 5;
        
        //vCheck validity of all the arguments
        CBlkInfo rcb = null;
        try
        {
            rcb = ccbInfo[c][r][s][m][n];
            if (fl < 1 || fl > numLayers || fl + nl - 1 > numLayers)
                throw new IllegalArgumentException();
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException("Code-block (t:" + t + ", c:" + c + ", r:" + r + ", s:" + s + ", " + m + "x" + +n + ") not found in codestream");
        }
        catch (NullPointerException e)
        {
            throw new IllegalArgumentException("Code-block (t:" + t + ", c:" + c + ", r:" + r + ", s:" + s + ", " + m + "x" + n + ") not found in bit stream");
        }

        if (ccb == null)
            ccb = new DecLyrdCBlk();

        ccb.m = m;
        ccb.n = n;
        ccb.nl = 0;
        ccb.dl = 0;
        ccb.nTrunc = 0;
        ccb.data = ccbData[c][r][s][m][n];
        
        // if code-block was skipped when reading, return no data
        if (rcb == null || ccb.data == null || rcb.pktIdx[0] == PktDecoder.NOT_READY)
        {
            ccb.skipMSBP = 0;
            ccb.prog = false;
            ccb.w = ccb.h = ccb.ulx = ccb.uly = 0;
            return ccb;
        }

        // ccb creation
        ccb.skipMSBP = rcb.msbSkipped;
        ccb.ulx = rcb.ulx;
        ccb.uly = rcb.uly;
        ccb.w = rcb.w;
        ccb.h = rcb.h;
        ccb.ftpIdx = 0;      
        
        // Search for index of first truncation point (first layer where
        // length of data is not zero)
        int l = 0;
        while ((l < rcb.len.length) && (rcb.len[l] == 0))
        {
            ccb.ftpIdx += rcb.ntp[l];
            l++;
        }

        // Calculate total length, number of included layer and number of
        // truncation points
        for (l = fl - 1; l < fl + nl - 1; l++)
        {
            ccb.nl++;
            ccb.dl += rcb.len[l];
            ccb.nTrunc += rcb.ntp[l];
        }
        
        // Calculate number of terminated segments
        int nts;
        int tpidx;
        int passtype;
        
        if ((options & OPT_TERM_PASS) != 0)
        {
            // Regular termination in use One segment per pass
            // (i.e. truncation point)
            nts = ccb.nTrunc - ccb.ftpIdx;
        }
        else if ((options & OPT_BYPASS) != 0)
        {
            // Selective arithmetic coding bypass mode in use, but no regular
            // termination: 1 segment upto the end of the last pass of the 4th
            // most significant bit-plane, and, in each following bit-plane,
            // one segment upto the end of the 2nd pass and one upto the end
            // of the 3rd pass.

            if (ccb.nTrunc <= FIRST_BYPASS_PASS_IDX)
            {
                nts = 1;
            }
            else
            {
                nts = 1;
                // Adds one for each terminated pass
                for (tpidx = ccb.ftpIdx; tpidx < ccb.nTrunc; tpidx++)
                {
                    if (tpidx >= FIRST_BYPASS_PASS_IDX - 1)
                    {
                        passtype = (tpidx + NUM_EMPTY_PASSES_IN_MS_BP) % NUM_PASSES;
                        if (passtype == 1 || passtype == 2)
                        {
                            // lazy pass just before MQ pass or MQ pass just
                            // before lazy pass => terminated
                            nts++;
                        }
                    }
                }
            }
        }
        else
        {
            // Nothing special in use, just one terminated segment
            nts = 1;
        }

        // ccb.tsLengths creation
        if (nts > 1 && (ccb.tsLengths == null || ccb.tsLengths.length < nts))
        {
            ccb.tsLengths = new int[nts];
        }
        else if (nts > 1 && (options & (OPT_BYPASS | OPT_TERM_PASS)) == OPT_BYPASS)
        {
            ArrayUtil.intArraySet(ccb.tsLengths, 0);
        }

        // get segment lengths
        tpidx = ccb.ftpIdx;
        int ctp = ccb.ftpIdx; // Cumulative number of truncation
        // point for the current layer layer
        int tsidx = 0;
        int j;

        for (l = fl - 1; l < fl + nl - 1; l++)
        {
            ctp += rcb.ntp[l];
            // No data in this layer
            if (rcb.len[l] == 0)
                continue;
            
            // Get the terminated segment lengths, if any
            if (nts == 1)
                continue;
            if ((options & OPT_TERM_PASS) != 0)
            {
                // Regular termination => each pass is terminated
                for (j = 0; tpidx < ctp; j++, tpidx++)
                {
                    if (rcb.segLen[l] != null)
                    {
                        ccb.tsLengths[tsidx++] = rcb.segLen[l][j];
                    }
                    else
                    { // Only one terminated segment in packet
                        ccb.tsLengths[tsidx++] = rcb.len[l];
                    }
                }
            }
            else
            {
                // Lazy coding without regular termination
                for (j = 0; tpidx < ctp; tpidx++)
                {
                    if (tpidx >= FIRST_BYPASS_PASS_IDX - 1)
                    {
                        passtype = (tpidx + NUM_EMPTY_PASSES_IN_MS_BP) % NUM_PASSES;
                        if (passtype != 0)
                        {
                            // lazy pass just before MQ pass or MQ
                            // pass just before lazy pass =>
                            // terminated
                            if (rcb.segLen[l] != null)
                            {
                                ccb.tsLengths[tsidx++] += rcb.segLen[l][j++];
                                rcb.len[l] -= rcb.segLen[l][j - 1];
                            }
                            else
                            { // Only one terminated segment in packet
                                ccb.tsLengths[tsidx++] += rcb.len[l];
                                rcb.len[l] = 0;
                            }
                        }

                    }
                }

                // Last length in packet always in (either terminated segment
                // or contribution to terminated segment)
                if (rcb.segLen[l] != null && j < rcb.segLen[l].length)
                {
                    ccb.tsLengths[tsidx] += rcb.segLen[l][j];
                    rcb.len[l] -= rcb.segLen[l][j];
                }
                else
                { // Only one terminated segment in packet
                    if (tsidx < nts)
                    {
                        ccb.tsLengths[tsidx] += rcb.len[l];
                        rcb.len[l] = 0;
                    }
                }
            }
        }
        
        if (nts == 1 && ccb.tsLengths != null)
            ccb.tsLengths[0] = ccb.dl;

        // Set the progressive flag
        int lastlayer = fl + nl - 1;
        if (lastlayer < numLayers - 1)
        {
            for (l = lastlayer + 1; l < numLayers; l++)
            {
                // It remains data for this code-block in the bit stream
                if (rcb.len[l] != 0)
                {
                    ccb.prog = true;
                }
            }
        }

        return ccb;
    }


    @Override
    public void nextTile()
    {

    }


    @Override
    public void setTile(int x, int y)
    {
        // check validity of tile indexes
        if (x < 0 || y < 0 || x >= ntX || y >= ntY)
            throw new IllegalArgumentException(x + "," + y);

        int t = (y * ntX + x);
        if (getTileIdx() == t)
            return;

        // Set the new current tile
        ctX = x;
        ctY = y;

        // Calculate tile relative points
        int ctox = (x == 0) ? ax : px + x * ntW;
        int ctoy = (y == 0) ? ay : py + y * ntH;
        for (int i = nc - 1; i >= 0; i--)
        {
            culx[i] = (ctox + hd.getCompSubsX(i) - 1) / hd.getCompSubsX(i);
            culy[i] = (ctoy + hd.getCompSubsY(i) - 1) / hd.getCompSubsY(i);
            offX[i] = (px + x * ntW + hd.getCompSubsX(i) - 1) / hd.getCompSubsX(i);
            offY[i] = (py + y * ntH + hd.getCompSubsY(i) - 1) / hd.getCompSubsY(i);
        }

        // initialize subband tree
        initSubbandTree(t);

        // intialize packet decoder
        int numLayers = ((Integer) decSpec.nls.getTileDef(t)).intValue();
        ccbInfo = pktDec.restart(nc, mdl, numLayers, false, null);
        
        // initialize cache array
        ccbData = new byte[nc][][][][][];
        
        // if cdata was not initialized for this subband, do it!
        for (int c=0; c<nc; c++)
        {
            int nr = ccbInfo[c].length;
            ccbData[c] = new byte[nr][][][][]; // num of res levels in each component
            
            for (int r=0; r<nr; r++)
            {
                int ns = ccbInfo[c][r].length;
                ccbData[c][r] = new byte[ns][][][];  // num of subbands in each res level
                
                for (int s=0; s<ns; s++)
                {
                    if (ccbInfo[c][r][s] != null)
                    {
                        int ny = ccbInfo[c][r][s].length;
                        ccbData[c][r][s] = new byte[ny][][]; // num of vert. cb in each subband
                        
                        for (int cy=0; cy<ny; cy++)
                        {
                            int nx = ccbInfo[c][r][s][cy].length;
                            ccbData[c][r][s][cy] = new byte[nx][]; // num of horiz. cb in each subband
                        }
                    }
                }
            }
        }
    }


    protected void initSubbandTree(int t)
    {
        subbTrees = new SubbandSyn[nc];
        mdl = new int[nc];
        derived = new boolean[nc];
        params = new StdDequantizerParams[nc];
        gb = new int[nc];

        for (int c = 0; c < nc; c++)
        {
            derived[c] = decSpec.qts.isDerived(t, c);
            params[c] = (StdDequantizerParams) decSpec.qsss.getTileCompVal(t, c);
            gb[c] = ((Integer) decSpec.gbs.getTileCompVal(t, c)).intValue();
            mdl[c] = ((Integer) decSpec.dls.getTileCompVal(t, c)).intValue();

            int tcWidth = getTileCompWidth(t, c, mdl[c]);
            int tcHeight = getTileCompHeight(t, c, mdl[c]);
            int resULX = getResULX(c, mdl[c]);
            int resULY = getResULY(c, mdl[c]);
            SynWTFilter[] hFilters = decSpec.wfs.getHFilters(t, c);
            SynWTFilter[] vFilters = decSpec.wfs.getVFilters(t, c);

            subbTrees[c] = new SubbandSyn(tcWidth, tcHeight, resULX, resULY, mdl[c], hFilters, vFilters);
            initSubbandsFields(c, subbTrees[c]);
        }
    }
    
    
    public JPIPListener getListener()
    {
        return this.listener;
    }


    public void setListener(JPIPListener listener)
    {
        this.listener = listener;
    }
    
    
    public HeaderDecoder getHeaderDecoder()
    {
        return this.hd;
    }


    public DecoderSpecs getDecoderSpecs()
    {
        return this.decSpec;
    }

}
