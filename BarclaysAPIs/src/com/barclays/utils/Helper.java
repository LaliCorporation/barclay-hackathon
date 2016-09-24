package com.barclays.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class Helper {
	public static JSONObject filter(JSONArray nodes, String key, String val) {
		try {
			for (int i = 0; i < nodes.length(); i++) {
				JSONObject node = nodes.getJSONObject(i);
				if (node.has(key) && node.getString(key).equals(val))
					return node;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
