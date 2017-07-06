package com.creator;

import java.util.Map;

import net.sf.json.JSONObject;

import com.db.entity.LiveInfoEntity;
import com.db.entity.PlayDataEntity;

public interface ICreator {
	public  void insertRecord(PlayDataEntity liveinfo);
	public  Object getRowList();
	public  void init(JSONObject config,Map<String, String> dictionary);
	public  String getName();
	public void insertRecordWithTimeRange(LiveInfoEntity liveInfo,long starttime,long endtime);
}
