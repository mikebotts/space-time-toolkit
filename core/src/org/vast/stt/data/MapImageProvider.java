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

package org.vast.stt.data;


/**
 * <p><b>Title:</b><br/>
 * TopoMapProvider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This provider allows the composition of grid and image
 * data coming from two different sources (child providers).
 * Source for grid data can be for example a topo server (WCS)
 * or an automatically generated grid (ellipsoid mapping).
 * Source for image data can be WMS or fixed image for instance.
 * This class is responsible for generating texture coordinates.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jan 25, 2006
 * @version 1.0
 */
public class MapImageProvider extends CompositeProvider
{

    public MapImageProvider()
    {

    }


    @Override
    public DataNode getDataNode()
    {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void setDataNode(DataNode dataNode)
    {
        // TODO Auto-generated method stub
    }
}