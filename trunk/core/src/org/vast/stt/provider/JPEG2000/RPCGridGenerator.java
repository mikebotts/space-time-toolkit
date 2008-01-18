package org.vast.stt.provider.JPEG2000;

import java.net.URL;

import org.sensorML.process.RPC_Process;
import org.vast.cdm.common.DataBlock;
import org.vast.data.DataBlockFactory;
import org.vast.physics.SpatialExtent;
import org.vast.process.DataProcess;
import org.vast.process.ProcessException;
import org.vast.sensorML.SMLUtils;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;

public class RPCGridGenerator {
	RPC_Process rpcProc;
	SpatialExtent bounds;
	int width, length;

	public RPCGridGenerator() {
		initRPC_Process();
		loadRPCParams();
	}

	protected void initRPC_Process() {
		try {
			URL procUrl = RPCGridGenerator.class.getResource("SpotRPC.xml");

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
			System.out.println(rpcProc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadRPCParams() {
		//  hardwired for now- read from JPEG image
		double[] xn = { 0.0, 0.7691493101560709, -0.892399629819144, 0.0,
				-0.052123448371891555, -0.032796986796494954, 0.0,
				-0.04416324122878225, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0 };
		double[] xd = { 1.0, 0.8163474286295702, 1.0173782509708404, 0.0,
				0.1310473225521477, 0.34581060961113347, 0.0,
				0.2260471500537555, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0 };
		double[] yn = { 0.0, 0.7838926784502444, -0.9278202443391601, 0.0,
				-0.014485051124534938, -0.019472198683583835, 0.0,
				-0.08264786143463312, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0 };
		double[] yd = { 1.0, -0.0882421410721561, -0.017304385965209567, 0.0,
				0.01188621534474107, 0.023752129032539458, 0.0,
				0.013364655371989076, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0 };
		//  test one pt
		rpcProc.setImageOffset(0.0, 0.0);
		rpcProc.setImageScale(1.0, 1.0);
		rpcProc.setTargetOffset(0.0, 0.0, 0.0);
		rpcProc.setTargetScale(1.0, 1.0, 1.0);
		rpcProc.setX_Denominator(xd);
		rpcProc.setY_Denominator(yd);
		rpcProc.setX_Numerator(xn);
		rpcProc.setY_Numerator(yn);

	}

	public DataBlock createGrid() {
		DataBlock gridBlock;

		// build grid
		int gridWidth = 10;
		int gridLength = 10;
		double minY = bounds.getMinY();
		double maxY = bounds.getMaxY();
		double minX = bounds.getMinX();
		double maxX = bounds.getMaxX();

		gridBlock = DataBlockFactory.createBlock(new double[gridLength
				* gridWidth * 3]);
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
				double imX = rpcProc.getOutputX();
				double imY = rpcProc.getOutputY();

				gridBlock.setDoubleValue(pointNum, lon);
//				gridBlock.setDoubleValue(pointNum, imX);
				pointNum++;
				gridBlock.setDoubleValue(pointNum, lat);
//				gridBlock.setDoubleValue(pointNum, imY);
				pointNum++;
				gridBlock.setDoubleValue(pointNum, 0.0); // flat earth for now
				pointNum++;
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
}
