package org.vast.sttx.gui.widgets.WPS_Demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.vast.sensorML.SMLException;
import org.vast.util.DateTimeFormat;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.w3c.dom.Element;


public class WPS_DemoSubmit 
{
	protected String statusUrl;
	String defaultServerURL = "http://data.laits.gmu.edu:8091/wpsr3/WebProcessingService";
	String oldBegin, oldEnd, newBegin, newEnd;
	
	public WPS_DemoSubmit()
	{
	}

	//  Request from precanned template
	public boolean invokeWPS() {
//		if(oldBegin== null || newBegin == null || oldEnd == null || newEnd == null) {
//			System.err.println("WPS_DemoSubmit.invokeWPS(): not all times are present.  Request aborted.");
//			return false;
//		}
		InputStream response = null;
		try {
			URL templateUrl = WPS_DemoSubmit.class.getResource("WPS_DemoSubmitTemplate2.xml");
			DOMHelper dom = new DOMHelper(templateUrl.toString(), false);
//			Element rootElt = dom.getRootElement();
//			Element inputsElt = dom.getElement(rootElt, "DataInputs");
//			NodeList inputList = dom.getAllChildElements(inputsElt);
//			Element oldInputElt = (Element)inputList.item(0);
//			Element newInputElt = (Element)inputList.item(1);
//			Element timeElt = dom.getElement(oldInputElt, "Reference/Body/GetObservation/eventTime/TM_During/TimePeriod");
//			Element beginElt = dom.getElement(timeElt,"beginPosition");
//			Element endElt = dom.getElement(timeElt,"endPosition");
//			dom.setElementValue(beginElt, oldBegin);
//			dom.setElementValue(endElt, oldEnd);
//			timeElt = dom.getElement(newInputElt, "Reference/Body/GetObservation/eventTime/TM_During/TimePeriod");
//			beginElt = dom.getElement(timeElt,"beginPosition");
//			endElt = dom.getElement(timeElt,"endPosition");
//			dom.setElementValue(beginElt, newBegin);
//			dom.setElementValue(endElt, newEnd);
//			
//			dom.serialize(rootElt, System.out, true);
			response = sendPOSTRequest(dom);
			if(response == null)
				return false;
			
			statusUrl = parseWPSResponse(response);
			if(statusUrl == null)
				return false;
			System.err.println(statusUrl);
			return true;
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

	public String parseWPSResponse_rootElt(InputStream resp) throws DOMHelperException, Exception {
		DOMHelper dom = new DOMHelper(resp, false);
		System.err.println("Response:");
		Element rootElt = dom.getRootElement();
		dom.serialize(rootElt, System.err, true);
		String statusUrl = dom.getAttributeValue(rootElt, "statusLocation");
		
		return statusUrl;
	}
	
	public String parseWPSResponse(InputStream resp) throws DOMHelperException, Exception {
		DOMHelper dom = new DOMHelper(resp, false);
//		InputStream is = new FileInputStream("C:/tcook/work/bir/src/test/WPS_GMU_resp.xml");
//		DOMHelper dom = new DOMHelper(is, false);
		Element rootElt = dom.getRootElement();
		Element statusElt = dom.getElement(rootElt, "Process/Metadata");
		String statusUrl = dom.getAttributeValue(statusElt, "href");
		
		return statusUrl;
	}
	
	//  May need more params than this...
	public String [] pollStatus () throws IOException, DOMHelperException {
		String [] times = new String[2];
		InputStream response = null;

		DOMHelper dom = new DOMHelper(statusUrl, false);
		Element rootElt = dom.getRootElement();
		times[0] = dom.getElementValue(rootElt, "VideoStartTime");
		times[1] = dom.getElementValue(rootElt, "VideoEndTime");
		
		if(times[0].trim().length() == 0 || times[1].trim().length() == 0)
			return null;
		
		return times;
	}
	
	public InputStream sendPOSTRequest(DOMHelper req){

		InputStream responseStream = null;
		OutputStream out = null;
		try {
			// send Request to URL
			URL url = new URL(defaultServerURL	);
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
	
	public void setOldVideoTimes(double startT, double endT) {
		if(startT > endT) {
			double tempT = endT;
			endT = startT;
			startT = tempT;
		}
		oldBegin = DateTimeFormat.formatIso(startT, 0);
		oldEnd = DateTimeFormat.formatIso(endT, 0);
	}
	
	public void setNewVideoTimes(double startT, double endT) {
		if(startT > endT) {
			double tempT = endT;
			endT = startT;
			startT = tempT;
		}
		newBegin = DateTimeFormat.formatIso(startT, 0);
		newEnd = DateTimeFormat.formatIso(endT, 0);
	}
	
	public static void main(String [] args) throws SMLException, Exception {
		//  First, define the URI for SPS Process.  If you don't have it, you can use this:
		// <Process>
		//    <uri>urn:ogc:def:process:SPS:1.0</uri>
		//    <class>org.sensorML.process.SPS_Process</class>
		//  </Process>
		WPS_DemoSubmit wpsReq = new WPS_DemoSubmit();

		boolean ok = wpsReq.invokeWPS();
		System.err.println("StatusUrl: " + wpsReq.statusUrl);
		Thread.sleep(5000);
		System.err.println("Begin polling...");
//		
//		wpsReq.statusUrl = "http://data.laits.gmu.edu:8091/wpsr3/Databases/FlatFile/1276131130827changeVideoresult.XML";
		while(true) {
			String [] times = wpsReq.pollStatus();
			System.err.println("Times: " + times[0] + ", " + times[1]);
			if(times[1] != null && times[1].trim().length() > 0 )
				break;
			Thread.sleep(10000);
		}
//		wpsReq.parseWPSResponse(null);
//		acp.dumpTables();
	}

}

