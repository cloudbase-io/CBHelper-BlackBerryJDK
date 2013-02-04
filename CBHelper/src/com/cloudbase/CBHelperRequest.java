/* Copyright (C) 2012 cloudbase.io
 
 This program is free software; you can redistribute it and/or modify it under
 the terms of the GNU General Public License, version 2, as published by
 the Free Software Foundation.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; see the file COPYING.  If not, write to the Free
 Software Foundation, 59 Temple Place - Suite 330, Boston, MA
 02111-1307, USA.
 */
package com.cloudbase;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;

import org.json.me.JSONObject;
import org.json.me.JSONTokener;

/**
 * A Runnable object used by the CBHelper class to execute HTTP calls asynchronously. This should not be called
 * directly.
 * @author Stefano Buliani
 *
 */
public class CBHelperRequest extends Thread {

	private String url;
	private String function;
	private String fileId;
	private Hashtable postData;
	private Vector files;
	private static final String FIELD_BOUNDARY = "---------------------------14737809831466499882746641449";
	
	private String temporaryFilePath;
	
	private CBHelperResponder responder;
	private CBHelperResponse resp;
	
	public CBHelperRequest(String apiUrl, String func) { 
		this.url = apiUrl;
		this.function = func;
	}
	
	public void run() {
		HttpConnection hc = null;
		InputStream is = null;
 		
 		try {
 			hc = (HttpConnection) Connector.open(url+";deviceside=true");
 			hc.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + CBHelperRequest.FIELD_BOUNDARY);

 			hc.setRequestMethod(HttpConnection.POST);

 			ByteArrayOutputStream request = new ByteArrayOutputStream();//hc.openOutputStream();
	
		    // Add your data
		    Enumeration params = this.postData.keys();
		    while (params.hasMoreElements())
		    {
		    	String curKey = (String)params.nextElement();
		    	request.write(this.getParameterPostData(curKey, (String)this.postData.get(curKey)));
		    	//entity.addPart(new CBStringPart(curKey, this.postData.get(curKey)));
		    }
		    // if we have file attachments then add each file to the multipart request
		    if (this.files != null && this.files.size() > 0) {
		    	
		    	for (int i = 0; i < this.files.size(); i++) {
		    		CBHelperAttachment curFile = (CBHelperAttachment)this.files.elementAt(i);
			    	String name = curFile.getFileName();
			    	
			    	request.write(this.getFilePostData(i, name, curFile.getFileData()));
			    }
		    }
		    request.flush();
		    hc.setRequestProperty("Content-Length", "" + request.toByteArray().length);
		    OutputStream requestStream = hc.openOutputStream();
		    requestStream.write(request.toByteArray());
		    requestStream.close();
		    
		    
		    is = hc.openInputStream();
		    
		    // if we have a responder then parse the response data into the global CBHelperResponse object
		    if (this.responder != null) {
		    	try {
			    	resp = new CBHelperResponse();
			    	resp.setFunction(this.function);
			    	
			    	
			    	int ch;
			    	ByteArrayOutputStream response = new ByteArrayOutputStream();
			    	 
					while ((ch = is.read()) != -1)
					{
						response.write(ch);
					}
			    	
			    	// if it's a download then we need to save the file content into a temporary file in
			    	// application cache folder. Then return that file to the responder
				    if (this.function.equals("download")) {
				    	String filePath = "file:///store/home/user/" + this.fileId;
				    	FileConnection fconn = (FileConnection)Connector.open(filePath);
				    	if (fconn.exists()) {
				    		fconn.delete();
				    	}
				    	fconn.create();
				    	
				    	DataOutputStream fs = fconn.openDataOutputStream();
				    	fs.write(response.toByteArray());
				    	fs.close();
				    	fconn.close();
				    	resp.setDownloadedFile(filePath);
				    } else {
				    	
				    	// if it's not a download parse the JSON response and set all 
				    	// the variables in the CBHelperResponse object
				    	String responseString = new String(response.toByteArray());//EntityUtils.toString(response.getEntity());
				    	
				    	//System.out.println("Received response string: " + responseString);
				    	resp.setResponseDataString(responseString);
				    	
				    	JSONTokener tokener = new JSONTokener(responseString);
				    	JSONObject jsonOutput = new JSONObject(tokener);
					    
					    // Use the cloudbase.io deserializer to get the data in a Map<String, Object>
					    // format.
					    JSONObject outputData = jsonOutput.getJSONObject(this.function);
					    
				    	resp.setData(outputData.get("message"));
					    resp.setErrorMessage((String)outputData.get("error"));
					    resp.setSuccess(((String)outputData.get("status")).equals("OK"));
			    	}
				    //Application.getApplication().enterEventDispatcher();
				    responder.handleResponse(resp);
		    	} catch (Exception e) { 
		    		System.out.println("Error while parsing response: " + e.getMessage());
		    		e.printStackTrace();
		    	}
		    }
		    
		} catch (Exception e) {
			System.out.println("Error while opening connection and sending data: " + e.getMessage());
    		e.printStackTrace();
		}
	}
	
	private byte[] getParameterPostData(String name, String value) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		String paramName = "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n";
		
		bos.write(this.getBoundaryMessage().getBytes());
		bos.write(paramName.getBytes());
 		bos.write(value.getBytes());
 		bos.write("\r\n".getBytes());
 		return bos.toByteArray();
	}
	
	private byte[] getFilePostData(int fileCounter, String fileName, byte[] fileContent) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		String paramName = "Content-Disposition: attachment; name=\"file_" + fileCounter + "\"; filename=\"" + fileName + "\"\r\n";
		
		bos.write(this.getBoundaryMessage().getBytes());
		bos.write(paramName.getBytes());
		bos.write("Content-Type: application/octet-stream\r\n\r\n".getBytes());
		bos.write(fileContent);
		bos.write("\r\n".getBytes());
		bos.write(this.getBoundaryMessage().getBytes());
		
		return bos.toByteArray();
	}
	
	private String getBoundaryMessage() {
		return "--" + CBHelperRequest.FIELD_BOUNDARY + "\r\n";
	}
	
	public Hashtable getPostData() {
		return postData;
	}
	public void setPostData(Hashtable postData) {
		this.postData = postData;
	}
	public CBHelperResponder getResponder() {
		return responder;
	}
	public void setResponder(CBHelperResponder responder) {
		this.responder = responder;
	}
	public Vector getFiles() {
		return files;
	}
	public void setFiles(Vector files) {
		this.files = files;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getTemporaryFilePath() {
		return temporaryFilePath;
	}

	public void setTemporaryFilePath(String temporaryFilePath) {
		this.temporaryFilePath = temporaryFilePath;
	}
}
