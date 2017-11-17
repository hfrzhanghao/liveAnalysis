package com.db.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.AboutTime;
import com.db.dao.BaseDao;
import com.db.dao.LiveInfoDao;
import com.db.entity.LoadPlayerEntity;
import com.db.entity.PlayDataEntity;
import com.dto.PlayListDto;
import com.external.common.CommonConstants;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class LiveInfoDaoImpl extends BaseDao implements LiveInfoDao {

	private Logger logger = Logger.getLogger(this.getClass());
	final String LIVE_INFO_TAB = "live_data";
	final String LOG_TIME_TAB = "live_time";
	final String LIVE_PRE_DATA = "live_pre_data";
	final String LIVE_ONLINE_DATA = "live_online_data";

	final String HAS_NO_LOADPLAYER_EVENT = CommonConstants.HAS_NO_LOADPLAYER_EVENT;

	public LiveInfoDaoImpl() {
	}

	/**
	 * 按照原始数据进行查找
	 * **/
	@Override
	public PlayListDto findLive(long startTime, long endTime, String url, String domain, String isp, String openType, String businessID, String userName,
			String durationSelect, String duration, String firstPicDurationSelect, String firstPicDuration, int pageSize, int currPage, boolean iscount) {

		PlayListDto liveDTO = new PlayListDto();
		List<PlayDataEntity> playDataList = new ArrayList<PlayDataEntity>();

		// 播放器信息搜索线程
		LoadThread loadThread = new LoadThread(startTime, endTime, url, domain, isp, openType, businessID, userName, pageSize, currPage, iscount);
		// 播放过程日志搜索线程
		PlayThread playThread = new PlayThread(startTime, endTime, url, businessID, durationSelect, duration, firstPicDurationSelect, firstPicDuration,
				pageSize, currPage, iscount);

		Thread loadT = new Thread(loadThread);
		Thread loadP = new Thread(playThread);
		loadT.start();
		loadP.start();

		try {
			// 需要两个线程都执行结束才能进行下一步
			while (!loadThread.loadComplete || !playThread.playComplete) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					logger.error("统计中LiveInfoDaoImpl: ", e);
					System.exit(0);// 退出程序
				}
			}

			long mergStart = System.currentTimeMillis();
			// 开始将播放器信息和播放过程信息按照key（uid和operationGUID和businessID的组合）进行合并，即若uid和operationGUID和businessID都相同，
			// 那么说明它们来自于同一次播放过程
			for (String key_MapPlay : playThread.mapPlay.keySet()) {
				String uid_guid_bid = key_MapPlay.substring(0, getCharacterPosition(key_MapPlay, 3));
				PlayDataEntity playDataEntity = playThread.mapPlay.get(key_MapPlay);

				// 如果mapload中有本次播放的播放器信息，则将mapload与mapplay结合
				if (loadThread.mapLoad.keySet().contains(uid_guid_bid)) {

					LoadPlayerEntity loadPlayerEntity = loadThread.mapLoad.get(uid_guid_bid);
					playDataEntity.setBrowser_version(loadPlayerEntity.getBrowser_version());
					playDataEntity.setCity(loadPlayerEntity.getCity());

					// 获取不到device_type_name并且sdk为flash，则device_name为PC
					if (loadPlayerEntity.getDevice_type_name().equals("unknown") && loadPlayerEntity.getPlayer_sdk_type().toLowerCase().equals("flash")) {
						playDataEntity.setDevice_name("PC");
					} else {
						playDataEntity.setDevice_name(loadPlayerEntity.getDevice_type_name());
					}

					// 获取不到device_type_name则为PC
					playDataEntity.setDevice_name(loadPlayerEntity.getDevice_type_name().equals("unknown") ? "PC" : loadPlayerEntity.getDevice_type_name());
					playDataEntity.setOpenType(loadPlayerEntity.getOpen_type());
					playDataEntity.setOperation_guid(loadPlayerEntity.getOperation_guid());
					playDataEntity.setOs_type(loadPlayerEntity.getOs_type());
					playDataEntity.setOs_version(loadPlayerEntity.getOs_version());
					playDataEntity.setPlayer_sdk_type(loadPlayerEntity.getPlayer_sdk_type());
					playDataEntity.setProvince(loadPlayerEntity.getProvince());
					playDataEntity.setUid(loadPlayerEntity.getUid());
					playDataEntity.setUserName(loadPlayerEntity.getUser_name());

				} else {
					// 否则将播放器信息都置为HAS_NO_LOADPLAYER_EVENT
					playDataEntity.setBrowser_version(HAS_NO_LOADPLAYER_EVENT);
					playDataEntity.setCity(HAS_NO_LOADPLAYER_EVENT);
					playDataEntity.setDevice_name(HAS_NO_LOADPLAYER_EVENT);
					playDataEntity.setOpenType(HAS_NO_LOADPLAYER_EVENT);
					playDataEntity.setOs_type(HAS_NO_LOADPLAYER_EVENT);
					playDataEntity.setOs_version(HAS_NO_LOADPLAYER_EVENT);
					playDataEntity.setPlayer_sdk_type(HAS_NO_LOADPLAYER_EVENT);
					playDataEntity.setProvince(HAS_NO_LOADPLAYER_EVENT);
					playDataEntity.setUserName(HAS_NO_LOADPLAYER_EVENT);
				}

				// 之前的查找是将时间往前延伸1小时，最终只统计结束时间在设定起始时间之后的
				if (AboutTime.toLongSSS(playDataEntity.getEnd_time()) >= startTime) {
					playDataList.add(playDataEntity);
				}
			}

			long mergEnd = System.currentTimeMillis();
			System.out.println("merge:" + (mergEnd - mergStart));

			liveDTO.setTotalData(loadThread.countLoad);
			liveDTO.setTotalPage(loadThread.totalPageLoad);
			liveDTO.setList(playDataList);

			playDataList = null;
			playThread.mapPlay = null;
			loadThread.mapLoad = null;

			System.gc();

		} catch (Exception e) {
			logger.error("统计中 查询findLive：", e);
			System.out.println(e);
		}
		return liveDTO;
	}

	/**
	 * 从播放器信息数据库live_pre_data（已经经过预处理）中查找，例如查找有关省份，设备信息，用户播放时长的信息
	 * */
	@Override
	public List<JSONObject> findLiveWithPretreat(long startTime, long endTime, String url, String domainName, String domain, String isp, String openType,
			String businessID, String userName, int pageSize, int currPage, boolean iscount) {

		List<JSONObject> listJSONObject = new ArrayList<JSONObject>();

		int countPre;
		int totalPagePre;

		DBCollection employee = db.getCollection(LIVE_PRE_DATA);
		DBCursor curPre = null;
		BasicDBObject dboPre = new BasicDBObject("startTime", new BasicDBObject("$lt", endTime));
		dboPre.append("endTime", new BasicDBObject("$gt", startTime));
		// 如果设置了url过滤，优先按照url过滤，否则按照domain_name过滤
		if (url == null) {
			if (domainName != null) {
				dboPre.append("domain_name", domainName);
			}
		} else {
			if (url.split(",").length > 0) {
				BasicDBList urlList = new BasicDBList();
				String[] urlArray = url.split(",");
				for (int i = 0; i < urlArray.length; i++) {
					urlList.add(new BasicDBObject("content", urlArray[i]));
				}
				dboPre.put("$or", urlList);
			} else {
				dboPre.append("content", url);
			}
		}

		// 按照地区过滤
		if (domain != null) {
			dboPre.append("province_key", domain);
		}

		// 按照运营商过滤
		if (isp != null) {
			if (!isp.equals("else")) {
				dboPre.append("isp_key", isp);
			} else {
				BasicDBList values = new BasicDBList();
				values.add("电信");
				values.add("移动");
				values.add("联通");
				dboPre.append("isp_key", new BasicDBObject("$nin", values));
			}
		}

		// 按照打开方式过滤
		if (openType != null) {
			dboPre.append("open_type_key", openType);
		}

		// 企业号过滤
		if (userName != null) {
			dboPre.append("user_name_key", userName);
		}

		countPre = employee.find(dboPre).count();

		totalPagePre = countPre % pageSize == 0 ? countPre / pageSize : (countPre / pageSize) + 1;

		for (int currPg = 1; currPg <= totalPagePre; currPg++) {
			int skip = (currPg - 1) * pageSize;
			curPre = employee.find(dboPre).skip(skip).limit(pageSize);
			while (curPre.hasNext()) {
				DBObject preObject = curPre.next();
				JSONObject preJson = JSONObject.fromObject(preObject.toString());
				listJSONObject.add(preJson);
			}
		}
		System.gc();
		return listJSONObject;
	}

	/**
	 * 从用户在线进入离开数据库live_online_data（已经经过预处理）中查找，例如用户在线、进入和离开信息
	 * */
	@Override
	public List<JSONObject> findOnlineWithPretreat(long startTime, long endTime, String url, String domainName, int pageSize, int currPage, boolean iscount) {

		List<JSONObject> listJSONObject = new ArrayList<JSONObject>();

		int countPre;
		int totalPagePre;

		DBCollection employee = db.getCollection(LIVE_ONLINE_DATA);
		DBCursor curPre = null;
		BasicDBObject dboPre = new BasicDBObject("startTime", new BasicDBObject("$lt", endTime));
		dboPre.append("endTime", new BasicDBObject("$gt", startTime));
		// 如果设置了url过滤，优先按照url过滤，否则按照domainName过滤
		if (url != null && !url.equals("多条以英文逗号隔开") && !url.equals("")) {
			if (url.split(",").length > 0) {
				BasicDBList urlList = new BasicDBList();
				String[] urlArray = url.split(",");
				for (int i = 0; i < urlArray.length; i++) {
					urlList.add(new BasicDBObject("content", urlArray[i]));
				}
				dboPre.put("$or", urlList);
			} else {
				dboPre.append("content", url);
			}
		} else if (domainName != null && !domainName.equals("")) {
			dboPre.append("domainName", domainName);
		}
		dboPre.append("isTen", null);//不查找按照10分钟为颗粒度进行预统计的数据

		countPre = employee.find(dboPre).count();

		totalPagePre = countPre % pageSize == 0 ? countPre / pageSize : (countPre / pageSize) + 1;

		for (int currPg = 1; currPg <= totalPagePre; currPg++) {
			int skip = (currPg - 1) * pageSize;
			curPre = employee.find(dboPre).skip(skip).limit(pageSize);
			while (curPre.hasNext()) {
				DBObject preObject = curPre.next();
				preObject.put("raw", 1);// 标记该数据为按照1分钟为颗粒度进行预统计后的数据
				JSONObject preJson = JSONObject.fromObject(preObject.toString());
				listJSONObject.add(preJson);
			}
		}
		System.gc();
		return listJSONObject;
	}

	public int getCharacterPosition(String string, int i) {
		// 这里是获取"_"符号的位置
		Matcher slashMatcher = Pattern.compile("_").matcher(string);
		int mIdx = 0;
		while (slashMatcher.find()) {
			mIdx++;
			// 当"_"符号第i次出现的位置
			if (mIdx == i) {
				break;
			}
		}
		return slashMatcher.start();
	}

	/**
	 * 查找某域名下的所有内容的播放热度
	 * */
	@Override
	public List<JSONObject> findLivePopular(long startTime, long endTime, String domainName, String topN, int pageSize, int currPage, boolean iscount) {

		List<JSONObject> listJSONObject = new ArrayList<JSONObject>();

		int countPre;
		int totalPagePre;

		DBCollection employee = db.getCollection(LIVE_PRE_DATA);
		DBCursor curPre = null;
		BasicDBObject dboPre = new BasicDBObject("startTime", new BasicDBObject("$lt", endTime));
		dboPre.append("endTime", new BasicDBObject("$gt", startTime));

		countPre = employee.find(dboPre).count();

		totalPagePre = countPre % pageSize == 0 ? countPre / pageSize : (countPre / pageSize) + 1;

		for (int currPg = 1; currPg <= totalPagePre; currPg++) {
			int skip = (currPg - 1) * pageSize;
			curPre = employee.find(dboPre).skip(skip).limit(pageSize);
			while (curPre.hasNext()) {
				DBObject preObject = curPre.next();
				JSONObject preJson = JSONObject.fromObject(preObject.toString());
				listJSONObject.add(preJson);
			}
		}
		return listJSONObject;
	}
}
