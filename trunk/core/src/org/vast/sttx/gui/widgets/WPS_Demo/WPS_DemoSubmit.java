package org.vast.sttx.gui.widgets.WPS_Demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.vast.sensorML.SMLException;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.w3c.dom.Element;


public class WPS_DemoSubmit 
{
	private String statusUrl;

	public WPS_DemoSubmit(String cameraEndPoint)
	{
	}

	//  Request from precanned template
	public boolean invokeWPS(double startTime, double stopTime) {
		InputStream response = null;
		try {

			URL templateUrl = WPS_DemoSubmit.class.getResource("WPS_DemoSubmitTemplate2.xml");
			DOMHelper dom = new DOMHelper(templateUrl.toString(), false);
//			Element rootElt = dom.getRootElement();
//			NodeList inputNodes = dom.getAllElements("InputParameter");  //rootElt.getElementsByTagName("sensorParam");
//			int numNodes = inputNodes.getLength();
//			for(int i=0; i<numNodes; i++){
//				Element eltTmp = (Element)inputNodes.item(i);
//				String paramId = eltTmp.getAttribute("parameterID");
//				if(paramId.equalsIgnoreCase("AOV")){
//					Element aovValElt = dom.getElement(eltTmp, "value/Quantity/value");
//					aovValElt.setTextContent(zoom + "");
////					System.err.println(aovValElt);
//				} else if (paramId.equalsIgnoreCase("PositionToPointAt")){
//					Element vectorElt = dom.getElement(eltTmp, "value/Position/location/Vector");
//					NodeList coords = dom.getChildElements(vectorElt);
//					Element coordElt, coordValElt;
//					for(int j=0; j<coords.getLength(); j++){
//						coordElt = (Element)coords.item(j);
//						coordValElt = dom.getElement(coordElt, "Quantity/value");
//						switch(j){
//						case 0:
//							coordValElt.setTextContent(lat + "");
//							break;
//						case 1:
//							coordValElt.setTextContent(lon + "");
//							break;
//						case 2:
//							coordValElt.setTextContent(alt + "");
//							break;
//						}
//					}
//				}
//			}
//			//dom.serialize(rootElt, System.out, true);
//			String response = sendPOSTRequest(dom);
			response = sendPOSTRequest(dom);
			if(response == null)
				return false;
			
			statusUrl = parseWPSResponse(response);
			if(statusUrl == null)
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}   

	public String parseWPSResponse(InputStream resp) throws DOMHelperException, Exception {
		DOMHelper dom = new DOMHelper(resp, false);
		Element rootElt = dom.getRootElement();
		String statusUrl = dom.getAttributeValue(rootElt, "statusLocation");
		
		return statusUrl;
	}
	
	public String parseWPSResponse_ProcMDStatus(InputStream resp) throws DOMHelperException, Exception {
		DOMHelper dom = new DOMHelper(resp, false);
//		InputStream is = new FileInputStream("C:/tcook/work/bir/src/test/WPS_GMU_resp.xml");
//		DOMHelper dom = new DOMHelper(is, false);
		Element rootElt = dom.getRootElement();
		Element statusElt = dom.getElement(rootElt, "Process/Metadata");
		String statusUrl = dom.getAttributeValue(statusElt, "href");
		
		return statusUrl;
	}
	
	//  May need more params than this...
	public String [] getVideoTimes () throws IOException, DOMHelperException {
		String [] times = new String[2];
		InputStream response = null;

		DOMHelper dom = new DOMHelper(statusUrl, false);
		Element rootElt = dom.getRootElement();
		times[0] = dom.getElementValue(rootElt, "VideoStartTime");
		times[1] = dom.getElementValue(rootElt, "VideoEndTime");
		
		return times;
	}
	
	public InputStream sendPOSTRequest(DOMHelper req){

		InputStream responseStream = null;
		OutputStream out = null;
		try {
			// send Request to URL
			URL url = new URL("http://data.laits.gmu.edu:8091/wpsr3/WebProcessingService");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);

			out = con.getOutputStream();
			req.serialize(req.getRootElement(), out, false);
			out.flush();
			out.close();

			responseStream = con.getInputStream();
			return responseStream;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
				
		}
		return null;
	}

	public static void main(String [] args) throws SMLException, Exception {
		//  First, define the URI for SPS Process.  If you don't have it, you can use this:
		// <Process>
		//    <uri>urn:ogc:def:process:SPS:1.0</uri>
		//    <class>org.sensorML.process.SPS_Process</class>
		//  </Process>
		WPS_DemoSubmit wpsReq = new WPS_DemoSubmit("http://data.laits.gmu.edu:8091/wpsr3/WebProcessingService");

		boolean ok = wpsReq.invokeWPS(0.0,0.0);
		System.err.println("StatusUrl: " + wpsReq.statusUrl);
		Thread.sleep(5000);
		System.err.println("Begin polling...");
//		
//		wpsReq.statusUrl = "http://data.laits.gmu.edu:8091/wpsr3/Databases/FlatFile/1276131130827changeVideoresult.XML";
		while(true) {
			String [] times = wpsReq.getVideoTimes();
			System.err.println("Times: " + times[0] + ", " + times[1]);
			if(times[1] != null && times[1].trim().length() > 0 )
				break;
			Thread.sleep(10000);
		}
//		wpsReq.parseWPSResponse(null);
//		acp.dumpTables();
	}

}

