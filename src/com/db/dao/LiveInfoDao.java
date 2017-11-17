package com.db.dao;

import java.util.List;

import net.sf.json.JSONObject;

import com.dto.PlayListDto;

public interface LiveInfoDao {

	public PlayListDto findLive(long startTime,long endTime,String url,String domain,String isp,String openType,String businessID,String userName,String durationSelect,String duration,String firstPicDurationSelect,String firstPicDuration,int pageSize, int currPage,boolean iscount);
	public List<JSONObject> findLiveWithPretreat(long startTime, long endTime, String url,String domainNameFilter, String domain, String isp, String openType, String businessID, String userName,
			int pageSize, int currPage, boolean iscount);
	public List<JSONObject> findOnlineWithPretreat(long startTime, long endTime, String url, String domainNameFilter, int pageSize, int currPage, boolean iscount);
	public List<JSONObject> findLivePopular(long startTime, long endTime, String domainName, String topN, int pageSize, int currPage, boolean iscount);
	
}
