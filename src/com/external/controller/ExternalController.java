package com.external.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONObject;
import com.external.common.ObjectFactory;
import com.external.common.IBaseService;
//import com.channelsoft.setsail.external.common.IOrignalBaseService;
import com.external.common.dto.CheckObjectDto;
import com.external.common.dto.MessageInfoDto;
import com.external.common.dto.ResponseDto;
import com.external.exception.ExternalServiceException;
/*import com.channelsoft.setsail.external.report.ReportCache;
 import com.channelsoft.setsail.external.report.dto.ReportInfoDto;*/
import com.util.CheckUtil;
import com.util.PropertiesUtil;
import com.util.StringUtil;
import com.util.BeanFactoryUtil;

/**
 * <dl>
 * <dt>ExternalController.java</dt>
 * <dd>Description:接口服务类</dd>
 * <dd>Company: 北京红云融通技术有限公司</dd>
 * <dd>CreateDate: 2016-2-26</dd>
 * </dl>
 */
@Controller
public class ExternalController {

	private static Logger logger = LoggerFactory.getLogger(ExternalController.class);

	/**
	 * 后台管理接口入口。
	 * 
	 * @param service
	 *            调用接口服务名
	 * @param params
	 *            调用接口参数名
	 * @param request
	 *            request对象
	 * @return 调用结果
	 * @throws UnsupportedEncodingException 
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/externalService")
	@ResponseBody
	public String executeExternalService(HttpServletRequest request, HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		response.addHeader("Access-Control-Allow-Origin", "*"); // 跨域访问用
		response.setHeader("contentType", "text/html; charset=utf-8");
		String service = request.getParameter("service");
		String params = "";
		System.out.println(request.getParameter("params"));
		params = request.getParameter("params");

		ResponseDto rspDto = new ResponseDto();
		String rspStr = "";
		try {
			// 固定参数检查

			// 判断参数是否是Json数据
			CheckObjectDto co = CheckUtil.checkJson(params);
			JSONObject jobj = co.getJsonObject();
			jobj.put("service", service);
			IBaseService baseService = (IBaseService) BeanFactoryUtil.getExternalBean("externalService");
			rspDto = baseService.process(JSONObject.fromObject(jobj.toString()), request, response);
			
			if (rspDto.getState() == 0) {
				// rspDto.setMessageInfo(messageInfo);
			}
			if (rspDto.getState() == 2) {
				// rspDto.setMessageInfo(messageInfo);
			}
		} catch (BeansException e) {
			logger.error("BeansException:", e);
		} catch (ExternalServiceException e) {
			rspDto.setMessageInfo(e.getMessageInfo());
		} catch (Throwable e) {
			logger.error("exception:", e);
		} finally {
			if(rspDto.getData() != null){
				rspStr = rspDto.getData().toString();
			}else{
				JSONObject jo = new JSONObject();
				jo.put("result", -2);
				jo.put("info", "数据获取异常");
				rspStr = jo.toString();
			}
			// clock.stop();
		}
		return rspStr;
	}
}
