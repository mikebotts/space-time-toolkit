/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
 Alexandre Robin <robin@nsstc.uah.edu>
 
 ******************************* END LICENSE BLOCK ***************************/

package org.vast.jpip;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import jj2000.j2k.codestream.Markers;
import jj2000.j2k.io.RandomAccessIO;
import jj2000.j2k.util.ISRandomAccessIO;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.vast.jpip.message.ImageReturnTypes;
import org.vast.jpip.message.JPIPMessage;
import org.vast.jpip.message.JPIPRequestFields;
import org.vast.jpip.message.JPIPResponse;
import org.vast.jpip.message.JPIPResponseFields;
import org.vast.jpip.network.*;


/**
 * <p><b>Title:</b>
 * JPIP HTTP Client
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO JPIPHttpClient type description
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Feb 22, 2008
 * @version 1.0
 */
public class JPIPHttpClient
{
	protected String serverUrl;
	protected int port;
	protected JPIPRequestEncoder encoder;
	//protected HttpURLConnection connection;
	protected HttpMethod method;
	protected HttpClient httpClient;
	protected JPIPResponseFieldsParser responseFieldsParser;
	protected JPIPMessageDecoder messageDecoder;
	protected JPIPRequestFields request;
	protected boolean useSessions = true;
	protected boolean newRequest = false;


	public JPIPHttpClient(String serverUrl, boolean useSessions) throws IOException
	{
		this.serverUrl = serverUrl;
		this.useSessions = useSessions;

		this.httpClient = new HttpClient();
		this.encoder = new JPIPRequestEncoder(null);
		this.responseFieldsParser = new JPIPResponseFieldsParser();
		this.messageDecoder = new JPIPMessageDecoder();
	}


	public RandomAccessIO connect(String target) throws IOException
	{
		// set request options for init
		request = new JPIPRequestFields();
		request.setTarget(target);
		if (useSessions)
			request.setCNew(true, false); // http session
		else
			request.setCNew(false, false);
		request.setType(ImageReturnTypes.JPP_STREAM);
		//request.setLen(2000);
		request.setTID("0");
		//request.metareq = "[*:0]";

		// send request
		//connection = sendRequest();
		//int datalen = connection.getContentLength();
		sendRequest();

		// parse JPIP response headers
		JPIPResponse response = readResponse();
		byte[] body = response.getHeaderMessage().messageBody;

		// add SOT marker at the end of message body
		// hack for JJ2000 HeaderDecoder to stop gracefully
		byte[] extendedBody = new byte[body.length + 2];
		System.arraycopy(body, 0, extendedBody, 0, body.length);
		extendedBody[extendedBody.length - 2] = (byte) (Markers.SOT >> 8);
		extendedBody[extendedBody.length - 1] = (byte) Markers.SOT;
		InputStream jp2kHeaderIs = new ByteArrayInputStream(extendedBody);

		// prepare random access stream
		RandomAccessIO in = new ISRandomAccessIO(jp2kHeaderIs, extendedBody.length, 1, extendedBody.length);

		return in;
	}


//	public HttpURLConnection sendRequest() throws IOException
//	{
//		newRequest = true;
//
//		// encode request as HTTP GET
//		encoder.setJpipRequestFields(request);
//		String url = serverUrl + encoder.createGetRequest();
//		URL requestUrl = new URL(url);
//		System.out.println("Sending GET request: " + url);
//
//		// open connection and set HTTP options
//        connection = (HttpURLConnection) requestUrl.openConnection();
//        connection.setRequestMethod("GET");
//        connection.setRequestProperty("Accept", "image/" + request.serverControlField.type[0]);
//        connection.setRequestProperty("Host", requestUrl.getHost());
//        connection.setRequestProperty("Connection", "keep-alive");
//        connection.setDoInput(true);
//        connection.setDoOutput(false);
//        connection.connect();
//
//		return connection;
//	}
	
	
	public void sendRequest() throws IOException
	{
		newRequest = true;

		// encode request as HTTP GET
		encoder.setJpipRequestFields(request);
		String url = serverUrl + encoder.createGetRequest();
		URL requestUrl = new URL(url);
		System.out.println("Sending GET request: " + url);

		// open connection and set HTTP options		
		method = new GetMethod(url);

		try
		{
			method.addRequestHeader("Accept", "image/" + request.serverControlField.type[0]);
			method.addRequestHeader("Host", requestUrl.getHost());
			method.addRequestHeader("Connection", "keep-alive");

			int statusCode = httpClient.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK)
				System.err.println("Method failed: " + method.getStatusLine());
		}
		catch (HttpException e)
		{
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		}
	}


	public JPIPResponseFields readResponseHeader() throws IOException
	{
		// construct header list
		Header[] headers = method.getResponseHeaders();
		Map<String, List<String>> headerFields = new Hashtable<String, List<String>>();
		for (Header h: headers)
		{
			List<String> headerValues = headerFields.get(h.getName());
			if (headerValues == null)
			{
				headerValues = new ArrayList<String>();
				headerFields.put(h.getName(), headerValues);
			}				
			headerValues.add(h.getValue());
		}
		
		// read jpip http headers
		//Map<String, List<String>> headerFields = connection.getHeaderFields();
		responseFieldsParser.parse(headerFields);
		JPIPResponseFields jpipResponseFields = responseFieldsParser.getJPIPResponseFields();
		jpipResponseFields.list(System.out);

		// update request parameters using response fields
		updateRequestParameters(jpipResponseFields);

		return jpipResponseFields;
	}


	public JPIPResponse readResponse() throws IOException
	{
		// read header fields
		JPIPResponseFields jpipResponseFields = readResponseHeader();

		// read response messages
		//InputStream httpInputStream = new BufferedInputStream(connection.getInputStream());
		InputStream httpInputStream = new BufferedInputStream(method.getResponseBodyAsStream());
		messageDecoder.setInputStream(httpInputStream);
		JPIPResponse response = new JPIPResponse(jpipResponseFields);
		JPIPMessage jpipMessage = null;

		// loop until all messages are read (EOR)
		do
		{
			jpipMessage = messageDecoder.readMessage();
			jpipMessage.list(System.out);
			//System.out.println(jpipMessage);
			response.addMessage(jpipMessage);
		}
		while (!jpipMessage.header.isEOR);
		
		// release the connection so it can be reused
		releaseConnection();
		
		return response;
	}


	public JPIPMessage readNextMessage() throws IOException
	{
		if (newRequest)
		{
			//InputStream httpInputStream = new BufferedInputStream(connection.getInputStream());
			InputStream httpInputStream = new BufferedInputStream(method.getResponseBodyAsStream());
			messageDecoder.setInputStream(httpInputStream);
			newRequest = false;
		}

		JPIPMessage jpipMessage = messageDecoder.readMessage();
		//jpipMessage.list(System.out);
		//System.out.println(jpipMessage);
		return jpipMessage;
	}
	
	
	public void releaseConnection()
	{
		method.releaseConnection();
	}


	public void updateRequestParameters(JPIPResponseFields jpipResponseFields)
	{
		/*
		if (jpipResponseFields.fsiz != null)
		{
		    request.viewWindowField.fsiz[0] = jpipResponseFields.fsiz[0];
		    request.viewWindowField.fsiz[1] = jpipResponseFields.fsiz[1];
		}
		
		if (jpipResponseFields.rsiz != null)
		{
		    request.viewWindowField.rsiz[0] = jpipResponseFields.rsiz[0];
		    request.viewWindowField.rsiz[1] = jpipResponseFields.rsiz[1];
		}
		
		if (jpipResponseFields.roff != null)
		{
		    request.viewWindowField.roff[0] = jpipResponseFields.roff[0];
		    request.viewWindowField.roff[1] = jpipResponseFields.roff[1];
		}
		
		if (jpipResponseFields.comps != null)
		{
		    request.viewWindowField.comps = new int[jpipResponseFields.comps.length][2];
		    for (int i = 0; i < jpipResponseFields.comps.length; i++)
		    {
		        request.viewWindowField.comps[i][0] = jpipResponseFields.comps[i][0];
		        request.viewWindowField.comps[i][1] = jpipResponseFields.comps[i][1];
		    }
		}*/

		if (jpipResponseFields.layers != -1)
		{
			request.viewWindowField.layers = jpipResponseFields.layers;
		}

		if (jpipResponseFields.len != -1)
		{
			request.dataLimitField.len = jpipResponseFields.len;
		}

		if (jpipResponseFields.type != -1)
		{
			request.setType(jpipResponseFields.type);
		}

		if (jpipResponseFields.tid != null)
		{
			request.targetField.tid = jpipResponseFields.tid;
		}

		if (jpipResponseFields.cid != null)
		{
			request.channelField.cid = jpipResponseFields.cid;
			request.channelField.cnew = null;
		}

		if (jpipResponseFields.host != null)
		{
			serverUrl = jpipResponseFields.host;
		}

		if (jpipResponseFields.port != -1)
		{
			port = jpipResponseFields.port;
		}

		if (jpipResponseFields.path != null)
		{
			request.channelField.path = jpipResponseFields.path;
		}
	}


	public JPIPRequestFields getLastRequest()
	{
		return request;
	}
}
