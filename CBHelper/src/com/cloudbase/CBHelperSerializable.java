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

import org.json.me.JSONException;
import org.json.me.JSONObject;

public interface CBHelperSerializable {
	/**
	 * Serializes the current object to its JSONObject representation
	 * @return A JSONObject with all the data of the current object
	 */
	JSONObject toJSONObject()  throws JSONException;
	
	/**
	 * Populates the current object with the data from the given JSONObject
	 * @param data The data received
	 */
	void fromJSONObject(JSONObject data) throws JSONException;
}
