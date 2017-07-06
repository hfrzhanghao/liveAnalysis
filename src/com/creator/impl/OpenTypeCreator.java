package com.creator.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import com.creator.ICreator;
import com.db.entity.BaseEntity;
import com.db.entity.LiveInfoEntity;
import com.db.entity.PlayDataEntity;
import com.dto.BaseCols;
import com.dto.StatCol;
import com.dto.StatRow;
import com.external.common.CommonConstants;

public class OpenTypeCreator implements ICreator{

	Map<String, BaseCols> m_tab = new TreeMap<String, BaseCols>();
	Map<String, Integer> openTypeMap = new TreeMap<String, Integer>();

	@Override
	public void insertRecord(PlayDataEntity liveinfo) {
		String openType = liveinfo.getOpenType() + "";
		if (null != m_tab.get(openType)) {
			m_tab.get(openType).add(liveinfo);
			openTypeMap.put(openType, openTypeMap.get(openType) + 1);
		} else {
			m_tab.put(openType, new BaseCols());
			openTypeMap.put(openType, 1);
			m_tab.get(openType).add(liveinfo);
		}
	}

	@Override
	public Object getRowList() {
		int total = 0;
		int notComplete = 0;
		String notc = CommonConstants.HAS_NO_LOADPLAYER_EVENT;
		for(String key : openTypeMap.keySet()){
			if(key.equals(notc)){
				notComplete = openTypeMap.get(notc);
				m_tab.remove(key);
				continue;
			}
			total += openTypeMap.get(key);
		}
		for(String key : openTypeMap.keySet()){
			if(notComplete != 0){
				if(!key.equals(notc)){
					BigDecimal btemp = new BigDecimal(openTypeMap.get(key) + notComplete * (openTypeMap.get(key) / (double)total)).setScale(0, BigDecimal.ROUND_HALF_UP);
					int itemp = Integer.parseInt(btemp.toString());;
					openTypeMap.put(key, itemp);
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
			entity_count.setName("openType_NUM");
			entity_count.setValue(openTypeMap.get(key));
			valuesCount.put("sample_number", entity_count);

			countCol.setValues(valuesCount);
			countCol.setName("次数");
			countCol.setDescription("出现该打开方式的次数");

			cols.add(countCol);

			listRow.get(key + "").setCols(cols);
		}
		return listRow;
	}

	@Override
	public void init(JSONObject config, Map<String, String> dictionary) {
		// TODO Auto-generated method stub
		
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
