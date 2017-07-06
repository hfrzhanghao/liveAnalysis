package com.dictionary;

import java.util.HashMap;
import java.util.Map;

public class ResultDic {
	public static Map<String,String> m_tab = new HashMap<String,String>();
	static{
		/*m_tab.put("正常", "200");
		m_tab.put("Host超时,同步接口结束", "2016");
		m_tab.put("用户被踢时,呼叫挂断", "2020");
		m_tab.put("HOST未连接", "2069");
		m_tab.put("异常", "2000"); */
		m_tab.put("200", "200");
		m_tab.put("2016", "2016");
		m_tab.put("2020", "2020");
		m_tab.put("2069", "2069");
		m_tab.put("2000", "2000"); 
	}
}
