package com.external.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.ResourceBundle;

public final class CommonConstants {
	public static final String USER_LOGIN_COOKIE = "userlogincookie";
	public static final String LIVE_ANALYSIS_URL;
	public static final String LIVE_USER_URL;
	public static final String GET_ENUM_URL;
	public static final int STATE_SUCCESS;
	public static final int DEFAULT_PAGE_SIZE;//数据分析过程中，分批读取数据（每批读取的记录个数）
	public static final int DEFAULT_TOTAL_USER;//数据分析过程中，分批读取数据（每批读取的记录个数）
	
	public static final int TIME_SCALE;
	public static final int MIN_TIME;
	public static final int SOCKED_TIMEOUT;
	
	public static final String HAS_NO_LOADPLAYER_EVENT;
	
	static {
		HAS_NO_LOADPLAYER_EVENT = "notComplete";
		TIME_SCALE = Integer.parseInt(ResourceBundle.getBundle("config").getString("timescale"));
		MIN_TIME = Integer.parseInt(ResourceBundle.getBundle("config").getString("minTime"));
		LIVE_ANALYSIS_URL = ResourceBundle.getBundle("config").getString("liveAnalysisURL") + "/all/liveCount";
		LIVE_USER_URL = ResourceBundle.getBundle("config").getString("liveAnalysisURL") + "/user/lockCount";
		GET_ENUM_URL = ResourceBundle.getBundle("config").getString("liveAnalysisURL") + "/enum/enumValue";
		STATE_SUCCESS = 0;
		DEFAULT_PAGE_SIZE = Integer.parseInt(ResourceBundle.getBundle("config").getString("defaultpageSize"));
		DEFAULT_TOTAL_USER = Integer.parseInt(ResourceBundle.getBundle("config").getString("defaultTotalUser"));
		SOCKED_TIMEOUT = Integer.parseInt(ResourceBundle.getBundle("config").getString("socketTimeout"));
		
	}

	public static String roomjson() {
		File file = new File(CommonConstants.class.getClassLoader().getResource("").getFile() + "json.txt");
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buf = new byte[1024];
			StringBuffer sb = new StringBuffer();
			while ((fis.read(buf)) != -1) {
				sb.append(new String(buf));
				buf = new byte[1024];
			}

			return new String(sb.toString().getBytes("GBK"), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
