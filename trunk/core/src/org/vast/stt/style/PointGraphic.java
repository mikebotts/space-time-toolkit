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


/**
 * <p><b>Title:</b><br/>
 * Point Graphic
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO PointGraphic Class Description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class PointGraphic implements Graphic
{
	enum Shape {SQUARE, CIRCLE, TRIANGLE, VECTOR}
	
	public double x, y, z;
	public float r, g, b;
	public float a = 1.0f;
	public float orientation;
	public int size;
	public Shape shape;
}
