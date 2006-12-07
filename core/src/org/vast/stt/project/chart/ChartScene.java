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

package org.vast.stt.project.chart;

import org.vast.stt.project.scene.Scene;
import org.vast.stt.renderer.SceneRenderer;
import org.vast.stt.renderer.JFreeChart.ChartRenderer;
import org.vast.stt.style.DataStyler;


/**
 * <p><b>Title:</b><br/>
 * Chart Scene Descriptor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Encapsulate the current state of a chart scene.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class ChartScene extends Scene<SceneRenderer<ChartScene>>
{
    
    public ChartScene()
    {
        super();
        renderer = new ChartRenderer();
    }

    
    @Override
    protected void prepareStyler(DataStyler styler)
    {
       
    }	
}
