package com.facade;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.CommStats;
import com.db.business.AllLiveService;
import com.db.business.impl.AllLiveServiceImpl;
import com.dto.AllIPDto;
import com.dto.AllLiveDto;
import com.dto.AllLogDto;
import com.external.common.CommonConstants;
import com.result.ItemsResult;

@Service
@Path("/all")
public class AllServiceFacade {
	private Logger logger = Logger.getLogger(this.getClass());

	@Path("/liveCount")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String liveCount(
			@FormParam("startTime") long startTime, @FormParam("endTime") long endTime,
			@FormParam("playType") String playType, 
			@FormParam("url") String url, 
			@FormParam("domain") String domain, 
			@FormParam("isp") String isp,
			@FormParam("openType") String openType, 
			@FormParam("businessID") String businessID, 
			@FormParam("userName") String userName,
			@FormParam("durationSelect") String durationSelect, @FormParam("duration") String duration, 
			@FormParam("firstPicDurationSelect") String firstPicDurationSelect, @FormParam("firstPicDuration") String firstPicDuration, 
			@FormParam("service") String service) {

		AllLiveService biz = null;
		biz = new AllLiveServiceImpl(startTime,endTime);
		ItemsResult<AllLiveDto> result = new ItemsResult<AllLiveDto>();
		try {
			AllLiveDto listRow = null;

			listRow = biz.getData(startTime, endTime, url,domain,isp,openType,businessID,userName, durationSelect,duration,firstPicDurationSelect,firstPicDuration,service,CommonConstants.DEFAULT_PAGE_SIZE);

			if (listRow == null) {
				result.setResult(2);
			}
			result.setData(listRow);
		} catch (Exception e) {
			String message = e.getMessage() == null ? "" : e.getMessage();
			logger.error(message, e);
			result.setResult(-1);
		}
		String data = JSONObject.toJSONString(result/*, SerializerFeature.EMPTY*/);
		/*byte[] data = null;
		try {
			byte[] input = JSONObject.fromObject(result).toString().getBytes("UTF-8");
			data = GZipUtils.compress(input);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return data;
	}
	
	@Path("/logCheck")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON) 
	public String logCheck(@FormParam("startTime") long startTime, @FormParam("endTime") long endTime,@FormParam("playType") String playType, @FormParam("url") String url, @FormParam("IP") String ip) {

		AllLiveService biz = null;
		biz = new AllLiveServiceImpl(startTime,endTime);
		ItemsResult<AllLogDto> result = new ItemsResult<AllLogDto>();
		
		try {
			AllLogDto allLogDto = new AllLogDto();
			List<Map<String, String>> listRow = null;

			listRow = biz.getDataByIPUrl(startTime, endTime,url,ip, CommonConstants.DEFAULT_PAGE_SIZE);
			allLogDto.setList(listRow);

			if (listRow == null) {
				result.setResult(2);
			}
			result.setData(allLogDto);
		} catch (Exception e) {
			String message = e.getMessage() == null ? "" : e.getMessage();
			logger.error(message, e);
			result.setResult(-1);
		}
		String data = JSONObject.toJSONString(result);
		/*byte[] data = null;
		try {
			byte[] input = JSONObject.fromObject(result).toString().getBytes("UTF-8");
			data = GZipUtils.compress(input);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return data;
	}
	
	@Path("/ipCheck")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String ipCheck(@FormParam("ishigh") String ishigh,
			@FormParam("startTime") long startTime, @FormParam("endTime") long endTime, @FormParam("playType") String playType,
			@FormParam("url") String url, 
			@FormParam("lockCount") int lockCount,@FormParam("lockCountSelect") String lockCountSelect,
			@FormParam("picDuration") double picDuration,@FormParam("picDurationSelect") String picDurationSelect,
			@FormParam("result") String result_content) {

		AllLiveService biz = null;
		biz = new AllLiveServiceImpl(startTime,endTime);
		ItemsResult<AllIPDto> result = new ItemsResult<AllIPDto>();
		try {
			AllIPDto allIPDto = new AllIPDto();
			List<String> listRow = null;

			if(ishigh != null){
				listRow = biz.getIPByAll(startTime, endTime,url,lockCount,lockCountSelect,picDuration,picDurationSelect,result_content, CommonConstants.DEFAULT_PAGE_SIZE);
			}else{
				if(picDuration >= 0){
					listRow = biz.getIPByPicDuration(startTime, endTime,url,picDuration,picDurationSelect, CommonConstants.DEFAULT_PAGE_SIZE);
				}else if(lockCount >= 0){
					listRow = biz.getIPByLockCount(startTime, endTime,url,lockCount,lockCountSelect, CommonConstants.DEFAULT_PAGE_SIZE);
				}else if(result_content != null){
					listRow = biz.getIPByResult(startTime, endTime,url, result_content,CommonConstants.DEFAULT_PAGE_SIZE);
				}
			}
			allIPDto.setList(listRow);

			if (listRow == null) {
				result.setResult(2);
			}
			result.setData(allIPDto);
		} catch (Exception e) {
			String message = e.getMessage() == null ? "" : e.getMessage();
			logger.error(message, e);
			result.setResult(-1);
		}
		String data = JSONObject.toJSONString(result);
		/*byte[] data = null;
		try {
			byte[] input = JSONObject.fromObject(result).toString().getBytes("UTF-8");
			data = GZipUtils.compress(input);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return data;
	}
}
