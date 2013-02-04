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

/**
 * The operators used by <strong>CBSearchCondition</strong> objects when executed.
 */
public class CBSearchConditionOperator {
	public static final String CBOperatorEqual = "";
	public static final String CBOperatorLess = "$lt";
	public static final String CBOperatorLessOrEqual = "$te";
	public static final String CBOperatorBigger = "$gt";
	public static final String CBOperatorBiggerOrEqual = "$gte";
	public static final String CBOperatorAll = "$all";
	public static final String CBOperatorExists = "$exists";
	public static final String CBOperatorMod = "$mod";
	public static final String CBOperatorNe = "$ne";
	public static final String CBOperatorIn = "$in";
	public static final String CBOperatorNin = "$nin";
	public static final String CBOperatorSize = "$size";
	public static final String CBOperatorType = "$type";
	public static final String CBOperatorWithin = "$within";
	public static final String CBOperatorNear = "$near";
}
