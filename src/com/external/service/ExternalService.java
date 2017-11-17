package com.external.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import net.sf.json.JSONObject;
import com.external.common.CommonConstants;
import com.external.common.IBaseService;
import com.external.common.dto.ResponseDto;
import com.external.proxy.RestTemplateProxy;

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
		//必须要有service参数，进行任务指派
		if(params.get("service") == null){
			JSONObject message = new JSONObject();
			message.put("result", -1);
			message.put("info", "缺少service参数");
			responseDto.setData(message);
			return responseDto;
		}
		form.add("service", params.get("service") + "");
		
		//时间范围参数（开始时间和结束时间）为必须参数
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
		
		//当service为showPopular时，表示要统计某一域名下的内容热度，那么域名domainName和前N个播放内容topN为必须参数
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
		
		//前多少个内容
		if(params.get("topN") != null){
			form.add("topN", params.get("topN").toString());
		}
		
		//域名（只在service为showPopular时有效）
		if(params.get("domainName") != null){
			form.add("domainName", params.get("domainName"));
		}
		
		//域名过滤参数，只统计某个域名下的各项数据（在service为showPopular以外时有效）
		if(params.get("domainNameFilter") != null){
			form.add("domainNameFilter", params.get("domainNameFilter"));
		}
		
		//内容id
		if(params.get("url") != null){
			form.add("url", params.get("url"));
		}
		
		//地区
		if(params.get("domain") != null){
			form.add("domain", params.get("domain"));
		}
		
		//运营商
		if(params.get("isp") != null){
			form.add("isp", params.get("isp"));
		}
		
		//打开方式
		if(params.get("openType") != null){
			form.add("openType", params.get("openType"));
		}
		
		//企业号
		if(params.get("businessID") != null){
			form.add("businessID", params.get("businessID"));
		}
		
		//用户名
		if(params.get("userName") != null){
			form.add("userName", params.get("userName"));
		}
		
		//用户数量
		if(params.get("userAmount") != null){
			form.add("userAccount", params.get("userAmount")+"");
		}
		
		//播放时长
		if(params.get("duration") != null){
			form.add("duration", params.get("duration"));
		}
		
		//大于 小于 等于
		if(params.get("durationSelect") != null){
			form.add("durationSelect", params.get("durationSelect"));
		}
		
		//出画面时间
		if(params.get("firstPicDuration") != null){
			form.add("firstPicDuration", params.get("firstPicDuration"));
		}
		
		//大于 小于 等于
		if(params.get("firstPicDurationSelect") != null){
			form.add("firstPicDurationSelect", params.get("firstPicDurationSelect"));
		}

		JSONObject jsonObject = null;
		try {
			// 根据service字段指派任务
			if(service.equals("showTopNUser")){ 
				//展示播放时长前N个用户，临时使用，已经不再维护
				jsonObject = proxy.postFormWithReturnJSONObject1(CommonConstants.LIVE_USER_URL, form);
			}else if(service.equals("showPopular")){
				//获取某个域名下按照热度排名的内容id
				logger.info("CommonConstants.LIVE_POPULAR_URL:" + CommonConstants.LIVE_POPULAR_URL);
				jsonObject = proxy.postFormWithReturnJSONObject1(CommonConstants.LIVE_POPULAR_URL, form);
			}else if(service.equals("getEnumValue")){
				//获取枚举值接口
				logger.info("CommonConstants.GET_ENUM_URL:" + CommonConstants.GET_ENUM_URL);
				jsonObject = proxy.postFormWithReturnJSONObject1(CommonConstants.GET_ENUM_URL, form);
			}else{
				//一般项统计
				//数据库中有两类数据：①播放器上报的数据结构化后直接插入数据库；②将数据进行预处理后再插入数据库。由于10月份后才有预处理数据，所以要对输入条件进行判断。
				if(CommonConstants.OLD_DATA == 1 && (params.get("duration") != null || params.get("firstPicDuration") != null || Long.parseLong(params.get("startTime")+"") < Long.parseLong(CommonConstants.NEW_DATA_TIME))){
					//如果统计参数中有播放时长duration、出画面时长firstPicDuration等统计条件，则只能按原始数据来统计，因为没有对这种情形进行预处理
					//如果统计的开始时间参数startTime在产生预处理数据之前，那么按照原始数据统计
					//还要保证原始数据的开关OLD_DATA打开（值为1），才能够统计原始数据，否则，不允许统计原始数据。
					logger.info("CommonConstants.LIVE_ANALYSIS_URL:" + CommonConstants.LIVE_ANALYSIS_URL);
					jsonObject = proxy.postFormWithReturnJSONObject1(CommonConstants.LIVE_ANALYSIS_URL, form);
				}else{
					//以上条件有一项不满足，都按照预处理数据来统计
					logger.info("CommonConstants.LIVE_ANALYSIS_URL_PRETREAT:" + CommonConstants.LIVE_ANALYSIS_URL_PRETREAT);
					jsonObject = proxy.postFormWithReturnJSONObject1(CommonConstants.LIVE_ANALYSIS_URL_PRETREAT, form);
				}
			}
			logger.info("CDN统计返回数据：正常");

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		responseDto.setData(jsonObject);
		return responseDto;
	}
}
