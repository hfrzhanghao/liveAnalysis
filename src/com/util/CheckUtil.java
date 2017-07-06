/**
 * 南京青牛通讯技术有限公司
 * 日期：$$Date: 2016-09-06 17:30:34 +0800 (周二, 06 九月 2016) $$
 * 作者：$$Author: zhang.hao $$
 * 版本：$$Rev: 129750 $$
 * 版权：All rights reserved.
 */
package com.util;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;
import com.external.common.dto.CheckObjectDto;
import com.external.common.dto.MessageInfoDto;
import com.external.exception.ExternalServiceException;

public class CheckUtil
{
	private static Logger logger = LoggerFactory.getLogger(CheckUtil.class);

	/**
	 * 校验是否为JSON格式，请求参数为空返回NULL
	 * 
	 * @param json
	 * @return
	 */
	public static CheckObjectDto checkJson(String json)
	{
		CheckObjectDto co = new CheckObjectDto();
		JSONObject jo = null;
		// 参数为空的时候 默认检查成功
		if (StringUtils.isEmpty(json))
		{
			jo = new JSONObject();
			co.setJsonObject(jo);
			co.setFlag(true);
		}
		else
		{
			try
			{
				jo = JSONObject.fromObject(json)/*JSON.parseObject(json)*/;
				co.setJsonObject(jo);
				co.setFlag(true);
			}
			catch (Exception e)
			{
				logger.error(json, e.getMessage());
				co.setFlag(false);
			}
		}
		
		return co;
	}
	
	/**
	 * 必须check
	 * 
	 * @param json
	 * @return
	 */
	public static void checkEmpty(JSONObject params, String... keys) {
		StringBuilder sb  = new StringBuilder();
		for (String key : keys) {
			String value = params.getString(key);
			if (StringUtil.isBlank(value)) {
				sb.append(",");
				sb.append(key);
			}
		}

		if (sb.length() > 0) {
			MessageInfoDto messageInfo = PropertiesUtil.getMessageInfoByKey("params.empty");
			String message = messageInfo.getMessage();
			sb.delete(0, 1);
			message = MessageFormat.format(message, sb.toString());
			messageInfo.setMessage(message);
			
			throw new ExternalServiceException(messageInfo);
		}
	}
	
	/**
	 * 必须check
	 * 
	 * @param json
	 * @return
	 */
	public static void checkEmptyExceptEmptyString(JSONObject params, String... keys) {
		StringBuilder sb  = new StringBuilder();
		for (String key : keys) {
			String value = params.getString(key);
			if (value == null) {
				sb.append(",");
				sb.append(key);
			}
		}

		if (sb.length() > 0) {
			MessageInfoDto messageInfo = PropertiesUtil.getMessageInfoByKey("params.empty");
			String message = messageInfo.getMessage();
			sb.delete(0, 1);
			message = MessageFormat.format(message, sb.toString());
			messageInfo.setMessage(message);
			
			throw new ExternalServiceException(messageInfo);
		}
	}
}
