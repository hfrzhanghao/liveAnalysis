package com.creator.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.creator.ICreator;
import com.db.entity.BaseEntity;
import com.db.entity.LiveInfoEntity;
import com.db.entity.PlayDataEntity;
import com.dto.BaseCols;
import com.dto.StatCol;
import com.dto.StatRow;

public class DurationCreator implements ICreator {

	JSONArray configArray = null; // 读取配置文件中的档位
	Map<Integer, BaseCols> m_tab = new TreeMap<Integer, BaseCols>();
	Map<Integer, Integer> durationMap = new TreeMap<Integer, Integer>();
	Map<Integer, String> desc_tab = new HashMap<Integer, String>(); // 存放配置文件中的描述
	Map<Integer, String> name_tab = new HashMap<Integer, String>(); // 存放配置文件中的名称
	double sum = 0;
	int count = 0;
	
	public DurationCreator() {

		m_tab.put(-2, new BaseCols()); // 用于存放平均值
		durationMap.put(-2, 0);

	}

	@Override
	public void insertRecord(PlayDataEntity liveinfo) {
		m_tab.get(-2).add(liveinfo);
		
		double duration_tmp = liveinfo.getDuration();
		sum = sum + duration_tmp;
		durationMap.put(-2, durationMap.get(-2) + 1);
		
		for (int duration : m_tab.keySet()) {
			if (duration_tmp <= duration) {
				m_tab.get(duration).add(liveinfo);
				durationMap.put(duration, durationMap.get(duration) + 1);
				break;
			}
		}
	}

	@Override
	public Object getRowList() {
		Map<String, StatRow> listRow = new TreeMap<String, StatRow>();
		for (int key : m_tab.keySet()) {
			StatRow naStatRow = new StatRow();

			if(key != -2){
				naStatRow.setName(name_tab.get(key));
				naStatRow.setDescription(desc_tab.get(key));
			}else{
				naStatRow.setName("Duration_AVG");
				naStatRow.setDescription("平均播放时长");
			}
			naStatRow.setCols(m_tab.get(key).getCols());

			listRow.put(key + "", naStatRow);
			List<StatCol> cols = new ArrayList<StatCol>();
			
			StatCol countCol = new StatCol();
			Map<String, BaseEntity> valuesCount = new HashMap<String, BaseEntity>();
			BaseEntity entity_count = new BaseEntity();
			if(key != -2){
				entity_count.setUnit("个");
				entity_count.setName("Duration_NUM");
				entity_count.setValue(durationMap.get(key));
				valuesCount.put("sample_number", entity_count);

				countCol.setValues(valuesCount);
				countCol.setName("次数");
				countCol.setDescription("出现该档位的播放时长的次数");
			}else{
				entity_count.setUnit("秒");
				entity_count.setName("Duration_AVG");
				entity_count.setValue(durationMap.get(key)!=0?(sum / durationMap.get(key)):0);
				valuesCount.put("sample_number", entity_count);

				countCol.setValues(valuesCount);
				countCol.setName("Duration_AVG");
				countCol.setDescription("平均播放时长");
			}
			cols.add(countCol);
			listRow.get(key + "").setCols(cols);
		}
		return listRow;
	}

	@Override
	public void init(JSONObject config, Map<String, String> dictionary) {
		configArray = config.getJSONArray("duration");

		for (Object ob : configArray) {
			JSONObject jso = (JSONObject) ob;
			String desc = jso.getString("@desc");
			String name = jso.getString("@name");
			int duration = Integer.parseInt(jso.getString("@duration"));
			desc_tab.put(duration, desc);
			name_tab.put(duration, name);
			m_tab.put(duration, new BaseCols());
			durationMap.put(duration, 0);
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
