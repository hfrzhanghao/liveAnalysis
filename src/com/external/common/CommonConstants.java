package com.external.common;

import java.util.ResourceBundle;

public final class CommonConstants {
	public static final String USER_LOGIN_COOKIE = "userlogincookie";
	public static final String LIVE_ANALYSIS_URL;//按原始数据统计
	public static final String LIVE_ANALYSIS_URL_PRETREAT;//按预处理数据统计
	public static final String LIVE_USER_URL;
	public static final String LIVE_POPULAR_URL;//播放热度统计
	public static final String GET_ENUM_URL;//获取枚举值
	public static final int STATE_SUCCESS;
	public static final int DEFAULT_PAGE_SIZE;//数据分析过程中，分批读取数据（每批读取的记录个数）
	public static final int DEFAULT_TOTAL_USER;//数据分析过程中，分批读取数据（每批读取的记录个数）
	public static final int TIME_SCALE;//画曲线图时分成多少个点
	public static final int MIN_TIME;//统计在线人数时的最小时间颗粒度（s）
	public static final int SOCKED_TIMEOUT;//超时时间
	public static final int OLD_DATA;//是否启用按原始数据来统计
	public static final int THREAD_LIMIT;
	public static final String HAS_NO_LOADPLAYER_EVENT;//未获取到播放器加载数据时的替代字符串
	public static final String NEW_DATA_TIME;//产生预处理数据的时间
	
	static {
		HAS_NO_LOADPLAYER_EVENT = "notComplete";
		TIME_SCALE = Integer.parseInt(ResourceBundle.getBundle("config").getString("timescale"));
		MIN_TIME = Integer.parseInt(ResourceBundle.getBundle("config").getString("minTime"));
		LIVE_ANALYSIS_URL = ResourceBundle.getBundle("config").getString("liveAnalysisURL") + "/all/liveCount";
		LIVE_ANALYSIS_URL_PRETREAT = ResourceBundle.getBundle("config").getString("liveAnalysisURL") + "/all/liveCountWithPretreat";
		LIVE_POPULAR_URL = ResourceBundle.getBundle("config").getString("liveAnalysisURL") + "/popular/livePopularCount";
		LIVE_USER_URL = ResourceBundle.getBundle("config").getString("liveAnalysisURL") + "/user/lockCount";
		GET_ENUM_URL = ResourceBundle.getBundle("config").getString("liveAnalysisURL") + "/enum/enumValue";
		STATE_SUCCESS = 0;
		DEFAULT_PAGE_SIZE = Integer.parseInt(ResourceBundle.getBundle("config").getString("defaultpageSize"));
		DEFAULT_TOTAL_USER = Integer.parseInt(ResourceBundle.getBundle("config").getString("defaultTotalUser"));
		SOCKED_TIMEOUT = Integer.parseInt(ResourceBundle.getBundle("config").getString("socketTimeout"));
		NEW_DATA_TIME = ResourceBundle.getBundle("config").getString("new_data_time");
		OLD_DATA = Integer.parseInt(ResourceBundle.getBundle("config").getString("old_data"));
		THREAD_LIMIT = Integer.parseInt(ResourceBundle.getBundle("config").getString("thread_limit"));
		
	}

}
