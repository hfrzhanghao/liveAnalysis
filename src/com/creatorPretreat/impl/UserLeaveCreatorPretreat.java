package com.creatorPretreat.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.creatorPretreat.ICreatorPretreat;
import com.db.entity.BaseEntity;
import com.db.entity.LiveInfoEntity;
import com.dto.BaseCols;
import com.dto.StatCol;
import com.dto.StatRow;
import com.external.common.CommonConstants;

public class UserLeaveCreatorPretreat implements ICreatorPretreat{

	Map<String, BaseCols> m_tab = new TreeMap<String, BaseCols>();
	Map<String, Integer> userLeaveMap = new TreeMap<String, Integer>();
	List<Long> userLeaveTimeList = new ArrayList<Long>();
	long startTime = 0;
	long endTime = 0;
	
	long timeScale = 0;
	
	public UserLeaveCreatorPretreat(long start,long end){
		startTime = start - start % 60000;
		endTime = end - end % 60000 + 60000;
	}
	
	@Override
	public void insertRecord(JSONObject liveinfo) {
		long end = liveinfo.getLong("endTime");
		if(liveinfo.getInt("raw") == 0){//非原始数据
			JSONArray userLeaveCount = liveinfo.getJSONArray("userLeave");
			long trueStart = end - (userLeaveCount.size() - 1) * 60000;
			int index = 0;
			for(Object userLeaveCountObj: userLeaveCount){
				long thisTime = trueStart + index * 60000;
				int size = userLeaveTimeList.size();
				for(int indexList = 1; indexList < size; indexList++){
					if(thisTime > userLeaveTimeList.get(indexList - 1) && thisTime < userLeaveTimeList.get(indexList)){
						String thisPoint = userLeaveTimeList.get(indexList) + "";
						userLeaveMap.put(thisPoint, userLeaveMap.get(thisPoint) + Integer.parseInt(userLeaveCountObj.toString()));
						break;
					}
				}
				index++;
			}
		}else{
			int userLeaveCount = liveinfo.getInt("userLeave");
			int size = userLeaveTimeList.size();
			for(int indexList = 1; indexList < size; indexList++){
				if(end > userLeaveTimeList.get(indexList - 1) && end < userLeaveTimeList.get(indexList)){
					String thisPoint = userLeaveTimeList.get(indexList) + "";
					userLeaveMap.put(thisPoint, userLeaveMap.get(thisPoint) + userLeaveCount);
					break;
				}
			}
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
