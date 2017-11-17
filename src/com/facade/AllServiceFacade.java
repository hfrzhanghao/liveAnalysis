package com.facade;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.db.business.AllLiveService;
import com.db.business.AllLiveWithPreService;
import com.db.business.impl.AllLiveServiceImpl;
import com.db.business.impl.AllLiveServiceWithPreImpl;
import com.dto.AllLiveDto;
import com.external.common.CommonConstants;
import com.result.ItemsResult;

@Service
@Path("/all")
public class AllServiceFacade {
	private Logger logger = Logger.getLogger(this.getClass());

	/*
	 * 统计时按照原始数据来统计
	 * */
	@Path("/liveCount")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String liveCount(
			@FormParam("startTime") long startTime, 
			@FormParam("endTime") long endTime,
			@FormParam("playType") String playType, 
			@FormParam("url") String url, 
			@FormParam("domain") String domain, 
			@FormParam("isp") String isp,
			@FormParam("openType") String openType, 
			@FormParam("businessID") String businessID, 
			@FormParam("userName") String userName,
			@FormParam("durationSelect") String durationSelect, 
			@FormParam("duration") String duration, 
			@FormParam("firstPicDurationSelect") String firstPicDurationSelect, 
			@FormParam("firstPicDuration") String firstPicDuration, 
			@FormParam("service") String service) {

		AllLiveService biz = null;
		biz = new AllLiveServiceImpl(startTime,endTime);
		ItemsResult<AllLiveDto> result = new ItemsResult<AllLiveDto>();
		try {
			AllLiveDto listRow = null;
			//获取数据
			listRow = biz.getData(startTime, endTime, url,domain,isp,openType,businessID,userName, durationSelect,duration,firstPicDurationSelect,firstPicDuration,service,CommonConstants.DEFAULT_PAGE_SIZE);
			if (listRow == null) {
				result.setResult(2);
			}
			//填充结果
			result.setData(listRow);
			listRow = null;
			System.gc();
			System.runFinalization();
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage() == null ? "" : e.getMessage();
			logger.error(message, e);
			result.setResult(-1);
		}
		String data = JSONObject.toJSONString(result);
		return data;
	}
	
	/*
	 * 统计时按照预处理后的数据来统计
	 * */
	@Path("/liveCountWithPretreat")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String liveCountWithPretreat(
			@FormParam("startTime") long startTime, 
			@FormParam("endTime") long endTime,
			@FormParam("playType") String playType, 
			@FormParam("url") String url, 
			@FormParam("domainNameFilter") String domainNameFilter, 
			@FormParam("domain") String domain, 
			@FormParam("isp") String isp,
			@FormParam("openType") String openType, 
			@FormParam("businessID") String businessID, 
			@FormParam("userName") String userName,
			@FormParam("service") String service) {

		AllLiveWithPreService biz = null;
		biz = new AllLiveServiceWithPreImpl(startTime,endTime);
		ItemsResult<AllLiveDto> result = new ItemsResult<AllLiveDto>();
		try {
			AllLiveDto listRow = null;
			//获取数据
			listRow = biz.getDataWithPretreat(startTime, endTime, url,domainNameFilter,domain,isp,openType,businessID,userName, service,CommonConstants.DEFAULT_PAGE_SIZE);
			if (listRow == null) {
				result.setResult(2);
			}
			//填充结果
			result.setData(listRow);
			listRow = null;
			System.gc();
			System.runFinalization();
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage() == null ? "" : e.getMessage();
			logger.error(message, e);
			result.setResult(-1);
		}
		String data = JSONObject.toJSONString(result);
		return data;
	}

}
