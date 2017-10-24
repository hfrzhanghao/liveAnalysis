package com.db.business;

import java.util.List;
import java.util.Map;

import com.dto.AllLiveDto;
import com.dto.PopularDto;
//import com.dto.AllLogDto;
import com.dto.UserDto;

public interface AllLiveWithPreService {
	public AllLiveDto getDataWithPretreat(long startTime, long endTime, String url, String domain, String isp, String openType, String businessID, String userName,String service, int pageSize);
	public List<PopularDto> getPopular(long startTime, long endTime, String domainName, String topN, int pageSize);
}
