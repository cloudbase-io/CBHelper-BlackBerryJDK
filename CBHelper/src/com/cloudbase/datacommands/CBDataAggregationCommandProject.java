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
package com.cloudbase.datacommands;

import java.util.Vector;

import org.json.me.JSONObject;

/**
 * The project aggregation command filters the number of fields selected
 * from a document.
 * You can either populate the <strong>includeFields</strong> property
 * to exclude all fields and only include the ones selected or use
 * the <strong>excludeFields</strong> to set up an exclusion list.
 */
public class CBDataAggregationCommandProject extends CBDataAggregationCommand {
	private Vector includeFields;
	private Vector excludeFields;

	public CBDataAggregationCommandProject() {
		this.includeFields = new Vector();
		this.excludeFields = new Vector();

		this.setCommandType(CBDataAggregationCommandType.CBDataAggregationProject);
	}

	public Object serializeAggregateConditions() {
		JSONObject fieldList = new JSONObject();
		try {
			for (int i = 0; i < this.getIncludeFields().size(); i++) {
				String field = (String)this.getIncludeFields().elementAt(i);
				fieldList.put(field, new Integer(1));
			}
	
			for (int i = 0; i < this.getExcludeFields().size(); i++) {
				String field = (String)this.getExcludeFields().elementAt(i);
				fieldList.put(field, new Integer(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fieldList;
	}

	public Vector getIncludeFields() {
		return includeFields;
	}

	public void setIncludeFields(Vector includeFields) {
		this.includeFields = includeFields;
	}

	public Vector getExcludeFields() {
		return excludeFields;
	}

	public void setExcludeFields(Vector excludeFields) {
		this.excludeFields = excludeFields;
	}

}