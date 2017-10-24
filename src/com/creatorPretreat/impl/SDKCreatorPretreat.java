package com.creatorPretreat.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import com.creator.ICreator;
import com.creatorPretreat.ICreatorPretreat;
import com.db.entity.BaseEntity;
import com.db.entity.LiveInfoEntity;
import com.db.entity.PlayDataEntity;
import com.dto.BaseCols;
import com.dto.StatCol;
import com.dto.StatRow;
import com.external.common.CommonConstants;

public class SDKCreatorPretreat implements ICreatorPretreat{

	Map<String, BaseCols> m_tab = new TreeMap<String, BaseCols>();
	Map<String, Integer> playerSDKTypeMap = new TreeMap<String, Integer>();

	@Override
	public void insertRecord(JSONObject liveJSONObject) {
		JSONObject jsonObject = liveJSONObject.getJSONObject("player_sdk_version");
		for(Object keyObj: jsonObject.keySet()){
			String key = keyObj.toString();
			if(playerSDKTypeMap.containsKey(key)){
				playerSDKTypeMap.put(key, playerSDKTypeMap.get(key) + jsonObject.getInt(key));
			}else{
				m_tab.put(key, new BaseCols());
				playerSDKTypeMap.put(key, jsonObject.getInt(key));
			}
			
		}
	}

	@Override
	public Object getRowList() {
		int total = 0;
		int notComplete = 0;
		String notc = "";
		for(String key : playerSDKTypeMap.keySet()){
			if(key.equals(notc)){
				notComplete = playerSDKTypeMap.get(notc);
				m_tab.remove(key);
				continue;
			}
			total += playerSDKTypeMap.get(key);
		}
		for(String key : playerSDKTypeMap.keySet()){
			if(notComplete != 0){
				if(!key.equals(notc)){
					BigDecimal btemp = new BigDecimal(playerSDKTypeMap.get(key) + notComplete * (playerSDKTypeMap.get(key) / (double)total)).setScale(0, BigDecimal.ROUND_HALF_UP);
					int itemp = Integer.parseInt(btemp.toString());;
					playerSDKTypeMap.put(key, itemp);
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
			entity_count.setName("playerSDKType_NUM");
			entity_count.setValue(playerSDKTypeMap.get(key));
			valuesCount.put("sample_number", entity_count);

			countCol.setValues(valuesCount);
			countCol.setName("次数");
			countCol.setDescription("出现该SDK次数");

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
