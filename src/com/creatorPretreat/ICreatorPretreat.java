package com.creatorPretreat;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.db.entity.LiveInfoEntity;

public interface ICreatorPretreat {
	public  void insertRecord(JSONObject jSONObject);
	public  Object getRowList();
	public  void init(JSONObject config,Map<String, String> dictionary);
	public  String getName();
	public void insertRecordWithTimeRange(LiveInfoEntity liveInfo,long starttime,long endtime);
}
