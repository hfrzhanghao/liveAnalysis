package com.db.business;

import java.util.List;
import com.dto.AllLiveDto;
import com.dto.PopularDto;

public interface AllLiveWithPreService {
	public AllLiveDto getDataWithPretreat(long startTime, long endTime, String url, String domainNameFilter,String domain, String isp, String openType, String businessID, String userName,String service, int pageSize);
	public List<PopularDto> getPopular(long startTime, long endTime, String domainName, String topN, int pageSize);
}
