package org.vast.stt.gui.widgets.image;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * <p>Title: ImageCanvas.java</p>
 * <p>Description:  </p>
 * @author Tony Cook
 * @since Jun 11, 2010
 */

public class ImageCanvas extends Canvas implements PaintListener
{
	private Image image;
	String testImg = "C:/tcook/wx/chasing/2010/05-19-2010/HennesseyTor_256.jpg";
	private double scaleFac = 0.5;

	public ImageCanvas(Composite parent, int style){
		super(parent, style);
		this.addPaintListener(this);
//		loadImage(testImg);
//		createImage(256,256);
	}

	public void createImage(int w, int h){
		Display display = getDisplay();
		//image = new Image(display, w, h);
		PaletteData pal = new PaletteData(0xFF0000 , 0xFF00 , 0xFF);
		ImageData imData = new ImageData(w,h,24,pal);

		int pix;
		for (int y = 0; y < h; y++) {
//			data.getPixels(0, y, w, lineData,0);
//			// Analyze each pixel value in the line
			for (int x=0; x<w; x++){
				if(y<100)
					pix = 0xFF00;
				else pix = 0xFF;
				imData.setPixel(x, y, pix);
			}
		}
		image = new Image(display, imData.scaledTo((int)(w * 0.5), (int)(h * 0.5)));
	}
	
	// Hardwired for OWS7 demo
	public void createImage(int w, int h, byte [ ] b) {
		Display display = getDisplay();
		PaletteData pal = new PaletteData(0xFF0000, 0xFF00 , 0xFF);
//		PaletteData pal = new PaletteData(0xFF , 0xFF00 , 0xFF0000);
		ImageData imData = new ImageData(w, h, 32, pal);
		long t1 = System.currentTimeMillis();
		System.err.println("Started image create");
		int pixValue;
		RGB rgb;
//		imData.setPixels(0, 0, w, b, 0); don't work
		int bidx = 0;
		int sr, sg, sb;
		int bint;
		for (int y = 0; y < imData.height; y++) {
			for (int x=0; x<imData.width; x++, bidx+=3){
				//  Conv. from two's comp
				sr =  (short)((int)(b[bidx]) &  0x000000FF);
				sg = (short)((int)(b[bidx+1]) &  0x000000FF);
				sb = (short)((int)(b[bidx+2]) &  0x000000FF);
				bint = (int)(sr)<<16  | (int)(sg)<<8 | (int)(sb) ;
				pixValue = bint;
				imData.setPixel(x, y, pixValue);
			}
		};
		image = new Image(display,imData. scaledTo((int)(w * scaleFac), (int)(h * scaleFac)));
		long t2 = System.currentTimeMillis();
		System.err.println("Image create time: " + (t2-t1));
		this.redraw();
	}


	public Image loadImage(String filename) { 
		if(image!=null && !image.isDisposed()){
			image.dispose();
			image=null;
		}
		Display display = getDisplay();
		image= new Image(display,filename);
		ImageDescriptor id;
		return image;
	}

	public void setImage(Image image){
		if(image!=null && !image.isDisposed()){
			image.dispose();
			image=null;
		}
		this.image = image;
	}

	@Override
	public void paintControl(PaintEvent e) {
		System.err.println("Paint image");
		if(image != null) {
			e.gc.drawImage(image,0, 0);
		}
	}
	
	public static void main(String[] args) {
		PaletteData pal = new PaletteData(0xFF0000, 0xFF00, 0XFF);
		int pixTest = pal.getPixel(new RGB(255,0,0));
		System.err.println(pixTest);
		pixTest = pal.getPixel(new RGB(0,0,255));
		System.err.println(pixTest);
	}
	
}