package com.external.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.external.common.CommonConstants;
import com.external.common.dto.MessageInfoDto;
import com.external.exception.ExternalServiceException;
import com.util.StringUtil;
import com.util.HttpUtils;
import com.util.PropertiesUtil;
import com.http.HttpRequest;
import com.http.HttpRequest.HttpMethod;
import com.http.HttpRequestWithJson;
import com.http.RequestParams;
import com.util.HttpUtil;

/**
 * <dl>
 * <dt>BaseApiService.java</dt>
 * <dd>Description:接口访问基础类</dd>
 * <dd>Company: 北京红云融通技术有限公司</dd>
 * <dd>CreateDate: 2016-2-26</dd>
 * </dl>
 * 
 * @author yusy
 */
@Service
public class BaseApiService {
	private Logger logger = LoggerFactory.getLogger(BaseApiService.class);
	
	/**
	 * 发送http请求
	* Description:
	*
	* @param method
	* @param url
	* @param params
	* @return
	 */
	public JSONObject sendHttpRequest(HttpMethod method, String url, RequestParams params) {
		try {
			HttpRequest request = new HttpRequest(method, url);
			request.setRequestParams(params);
			String result = HttpUtil.sendRequest(request);
			logger.info("http接口返回：" + result);
			JSONObject jsonObject = JSON.parseObject(result);
	
			if(jsonObject == null) {
				jsonObject = new JSONObject();
			}
			return jsonObject;
		} catch(Exception ex) {
			return new JSONObject();
		}
	}
	
	
	/**
	 * 发送http请求
	* Description:
	*
	* @param method
	* @param url
	* @param params
	* @return
	 */
	public JSONObject sendHttpRequestNolog(HttpMethod method, String url, RequestParams params) {
		try {
			HttpRequest request = new HttpRequest(method, url);
			request.setRequestParams(params);
			String result = HttpUtil.sendRequest(request);
			//logger.info("http接口返回：" + result);
			JSONObject jsonObject = JSON.parseObject(result);
	
			if(jsonObject == null) {
				jsonObject = new JSONObject();
			}
			return jsonObject;
		} catch(Exception ex) {
			return new JSONObject();
		}
	}
	
	/**
	 * 发送http请求
	* Description:
	*
	* @param method
	* @param url
	* @param params
	* @return
	 */
	public JSONObject sendHttpNativeRequest(HttpMethod method, String url, String params) {
		if (HttpMethod.POST == method) {
			return sendHttpPost(url, params);
		} else {
			return sendHttpGet(url, params);
		}
	}
	
	public JSONObject sendHttpPost(String url, String params) {
		try {
			logger.info("url:{},params:{}", url, params);
			String result = HttpUtils.sendPost(url, params);
			logger.info("result:{}",  result);
			
			JSONObject jsonObject = JSON.parseObject(result);
	
			if(jsonObject == null) {
				jsonObject = new JSONObject();
			}
			return jsonObject;
		} catch(Exception ex) {
			return new JSONObject();
		}
	}
	
	public JSONObject sendHttpGet(String url, String params) {
		try {
			logger.info("url:{}", url + "?" + params);
			String result = HttpUtils.sendGet(url + "?" + params);
			logger.info("result:{}", result);
			
			JSONObject jsonObject = JSON.parseObject(result);
	
			if(jsonObject == null) {
				jsonObject = new JSONObject();
			}
			return jsonObject;
		} catch(Exception ex) {
			return new JSONObject();
		}
	}
	
	/**
	 * 发送http请求,POST方式，只传Url和Json字符串
	* Description:
	*
	* @param method
	* @param url
	* @param params
	* @return
	 */
	public JSONObject sendHttpRequestWithJsonPost(String url, String JsonString) {
		try {
			HttpRequestWithJson httpRequestWithJson = new HttpRequestWithJson(url);
			String result = httpRequestWithJson.post(JsonString);
			logger.info("http接口返回：" + result);
			JSONObject jsonObject = JSON.parseObject(result);
	
			if(jsonObject == null) {
				jsonObject = new JSONObject();
			}
			return jsonObject;
		} catch(Exception ex) {
			return new JSONObject();
		}
	}
	
	/**
	 * 上传文件
	 * @param file
	 * @param fileUploadUrl
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public JSONObject postFile(File file, String fileUploadUrl) 
			throws ClientProtocolException, IOException {			
		JSONObject resJsonObject = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		logger.info("fileUploadUrl=" + fileUploadUrl);
		
		try {			
			HttpPost httppost = new HttpPost(fileUploadUrl);
			FileBody bin = new FileBody(file);
			StringBody comment = new StringBody("A binary file of some kind",
					ContentType.TEXT_PLAIN);
			HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addPart("bin", bin).addPart("comment", comment).build();
			httppost.setEntity(reqEntity);
			
			logger.info("executing request " + httppost.getRequestLine());
			
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					String resString = inputStream2String(resEntity
							.getContent());
					
					logger.info(resString);
					
					resJsonObject = JSONObject.parseObject(resString);
				}
				EntityUtils.consume(resEntity);
			} finally {
				response.close();
			}
		} 
		catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
		} 
		catch (IOException e) {
			logger.error("IOException", e);
		} 
		finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error("IOException", e);
			}			
		}
		return resJsonObject;
	}
	
	/**
	 * 上传文件
	 * @param file
	 * @param fileUploadUrl
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public JSONObject postFile(JSONObject paramObj, String fileName, File file, String fileUploadUrl) {			
		JSONObject resJsonObject = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		logger.info("url:{},params：{}", fileUploadUrl, paramObj.toJSONString());
		
		try {			
			HttpPost httppost = new HttpPost(fileUploadUrl);
			FileBody bin = new FileBody(file);
			StringBody comment = new StringBody("A binary file of some kind",
					ContentType.TEXT_PLAIN);
			MultipartEntityBuilder buider = MultipartEntityBuilder.create()
					.addPart(fileName, bin).addPart("comment", comment);
			if (paramObj != null) {
				for (Entry<String, Object> entry : paramObj.entrySet()) {
					buider.addTextBody(entry.getKey(), String.valueOf(entry.getValue()));
				}
			}
			HttpEntity reqEntity = buider.build();
			httppost.setEntity(reqEntity);
			
			logger.info("executing request " + httppost.getRequestLine());
			
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					String resString = inputStream2String(resEntity
							.getContent());
					
					logger.info(resString);
					
					resJsonObject = JSONObject.parseObject(resString);
				}
				EntityUtils.consume(resEntity);
			} finally {
				response.close();
			}
		} 
		catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
		} 
		catch (IOException e) {
			logger.error("IOException", e);
		} 
		finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error("IOException", e);
			}			
		}
		return resJsonObject;
	}
	
	public static String inputStream2String(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}
	
	/**
     * 解析结果集
     * 
     * @param result 字符串
     * @return JSONObject
     */	
	public JSONObject resolveResult(JSONObject result) {
		if (result == null || result.isEmpty())
		{
			throw new ExternalServiceException(PropertiesUtil.getMessageInfoByKey("http.call.failure"));
		} else {
			int status = Integer.parseInt(result.getString("status"));
			if (CommonConstants.STATE_SUCCESS != status) {
				String message = result.getString("message");
				throw new ExternalServiceException(new MessageInfoDto(status, message));
			}
		}
		
		return result;
	}
	
	/**
     * 解析结果集
     * 
     * @param result 字符串
     * @return JSONObject
     */	
	public JSONObject resolveOrginalResult(JSONObject returnObj) {
		if (returnObj == null || returnObj.isEmpty())
		{
			throw new ExternalServiceException(PropertiesUtil.getMessageInfoByKey("http.call.failure"));
		} 
		int code = returnObj.getIntValue("code");
    	// 有异常时直接返回
    	if (!StringUtil.isRspSucess(code)) {
    		MessageInfoDto messageInfoDto = new MessageInfoDto();
    		messageInfoDto.setState(returnObj.getIntValue("code"));
    		messageInfoDto.setMessage(returnObj.getString("message"));
    		throw new ExternalServiceException(messageInfoDto);
    	} else {
    		MessageInfoDto messageInfo = PropertiesUtil.getMessageInfoSuccess();
			returnObj.remove("code");
			returnObj.put("state", messageInfo.getState());
			returnObj.put("message", messageInfo.getMessage());
    	}
		
		return returnObj;
	}
	
	/**
     * 解析结果集
     * 
     * @param result 字符串
     * @return JSONObject
     */	
	public JSONObject resolveOrginalResultWithNoException(JSONObject returnObj) {
		if (returnObj == null || returnObj.isEmpty())
		{
			throw new ExternalServiceException(PropertiesUtil.getMessageInfoByKey("http.call.failure"));
		} 
		int code = returnObj.getIntValue("code");
    	// 有异常时直接返回
    	if (StringUtil.isRspSucess(code)) {
    		MessageInfoDto messageInfo = PropertiesUtil.getMessageInfoSuccess();
			returnObj.remove("code");
			returnObj.put("state", messageInfo.getState());
			returnObj.put("message", messageInfo.getMessage());
    	} else {
    		returnObj.put("state", code);
			returnObj.remove("code");
    	}
		
		return returnObj;
	}
	
	public static void main(String[] args) {
		BaseApiService servie = new BaseApiService();
		JSONObject paramObj = new JSONObject();
		paramObj.put("uid", 74789);
		paramObj.put("oauth_token", "62d3bc0464a14c87c74799b38ba72b07");
		JSONObject returnObj = servie.postFile(paramObj, "userfile", new File("F:/picture/head.jpg"), "http://localhost:8080/setsail/external/orginalService/avatar");
	    System.err.print(returnObj.toJSONString());
	}
}
