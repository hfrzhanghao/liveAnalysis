package com.external.proxy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.external.common.dto.Result;

/**
 * spring类
 * 
 * @Author 刘艳伟
 * @date 2014-2-17上午10:54:41
 * @类功能　
 */

@Component
public class RestTemplateProxy implements InitializingBean {

	protected Logger logger = Logger.getLogger(RestTemplateProxy.class);

	@Autowired
	private RestTemplate restTemplate;

	public JSONObject postFormWithReturnJSONObject1(String url, MultiValueMap<String, Object> form) {
		String json = null;
		try {
			json = restTemplate.postForObject(url, form, String.class);
		} catch (Exception e) {
			logger.error("调用接口:" + url + "失败" + "(" + e.getMessage() + ")");
			e.printStackTrace();
			return null;
		}
		return getJSONObject(json);
	}

	public static HttpEntity<String> createHttpEntity(String json) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Accept", "application/json");
		requestHeaders.set("Accept-Charset", "utf-8");
		requestHeaders.set("Content-Type", "application/json;charset=utf-8");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, requestHeaders);
		return httpEntity;
	}

	@SuppressWarnings("unused")
	private Result handleResult(String json) {
		if (StringUtils.isEmpty(json)) {
			logger.error("数据访问层返回空字符串:" + json);
			return null;
		}
		try {
			JSONObject fromObject = JSONObject.fromObject(json);
			Result result = new Result(fromObject.getInt("result"), json);
			return result;
		} catch (Exception e) {
			logger.error("解析返回JSON出错");
			logger.error(e.getMessage(), e);
			return null;
		}

	}

	private JSONObject getJSONObject(String json) {
		if (StringUtils.isEmpty(json)) {
			logger.error("数据访问层返回空字符串:" + json);
			return null;
		}
		try {
			JSONObject fromObject = JSONObject.fromObject(json);
			return fromObject;
		} catch (Exception e) {
			logger.error("解析返回JSON出错!");
			logger.error(e.getMessage(), e);
			return null;
		}

	}
	
	public static String go(String urls, String postData) throws Exception {

		byte[] bt = postData.getBytes("UTF-8");
		URL url = new URL(urls);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
       
		OutputStream out = (OutputStream) connection.getOutputStream();

		out.write(bt);
		out.close();
		InputStream content = (InputStream) connection.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(content, "UTF-8"));
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = in.readLine()) != null) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}
