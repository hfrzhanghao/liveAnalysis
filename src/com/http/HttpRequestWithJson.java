package com.http;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("deprecation")
public class HttpRequestWithJson {

	private static String apiURL = "";
	private Log logger = LogFactory.getLog(this.getClass());
	private HttpClient httpClient = null;
	private HttpPost method = null;
	private long startTime = 0L;
	private long endTime = 0L;
	private int status = 0;

	/**
	 * 接口地址
	 * 
	 * @param url
	 */
	@SuppressWarnings({ "static-access" })
	public HttpRequestWithJson(String url) {

		if (url != null) {
			this.apiURL = url;
		}
		if (apiURL != null) {
			httpClient = new DefaultHttpClient();
			method = new HttpPost(apiURL);

		}
	}

	/**
	 * 调用 API
	 * 
	 * @param parameters
	 * @return
	 */
	public String post(String parameters) { //Json String
		String body = null;
		logger.info("parameters:" + parameters);

		if (method != null & parameters != null && !"".equals(parameters.trim())) {
			HttpResponse response;
			try {
				// 建立一个NameValuePair数组，用于存储欲传送的参数
				method.addHeader("Content-type", "application/json; charset=utf-8");
				method.setHeader("Accept", "application/json");
				method.setEntity(new StringEntity(parameters, Charset.forName("UTF-8")));
				response = httpClient.execute(method);
				int statusCode = response.getStatusLine().getStatusCode();
				logger.info("statusCode:" + statusCode);
				if (statusCode != HttpStatus.SC_OK) {
					logger.error("Method failed:" + response.getStatusLine());
					status = 1;
				}
				// Read the response body
				body = EntityUtils.toString(response.getEntity());
			} catch (IOException e) {
				// 网络错误
				status = 3;
			}
		}
		return body;
	}
	
	/**
	 * 0.成功 1.执行方法失败 2.协议错误 3.网络错误
	 * 
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

}
