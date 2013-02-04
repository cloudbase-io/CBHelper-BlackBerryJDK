package mypackage;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.cloudbase.CBHelperResponder;
import com.cloudbase.CBHelperResponse;

public class TestResponder implements CBHelperResponder {

	public void handleResponse(CBHelperResponse res) throws JSONException {
		// TODO Auto-generated method stub
		if (res.getFunction().equals("download")) {
			System.out.println("downloaded file " + res.getDownloadedFile());
		} else {
			System.out.println("Received response: " + res.getResponseDataString());
			
			if (res.getData() instanceof JSONObject)
				System.out.println("The output is an object");
			else if (res.getData() instanceof JSONArray)
				System.out.println("The output is an array");
			else 
				System.out.println("Unrecognized output");
		}
	}

}
