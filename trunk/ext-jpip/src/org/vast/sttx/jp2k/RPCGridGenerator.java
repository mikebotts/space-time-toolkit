/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Tony Cook <tcook@nsstc.uah.edu>
    Gregoire Berthiau <berthiau@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/
package org.vast.sttx.jp2k;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.StringTokenizer;
import org.sensorML.process.RPC_Process;
import org.vast.cdm.common.DataBlock;
import org.vast.cdm.common.DataComponent;
import org.vast.data.DataBlockFactory;
import org.vast.util.SpatialExtent;
import org.vast.process.DataProcess;
import org.vast.process.ProcessException;
import org.vast.sensorML.SMLUtils;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.w3c.dom.Element;

/**
 * <p><b>Title:</b><br/>
 * RPCGridGenerator
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Generates a Rectangular grid based on Bbox, w, h, and 
 * a set of RPC parameters
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Jan 22, 2008
 * @version 1.0
 */

public class RPCGridGenerator {
	RPC_Process rpcProc;
	SpatialExtent bounds;
	int width, length;
	private double paramValues[];

	public RPCGridGenerator() {
		initRPC_Process();
	}

	protected void initRPC_Process() {
		try {
			URL procUrl = new URL("file:/D:/SpotRPC.xml");//RPCGridGenerator.class.getResource("SpotRPC.xml");

			// launch SensorML reader using the version agnostic helper
			DOMHelper dom = new DOMHelper(procUrl.toString(), false);
			SMLUtils utils = new SMLUtils();
			utils.setCreateExecutableProcess(true);
			Element processElt = dom.getElement("member/*");
			DataProcess process = utils.readProcess(dom, processElt);
			rpcProc = (RPC_Process) process;
			// make sure all processes are initialized correctly
			rpcProc.init();

			// print out the chain layout
			//System.out.println(rpcProc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param valueStr - String containing values section of rpcBlock
	 */
	public void readParamValues(String valueStr) throws DOMHelperException {
		//System.err.println(valueStr);
		StringTokenizer st = new StringTokenizer(valueStr, ",");
		int valueCnt = st.countTokens();
		if(valueCnt != 96) {
			throw new DOMHelperException("Wrong number of values in Param Block");
		}
		paramValues = new double[valueCnt];
		for(int i=0;i<valueCnt; i++){
			paramValues[i] = Double.parseDouble(st.nextToken());
		}
//		System.err.println(valueStr);
	}
	
	/**
	 * Parse RPC param values out of rpc Block from JPEG200 image/stream
	 * AND put them into corresponding paramter block values
	 * @param is
	 */
	public void loadRPCParams(InputStream is) throws DOMHelperException {
	    DOMHelper dom = new DOMHelper(is, false);	
	    
	    Element root = dom.getRootElement();
	    String nodePath = "member/ProcessModel/parameters/ParameterList/parameter/DataArray/values";
	    
	    Element valuesElt = dom.getElement(root, nodePath);
	    if(valuesElt == null)
	    	throw new DOMHelperException("Values element not found in RPC parameter block");
	    String valuesStr = valuesElt.getTextContent();
	    valuesStr = valuesStr.trim();
	    valuesStr = valuesStr.replace(" ", "");
	    valuesStr = valuesStr.replace("\t", "");
	    
	    readParamValues(valuesStr);
	    
	    loadRPCParams();
	}

	/** 
	 * Convenience method to load param values array given a String containing 
	 * the entire RPC block from a J2k file/stream
	 * 
	 * @param rpcBlockStr
	 * @throws DOMHelperException
	 */
	public void loadRPCParams(String rpcBlockStr) throws DOMHelperException {
		ByteArrayInputStream bis = new ByteArrayInputStream(rpcBlockStr.getBytes());
		loadRPCParams(bis);
	}
	
	private void loadRPCParams() {
		String Component[] = {"constant","x","y","z","xx","xy","xz","yy","yz","zz",
							  "xxx","xxy","xxz","xyy","xyz","xzz","yyy","yyz","yzz","zzz"};
        int cnt = 0;
        
        DataComponent rpcParamSet = 
        	rpcProc.getParameterList().getComponent("rpc_parameter_series").getComponent("rpc_parameter_set");
        DataComponent imageRegion = rpcParamSet.getComponent("image_region");
        imageRegion.getComponent("zone_minX").getData().setDoubleValue(paramValues[cnt++]); 
        imageRegion.getComponent("zone_minY").getData().setDoubleValue(paramValues[cnt++]); 
        imageRegion.getComponent("zone_maxX").getData().setDoubleValue(paramValues[cnt++]); 
        imageRegion.getComponent("zone_maxY").getData().setDoubleValue(paramValues[cnt++]); 
        
        DataComponent imageAdj =  rpcParamSet.getComponent("image_adjustment");
        imageAdj.getComponent("image_x_offset").getData().setDoubleValue(paramValues[cnt++]); 
        imageAdj.getComponent("image_x_scale").getData().setDoubleValue(paramValues[cnt++]); 
        imageAdj.getComponent("image_y_offset").getData().setDoubleValue(paramValues[cnt++]); 
        imageAdj.getComponent("image_y_scale").getData().setDoubleValue(paramValues[cnt++]); 
        
        DataComponent targetAdj = rpcParamSet.getComponent("target_adjustment");
        targetAdj.getComponent("target_x_offset").getData().setDoubleValue(paramValues[cnt++]); 
        targetAdj.getComponent("target_x_scale").getData().setDoubleValue(paramValues[cnt++]); 
        targetAdj.getComponent("target_y_offset").getData().setDoubleValue(paramValues[cnt++]); 
        targetAdj.getComponent("target_y_scale").getData().setDoubleValue(paramValues[cnt++]); 
        targetAdj.getComponent("target_z_offset").getData().setDoubleValue(paramValues[cnt++]); 
        targetAdj.getComponent("target_z_scale").getData().setDoubleValue(paramValues[cnt++]); 
        
        DataComponent xNum = rpcParamSet.getComponent("x_numerator_coefficients");
        for(int i=0; i<Component.length; i++){
        	xNum.getComponent(Component[i]).getData().setDoubleValue(paramValues[cnt++]); 
        }
        DataComponent xDen = rpcParamSet.getComponent("x_denominator_coefficients");
        for(int i=0; i<Component.length; i++){
        	xDen.getComponent(Component[i]).getData().setDoubleValue(paramValues[cnt++]); 
        }
        DataComponent yNum = rpcParamSet.getComponent("y_numerator_coefficients");
        for(int i=0; i<Component.length; i++){
        	yNum.getComponent(Component[i]).getData().setDoubleValue(paramValues[cnt++]); 
        }
        DataComponent yDen = rpcParamSet.getComponent("y_denominator_coefficients");
        for(int i=0; i<Component.length; i++){
        	yDen.getComponent(Component[i]).getData().setDoubleValue(paramValues[cnt++]); 
        }

        DataComponent errorParams =  rpcParamSet.getComponent("error_parameters");
        errorParams.getComponent("error_bias").getData().setDoubleValue(paramValues[cnt++]); 
        errorParams.getComponent("error_random").getData().setDoubleValue(paramValues[cnt++]); 
	}
	
	/**
	 *  Create the geoloca grid based on the current bounds and rpcProc values 
	 * @return DataBlock containing the Geolocation grid
	 * 
	 * @todo support Z
	 * 
	 */
	public DataBlock createGrid() {
		DataBlock gridBlock;

		// build grid
		double minY = bounds.getMinY();
		double maxY = bounds.getMaxY();
		double minX = bounds.getMinX();
		double maxX = bounds.getMaxX();

		gridBlock = DataBlockFactory.createBlock(new float[width*length * 4]); // for lat,lon,imgX,imgY

		// compute data for grid block
		double xStep = (maxX - minX) / (width - 1);
		double yStep = (maxY - minY) / (length - 1);

		int pointNum = 0;
		for (int j = 0; j < length; j++) {
			for (int i = 0; i < width; i++) {
				double lon = minX + xStep * i;
				double lat = maxY - yStep * j;
				rpcProc.setInputLatLonAlt(lat, lon, 0.0);

				try {
					rpcProc.execute();
				} catch (ProcessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                //System.out.println(rpcProc.getOutputX() + "," + rpcProc.getOutputY());
				double imgX = (rpcProc.getOutputX() - 3000) / 1000;
				double imgY = (rpcProc.getOutputY() - 4000) / 1000;				
                
				gridBlock.setDoubleValue(pointNum, lat * Math.PI/180);
                pointNum++;
				gridBlock.setDoubleValue(pointNum, lon * Math.PI/180);
                pointNum++;
                gridBlock.setDoubleValue(pointNum, imgX);                
                pointNum++;
                gridBlock.setDoubleValue(pointNum, imgY);
				pointNum++;
				//  Add Z- must change project file styler mapping also....
//				gridBlock.setDoubleValue(pointNum, 0.0); // flat earth for now
//				pointNum++;
			}
		}

		return gridBlock;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setBounds(SpatialExtent bounds) {
		this.bounds = bounds;
	}
	
	public static void main(String [] args) throws Exception{
		RPCGridGenerator rpcGen = new RPCGridGenerator();
		
		//  read rpcBlock (from file for test)
		BufferedReader reader = 
			new BufferedReader(new InputStreamReader(new FileInputStream("c:/tcook/JPIP/SpotWcs.xml")));
		
		boolean feof = false;
		StringBuffer buff = new StringBuffer(2000);
		String inline = null;
		while(!feof) {
			inline = reader.readLine();
			if(inline==null)
				break;
			buff.append(inline);
		}
		//System.err.println(buff.toString());
		String rpcStr = buff.toString();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(rpcStr.getBytes());
		rpcGen.loadRPCParams(bis);
	}
}
