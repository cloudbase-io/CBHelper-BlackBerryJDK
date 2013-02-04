package mypackage;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.cloudbase.CBHelperSerializable;

public class TestDataObject implements CBHelperSerializable {

	private String firstName;
	private String lastName;
	private String title;
	
	public JSONObject toJSONObject()  throws JSONException {
		JSONObject output = new JSONObject();
		output.put("first_name", this.firstName);
		output.put("last_name", this.lastName);
		output.put("title", this.title);
		
		return output;
	}

	public void fromJSONObject(JSONObject data) throws JSONException {
		this.firstName = data.getString("first_name");
		this.lastName = data.getString("last_name");
		this.title = data.getString("title");
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
