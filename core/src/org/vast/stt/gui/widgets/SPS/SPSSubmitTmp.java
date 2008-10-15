package org.vast.stt.gui.widgets.SPS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.vast.cdm.common.DataComponent;
import org.vast.data.DataValue;
import org.vast.process.DataProcess;
import org.vast.sensorML.ProcessLoader;
import org.vast.sensorML.SMLException;
import org.vast.sensorML.SMLUtils;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SPSSubmitTmp {

	public List<LLAZ> goodPoints;
	public List<LLAZ> badPoints;
	
	public SPSSubmitTmp(String cameraEndPoint)
	{
		goodPoints = new ArrayList<LLAZ>(200);
		badPoints = new ArrayList<LLAZ>(200);
	}
	
	public void dumpTables(){
		System.err.println("GOOD POINTS:");
		for(LLAZ llazTmp: goodPoints){
			System.err.println(llazTmp);
		}
		System.err.println("\n\n\n=======================\n\n\n");
		System.err.println("BAD POINTS:");
		for(LLAZ llazTmp: goodPoints){
			System.err.println(llazTmp);
		}
	}


	//  Request from precanned template
	public boolean requestSPS(double lat, double lon, double alt, double zoom) {

		try {

			URL templateUrl = SPSSubmitTmp.class.getResource("SPS_SubmitLLATemplate.xml");
			//			URL procURL = TestVast1camSubmit.class.getResource("SPS_ExcludePositionElt.xml");
			DOMHelper dom = new DOMHelper(templateUrl.toString(), false);
			Element rootElt = dom.getRootElement();
			NodeList inputNodes = dom.getAllElements("InputParameter");  //rootElt.getElementsByTagName("sensorParam");
			int numNodes = inputNodes.getLength();
			for(int i=0; i<numNodes; i++){
				Element eltTmp = (Element)inputNodes.item(i);
				String paramId = eltTmp.getAttribute("parameterID");
				if(paramId.equalsIgnoreCase("AOV")){
					Element aovValElt = dom.getElement(eltTmp, "value/Quantity/value");
					aovValElt.setTextContent(zoom + "");
//					System.err.println(aovValElt);
				} else if (paramId.equalsIgnoreCase("PositionToPointAt")){
					Element vectorElt = dom.getElement(eltTmp, "value/Position/location/Vector");
					NodeList coords = dom.getChildElements(vectorElt);
					Element coordElt, coordValElt;
					for(int j=0; j<coords.getLength(); j++){
						coordElt = (Element)coords.item(j);
						coordValElt = dom.getElement(coordElt, "Quantity/value");
						switch(j){
						case 0:
							coordValElt.setTextContent(lat + "");
							break;
						case 1:
							coordValElt.setTextContent(lon + "");
							break;
						case 2:
							coordValElt.setTextContent(alt + "");
							break;
						}
					}
				}
			}
			//dom.serialize(rootElt, System.out, true);
			boolean llaOk = sendPOSTRequest(dom);
			LLAZ llaz = new LLAZ();
			llaz.lat = lat;
			llaz.lon = lon; 
			llaz.alt = alt;
			llaz.zoom = zoom;
			if(llaOk) {
				//goodPoints.add(llaz);
				//System.err.println("GOOD: " + llaz);
				return true;
			} else {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"STT Error", "SPS Submission error.  Requested point out of range for Camera vastcam1.");
				//badPoints.add(llaz);
				//System.err.println("BAD: " + llaz);
				return false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}   

	public boolean  sendPOSTRequest(DOMHelper req){
//		throws SPSException, ServiceException {

		BufferedReader responseStream = null;
		try {
			// send Request to URL
			URL url = new URL("http://vast1:8080/SPS/SPS");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);

			OutputStream out = con.getOutputStream();
			//PrintWriter pw = new PrintWriter(out);
			// String buff = xts.toString();
			//pw.print(buff);
			req.serialize(req.getRootElement(), out, false);
			out.flush();
			out.close();

			// get answer from service
			responseStream = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while(true){
				String respLine = responseStream.readLine();
				if(respLine == null)
					break;
//				System.err.println(respLine);
				if(respLine.contains("out of range")){
					return false;
				}
			}
			return true;
//			// parse answer
//			XmlObject xobj = XmlObject.Factory.parse(brServiceAnswer);
//
//			if (xobj instanceof ExceptionReportDocument) {
//
//				ExceptionReportDocument erd = (ExceptionReportDocument) xobj;
//				if (!erd.validate()) {
//					throw new SPSException(
//							"Received invalid ExceptionReportDocument from " + url);
//				} // else
//				ServiceException se = new ServiceException();
//				se.addExceptionReport(erd);
//				throw se;
//			} else if (xobj instanceof ExceptionDocument) {
//
//				ExceptionDocument ed = (ExceptionDocument) xobj;
//				if (!ed.validate()) {
//					throw new SPSException(
//							"Received invalid ExceptionDocument from " + url);
//				} // else
//				ServiceException se = new ServiceException();
//				se.addExceptionReport(ed);
//				throw se;
//			} else {
//				return xobj; // should be the correct answer
//			}
//
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (responseStream != null) {
				try {
					responseStream.close();
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
			}
		}
		System.err.println("Why at the end of sendPost??");
		return false;
	}


	private void requestSPS_viaProc(double lat, double lon, double alt, double zoom) {

		try {

			//			URL procURL = TestVast1camSubmit.class.getResource("SPS_IncludePositionElt.xml");
			URL procURL = SPSSubmitTmp.class.getResource("SPS_ExcludePositionElt.xml");
			DOMHelper dom = new DOMHelper(procURL.toString(), false);
			SMLUtils utils = new SMLUtils();
			utils.setCreateExecutableProcess(true);
			Element procElt = dom.getElement("member/*");
			DataProcess process = utils.readProcess(dom, procElt);

			// make sure all processes are initialized correctly
			process.init();

			// set inputs and test chain execution
			process.getInputList().getComponent("AOV").getData().setDoubleValue(zoom);
			DataComponent ptComp = process.getInputList().getComponent("PositionToPointAt");
			DataComponent latComp = ptComp.getComponent("Geodetic latitude");
			DataComponent lonComp = ptComp.getComponent("Geodetic longitude");
			DataComponent htComp = ptComp.getComponent("Ellipsoidal height");
			latComp.getData().setDoubleValue(lat);
			lonComp.getData().setDoubleValue(lon);
			htComp.getData().setDoubleValue(alt);

			process.execute();

			DataValue response = (DataValue)process.getOutputList().getComponent("response");
			//			DataComponent dataRetrievalService = process.getOutputList().getComponent("dataRetrievalService");
			//			DataComponent serviceType = dataRetrievalService.getComponent(0).getComponent("serviceType");
			//			DataComponent serviceURL = dataRetrievalService.getComponent(0).getComponent("serviceURL");
			//			DataComponent request = dataRetrievalService.getComponent(0).getComponent("request");

			System.out.println(response.getData().getStringValue());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}   

	public static void main(String [] args) throws SMLException, Exception {
		//  First, define the URI for SPS Process.  If you don't have it, you can use this:
		// <Process>
		//    <uri>urn:ogc:def:process:SPS:1.0</uri>
		//    <class>org.sensorML.process.SPS_Process</class>
		//  </Process>
		String mapUrl = new String("file:///C:/tcook/work/STT3/src/org/vast/stt/gui/widgets/SPS/ProcessMap.xml");
		ProcessLoader.reloadMaps(mapUrl);

		SPSSubmitTmp acp = new SPSSubmitTmp("http://vastcam1.nsstc.uah.edu");

		double lat, lon, alt, height = 5000., zoom= 55.;
//		double minHt = 230.0, maxHt = 330.0, htStep = 5.0;
//		for(double ht = minHt; ht <maxHt; ht+= htStep) {
//			boolean ok = acp.requestSPS(34.64, -86.70, ht, zoom);
//			if (ok)
//				Thread.sleep(2000);
//			else
//				Thread.sleep(200);
//		}
//		if(true)
//		System.exit(1);
//		<ows:LowerCorner>-86.764190 34.638439</ows:LowerCorner>
//		<ows:UpperCorner>-86.546472 34.823472</ows:UpperCorner>
		double westLon = -88.0;
		double eastLon = -84.0;
		double southLat = 33.0;
		double northLat = 37.0;
		double latStep = .25;
		double lonStep = .25;
		for(lat = southLat; lat < northLat; lat+= latStep){
			for(lon = westLon; lon <eastLon; lon+= lonStep) {
				boolean ok = acp.requestSPS(lat, lon, height, zoom);
				if (ok)
					Thread.sleep(2000);
				else
					Thread.sleep(200);
			}
		}
		acp.dumpTables();
	}

}

class LLAZ {
	public double lat,lon,alt,zoom;
	
	public String toString(){
		return (lat + " " + lon  + " " + alt + " " + zoom);
	}
}