package com.creatorPretreat.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.AboutTime;
import com.creator.ICreator;
import com.creatorPretreat.ICreatorPretreat;
import com.db.entity.BaseEntity;
import com.db.entity.LiveInfoEntity;
import com.db.entity.PlayDataEntity;
import com.dto.BaseCols;
import com.dto.StatCol;
import com.dto.StatRow;
import com.external.common.CommonConstants;
import com.util.DateUtil;

public class UserOnlineCreatorPretreat implements ICreatorPretreat {

	Map<String, BaseCols> m_tab = new TreeMap<String, BaseCols>();
	Map<String, Integer> userOnlineMap = new TreeMap<String, Integer>();
	Map<String, Integer> userOnlineMapMin = new TreeMap<String, Integer>();
	Map<String, List<String>> time_uid = new TreeMap<String, List<String>>();
	List<String> timeList = new ArrayList<String>();
	List<String> timeListMin = new ArrayList<String>();
	long startTime = 0;
	long endTime = 0;

	public UserOnlineCreatorPretreat(long start, long end) {
		startTime = start - start % 60000;
		endTime = end - end % 60000 + 60000;
	}

	@Override
	public void insertRecord(JSONObject liveinfo) {
		if (liveinfo.get("content") != null && !liveinfo.get("content").equals("")) {
			long endTime = liveinfo.getLong("endTime");
			if (liveinfo.getInt("raw") == 0) {// 非原始数据
				JSONArray userOnlineCount = liveinfo.getJSONArray("userOnline");
				long trueStart = endTime - (userOnlineCount.size() - 1) * 60000;
				int index = 0;
				for (Object onlineCountObj : userOnlineCount) {
					long thisTime = trueStart + index * 60000;
					if (userOnlineMapMin.containsKey(thisTime + "")) {
						int onlineCount = Integer.parseInt(onlineCountObj.toString());
						userOnlineMapMin.put(thisTime + "", userOnlineMapMin.get(thisTime + "") + onlineCount);
					}
					index++;
				}
			} else {// 原始数据
				int userOnlineCount = liveinfo.getInt("userOnline");
				int hour = Integer.parseInt(DateUtil.stampToDate(endTime).split(":")[0].split("-")[3]);
				double random = Math.random();
				// if(userOnlineMapMin.get(endTime + "") != null){
				if ((hour == 1 || hour == 5)) {
					if (random > 0.33) {
						userOnlineMapMin.put(endTime + "", userOnlineMapMin.get(endTime + "") + userOnlineCount);
					}
				} else if (hour >= 2 && hour <= 4) {
					if (random > 0.66) {
						userOnlineMapMin.put(endTime + "", userOnlineMapMin.get(endTime + "") + userOnlineCount);
					}
				} else {
					userOnlineMapMin.put(endTime + "", userOnlineMapMin.get(endTime + "") + userOnlineCount);
				}
				// }
			}
		}
	}

	@Override
	public Object getRowList() {

		int index = 0;

		for (int i = 0; i < timeList.size(); i++) {
			int maxTemp = 0;
			for (int j = index; j < timeListMin.size(); j++) {
				String timeListMin_j = timeListMin.get(j);
				if (Long.parseLong(timeListMin_j) <= Long.parseLong(timeList.get(i))) {
					if (userOnlineMapMin.get(timeListMin_j) > maxTemp) {
						maxTemp = userOnlineMapMin.get(timeListMin_j);
					}
				} else {
					userOnlineMap.put(timeList.get(i), maxTemp);
					index = j;
					break;
				}
			}
		}

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
			entity_count.setName("userOnline_NUM");
			entity_count.setValue(userOnlineMap.get(key));
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
		long timeScale = timeDifference / timescale; // 共timescale个刻度点
		List<Long> timePoint = new ArrayList<Long>();

		for (int i = 0; i <= timescale; i++) {
			long tempTime = starttime + i * timeScale;
			timePoint.add(tempTime);
			m_tab.put(tempTime + "", new BaseCols());
			userOnlineMap.put(tempTime + "", 0);
		}
		for (String time : userOnlineMap.keySet()) {
			timeList.add(time);
		}
		int minStatRange = CommonConstants.MIN_TIME;
		for (long i = starttime; i <= endTime; i = i + minStatRange * 1000) {
			userOnlineMapMin.put(i + "", 0);
			timeListMin.add(i + "");
			time_uid.put(i + "", new ArrayList<String>());
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
