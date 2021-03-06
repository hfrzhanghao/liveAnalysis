package com.db.business;

import com.dto.AllLiveDto;

public interface AllLiveService {
	//public List<UserDto> getUser(long startTime, long endTime,String url,String domain,String isp,String openType,String businessID,String userName,String durationSelect,String duration,String firstPicDurationSelect,String firstPicDuration,String lockCount,String firstPicMin,String firstPicMax,String userAccount, String service,int pageSize);
	public AllLiveDto getData(long startTime, long endTime,String url,String domain,String isp,String openType,String businessID,String userName,String durationSelect,String duration,String firstPicDurationSelect,String firstPicDuration,String service,int pageSize);
	//public List<PopularDto> getPopular(long startTime, long endTime, String domainName, String topN);
	//public List<Map<String, String>> getDataByIPUrl(long startTime, long endTime,String url,String cip,int pageSize);
	//public List<String> getIPByLockCount(long startTime, long endTime,String url,int lockCount,String lockCountSelect,int pageSize);
	//public List<String> getIPByPicDuration(long startTime, long endTime,String url,double picDuration,String picDurationSelect,int pageSize);
	//public List<String> getIPByResult(long startTime, long endTime,String url,String checkResult,int pageSize);
	//public List<String> getIPByAll(long startTime, long endTime,String url,int lockCount,String lockCountSelect,double picDuration,String picDurationSelect,String checkResult,int pageSize);
	//public AllLiveDto getDataWithPretreat(long startTime, long endTime, String url, String domain, String isp, String openType, String businessID, String userName,String service, int pageSize);
	
}
