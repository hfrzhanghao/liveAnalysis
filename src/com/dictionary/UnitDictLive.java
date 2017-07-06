package com.dictionary;

import java.util.HashMap;
import java.util.Map;

import com.util.UnitEntity;

public class UnitDictLive {
	public static Map<String,UnitEntity> m_tab = new HashMap<String, UnitEntity>();
	
	static{
		m_tab.put("times", new UnitEntity("次","播放次数"));
	}
}
