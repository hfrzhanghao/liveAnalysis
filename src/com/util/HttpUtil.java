package com.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.external.common.ObjectFactory;
import com.http.HttpRequest;

@SuppressWarnings("deprecation")
public class HttpUtil {

	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	
    public static final String YANFAN_URL = ObjectFactory.getUrlProps().getProperty("yanfanUrl");

	public static String httpPost(String url, List<NameValuePair> nvps) {
		String returnStr = "";
		logger.info("Post,http请求地址：" + url);
		HttpPost httpPost = new HttpPost(url);
		HttpClient httpClient = new DefaultHttpClient();
		setConnectionTimeOut(httpClient);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				StringBuffer sb = new StringBuffer();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				returnStr = sb.toString();
			} else {
				throw new Exception("http proxy:" + httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			logger.error("Exception", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
        
		return returnStr;
	}
	
	public static String httpPost(String url, JSONObject obj) {

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (obj != null)
		{
			for (Entry<String, Object> entry : obj.entrySet())
			{
				nvps.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
			}
		}

		String returnStr = httpPost(url, nvps);
        logger.debug("url:{},params:{}", url, obj.toJSONString());
        logger.debug("result:{}", returnStr);
        
		return returnStr;
	}
	
	/**
	 * 设置网络超时
	 * 
	 * @param httpClient
	 */
	private static void setConnectionTimeOut(HttpClient httpClient) {
		HttpParams httpParams = httpClient.getParams();
		// 设置网络超时参数
		// 20秒未连接则超时
		HttpConnectionParams.setConnectionTimeout(httpParams, 60 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 60 * 1000);
	}

	/*
	 * 通过url传递参数的方式是get，这里是混合模式
	 */
	public static String httpGet(String url) {
		URL u = null;
		HttpURLConnection con = null;
		InputStreamReader in = null;
		OutputStreamWriter osw = null;
		BufferedReader br = null;
		StringBuffer buffer = new StringBuffer();

		logger.info(url);
		try {
			// 尝试发送请求
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(20000);// 20秒未连接则超时
			con.setReadTimeout(20000);// 20秒未响应则超时
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");

			// 读取返回内容
			in = new InputStreamReader(con.getInputStream(), "UTF-8");
			br = new BufferedReader(in);
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
				buffer.append("\n");
			}
		} catch (Exception e) {
			logger.error("Exception", e);
		} finally {
			try {
				if (osw != null) {
					osw.flush();
					osw.close();
				}
				if (br != null) {
					br.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (con != null) {
				con.disconnect();
			}
		}

		return buffer.toString();
	}
	
	
	public static String sendRequest(HttpRequest httpRequest) {
		String returnStr = "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		//PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        //设置线程数最大100,如果超过100为请求个数
		//connManager.setMaxTotal(100);
		//connManager.setDefaultMaxPerRoute(100);
		//CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connManager).build();
		RequestConfig config = RequestConfig.custom()
			    .setConnectionRequestTimeout(30 * 1000).setConnectTimeout(30 * 1000)
			    .setSocketTimeout(30 * 1000).build();
		httpRequest.setConfig(config);
		//setConnectionTimeOut(httpClient);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			logger.info(httpRequest.getURI().toString());
			httpRequest.setHeader("User-Agent", "http server");
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				in = new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8");
				br = new BufferedReader(in);
				StringBuffer sb = new StringBuffer();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				returnStr = sb.toString();
			} else {
				throw new Exception("http proxy:" + httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			logger.error("Exception", e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (in != null) {
					in.close();
				}
				if (httpclient != null) {
					httpclient.close();
				}
			} catch (IOException e) {
				logger.error("IOException", e);
			}
		}
		return returnStr;
	}
	
	public static String sendPost(String url, String param) {
		String result = "";
		logger.info(url + "?" + param);
		try {
			URL httpurl = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) httpurl.openConnection();
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setConnectTimeout(20*1000);
			httpConn.setReadTimeout(20*1000);
			PrintWriter out = new PrintWriter(httpConn.getOutputStream());
			
			out.print(param);

			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		logger.info(result);
		return result;
	}
	
	/**
	 * 拼接访问URL <br>
	 * 样式：${addr}/${servletName}?service=${serviceName}&params=${params}
	 * 
	 * @param addr
	 * @param servletName
	 * @param serviceName
	 * @param params
	 * @return
	 */
	public static String getHttpUrl(String url, String servletName, String serviceName, String params, String appId)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(url);
		builder.append("/");
		builder.append(servletName);
		builder.append("?service=");
		builder.append(serviceName);
		if (StringUtils.isNotBlank(params))
		{
			builder.append("&params=");
			builder.append(params);
		}
		builder.append("&appId=");
		builder.append(appId);
		
		return builder.toString();
	}
	
	/**
	 * 
	* Description:下载图片保存到文件
	*
	* @param downloadUrl 下载地址
	* @param saveFile 保存的文件
	 */
	public static void downloadToFile(String downloadUrl, File saveFile) {
		InputStream input = null;
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			URL url = new URL(downloadUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(30 * 1000);
			connection.setReadTimeout(30 * 1000);
			input = connection.getInputStream();
			fos = new FileOutputStream(saveFile);
			bos = new BufferedOutputStream(fos);
			byte[] buff = new byte[1024];
			int len;
			while ((len = input.read(buff)) != -1) {
				bos.write(buff, 0, len);
			}
			bos.flush();
		} catch (Exception e) {
			logger.error("下载文件异常", e);
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				logger.error("bos.close()异常", e);
			}
			
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				logger.error("input.close()异常", e);
			}
			
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				logger.error("fos.close()异常", e);
			}
		}
	}
}
