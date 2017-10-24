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
import com.db.business.AllLiveWithPreService;
import com.db.business.impl.AllLiveServiceImpl;
import com.db.business.impl.AllLiveServiceWithPreImpl;
import com.dto.AllIPDto;
import com.dto.AllLiveDto;
import com.dto.AllLogDto;
import com.dto.PopularDto;
import com.dto.UserDto;
import com.external.common.CommonConstants;
import com.result.ItemsResult;
import com.result.PopularResult;
import com.result.UsersResult;

@Service
@Path("/popular")
public class PopularServiceFacade {
	private Logger logger = Logger.getLogger(this.getClass());

	@Path("/livePopularCount")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String livePopularCount(
			@FormParam("startTime") long startTime, @FormParam("endTime") long endTime,
			@FormParam("domainName") String domainName, 
			@FormParam("topN") String topN) {

		AllLiveWithPreService biz = null;
		biz = new AllLiveServiceWithPreImpl(startTime,endTime);
		PopularResult result = new PopularResult();
		try {
			List<PopularDto> listRow = null;

			listRow = biz.getPopular(startTime, endTime, domainName, topN, CommonConstants.DEFAULT_PAGE_SIZE);

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
		return data;
	}
}
