package com.db.dao;

import java.util.List;

import net.sf.json.JSONObject;

import com.dto.LiveListDto;
import com.dto.PlayListDto;

public interface LiveInfoDao {

	public PlayListDto findLive(long startTime,long endTime,String url,String domain,String isp,String openType,String businessID,String userName,String durationSelect,String duration,String firstPicDurationSelect,String firstPicDuration,int pageSize, int currPage,boolean iscount);
	//public LiveListDto findLive(long startTime,long endTime,String url,String domain,String isp,String openType,String businessID,String userName,String durationSelect,String duration,String firstPicDurationSelect,String firstPicDuration,int pageSize, int currPage,boolean iscount);
	public PlayListDto findDurationUser(long startTime,long endTime,String url,String domain,String isp,String openType,String businessID,String userName,String durationSelect,String duration,String firstPicDurationSelect,String firstPicDuration,String userAccount,int totalUser, boolean iscount);
	public PlayListDto findLockUser(long startTime,long endTime,String url,String domain,String isp,String openType,String businessID,String userName,String durationSelect,String duration,String firstPicDurationSelect,String firstPicDuration,String lockCount,int totalUser, boolean iscount);
	public PlayListDto findFirstPicDurationUser(long startTime,long endTime,String url,String domain,String isp,String openType,String businessID,String userName,String durationSelect,String duration,String firstPicDurationSelect,String firstPicDuration,String firstPicMin,String firstPicMax,int totalUser, boolean iscount);
	public PlayListDto findLiveLog(long startTime, long endTime,String url,String cip,int pageSize, int currPage, boolean iscount);
	public PlayListDto findLiveIPByLockCount(long startTime, long endTime,String url,int lockCount,String lockCountSelect,int pageSize, int currPage, boolean iscount);
	public PlayListDto findLiveIPByAll(long startTime, long endTime,String url,int lockCount,String lockCountSelect,double picDuration,String picDurationSelect,String result,int pageSize, int currPage, boolean iscount);
	public PlayListDto findLiveIPByPicDuration(long startTime, long endTime,String url,double picDuration,String picDurationSelect,int pageSize, int currPage, boolean iscount);
	public PlayListDto findLiveIPByResult(long startTime, long endTime,String url,String result,int pageSize, int currPage, boolean iscount);
	public LiveListDto findLiveAllFailedIPByResult(long startTime, long endTime,int pageSize, int currPage, boolean iscount);
	public List<JSONObject> findLiveWithPretreat(long startTime, long endTime, String url, String domain, String isp, String openType, String businessID, String userName,
			int pageSize, int currPage, boolean iscount);
	public List<JSONObject> findOnlineWithPretreat(long startTime, long endTime, String url, int pageSize, int currPage, boolean iscount);
	public List<JSONObject> findLivePopular(long startTime, long endTime, String domainName, String topN, int pageSize, int currPage, boolean iscount);
	
}
