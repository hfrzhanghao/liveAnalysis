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

public class LockCountCreatorPretreat implements ICreatorPretreat {

	JSONArray configArray = null; // 读取配置文件中的档位
	Map<Integer, BaseCols> m_tab = new TreeMap<Integer, BaseCols>();
	Map<Integer, Integer> lockCountmap = new TreeMap<Integer, Integer>();
	Map<Integer, String> desc_tab = new HashMap<Integer, String>(); // 存放配置文件中的描述
	Map<Integer, String> name_tab = new HashMap<Integer, String>(); // 存放配置文件中的名称
	int lockCountSum = 0;
	int fail_sum = 0;
	int count = 0;

	public LockCountCreatorPretreat() {

		m_tab.put(-2, new BaseCols()); // 用于存放平均值
		lockCountmap.put(-2, 0);

	}

	@Override
	public void insertRecord(JSONObject liveinfo) {

		JSONObject lockCountObject = liveinfo.getJSONObject("lock_count");
		for(Object keyObject : lockCountObject.keySet()){
			int key = Integer.parseInt(keyObject.toString());
			if(key != 5){
				lockCountmap.put(key, lockCountmap.get(key) + lockCountObject.getInt(key+""));
				lockCountmap.put(-2, lockCountmap.get(-2) + lockCountObject.getInt(key+""));
			}
			
		}
		lockCountSum = lockCountSum + liveinfo.getInt("lock_count_total");
	}

	@Override
	public Object getRowList() {
		Map<String, StatRow> listRow = new TreeMap<String, StatRow>();
		for (int key : m_tab.keySet()) {
			StatRow naStatRow = new StatRow();

			if (key != -2) {
				naStatRow.setName(name_tab.get(key));
				naStatRow.setDescription(desc_tab.get(key));
			} else if (key == -2) {
				naStatRow.setName("平均卡顿次数");
				naStatRow.setDescription("平均卡顿次数");
			}
			naStatRow.setCols(m_tab.get(key).getCols());

			listRow.put(key + "", naStatRow);

			List<StatCol> cols = new ArrayList<StatCol>();

			StatCol countCol = new StatCol();
			Map<String, BaseEntity> valuesCount = new HashMap<String, BaseEntity>();
			BaseEntity entity_count = new BaseEntity();

			if (key != -2 && key != -1) {
				entity_count.setUnit("个");
				entity_count.setName("LockCount_NUM");
				entity_count.setValue(lockCountmap.get(key));
				valuesCount.put("sample_number", entity_count);

				countCol.setValues(valuesCount);
				countCol.setName("次数");
				countCol.setDescription("出现该档位的卡顿次数的播放次数");
			} else if (key == -2) {
				entity_count.setUnit("个");
				entity_count.setName("LockCount_NUM");
				double i = lockCountmap.get(key) != 0 ? (lockCountSum / (double) lockCountmap.get(key)) : 0;
				entity_count.setValue(i);
				valuesCount.put("sample_number", entity_count);

				countCol.setValues(valuesCount);
				countCol.setName("LockCount_AVG");
				countCol.setDescription("平均卡顿次数");
			} else if (key == -1) {
				entity_count.setUnit("个");
				entity_count.setName("LockCount_FAILED");
				entity_count.setValue(lockCountmap.get(key));
				valuesCount.put("sample_number", entity_count);

				countCol.setValues(valuesCount);
				countCol.setName("LockCount_FAILED");
				countCol.setDescription("播放失败次数");
			}
			cols.add(countCol);
			listRow.get(key + "").setCols(cols);
		}
		return listRow;
	}

	@Override
	public void init(JSONObject config, Map<String, String> dictionary) {
		configArray = config.getJSONArray("lockCount");

		for (Object ob : configArray) {
			JSONObject jso = (JSONObject) ob;
			String desc = jso.getString("@desc");
			String name = jso.getString("@name");
			int lockCount = Integer.parseInt(jso.getString("@lockCount"));
			desc_tab.put(lockCount, desc);
			name_tab.put(lockCount, name);
			m_tab.put(lockCount, new BaseCols());
			lockCountmap.put(lockCount, 0);
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
