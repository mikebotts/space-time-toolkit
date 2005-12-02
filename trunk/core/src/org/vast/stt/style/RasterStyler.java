/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

package org.vast.stt.style;

import org.vast.ows.sld.RasterSymbolizer;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.util.SpatialExtent;


/**
 * <p><b>Title:</b><br/>
 * Raster Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Renders rasters (i.e. mapped texture, grid data)
 * based on data obtained from a Data Provider and 
 * style info given by a Raster Symbolizer.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class RasterStyler extends AbstractStyler
{
	protected RasterSymbolizer symbolizer;
	
	
	public RasterStyler()
	{
		// TEXTURE STUFFS
//		GL.glEnable(OpenGLDrawer.GL_TEXTURE_TARGET);
//		
//		byte[] data = new byte[100*64*3];
//		for (int i=0; i<data.length; i++)
//		{
//			if (i%9 == 0)
//				data[i] = (byte)0xFF;
//		}
//		
//		GL.glBindTexture(OpenGLDrawer.GL_TEXTURE_TARGET, 1);
//		
//		if (GL.glGetError() == GL.GL_INVALID_ENUM)
//			System.out.println("Invalid texture target");	
//		
//		GL.glTexImage2D(OpenGLDrawer.GL_TEXTURE_TARGET, 0, 4, 100, 64, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, data);
//		
//		if (GL.glGetError() == GL.GL_INVALID_VALUE)
//			System.out.println("Invalid texture size");
//
//		GL.glTexParameteri(OpenGLDrawer.GL_TEXTURE_TARGET, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
//        GL.glTexParameteri(OpenGLDrawer.GL_TEXTURE_TARGET, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
	}
	
	
	public SpatialExtent getBoundingBox()
	{
		// TODO Auto-generated method stub
		return null;
	}


	public double[] getCenterPoint()
	{
		// TODO Auto-generated method stub
		return null;
	}


	public void updateBoundingBox()
	{
		// TODO Auto-generated method stub
	}


	public void updateDataMappings()
	{
		// TODO Auto-generated method stub		
	}
	
	
	public RasterSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (RasterSymbolizer)sym;
	}


	public void accept(StylerVisitor visitor)
	{
		visitor.visit(this);		
	}
}
