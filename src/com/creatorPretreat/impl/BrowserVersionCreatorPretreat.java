package com.creatorPretreat.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;


import com.creatorPretreat.ICreatorPretreat;
import com.db.entity.BaseEntity;
import com.db.entity.LiveInfoEntity;
import com.dto.BaseCols;
import com.dto.StatCol;
import com.dto.StatRow;

public class BrowserVersionCreatorPretreat implements ICreatorPretreat {

	Map<String, BaseCols> m_tab = new TreeMap<String, BaseCols>();
	Map<String, Integer> browsermap = new TreeMap<String, Integer>();

	@Override
	public void insertRecord(JSONObject liveJSONObject) {
		JSONObject browserVersionObj = liveJSONObject.getJSONObject("browser_version");
		for(Object browserObj: browserVersionObj.keySet()){
			String browser = browserObj.toString();
			if(browser.equals("未知") || browser.equals("未汇报")){
				browsermap.put("app", browsermap.get("app") + browserVersionObj.getInt(browser));
			}else{
				if(m_tab.containsKey(browser)){
					browsermap.put(browser, browsermap.get(browser) + browserVersionObj.getInt(browser));
				}else{
					m_tab.put(browser, new BaseCols());
					browsermap.put(browser, browserVersionObj.getInt(browser));
				}
			}
		}
	}

	@Override
	public Object getRowList() {
		int total = 0;
		int notComplete = 0;
		String notc = "";
		for (String key : browsermap.keySet()) {
			if (key.equals(notc)) {
				notComplete = browsermap.get(notc);
				m_tab.remove(key);
				continue;
			}
			total += browsermap.get(key);
		}
		for (String key : browsermap.keySet()) {
			if (notComplete != 0) {
				if (!key.equals(notc)) {
					//TODO
					if(total == 0){
						System.out.println();
					}
					BigDecimal btemp = new BigDecimal(browsermap.get(key) + notComplete * (browsermap.get(key) / (double) total)).setScale(0,
							BigDecimal.ROUND_HALF_UP);
					int itemp = Integer.parseInt(btemp.toString());
					browsermap.put(key, itemp);
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
			entity_count.setName("browser_NUM");
			entity_count.setValue(browsermap.get(key));
			valuesCount.put("sample_number", entity_count);

			countCol.setValues(valuesCount);
			countCol.setName("次数");
			countCol.setDescription("出现该浏览器版本次数");

			cols.add(countCol);

			listRow.get(key + "").setCols(cols);
		}
		return listRow;
	}

	@Override
	public void init(JSONObject config, Map<String, String> dictionary) {
		m_tab.put("app", new BaseCols());
		browsermap.put("app", 0);

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
