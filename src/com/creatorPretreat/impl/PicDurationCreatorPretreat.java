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

public class PicDurationCreatorPretreat implements ICreatorPretreat{

	JSONArray configArray = null; // 读取配置文件中的档位
	Map<Integer, BaseCols> m_tab = new TreeMap<Integer, BaseCols>();
	Map<Integer, Integer> picDurationMap = new TreeMap<Integer, Integer>();
	Map<Integer, String> desc_tab = new HashMap<Integer, String>(); // 存放配置文件中的描述
	Map<Integer, String> name_tab = new HashMap<Integer, String>(); // 存放配置文件中的名称
	double sum = 0;
	int count = 0;
	
	public PicDurationCreatorPretreat() {

		m_tab.put(-2, new BaseCols()); // 用于存放平均值
		picDurationMap.put(-2, 0);

	}

	@Override
	public void insertRecord(JSONObject liveinfo) {
		
		JSONObject firstPicDurationObject = liveinfo.getJSONObject("first_pic_duration");
		for(Object keyObject : firstPicDurationObject.keySet()){
			int key = Integer.parseInt(keyObject.toString());
			picDurationMap.put(key * 1000, picDurationMap.get(key * 1000) + firstPicDurationObject.getInt(key+""));
			picDurationMap.put(-2, picDurationMap.get(-2) + firstPicDurationObject.getInt(key+""));
		}
		sum = sum + liveinfo.getLong("first_pic_duration_total");
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
				naStatRow.setName("平均出画面时间（毫秒）");
				naStatRow.setDescription("平均出画面时间（毫秒）");
			}
			naStatRow.setCols(m_tab.get(key).getCols());

			listRow.put(key + "", naStatRow);
			List<StatCol> cols = new ArrayList<StatCol>();

			StatCol countCol = new StatCol();
			Map<String, BaseEntity> valuesCount = new HashMap<String, BaseEntity>();
			BaseEntity entity_count = new BaseEntity();
			if(key != -2){
				entity_count.setUnit("个");
				entity_count.setName("FirstPicDuration_NUM");
				entity_count.setValue(picDurationMap.get(key));
				valuesCount.put("sample_number", entity_count);

				countCol.setValues(valuesCount);
				countCol.setName("次数");
				countCol.setDescription("出现该档位的出画面时间的次数");
			}else{
				entity_count.setUnit("毫秒");
				entity_count.setName("FirstPicDuration_AVG");
				entity_count.setValue(picDurationMap.get(key)!=0?(Math.round(sum / picDurationMap.get(key))):0);
				valuesCount.put("sample_number", entity_count);

				countCol.setValues(valuesCount);
				countCol.setName("平均出画面时间（毫秒）");
				countCol.setDescription("平均出画面时间（毫秒）");
			}
			cols.add(countCol);
			listRow.get(key + "").setCols(cols);
		}
		return listRow;
	}

	@Override
	public void init(JSONObject config, Map<String, String> dictionary) {
		configArray = config.getJSONArray("picDuration");

		for (Object ob : configArray) {
			JSONObject jso = (JSONObject) ob;
			String desc = jso.getString("@desc");
			String name = jso.getString("@name");
			int picDuration = Integer.parseInt(jso.getString("@picDuration"));
			desc_tab.put(picDuration, desc);
			name_tab.put(picDuration, name);
			m_tab.put(picDuration, new BaseCols());
			picDurationMap.put(picDuration, 0);
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
