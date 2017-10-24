package com.external.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import net.sf.json.JSONObject;
import com.http.HttpRequest.HttpMethod;
import com.external.common.CommonConstants;
import com.external.common.IBaseService;
import com.external.common.dto.ResponseDto;
import com.external.dto.GetAlbumListDto;
import com.external.proxy.RestTemplateProxy;
import com.util.CheckUtil;
import com.util.PropertiesUtil;

/**
 * <dl>
 * <dt>ExternalService.java</dt>
 * <dd>Copyright: Copyright (C) 2016</dd>
 * <dd>Company: 北京红云融通技术有限公司</dd>
 * <dd>CreateDate: 2016年8月30日</dd>
 * </dl>
 * 
 * @author Cyril
 * 
 */
@Service("externalService")
public class ExternalService implements IBaseService {

	private static Logger logger = LoggerFactory.getLogger(ExternalService.class);
	
	@Autowired
    protected RestTemplateProxy proxy;
	
	/*@Autowired
	private ExternalApiService externalApiService;*/
	

	/**
	 * process 处理POST请求返回结果，包括返回码和返回码描述
	 * 
	 * @param params
	 *            业务参数
	 * @param request
	 *            request对象
	 * @param response
	 *            response对象
	 * @return 返回码及描述
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResponseDto<JSONObject> process(JSONObject params, HttpServletRequest request, HttpServletResponse response) {
		MultiValueMap<String, Object> form = new LinkedMultiValueMap<String, Object>();
		ResponseDto<JSONObject> responseDto = new ResponseDto<JSONObject>();
		if(params.get("service") == null){
			JSONObject message = new JSONObject();
			message.put("result", -1);
			message.put("info", "缺少service参数");
			responseDto.setData(message);
			return responseDto;
		}
		form.add("service", params.get("service") + "");
		
		if(!params.get("service").equals("getEnumValue")){
			if(params.get("startTime") == null || params.get("endTime") == null){
				JSONObject message = new JSONObject();
				message.put("result", -1);
				message.put("info", "缺少startTime或endTime参数");
				responseDto.setData(message);
				return responseDto;
			}
			form.add("startTime", params.get("startTime") + "");
			form.add("endTime", params.get("endTime") + "");
		}
		
		if(params.get("service").equals("showPopular")){
			if(params.get("topN") == null || params.get("domainName") == null){
				JSONObject message = new JSONObject();
				message.put("result", -1);
				message.put("info", "缺少域名参数domainName或前N个内容参数topN");
				responseDto.setData(message);
				return responseDto;
			}
		}
		
		String service = params.get("service") + "";
		
		String playType = "1";
		
		if(params.get("playType") != null){
			playType = params.get("playType") + "";
		}
		
		if(params.get("topN") != null){
			form.add("topN", params.get("topN").toString());
		}
		
		if(params.get("domainName") != null){
			form.add("domainName", params.get("domainName"));
		}
		
		if(params.get("url") != null){
			form.add("url", params.get("url"));
		}
		
		if(params.get("domain") != null){
			form.add("domain", params.get("domain"));
		}
		
		if(params.get("isp") != null){
			form.add("isp", params.get("isp"));
		}
		
		if(params.get("openType") != null){
			form.add("openType", params.get("openType"));
		}
		
		if(params.get("businessID") != null){
			form.add("businessID", params.get("businessID"));
		}
		
		if(params.get("userName") != null){
			form.add("userName", params.get("userName"));
		}
		
		if(params.get("userAmount") != null){
			form.add("userAccount", params.get("userAmount")+"");
		}
		
		if(params.get("duration") != null){
			form.add("duration", params.get("duration"));
		}
		
		if(params.get("durationSelect") != null){
			form.add("durationSelect", params.get("durationSelect"));
		}
		
		if(params.get("firstPicDuration") != null){
			form.add("firstPicDuration", params.get("firstPicDuration"));
		}
		
		if(params.get("firstPicDurationSelect") != null){
			form.add("firstPicDurationSelect", params.get("firstPicDurationSelect"));
		}

		JSONObject jsonObject = null;
		try {
			// 将查询框表单值传送到monitorServer
			if(service.equals("showTopNUser")){
				jsonObject = proxy.postFormWithReturnJSONObject1(CommonConstants.LIVE_USER_URL, form);
			}else if(service.equals("showPopular")){
				logger.info("CommonConstants.LIVE_POPULAR_URL:" + CommonConstants.LIVE_POPULAR_URL);
				jsonObject = proxy.postFormWithReturnJSONObject1(CommonConstants.LIVE_POPULAR_URL, form);
			}else if(service.equals("getEnumValue")){
				logger.info("CommonConstants.GET_ENUM_URL:" + CommonConstants.GET_ENUM_URL);
				jsonObject = proxy.postFormWithReturnJSONObject1(CommonConstants.GET_ENUM_URL, form);
			}else{
				if(CommonConstants.OLD_DATA == 1 && (params.get("duration") != null || params.get("firstPicDuration") != null || Long.parseLong(params.get("startTime")+"") < Long.parseLong(CommonConstants.NEW_DATA_TIME))){
					logger.info("CommonConstants.LIVE_ANALYSIS_URL:" + CommonConstants.LIVE_ANALYSIS_URL);
					jsonObject = proxy.postFormWithReturnJSONObject1(CommonConstants.LIVE_ANALYSIS_URL, form);
				}else{
					logger.info("CommonConstants.LIVE_ANALYSIS_URL_PRETREAT:" + CommonConstants.LIVE_ANALYSIS_URL_PRETREAT);
					jsonObject = proxy.postFormWithReturnJSONObject1(CommonConstants.LIVE_ANALYSIS_URL_PRETREAT, form);
				}
				/*if(params.get("duration") != null || params.get("firstPicDuration") != null || Long.parseLong(params.get("startTime")+"") < Long.parseLong(CommonConstants.NEW_DATA_TIME)){
					logger.info("CommonConstants.LIVE_ANALYSIS_URL:" + CommonConstants.LIVE_ANALYSIS_URL);
					jsonObject = proxy.postFormWithReturnJSONObject1(CommonConstants.LIVE_ANALYSIS_URL, form);
				}else{
					logger.info("CommonConstants.LIVE_ANALYSIS_URL_PRETREAT:" + CommonConstants.LIVE_ANALYSIS_URL_PRETREAT);
					jsonObject = proxy.postFormWithReturnJSONObject1(CommonConstants.LIVE_ANALYSIS_URL_PRETREAT, form);
				}*/
			}
			logger.info("CDN统计返回数据：正常");

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		responseDto.setData(jsonObject);
		return responseDto;
	}
}
