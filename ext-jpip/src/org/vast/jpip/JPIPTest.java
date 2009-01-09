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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import org.vast.jpip.message.ViewWindowField;
import org.vast.util.Bbox;
import jj2000.disp.ImgScrollPane;
import jj2000.j2k.codestream.PrecInfo;
import jj2000.j2k.codestream.reader.HeaderDecoder;
import jj2000.j2k.decoder.Decoder;
import jj2000.j2k.decoder.DecoderSpecs;
import jj2000.j2k.entropy.decoder.EntropyDecoder;
import jj2000.j2k.image.BlkImgDataSrc;
import jj2000.j2k.image.DataBlkInt;
import jj2000.j2k.image.ImgDataConverter;
import jj2000.j2k.quantization.dequantizer.Dequantizer;
import jj2000.j2k.util.ParameterList;
import jj2000.j2k.wavelet.synthesis.InverseWT;


/**
 * <p><b>Title:</b>
 * JPIPDataProvider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO JPIPDataProvider type description
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Feb 24, 2008
 * @version 1.0
 */
public class JPIPTest implements JPIPListener
{
    protected static final int GRAY = 0;
    protected static final int RGB = 1;
    protected static final int RGBA = 2;
    protected BlkImgDataSrc decodedImage;
    protected BufferedImage image;
    protected ImgScrollPane scrollPane;
    protected Frame win;
        

    public void createWindow(String url, int w, int h)
    {
        Dimension winDim;
        Insets ins = null;
        String btitle = "JJ2000: " + url;
        
        win = new Frame(btitle + " @ (0,0) : 1");
        win.setBackground(Color.white);
        
        //scrollPane = new ImgScrollPane(ImgScrollPane.SCROLLBARS_AS_NEEDED);
        //win.add(scrollPane, BorderLayout.CENTER);

        win.addNotify(); // Instantiate peer to get insets
        ins = win.getInsets();
        winDim = new Dimension(w + ins.left + ins.right, h + ins.top + ins.bottom);
        win.setSize(winDim);
        win.validate();
        win.setVisible(true);
    }
    
    
    protected ParameterList getDefaultParameters()
    {
        String[][] param = Decoder.getAllParameters();
        ParameterList defpl = new ParameterList();

        for (int i = param.length - 1; i >= 0; i--)
        {
            if (param[i][3] != null)
                defpl.put(param[i][0], param[i][3]);
        }

        // Create parameter list using defaults
        return new ParameterList(defpl);
    }


    public void requestData(String url, String target)
    {
        try
        {
            ParameterList params = getDefaultParameters();
            
            JPIPBitstreamReader jpipReader = JPIPBitstreamReader.createJpipReader(url, target, params, true);
            jpipReader.setListener(this);
            
            // get some header options
            HeaderDecoder hd = jpipReader.getHeaderDecoder();
            DecoderSpecs decSpec = jpipReader.getDecoderSpecs();
            int nComps = hd.getNumComps();
            int[] bitDepths = new int[nComps];
            for (int i = 0; i < nComps; i++)
                bitDepths[i] = hd.getOriginalBitDepth(i);

            // construct JJ2000 processing chain
            EntropyDecoder entdec = hd.createEntropyDecoder(jpipReader, params);
            //ROIDeScaler roids = hd.createROIDeScaler(entdec, params, decSpec);
            Dequantizer deq = hd.createDequantizer(entdec, bitDepths, decSpec);
            InverseWT invWT = InverseWT.createInstance(deq, decSpec);
            ImgDataConverter converter = new ImgDataConverter(invWT, 0);
            InvCompTransf ictransf = new InvCompTransf(converter, decSpec, bitDepths, params);
            this.decodedImage = ictransf;

            // set resolution levels
            int res = 3;
            invWT.setImgResLevel(res);
            invWT.setTile(0, 0);
            
            // prepare image
            int width = decodedImage.getImgWidth();
            int height = decodedImage.getImgHeight();
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                        
            // retrieve jpip data
            int[] fsiz = new int[] { width, height, ViewWindowField.CLOSEST };
            int[] rsiz = new int[] { 512, 512 };
            int[] roff = new int[] { 0, 0 };
            int[][] comps = new int[][] { { 0, 2 } };
            int layers = 10;
            jpipReader.requestData(fsiz, rsiz, roff, comps, layers);
            
//            Thread.sleep(10000);            
//            roff = new int[] { 128, 0 };
//            jpipReader.requestData(fsiz, rsiz, roff, comps, layers);
//            
//            Thread.sleep(3000);
//            roff = new int[] { 128, 128 };
//            jpipReader.requestData(fsiz, rsiz, roff, comps, layers);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    boolean newPacketsAvailable = false;
    Bbox updateBox = new Bbox();
    
    public void precinctReceived(int comp, int resLevel, PrecInfo prec)
    {
        synchronized (updateBox)
        {
            this.newPacketsAvailable = true;
            
            // figure out what region of the image to update!!
            System.out.println("res=" + resLevel + " -> " + prec.ulx + "," + prec.uly + " - " + prec.w + "x" + prec.h);
            int shift = 3 - resLevel;
            int ox = prec.ulx << shift;
            int oy = prec.uly << shift;
            int w = prec.w << shift;
            int h = prec.h << shift;
            
            // add to updateBbox
            updateBox.resizeToContain(ox, oy, 0);
            updateBox.resizeToContain(ox+w, oy+h, 0);
            
            updateBox.notify();
        }
    }    
    
    Thread decodingThread = new Thread()
    {
        public void run()
        {
            while (true)
            {
                Bbox bbox = null;
                
                try
                {
                	synchronized (updateBox)
                    {
                        while (!newPacketsAvailable)
                        	updateBox.wait();
                        
                        bbox = updateBox.copy();
                        updateBox.nullify();
                        newPacketsAvailable = false;
                    }
                    
                    int ox = (int)bbox.getMinX();
                    int oy = (int)bbox.getMinY();
                    int w = (int)bbox.getSizeX();
                    int h = (int)bbox.getSizeY();
                    refreshImage(decodedImage, ox, oy, w, h, 3);
                    //refreshImage(decodedImage, 0, 0, 1500, 1500, 3);
                    
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
    };


    public void refreshImage(BlkImgDataSrc src, int ox, int oy, int width, int height, int numComps)
    {
        int i, k1, k2, k3, k4, l; // counters
        int tmp1, tmp2, tmp3, tmp4; // temporary storage for sample values
        int mv1, mv2, mv3, mv4; // max value for each component
        int ls1, ls2, ls3, ls4; // level shift for each component
        int fb1, fb2, fb3, fb4; // fractional bits for each component
        int[] data1, data2, data3, data4; // references to data buffers
        //int height; // image height
        //int width; // image width
        DataBlkInt db1, db2, db3, db4; // data-blocks to request data from src
        boolean prog; // Flag for progressive data

        if (numComps < 0)
            numComps = src.getNumComps();
        
        int imgheight = src.getImgHeight();
        int imgwidth = src.getImgWidth();
        
        if (ox >= imgwidth)
            ox -= imgwidth;
        
        if (oy >= imgheight)
            oy -= imgheight;
        
        int dw = (ox + width) - imgwidth;
        if (dw > 0)
            width -= dw;
        
        int dh = (oy + height) - imgheight;
        if (dh > 0)
            height -= dh;
        
        //System.out.println("Redrawing " + ox + "," + oy + " - " + (ox+width) + "," + (oy+height));
        
        // Check for image type
        int type;
        switch (numComps)
        {
            case 1:
                type = GRAY;
                break;
            case 3:
                type = RGB;
                break;
            case 4:
                type = RGBA;
                break;
            default:
                throw new IllegalArgumentException("Only 1, 3, and 4 components " + "supported");
        }

        // Initialize
        ls2 = fb2 = mv2 = 0; // to keep compiler happy
        ls3 = fb3 = mv3 = 0; // to keep compiler happy
        ls4 = fb4 = mv4 = 0; // to keep compiler happy
        db1 = db2 = db3 = db4 = null; // to keep compiler happy
        
        switch (type)
        {
            case RGBA:
                //db4 = new DataBlkInt(); // Alpha plane
                ls4 = 1 << (src.getNomRangeBits(3) - 1);
                mv4 = (1 << src.getNomRangeBits(3)) - 1;
                fb4 = src.getFixedPoint(3);
                
            case RGB:
                //db3 = new DataBlkInt(); // Blue plane
                ls3 = 1 << (src.getNomRangeBits(2) - 1);
                mv3 = (1 << src.getNomRangeBits(2)) - 1;
                fb3 = src.getFixedPoint(2);
                db2 = new DataBlkInt(); // Green plane
                ls2 = 1 << (src.getNomRangeBits(1) - 1);
                mv2 = (1 << src.getNomRangeBits(1)) - 1;
                fb2 = src.getFixedPoint(1);
                
            case GRAY:
                //db1 = new DataBlkInt(); // Gray or Red plane
                ls1 = 1 << (src.getNomRangeBits(0) - 1);
                mv1 = (1 << src.getNomRangeBits(0)) - 1;
                fb1 = src.getFixedPoint(0);
                break;
                
            default:
                throw new Error("Internal JJ2000 error");
        }
        
        src.setTile(0, 0);
        //int height = src.getImgHeight();
        //int width = src.getImgWidth();
        
        // Deliver in lines to reduce memory usage
        int [] buffer = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        for (l = oy; l < oy+height; l++)
        {
            // Request line data
            prog = false;
            switch (type)
            {
                case RGBA:
                    // Request alpha plane
                    //db4.ulx = ox;
                    //db4.uly = l;
                    //db4.w = width;
                    //db4.h = 1;
                    db4 = new DataBlkInt(ox, l, width, 1);
                    src.getInternCompData(db4, 3);
                    prog = prog || db4.progressive;
                    
                case RGB:
                    // Request blue and green planes
                    //db2.ulx = db3.ulx = ox;
                    //db2.uly = db3.uly = l;
                    //db2.w = db3.w = width;
                    //db2.h = db3.h = 1;
                    db2 = new DataBlkInt(ox, l, width, 1);
                    db3 = new DataBlkInt(ox, l, width, 1);
                    src.getInternCompData(db3, 2);
                    prog = prog || db3.progressive;
                    src.getInternCompData(db2, 1);
                    prog = prog || db2.progressive;
                    
                case GRAY:
                    // Request 
                    //db1.ulx = ox;
                    //db1.uly = l;
                    //db1.w = width;
                    //db1.h = 1;
                    db1 = new DataBlkInt(ox, l, width, 1);
                    src.getInternCompData(db1, 0);
                    prog = prog || db1.progressive;
                    break;
            }
            
            if (prog)
            { // Progressive data not supported
                // We use abort since maybe at a later time 
                // the data won't
                // be progressive anymore
                // (DSC: this need to be improved of course)
                
                return; // can not continue processing
            }
            
            // Put pixel data in buffer
            switch (type)
            {
                case GRAY:
                    data1 = db1.data;
                    k1 = db1.offset + width - 1;
                    for (i = ox + width - 1; i >= ox; i--)
                    {
                        tmp1 = (data1[k1--] >> fb1) + ls1;
                        tmp1 = (tmp1 < 0) ? 0 : ((tmp1 > mv1) ? mv1 : tmp1);
                        int val = (0xFF << 24) | (tmp1 << 16) | (tmp1 << 8) | tmp1;
                        buffer[i + l*imgwidth] = val;
                    }
                    break;
                    
                case RGB:
                    data1 = db1.data; // red
                    data2 = db2.data; // green
                    data3 = db3.data; // blue
                    k1 = db1.offset + width - 1;
                    k2 = db2.offset + width - 1;
                    k3 = db3.offset + width - 1;
                    for (i = ox + width - 1; i >= ox; i--)
                    {
                        tmp1 = (data1[k1--] >> fb1) + ls1;
                        tmp1 = (tmp1 < 0) ? 0 : ((tmp1 > mv1) ? mv1 : tmp1);
                        tmp2 = (data2[k2--] >> fb2) + ls2;
                        tmp2 = (tmp2 < 0) ? 0 : ((tmp2 > mv2) ? mv2 : tmp2);
                        tmp3 = (data3[k3--] >> fb3) + ls3;
                        tmp3 = (tmp3 < 0) ? 0 : ((tmp3 > mv3) ? mv3 : tmp3);
                        int val = (0xFF << 24) | (tmp1 << 16) | (tmp2 << 8) | tmp3;
                        buffer[i + l*imgwidth] = val;
                    }
                    break;
                    
                case RGBA:
                    data1 = db1.data; // red
                    data2 = db2.data; // green
                    data3 = db3.data; // blue
                    data4 = db4.data; // alpha
                    k1 = db1.offset + width - 1;
                    k2 = db2.offset + width - 1;
                    k3 = db3.offset + width - 1;
                    k4 = db4.offset + width - 1;
                    for (i = ox + width - 1; i >= ox; i--)
                    {
                        tmp1 = (data1[k1--] >> fb1) + ls1;
                        tmp1 = (tmp1 < 0) ? 0 : ((tmp1 > mv1) ? mv1 : tmp1);
                        tmp2 = (data2[k2--] >> fb2) + ls2;
                        tmp2 = (tmp2 < 0) ? 0 : ((tmp2 > mv2) ? mv2 : tmp2);
                        tmp3 = (data3[k3--] >> fb3) + ls3;
                        tmp3 = (tmp3 < 0) ? 0 : ((tmp3 > mv3) ? mv3 : tmp3);
                        tmp4 = (data4[k4--] >> fb4) + ls4;
                        tmp4 = (tmp4 < 0) ? 0 : ((tmp4 > mv4) ? mv4 : tmp4);
                        int val = (tmp4 << 24) | (tmp1 << 16) | (tmp2 << 8) | tmp3;
                        buffer[i + l*imgwidth] = val;
                    }
                    break;
            }
        }
        
        //scrollPane.setImage(image);
        //scrollPane.getComponent(2).repaint();
        win.getGraphics().drawImage(image, 0, 0, null);
    }


    public static void main(String[] args)
    {
        //String url = "http://127.0.0.1";
        //String target = "RSA1.jp2";
        String url = "http://ws.spotimage.com";
        String target = "jpip/RSA1.jp2";
        //String url = "http://216.150.195.214";
        //String target = "JP2Server/05MAY14083758-M1BS-005693793010_01_P001.jp2";
        
        JPIPTest jpipTest = new JPIPTest();
        jpipTest.createWindow(url, 1024, 1024);
        jpipTest.decodingThread.start();
        jpipTest.requestData(url, target);
    }


    public void windowActivated(WindowEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }

}
