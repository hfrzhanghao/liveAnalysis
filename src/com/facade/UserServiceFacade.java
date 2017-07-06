package com.facade;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;


import com.CommStats;
import com.DataTypesConvert;
import com.GZipUtils;
import com.db.business.AllLiveService;
import com.db.business.impl.AllLiveServiceImpl;
import com.dto.AllIPDto;
import com.dto.AllLiveDto;
import com.dto.AllLogDto;
import com.dto.UserDto;
import com.external.common.CommonConstants;
import com.result.ItemsResult;
import com.result.UsersResult;

@Service
@Path("/user")
public class UserServiceFacade {
	private Logger logger = Logger.getLogger(this.getClass());

	@Path("/lockCount")
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
			@FormParam("userAccount") String userAccount,
			@FormParam("durationSelect") String durationSelect, @FormParam("duration") String duration, 
			@FormParam("firstPicDurationSelect") String firstPicDurationSelect, @FormParam("firstPicDuration") String firstPicDuration, 
			@FormParam("lockCount") String lockCount,
			@FormParam("firstPicMin") String firstPicMin,@FormParam("firstPicMax") String firstPicMax, 
			@FormParam("service") String service) {

		AllLiveService biz = null;
		biz = new AllLiveServiceImpl(startTime,endTime);
		UsersResult result = new UsersResult();
		try {
			List<UserDto> listRow = null;

			listRow = biz.getUser(startTime, endTime, url, domain, isp, openType,businessID,userName, durationSelect,duration,firstPicDurationSelect,firstPicDuration,lockCount, firstPicMin,firstPicMax,userAccount,service, CommonConstants.DEFAULT_TOTAL_USER);

			if (listRow == null) {
				result.setResult(2);
			}
			result.setData(listRow);
		} catch (Exception e) {
			String message = e.getMessage() == null ? "" : e.getMessage();
			logger.error(message, e);
			result.setResult(-1);
		}
		String data = JSONObject.fromObject(result).toString();
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
