package com;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ConfigToJson {
	public static JSONArray getJSONArray(String key) {
		JSONObject jsono = getJSONObject();
		JSONArray jarray = jsono.getJSONArray(key);
		return jarray;
	}

	public static JSONObject getJSONObject() {
		// TODO 如果发布到tomcat中，上面的地址需要修改
		String path = ConfigToJson.class.getClassLoader().getResource("/").toString();
		path = path.substring(6);
		try {
			path = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		JSON json = XML2JSON.json(path + "config.xml");
		JSONObject jsono = JSONObject.fromObject(json);
		return jsono;
	}

	public static void main(String[] args) throws IOException {

		JSONArray qualityInfo = ConfigToJson.getJSONArray("qualityInfo");

		for (Object object : qualityInfo) {
			JSONObject qinfo = (JSONObject) object;
			System.out.print(qinfo.get("@lost") + " ");
			System.out.print(qinfo.get("@lostType") + " ");
			System.out.print(qinfo.get("@connector") + " ");
			System.out.print(qinfo.get("@delay") + " ");
			System.out.println(qinfo.get("@delayType"));
		}

		System.out.println(qualityInfo);

	}
}
