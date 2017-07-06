package com.creator.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import com.AboutTime;
import com.creator.ICreator;
import com.db.entity.BaseEntity;
import com.db.entity.LiveInfoEntity;
import com.db.entity.PlayDataEntity;
import com.dto.BaseCols;
import com.dto.StatCol;
import com.dto.StatRow;
import com.external.common.CommonConstants;

public class UserLeaveCreator implements ICreator{

	Map<String, BaseCols> m_tab = new TreeMap<String, BaseCols>();
	Map<String, Integer> userLeaveMap = new TreeMap<String, Integer>();
	List<Long> userLeaveTimeList = new ArrayList<Long>();
	long startTime = 0;
	long endTime = 0;
	
	long timeScale = 0;
	
	public UserLeaveCreator(long start,long end){
		startTime = start - start % 60000;
		endTime = end;
	}
	
	@Override
	public void insertRecord(PlayDataEntity liveinfo) {
		long end = AboutTime.toLongSSS(liveinfo.getEnd_time());
		int index = (int) Math.ceil((end - startTime) / (double)timeScale);
		int timscale = CommonConstants.TIME_SCALE;
		if(index >= 0 && index <= timscale){
			long userLeave = userLeaveTimeList.get(index);
			userLeaveMap.put(userLeave + "", userLeaveMap.get(userLeave + "") + 1);
		}
		
	}

	@Override
	public Object getRowList() {
		Map<String, StatRow> listRow = new TreeMap<String, StatRow>();
		for (String key : m_tab.keySet()) {
			StatRow naStatRow = new StatRow();

			naStatRow.setName(key);
			naStatRow.setDescription(key);
			naStatRow.setCols(m_tab.get(key).getCols());

			listRow.put(key, naStatRow);

			List<StatCol> cols = new ArrayList<StatCol>();

			StatCol countCol = new StatCol();
			Map<String, BaseEntity> valuesCount = new HashMap<String, BaseEntity>();
			BaseEntity entity_count = new BaseEntity();

			entity_count.setUnit("个");
			entity_count.setName("userLeave_NUM");
			entity_count.setValue(userLeaveMap.get(key));
			valuesCount.put("sample_number", entity_count);

			countCol.setValues(valuesCount);
			countCol.setName("次数");
			countCol.setDescription("次数");

			cols.add(countCol);

			listRow.get(key + "").setCols(cols);
		}
		return listRow;
	}

	@Override
	public void init(JSONObject config, Map<String, String> dictionary) {
		long starttime = startTime;
		long endtime = endTime;
		long timeDifference = endtime - starttime;
		
		int timescale = CommonConstants.TIME_SCALE;
		timeScale = timeDifference / timescale; //共288个刻度点
		List<Long> timePoint = new ArrayList<Long>();
		
		for(int i = 0; i <= timescale; i++){
			long tempTime = starttime + i * timeScale;
			timePoint.add(tempTime);
			m_tab.put(tempTime + "", new BaseCols());
			userLeaveTimeList.add(tempTime);
			userLeaveMap.put(tempTime + "", 0);
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertRecordWithTimeRange(LiveInfoEntity liveInfo, long starttime, long endtime) {
		// TODO Auto-generated method stub
		
	}

}
