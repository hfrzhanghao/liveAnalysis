package com.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.db.entity.BaseEntity;
import com.db.entity.LiveInfoEntity;
import com.db.entity.PlayDataEntity;
import com.dictionary.UnitDictLive;
import com.util.OptionAverage;
import com.util.UnitEntity;


public class BaseCols {
	Map<String, OptionAverage> m_tab = new HashMap<String, OptionAverage>();
	
	public BaseCols() {
		Map<String, UnitEntity> unitdiclive = UnitDictLive.m_tab;
		for (String unitKey : unitdiclive.keySet()) {
			m_tab.put(unitKey, new OptionAverage());
		}
	}
	
	public void add(PlayDataEntity liveinfo) {
		/*if (liveinfo != null) {
			if(!"".equals(liveinfo.getDevice_type() + "")){
				m_tab.get("times").add(1);
			}
			
		}*/
	}
	
	public List<StatCol> getCols() {
		List<StatCol> colsList = new ArrayList<StatCol>();
		for (String oaKey : m_tab.keySet()) {
			OptionAverage oaver = m_tab.get(oaKey);
			StatCol col = new StatCol();
			Map<String, BaseEntity> values = new HashMap<String, BaseEntity>();

			BaseEntity entity_simples = new BaseEntity();

			UnitEntity en = UnitDictLive.m_tab.get(oaKey);
			
			entity_simples.setDescription(en.desc);
			entity_simples.setUnit("次");
			entity_simples.setName("sample_number");
			entity_simples.setValue(oaver.m_samples);

			values.put(entity_simples.getName(), entity_simples);

			col.setValues(values);
			col.setName(oaKey);
			col.setDescription(en.desc);

			colsList.add(col);
		}

		return colsList;
	}
}
