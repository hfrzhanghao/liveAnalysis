package com.db.dao.impl;

//import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.AboutTime;
import com.db.dao.BaseDao;
import com.db.dao.LiveInfoDao;
import com.db.entity.LivePreDataEntity;
import com.db.entity.LoadPlayerEntity;
import com.db.entity.PlayDataEntity;
import com.dto.LiveListDto;
import com.dto.PlayListDto;
import com.external.common.CommonConstants;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.util.BeanUtil;
import com.util.DateUtil;

public class LiveInfoDaoImpl extends BaseDao implements LiveInfoDao {

	private Logger logger = Logger.getLogger(this.getClass());
	final String LIVE_INFO_TAB = "live_data";
	final String LOG_TIME_TAB = "live_time";
	final String LIVE_PRE_DATA = "live_pre_data";
	final String LIVE_ONLINE_DATA = "live_online_data";

	final String HAS_NO_LOADPLAYER_EVENT = CommonConstants.HAS_NO_LOADPLAYER_EVENT;

	public LiveInfoDaoImpl() {
	}

	@Override
	public PlayListDto findLockUser(long startTime, long endTime, String url, String domain, String isp, String openType, String businessID, String userName,
			String durationSelect, String duration, String firstPicDurationSelect, String firstPicDuration, String lockCount, int totalUser, boolean iscount) {
		PlayListDto liveDTO = new PlayListDto();
		List<PlayDataEntity> playDataList = new ArrayList<PlayDataEntity>();
		try {
			DBCollection employee = db.getCollection(LIVE_INFO_TAB);
			DBCursor curLoad = null;
			DBCursor curPlay = null;
			BasicDBObject dbo = new BasicDBObject("playtime", new BasicDBObject("$gte", startTime).append("$lt", endTime));

			dbo.append("event_type", "LoadPlayer");

			if (domain != null) {
				dbo.append("province", domain);
			}
			if (isp != null) {
				dbo.append("isp", isp);
			}

			if (openType != null) {
				dbo.append("open_type", openType);
			}

			if (businessID != null) {
				dbo.append("business_id", businessID);
			}

			if (userName != null) {
				dbo.append("user_name", userName);
			}

			curLoad = employee.find(dbo);

			Map<String, LoadPlayerEntity> mapLoad = new ConcurrentHashMap<String, LoadPlayerEntity>();
			while (curLoad.hasNext()) {
				DBObject loadObject = curLoad.next();
				String uid_guid = loadObject.get("uid").toString() + "_" + loadObject.get("operation_guid").toString();
				mapLoad.put(uid_guid, BeanUtil.dbObject2Bean(loadObject, new LoadPlayerEntity())); // 得到初步筛选结果
			}

			for (String key : mapLoad.keySet()) {
				String uid = key.split("_")[0];
				String guid = key.split("_")[1];
				BasicDBObject dboPlay = new BasicDBObject("playtime", new BasicDBObject("$gte", startTime).append("$lt", endTime));
				dboPlay.append("uid", uid);
				dboPlay.append("operation_guid", guid);
				BasicDBList eventList = new BasicDBList();
				eventList.add(new BasicDBObject("event_type", "play"));
				eventList.add(new BasicDBObject("event_type", "playlock"));
				dboPlay.append("$or", eventList);

				BasicDBList urlList = new BasicDBList();
				if (url != null && !url.equals("多条以英文逗号隔开") && !url.equals("")) {
					if (url.split(",").length > 0) {
						String[] urlArray = url.split(",");
						for (int i = 0; i < urlArray.length; i++) {
							urlList.add(new BasicDBObject("content", urlArray[i]));
						}
						dboPlay.put("$or", urlList);
					} else {
						dboPlay.append("content", url);
					}
				}
				curPlay = employee.find(dboPlay);

				Map<String, PlayDataEntity> mapPlay = new ConcurrentHashMap<String, PlayDataEntity>();

				// 先进行装载
				while (curPlay.hasNext()) {
					DBObject playObject = curPlay.next();

					String url_key = playObject.get("url").toString();

					if (mapPlay.get(url_key) == null) {
						mapPlay.put(url_key, new PlayDataEntity());
					}

					String time = playObject.get("time").toString();

					if (playObject.get("event_type").equals("play")) {
						if (playObject.get("result") != null && !"".equals(playObject.get("result"))) {
							mapPlay.get(url_key).getResult().add(Integer.parseInt(playObject.get("result").toString()));
						}

						mapPlay.get(url_key).getFirstPicDuration().add(Integer.parseInt(playObject.get("first_pic_duration").toString()));
					} else if (playObject.get("event_type").equals("playlock")) {
						int duration_new = Integer.parseInt(playObject.get("duration").toString());
						int lockCount_new = Integer.parseInt(playObject.get("lock_count").toString());
						if (mapPlay.get(url_key).getDuration() == null || mapPlay.get(url_key).getDuration() < duration_new) {
							mapPlay.get(url_key).setDuration(duration_new);
						}
						if (mapPlay.get(url_key).getLockCount() == null || mapPlay.get(url_key).getLockCount() < lockCount_new) {
							mapPlay.get(url_key).setLockCount(lockCount_new);
						}
					}

					if (mapPlay.get(url_key).getStart_time() == null || AboutTime.toLongSSS(mapPlay.get(url_key).getStart_time()) > AboutTime.toLongSSS(time)) {
						mapPlay.get(url_key).setStart_time(time);
					}

					if (mapPlay.get(url_key).getEnd_time() == null || AboutTime.toLongSSS(mapPlay.get(url_key).getEnd_time()) < AboutTime.toLongSSS(time)) {
						mapPlay.get(url_key).setEnd_time(time);
					}
				}

				// 不符合条件的数据，删除
				if (lockCount != null) {
					if (!lockCount.contains(">")) {
						for (String urlkey : mapPlay.keySet()) {
							if (mapPlay.get(urlkey).getLockCount() == null || mapPlay.get(urlkey).getLockCount() != Integer.parseInt(lockCount)) {
								mapPlay.remove(urlkey);
							}
						}
					} else {
						for (String urlkey : mapPlay.keySet()) {
							if (mapPlay.get(urlkey).getLockCount() == null || mapPlay.get(urlkey).getLockCount() <= Integer.parseInt(lockCount.split(">")[1])) {
								mapPlay.remove(urlkey);
							}
						}
					}
				}

				if (duration != null) {
					int duratinInt = Integer.parseInt(duration);
					for (String urlkey : mapPlay.keySet()) {
						if (mapPlay.get(urlkey).getDuration() == null) {
							mapPlay.remove(urlkey);
						} else {
							if (durationSelect == null) {
								if (mapPlay.get(urlkey).getDuration() < duratinInt) {
									mapPlay.remove(urlkey);
								}
							} else if (durationSelect.equals("lt")) {
								if (mapPlay.get(urlkey).getDuration() > duratinInt) {
									mapPlay.remove(urlkey);
								}
							} else if (durationSelect.equals("gt")) {
								if (mapPlay.get(urlkey).getDuration() < duratinInt) {
									mapPlay.remove(urlkey);
								}
							} else {
								if (mapPlay.get(urlkey).getDuration() != duratinInt) {
									mapPlay.remove(urlkey);
								}
							}
						}
					}
				}

				if (firstPicDuration != null) {
					// 如果过滤条件中有出画面时长，而此条数据中没有出画面时长数据，删除
					for (String urlkey : mapPlay.keySet()) {
						if (mapPlay.get(urlkey).getFirstPicDuration().size() == 0) {
							mapPlay.remove(urlkey);
						}
					}
					// 不符合条件的数据，删除
					for (String urlkey : mapPlay.keySet()) {
						int firstPicDurationInt = Integer.parseInt(firstPicDuration);
						if (firstPicDurationSelect == null) {
							if (Collections.max(mapPlay.get(urlkey).getFirstPicDuration()) < firstPicDurationInt) {
								mapPlay.remove(urlkey);
							}
						} else if (firstPicDurationSelect.equals("lt")) {
							if (Collections.min(mapPlay.get(urlkey).getFirstPicDuration()) > firstPicDurationInt) {
								mapPlay.remove(urlkey);
							}
						} else if (firstPicDurationSelect.equals("gt")) {
							if (Collections.max(mapPlay.get(urlkey).getFirstPicDuration()) < firstPicDurationInt) {
								mapPlay.remove(urlkey);
							}
						} else {
							boolean isEquals = false;
							for (int firstPD : mapPlay.get(urlkey).getFirstPicDuration()) {
								if (firstPD == firstPicDurationInt) {
									isEquals = true;
									break;
								}
							}
							if (isEquals == false) {
								mapPlay.remove(urlkey);
							}
						}
					}
				}

				for (String urlkey : mapPlay.keySet()) {
					mapPlay.get(urlkey).setBrowser_version(mapLoad.get(key).getBrowser_version());
					mapPlay.get(urlkey).setBusinessID(mapLoad.get(key).getBusiness_id());
					mapPlay.get(urlkey).setCity(mapLoad.get(key).getCity());
					mapPlay.get(urlkey).setDevice_name(mapLoad.get(key).getDevice_type_name());
					mapPlay.get(urlkey).setOpenType(mapLoad.get(key).getOpen_type());
					mapPlay.get(urlkey).setOperation_guid(mapLoad.get(key).getOperation_guid());
					mapPlay.get(urlkey).setOs_type(mapLoad.get(key).getOs_type());
					mapPlay.get(urlkey).setOs_version(mapLoad.get(key).getOs_version());
					mapPlay.get(urlkey).setPlayer_sdk_type(mapLoad.get(key).getPlayer_sdk_type());
					mapPlay.get(urlkey).setProvince(mapLoad.get(key).getProvince());
					mapPlay.get(urlkey).setUid(mapLoad.get(key).getUid());
					mapPlay.get(urlkey).setUrl(urlkey);
					mapPlay.get(urlkey).setUserName(mapLoad.get(key).getUser_name());
					playDataList.add(mapPlay.get(urlkey));
				}
			}

			Collections.sort(playDataList, new Comparator<PlayDataEntity>() {
				@Override
				public int compare(PlayDataEntity o1, PlayDataEntity o2) {
					if (DateUtil.toLongSSS(o1.getStart_time()) > DateUtil.toLongSSS(o2.getStart_time())) {
						return -1;
					}
					if (DateUtil.toLongSSS(o1.getStart_time()) == DateUtil.toLongSSS(o2.getStart_time())) {
						return 0;
					}
					return 1;
				}
			});

			if (playDataList.size() > totalUser) {
				liveDTO.setList(playDataList.subList(0, totalUser));
			} else {
				liveDTO.setList(playDataList);
			}

		} catch (Exception e) {
			logger.error("统计中 查询端到端findLive：", e);
			System.out.println(e);
		} finally {
			conn.destory(mongo, db);
		}
		return liveDTO;
	}

	@Override
	public PlayListDto findDurationUser(long startTime, long endTime, String url, String domain, String isp, String openType, String businessID,
			String userName, String durationSelect, String duration, String firstPicDurationSelect, String firstPicDuration, String userAccount, int totalUser,
			boolean iscount) {
		PlayListDto liveDTO = new PlayListDto();
		List<PlayDataEntity> playDataList = new ArrayList<PlayDataEntity>();
		try {
			DBCollection employee = db.getCollection(LIVE_INFO_TAB);
			DBCursor curLoad = null;
			DBCursor curPlay = null;
			BasicDBObject dbo = new BasicDBObject("playtime", new BasicDBObject("$gte", startTime).append("$lt", endTime));

			dbo.append("event_type", "LoadPlayer");

			if (domain != null) {
				dbo.append("province", domain);
			}
			if (isp != null) {
				dbo.append("isp", isp);
			}

			if (openType != null) {
				dbo.append("open_type", openType);
			}

			if (businessID != null) {
				dbo.append("business_id", businessID);
			}

			if (userName != null) {
				dbo.append("user_name", userName);
			}

			curLoad = employee.find(dbo);

			Map<String, LoadPlayerEntity> mapLoad = new ConcurrentHashMap<String, LoadPlayerEntity>();
			while (curLoad.hasNext()) {
				DBObject loadObject = curLoad.next();
				String uid_guid = loadObject.get("uid").toString() + "_" + loadObject.get("operation_guid").toString();
				mapLoad.put(uid_guid, BeanUtil.dbObject2Bean(loadObject, new LoadPlayerEntity())); // 得到初步筛选结果
			}

			for (String key : mapLoad.keySet()) {
				String uid = key.split("_")[0];
				String guid = key.split("_")[1];
				BasicDBObject dboPlay = new BasicDBObject("playtime", new BasicDBObject("$gte", startTime).append("$lt", endTime));
				dboPlay.append("uid", uid);
				dboPlay.append("operation_guid", guid);
				BasicDBList eventList = new BasicDBList();
				eventList.add(new BasicDBObject("event_type", "play"));
				eventList.add(new BasicDBObject("event_type", "playlock"));
				dboPlay.append("$or", eventList);

				BasicDBList urlList = new BasicDBList();
				if (url != null && !url.equals("多条以英文逗号隔开") && !url.equals("")) {
					if (url.split(",").length > 0) {
						String[] urlArray = url.split(",");
						for (int i = 0; i < urlArray.length; i++) {
							urlList.add(new BasicDBObject("content", urlArray[i]));
						}
						dboPlay.put("$or", urlList);
					} else {
						dboPlay.append("content", url);
					}
				}
				curPlay = employee.find(dboPlay);

				Map<String, PlayDataEntity> mapPlay = new ConcurrentHashMap<String, PlayDataEntity>();

				// 先进行装载
				while (curPlay.hasNext()) {
					DBObject playObject = curPlay.next();

					String url_key = playObject.get("url").toString();

					if (mapPlay.get(url_key) == null) {
						mapPlay.put(url_key, new PlayDataEntity());
					}

					String time = playObject.get("time").toString();

					if (playObject.get("event_type").equals("play")) {
						if (playObject.get("result") != null && !"".equals(playObject.get("result"))) {
							mapPlay.get(url_key).getResult().add(Integer.parseInt(playObject.get("result").toString()));
						}

						mapPlay.get(url_key).getFirstPicDuration().add(Integer.parseInt(playObject.get("first_pic_duration").toString()));
					} else if (playObject.get("event_type").equals("playlock")) {
						int duration_new = Integer.parseInt(playObject.get("duration").toString());
						int lockCount_new = Integer.parseInt(playObject.get("lock_count").toString());
						if (mapPlay.get(url_key).getDuration() == null || mapPlay.get(url_key).getDuration() < duration_new) {
							mapPlay.get(url_key).setDuration(duration_new);
						}
						if (mapPlay.get(url_key).getLockCount() == null || mapPlay.get(url_key).getLockCount() < lockCount_new) {
							mapPlay.get(url_key).setLockCount(lockCount_new);
						}
					}

					if (mapPlay.get(url_key).getStart_time() == null || AboutTime.toLongSSS(mapPlay.get(url_key).getStart_time()) > AboutTime.toLongSSS(time)) {
						mapPlay.get(url_key).setStart_time(time);
					}

					if (mapPlay.get(url_key).getEnd_time() == null || AboutTime.toLongSSS(mapPlay.get(url_key).getEnd_time()) < AboutTime.toLongSSS(time)) {
						mapPlay.get(url_key).setEnd_time(time);
					}
				}

				// 不符合条件的数据，删除
				if (duration != null) {
					for (String urlkey : mapPlay.keySet()) {
						int duratinInt = Integer.parseInt(duration);
						if (durationSelect == null) {
							if (mapPlay.get(urlkey).getDuration() < duratinInt) {
								mapPlay.remove(urlkey);
							}
						} else if (durationSelect.equals("lt")) {
							if (mapPlay.get(urlkey).getDuration() > duratinInt) {
								mapPlay.remove(urlkey);
							}
						} else if (durationSelect.equals("gt")) {
							if (mapPlay.get(urlkey).getDuration() < duratinInt) {
								mapPlay.remove(urlkey);
							}
						} else {
							if (mapPlay.get(urlkey).getDuration() != duratinInt) {
								mapPlay.remove(urlkey);
							}
						}
					}
				}

				if (firstPicDuration != null) {
					// 如果过滤条件中有出画面时长，而此条数据中没有出画面时长数据，删除
					for (String urlkey : mapPlay.keySet()) {
						if (mapPlay.get(urlkey).getFirstPicDuration().size() == 0) {
							mapPlay.remove(urlkey);
						}
					}
					// 不符合条件的数据，删除
					for (String urlkey : mapPlay.keySet()) {
						int firstPicDurationInt = Integer.parseInt(firstPicDuration);
						if (firstPicDurationSelect == null) {
							if (Collections.max(mapPlay.get(urlkey).getFirstPicDuration()) < firstPicDurationInt) {
								mapPlay.remove(urlkey);
							}
						} else if (firstPicDurationSelect.equals("lt")) {
							if (Collections.min(mapPlay.get(urlkey).getFirstPicDuration()) > firstPicDurationInt) {
								mapPlay.remove(urlkey);
							}
						} else if (firstPicDurationSelect.equals("gt")) {
							if (Collections.max(mapPlay.get(urlkey).getFirstPicDuration()) < firstPicDurationInt) {
								mapPlay.remove(urlkey);
							}
						} else {
							boolean isEquals = false;
							for (int firstPD : mapPlay.get(urlkey).getFirstPicDuration()) {
								if (firstPD == firstPicDurationInt) {
									isEquals = true;
									break;
								}
							}
							if (isEquals == false) {
								mapPlay.remove(urlkey);
							}
						}
					}
				}

				for (String urlkey : mapPlay.keySet()) {
					mapPlay.get(urlkey).setBrowser_version(mapLoad.get(key).getBrowser_version());
					mapPlay.get(urlkey).setBusinessID(mapLoad.get(key).getBusiness_id());
					mapPlay.get(urlkey).setCity(mapLoad.get(key).getCity());
					mapPlay.get(urlkey).setDevice_name(mapLoad.get(key).getDevice_type_name());
					mapPlay.get(urlkey).setOpenType(mapLoad.get(key).getOpen_type());
					mapPlay.get(urlkey).setOperation_guid(mapLoad.get(key).getOperation_guid());
					mapPlay.get(urlkey).setOs_type(mapLoad.get(key).getOs_type());
					mapPlay.get(urlkey).setOs_version(mapLoad.get(key).getOs_version());
					mapPlay.get(urlkey).setPlayer_sdk_type(mapLoad.get(key).getPlayer_sdk_type());
					mapPlay.get(urlkey).setProvince(mapLoad.get(key).getProvince());
					mapPlay.get(urlkey).setUid(mapLoad.get(key).getUid());
					mapPlay.get(urlkey).setUrl(urlkey);
					mapPlay.get(urlkey).setUserName(mapLoad.get(key).getUser_name());
					playDataList.add(mapPlay.get(urlkey));
				}
			}

			Collections.sort(playDataList, new Comparator<PlayDataEntity>() {
				@Override
				public int compare(PlayDataEntity o1, PlayDataEntity o2) {
					if (o1.getDuration() > o2.getDuration()) {
						return -1;
					}
					if (o1.getDuration() == o2.getDuration()) {
						return 0;
					}
					return 1;
				}
			});

			if (userAccount != null && !userAccount.equals("")) {
				totalUser = Integer.parseInt(userAccount);
			}

			if (playDataList.size() > totalUser) {
				liveDTO.setList(playDataList.subList(0, totalUser));
			} else {
				liveDTO.setList(playDataList);
			}

		} catch (Exception e) {
			logger.error("统计中 查询端到端findLive：", e);
			System.out.println(e);
		} finally {
			conn.destory(mongo, db);
		}
		return liveDTO;
	}

	@Override
	public PlayListDto findFirstPicDurationUser(long startTime, long endTime, String url, String domain, String isp, String openType, String businessID,
			String userName, String durationSelect, String duration, String firstPicDurationSelect, String firstPicDuration, String firstPicMin,
			String firstPicMax, int totalUser, /* int currPage, */boolean iscount) {
		PlayListDto liveDTO = new PlayListDto();
		List<PlayDataEntity> playDataList = new ArrayList<PlayDataEntity>();
		try {
			DBCollection employee = db.getCollection(LIVE_INFO_TAB);
			DBCursor curLoad = null;
			DBCursor curPlay = null;
			BasicDBObject dbo = new BasicDBObject("playtime", new BasicDBObject("$gte", startTime).append("$lt", endTime));

			dbo.append("event_type", "LoadPlayer");

			if (domain != null) {
				dbo.append("province", domain);
			}
			if (isp != null) {
				dbo.append("isp", isp);
			}

			if (openType != null) {
				dbo.append("open_type", openType);
			}

			if (businessID != null) {
				dbo.append("business_id", businessID);
			}

			if (userName != null) {
				dbo.append("user_name", userName);
			}

			curLoad = employee.find(dbo);

			Map<String, LoadPlayerEntity> mapLoad = new ConcurrentHashMap<String, LoadPlayerEntity>();
			while (curLoad.hasNext()) {
				DBObject loadObject = curLoad.next();
				String uid_guid = loadObject.get("uid").toString() + "_" + loadObject.get("operation_guid").toString();
				mapLoad.put(uid_guid, BeanUtil.dbObject2Bean(loadObject, new LoadPlayerEntity())); // 得到初步筛选结果
			}

			for (String key : mapLoad.keySet()) {
				String uid = key.split("_")[0];
				String guid = key.split("_")[1];
				BasicDBObject dboPlay = new BasicDBObject("playtime", new BasicDBObject("$gte", startTime).append("$lt", endTime));
				dboPlay.append("uid", uid);
				dboPlay.append("operation_guid", guid);
				BasicDBList eventList = new BasicDBList();
				eventList.add(new BasicDBObject("event_type", "play"));
				eventList.add(new BasicDBObject("event_type", "playlock"));
				dboPlay.append("$or", eventList);

				BasicDBList urlList = new BasicDBList();
				if (url != null && !url.equals("多条以英文逗号隔开") && !url.equals("")) {
					if (url.split(",").length > 0) {
						String[] urlArray = url.split(",");
						for (int i = 0; i < urlArray.length; i++) {
							urlList.add(new BasicDBObject("content", urlArray[i]));
						}
						dboPlay.put("$or", urlList);
					} else {
						dboPlay.append("content", url);
					}
				}
				curPlay = employee.find(dboPlay);

				Map<String, PlayDataEntity> mapPlay = new ConcurrentHashMap<String, PlayDataEntity>();

				// 先进行装载
				while (curPlay.hasNext()) {
					DBObject playObject = curPlay.next();

					String url_key = playObject.get("url").toString();

					if (mapPlay.get(url_key) == null) {
						mapPlay.put(url_key, new PlayDataEntity());
					}

					String time = playObject.get("time").toString();

					if (playObject.get("event_type").equals("play")) {
						if (playObject.get("result") != null && !"".equals(playObject.get("result"))) {
							mapPlay.get(url_key).getResult().add(Integer.parseInt(playObject.get("result").toString()));
						}

						mapPlay.get(url_key).getFirstPicDuration().add(Integer.parseInt(playObject.get("first_pic_duration").toString()));
					} else if (playObject.get("event_type").equals("playlock")) {
						int duration_new = Integer.parseInt(playObject.get("duration").toString());
						int lockCount_new = Integer.parseInt(playObject.get("lock_count").toString());
						if (mapPlay.get(url_key).getDuration() == null || mapPlay.get(url_key).getDuration() < duration_new) {
							mapPlay.get(url_key).setDuration(duration_new);
						}
						if (mapPlay.get(url_key).getLockCount() == null || mapPlay.get(url_key).getLockCount() < lockCount_new) {
							mapPlay.get(url_key).setLockCount(lockCount_new);
						}
					}

					if (mapPlay.get(url_key).getStart_time() == null || AboutTime.toLongSSS(mapPlay.get(url_key).getStart_time()) > AboutTime.toLongSSS(time)) {
						mapPlay.get(url_key).setStart_time(time);
					}

					if (mapPlay.get(url_key).getEnd_time() == null || AboutTime.toLongSSS(mapPlay.get(url_key).getEnd_time()) < AboutTime.toLongSSS(time)) {
						mapPlay.get(url_key).setEnd_time(time);
					}
				}

				// 不符合条件的数据，删除
				if (duration != null) {
					for (String urlkey : mapPlay.keySet()) {
						int duratinInt = Integer.parseInt(duration);
						if (durationSelect == null) {
							if (mapPlay.get(urlkey).getDuration() < duratinInt) {
								mapPlay.remove(urlkey);
							}
						} else if (durationSelect.equals("lt")) {
							if (mapPlay.get(urlkey).getDuration() > duratinInt) {
								mapPlay.remove(urlkey);
							}
						} else if (durationSelect.equals("gt")) {
							if (mapPlay.get(urlkey).getDuration() < duratinInt) {
								mapPlay.remove(urlkey);
							}
						} else {
							if (mapPlay.get(urlkey).getDuration() != duratinInt) {
								mapPlay.remove(urlkey);
							}
						}
					}
				}

				// if (firstPicDuration != null) {
				// 如果过滤条件中有出画面时长，而此条数据中没有出画面时长数据，删除
				for (String urlkey : mapPlay.keySet()) {
					if (mapPlay.get(urlkey).getFirstPicDuration().size() == 0) {
						mapPlay.remove(urlkey);
					}
				}
				// 不符合条件的数据，删除
				for (String urlkey : mapPlay.keySet()) {
					int firstPicDurationMin = Integer.parseInt(firstPicMin) * 1000;
					int firstPicDurationMax = Integer.parseInt(firstPicMax) * 1000;

					boolean isBetween = false;
					for (int fir : mapPlay.get(urlkey).getFirstPicDuration()) {
						if (fir < firstPicDurationMax && fir > firstPicDurationMin) {
							isBetween = true;
							break;
						}
					}
					if (isBetween == false) {
						mapPlay.remove(urlkey);
					}
				}
				// }

				for (String urlkey : mapPlay.keySet()) {
					mapPlay.get(urlkey).setBrowser_version(mapLoad.get(key).getBrowser_version());
					mapPlay.get(urlkey).setBusinessID(mapLoad.get(key).getBusiness_id());
					mapPlay.get(urlkey).setCity(mapLoad.get(key).getCity());
					mapPlay.get(urlkey).setDevice_name(mapLoad.get(key).getDevice_type_name());
					mapPlay.get(urlkey).setOpenType(mapLoad.get(key).getOpen_type());
					mapPlay.get(urlkey).setOperation_guid(mapLoad.get(key).getOperation_guid());
					mapPlay.get(urlkey).setOs_type(mapLoad.get(key).getOs_type());
					mapPlay.get(urlkey).setOs_version(mapLoad.get(key).getOs_version());
					mapPlay.get(urlkey).setPlayer_sdk_type(mapLoad.get(key).getPlayer_sdk_type());
					mapPlay.get(urlkey).setProvince(mapLoad.get(key).getProvince());
					mapPlay.get(urlkey).setUid(mapLoad.get(key).getUid());
					mapPlay.get(urlkey).setUrl(urlkey);
					mapPlay.get(urlkey).setUserName(mapLoad.get(key).getUser_name());
					playDataList.add(mapPlay.get(urlkey));
				}
			}

			Collections.sort(playDataList, new Comparator<PlayDataEntity>() {
				@Override
				public int compare(PlayDataEntity o1, PlayDataEntity o2) {
					if (DateUtil.toLongSSS(o1.getStart_time()) > DateUtil.toLongSSS(o2.getStart_time())) {
						return -1;
					}
					if (DateUtil.toLongSSS(o1.getStart_time()) == DateUtil.toLongSSS(o2.getStart_time())) {
						return 0;
					}
					return 1;
				}
			});

			if (playDataList.size() > totalUser) {
				liveDTO.setList(playDataList.subList(0, totalUser));
			} else {
				liveDTO.setList(playDataList);
			}
		} catch (Exception e) {
			logger.error("统计中 查询findFirstPicDurationUser：", e);
			System.out.println(e);
		} finally {
			conn.destory(mongo, db);
		}
		return liveDTO;
	}

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
			while (!loadThread.loadComplete || !playThread.playComplete) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					logger.error("统计中LiveInfoDaoImpl: ", e);
					System.exit(0);// 退出程序
				}
			}

			long mergStart = System.currentTimeMillis();
			for (String key_MapPlay : playThread.mapPlay.keySet()) {
				String uid_guid_bid = key_MapPlay.substring(0, getCharacterPosition(key_MapPlay, 3));
				PlayDataEntity playDataEntity = playThread.mapPlay.get(key_MapPlay);

				// 如果mapload中有本次播放的播放器信息，则将mapload与mapplay结合
				if (loadThread.mapLoad.keySet().contains(uid_guid_bid)) {
					
					//System.out.println(uid_guid_bid);

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
	
	@Override
	public List<JSONObject> findLiveWithPretreat(long startTime, long endTime, String url, String domain, String isp, String openType, String businessID, String userName,int pageSize, int currPage, boolean iscount) {
		
		List<JSONObject> listJSONObject = new ArrayList<JSONObject>();

		int countPre;
		int totalPagePre;
		
		DBCollection employee = db.getCollection(LIVE_PRE_DATA);
		DBCursor curPre = null;
		BasicDBObject dboPre = new BasicDBObject("startTime", new BasicDBObject("$lt", endTime));
		dboPre.append("endTime", new BasicDBObject("$gt", startTime));
		// 如果设置了url过滤，优先按照url过滤，否则按照businessID过滤
		if (url == null) {
			if (businessID != null) {
				dboPre.append("business_id_key", businessID);
			}
		}else{
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
			
			/*for(DBObject preObject : listDBObject){
				JSONObject preJson = JSONObject.parseObject(preObject.toString());

				String totalKey = preJson.get("key").toString();
				if(!mapPre.containsKey(totalKey)){
					mapPre.put(totalKey, new LivePreDataEntity());
				}
				LivePreDataEntity livePreDataEntity = mapPre.get(totalKey);
				
				JSONObject provinceObj = preJson.getJSONObject("province");
				for(String provinceName : provinceObj.keySet()){
					if(!livePreDataEntity.getProvince().containsKey(provinceName)){
						livePreDataEntity.getProvince().put(provinceName, 1);
					}else{
						livePreDataEntity.getProvince().put(provinceName, livePreDataEntity.getProvince().get(provinceName) + 1);
					}
				}
				
			}*/
		}
		//TODO
		Runtime run = Runtime.getRuntime();
		long startMem = run.totalMemory()-run.freeMemory();
		System.gc();
		long endMem = run.totalMemory()-run.freeMemory();
		System.out.println("memory difference:" + (endMem-startMem));
		return listJSONObject;
	}
	
	@Override
	public List<JSONObject> findOnlineWithPretreat(long startTime, long endTime, String url, int pageSize, int currPage, boolean iscount) {
		
		List<JSONObject> listJSONObject = new ArrayList<JSONObject>();

		int countPre;
		int totalPagePre;
		
		DBCollection employee = db.getCollection(LIVE_ONLINE_DATA);
		DBCursor curPre = null;
		BasicDBObject dboPre = new BasicDBObject("startTime", new BasicDBObject("$lt", endTime));
		dboPre.append("endTime", new BasicDBObject("$gt", startTime));
		// 如果设置了url过滤，优先按照url过滤，否则按照businessID过滤
		/*if (url == null) {
			if (businessID != null) {
				dboPre.append("business_id_key", businessID);
			}
		}else{
			dboPre.append("url_key", url);
		}*/
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
			//dboPre.append("content", url);
		}
		dboPre.append("isTen", null);

		countPre = employee.find(dboPre).count();

		totalPagePre = countPre % pageSize == 0 ? countPre / pageSize : (countPre / pageSize) + 1;

		for (int currPg = 1; currPg <= totalPagePre; currPg++) {
			int skip = (currPg - 1) * pageSize;
			curPre = employee.find(dboPre).skip(skip).limit(pageSize);
			while (curPre.hasNext()) {
				DBObject preObject = curPre.next();
				preObject.put("raw", 1);//原始的数据
				JSONObject preJson = JSONObject.fromObject(preObject.toString());
				listJSONObject.add(preJson);
			}
		}
		//TODO
		Runtime run = Runtime.getRuntime();
		long startMem = run.totalMemory()-run.freeMemory();
		System.gc();
		long endMem = run.totalMemory()-run.freeMemory();
		System.out.println("memory difference:" + (endMem-startMem));
		return listJSONObject;
	}

	/*
	  public PlayListDto findLive(long startTime, long endTime, String url,
	  String domain, String isp, String openType, String businessID, String
	  userName, String durationSelect, String duration, String
	  firstPicDurationSelect, String firstPicDuration, int pageSize, int
	  currPage, boolean iscount) {
	  
	  PlayListDto liveDTO = new PlayListDto(); Map<String, LoadPlayerEntity>
	  mapLoad = new ConcurrentHashMap<String, LoadPlayerEntity>(); Map<String,
	  PlayDataEntity> mapPlay = new ConcurrentHashMap<String,
	  PlayDataEntity>();
	  
	  List<PlayDataEntity> playDataList = new ArrayList<PlayDataEntity>();
	  
	  try {
	  
	  DBCollection employee = db.getCollection(LIVE_INFO_TAB); DBCursor curLoad
	  = null; DBCursor curPlay = null;
	  
	  long loadStart = System.currentTimeMillis();
	  
	  BasicDBObject dboLoad = new BasicDBObject("playtime", new
	  BasicDBObject("$gte", startTime).append("$lt", endTime)); if(url ==
	  null){ if (businessID != null) { dboLoad.append("business_id",
	  businessID); } }
	  
	  if (domain != null) { dboLoad.append("province", domain); } if(isp !=
	  null){ if(!isp.equals("else")){ dboLoad.append("isp", isp); }else{
	  BasicDBList values = new BasicDBList(); values.add("电信");
	  values.add("移动"); values.add("联通"); dboLoad.append("isp", new
	  BasicDBObject("$nin", values)); } } if (openType != null) {
	  dboLoad.append("open_type", openType); } if (userName != null) {
	  dboLoad.append("user_name", userName); } dboLoad.append("event_type",
	  "LoadPlayer");
	  
	  int countLoad = employee.find(dboLoad).count(); int totalPageLoad =
	  countLoad % pageSize == 0 ? countLoad / pageSize : (countLoad / pageSize)
	  + 1;
	  
	  int countLoad1 = 0;
	  
	  for (int currPg = 1; currPg <= totalPageLoad; currPg++) { int skip =
	  (currPg - 1)  pageSize; curLoad =
	  employee.find(dboLoad).skip(skip).limit(pageSize); while
	  (curLoad.hasNext()) { countLoad1++; DBObject loadObject = curLoad.next();
	  if (loadObject.get("uid") != null &&
	  !loadObject.get("uid").equals("null") && loadObject.get("operation_guid")
	  != null && !loadObject.get("operation_guid").equals("null") &&
	  loadObject.get("business_id") != null &&
	  !loadObject.get("business_id").equals("null")) { String uid_guid_bid =
	  loadObject.get("uid").toString() + "_" +
	  loadObject.get("operation_guid").toString() + "_" +
	  loadObject.get("business_id").toString(); mapLoad.put(uid_guid_bid,
	  BeanUtil.dbObject2Bean(loadObject, new LoadPlayerEntity())); } } } long
	  loadEnd = System.currentTimeMillis(); System.out.println("load:" +
	  (loadEnd - loadStart) + "执行了hasNext" + countLoad1 + "次" + "mapload的size:"
	  + mapLoad.keySet().size());
	 ////
	
	  
	  long playStart = System.currentTimeMillis();
	  
	  BasicDBObject dboPlay = new BasicDBObject("playtime", new
	  BasicDBObject("$gte", startTime - 60  60  1000).append("$lt",
	  endTime));
	  
	  BasicDBList urlList = new BasicDBList(); if (url != null &&
	  !url.equals("多条以英文逗号隔开") && !url.equals("")) { if (url.split(",").length
	  > 0) { String[] urlArray = url.split(","); for (int i = 0; i <
	  urlArray.length; i++) { urlList.add(new BasicDBObject("content",
	  urlArray[i])); } dboPlay.put("$or", urlList); } else {
	  dboPlay.append("content", url); } }else{ if (businessID != null) {
	  dboPlay.append("business_id", businessID); } }
	  dboPlay.append("event_type", new BasicDBObject("$ne", "LoadPlayer"));
	  
	  int countPlay = employee.find(dboPlay).count(); int totalPagePlay =
	  countPlay % pageSize == 0 ? countPlay / pageSize : (countPlay / pageSize)
	  + 1;
	  
	  int countPlay1 = 0;
	  
	  for (int currPg = 1; currPg <= totalPagePlay; currPg++) { int skip =
	  (currPg - 1)  pageSize; curPlay =
	  employee.find(dboPlay).skip(skip).limit(pageSize);
	  
	  while (curPlay.hasNext()) { countPlay1++; DBObject playObject =
	  curPlay.next();
	  
	  if(playObject.get("uid").toString().equals(
	  "2afe123d-905a-4aa8-a057-1aca1b354063"
	  )&&playObject.get("url").toString().
	  equals("http://vod.butel.com/dbc4dcf8-4e47-4089-bc8c-b678d4e411ee.m3u8"
	  )){ System.out.println(playObject.get("url").toString()); }
	  
	  if (playObject.get("uid") != null &&
	  !playObject.get("uid").equals("null") && playObject.get("operation_guid")
	  != null) {
	  
	  String playBusinessID = playObject.get("business_id").toString(); if
	  (playBusinessID.equals("") || playBusinessID.equals("null")) {
	  playBusinessID = "未知"; }
	  
	  String uid_guid_bid_url = playObject.get("uid").toString() + "_" +
	  playObject.get("operation_guid").toString() + "_" + playBusinessID + "_"
	  + playObject.get("url").toString();
	  
	  
	  
	  if (mapPlay.get(uid_guid_bid_url) != null) {
	  
	  PlayDataEntity playDataEntity = mapPlay.get(uid_guid_bid_url);
	  
	  if (!playDataEntity.isPauseLong()) {
	  
	  int oldDuration = playDataEntity.getDuration(); int newDuration =
	  Integer.parseInt(playObject.get("duration").toString());
	  
	  long oldTime = AboutTime.toLongSSS(playDataEntity.getEnd_time()); long
	  newTime = AboutTime.toLongSSS(playObject.get("time").toString());
	  
	  if (playObject.get("event_type").toString().equals("playlock") &&
	  oldDuration == newDuration) { if (!playDataEntity.isPause()) {
	  playDataEntity.setPause(true); playDataEntity.setPauseTime(oldTime); }
	  else { if ((newTime - playDataEntity.getPauseTime()) > 600000) {
	  playDataEntity.setPauseLong(true); continue; } } }
	  
	  if (newTime < AboutTime.toLongSSS(playDataEntity.getStart_time())) {
	  playDataEntity.setStart_time(playObject.get("time").toString()); } if
	  (newTime > AboutTime.toLongSSS(playDataEntity.getEnd_time())) {
	  playDataEntity.setEnd_time(playObject.get("time").toString()); } if
	  (playObject.get("event_type").toString().equals("play")) { if
	  (!playObject.get("sub_type").equals("pause")) {
	  playDataEntity.setPauseTime(-1L); playDataEntity.setPause(false);
	  playDataEntity.getFirstPicDuration().add(Integer.parseInt(playObject.get(
	  "first_pic_duration").toString())); }
	  playDataEntity.getResult().add(Integer
	  .parseInt(playObject.get("result").toString())); } else if
	  (playObject.get("event_type").toString().equals("playlock")) { if
	  (Integer.parseInt(playObject.get("lock_count").toString()) >
	  playDataEntity.getLockCount()) {
	  playDataEntity.setLockCount(Integer.parseInt
	  (playObject.get("lock_count").toString())); } if (newDuration >
	  oldDuration) { playDataEntity.setDuration(newDuration); } } } } else {
	  PlayDataEntity playDataEntity = new PlayDataEntity();
	  playDataEntity.setUid(playObject.get("uid").toString());
	  playDataEntity.setOperation_guid
	  (playObject.get("operation_guid").toString());
	  playDataEntity.setBusinessID(playObject.get("business_id").toString());
	  playDataEntity.setUrl(playObject.get("url").toString());
	  playDataEntity.setStart_time(playObject.get("time").toString());
	  playDataEntity.setEnd_time(playObject.get("time").toString());
	  
	  if (playObject.get("event_type").toString().equals("play")) { if
	  (!playObject.get("sub_type").equals("pause")) {
	  playDataEntity.getFirstPicDuration
	  ().add(Integer.parseInt(playObject.get("first_pic_duration"
	  ).toString())); }
	  playDataEntity.getResult().add(Integer.parseInt(playObject
	  .get("result").toString())); } else if
	  (playObject.get("event_type").toString().equals("playlock")) {
	  playDataEntity
	  .setLockCount(Integer.parseInt(playObject.get("lock_count").toString()));
	  playDataEntity
	  .setDuration(Integer.parseInt(playObject.get("duration").toString())); }
	  mapPlay.put(uid_guid_bid_url, playDataEntity); } } } }
	  
	  // 不符合条件的数据，删除 if (duration != null && !duration.equals("")) { for
	  (String urlkey : mapPlay.keySet()) { int duratinInt =
	  Integer.parseInt(duration); if (durationSelect == null) { if
	  (mapPlay.get(urlkey).getDuration() < duratinInt) {
	  mapPlay.remove(urlkey); } } else if (durationSelect.equals("lt")) { if
	  (mapPlay.get(urlkey).getDuration() > duratinInt) {
	  mapPlay.remove(urlkey); } } else if (durationSelect.equals("gt")) { if
	  (mapPlay.get(urlkey).getDuration() < duratinInt) {
	  mapPlay.remove(urlkey); } } else { if (mapPlay.get(urlkey).getDuration()
	  != duratinInt) { mapPlay.remove(urlkey); } } } }
	  
	  if (firstPicDuration != null && !firstPicDuration.equals("")) { //
	  如果过滤条件中有出画面时长，而此条数据中没有出画面时长数据，删除 for (String urlkey : mapPlay.keySet()) {
	  if (mapPlay.get(urlkey).getFirstPicDuration().size() == 0) {
	  mapPlay.remove(urlkey); } } // 不符合条件的数据，删除 for (String urlkey :
	  mapPlay.keySet()) { int firstPicDurationFromPage =
	  Integer.parseInt(firstPicDuration); boolean isDisplay = false; if
	  (firstPicDurationSelect == null) { for (int fir :
	  mapPlay.get(urlkey).getFirstPicDuration()) { if (fir >
	  firstPicDurationFromPage) { isDisplay = true; break; } } } else if
	  (firstPicDurationSelect.equals("lt")) { for (int fir :
	  mapPlay.get(urlkey).getFirstPicDuration()) { if (fir <
	  firstPicDurationFromPage) { isDisplay = true; break; } } } else if
	  (firstPicDurationSelect.equals("gt")) { for (int fir :
	  mapPlay.get(urlkey).getFirstPicDuration()) { if (fir >
	  firstPicDurationFromPage) { isDisplay = true; break; } } } else { for
	  (int fir : mapPlay.get(urlkey).getFirstPicDuration()) { if (fir ==
	  firstPicDurationFromPage) { isDisplay = true; break; } } } if
	  (!isDisplay) { mapPlay.remove(urlkey); } } }
	  
	  long playEnd = System.currentTimeMillis(); System.out.println("play:" +
	  (playEnd - playStart) + "执行了hasNext" + countPlay1 + "次" + "mapPlay的size:"
	  + mapPlay.keySet().size());
	  
	  long mergStart = System.currentTimeMillis(); for (String key_MapPlay :
	  mapPlay.keySet()) { String uid_guid_bid = key_MapPlay.substring(0,
	  getCharacterPosition(key_MapPlay, 3)); PlayDataEntity playDataEntity =
	  mapPlay.get(key_MapPlay);
	  
	  if (mapLoad.keySet().contains(uid_guid_bid)) {
	  
	  LoadPlayerEntity loadPlayerEntity = mapLoad.get(uid_guid_bid);
	  playDataEntity.setBrowser_version(loadPlayerEntity.getBrowser_version());
	  //playDataEntity.setBusinessID(loadPlayerEntity.getBusiness_id());
	  playDataEntity.setCity(loadPlayerEntity.getCity());
	  
	  if (loadPlayerEntity.getDevice_type_name().equals("unknown") &&
	  loadPlayerEntity.getPlayer_sdk_type().toLowerCase().equals("flash")) {
	  playDataEntity.setDevice_name("PC"); } else {
	  playDataEntity.setDevice_name(loadPlayerEntity.getDevice_type_name()); }
	  
	  playDataEntity.setDevice_name(loadPlayerEntity.getDevice_type_name().equals
	  ("unknown") ? "PC" : loadPlayerEntity.getDevice_type_name());
	  playDataEntity.setOpenType(loadPlayerEntity.getOpen_type());
	  playDataEntity.setOperation_guid(loadPlayerEntity.getOperation_guid());
	  playDataEntity.setOs_type(loadPlayerEntity.getOs_type());
	  playDataEntity.setOs_version(loadPlayerEntity.getOs_version());
	  playDataEntity.setPlayer_sdk_type(loadPlayerEntity.getPlayer_sdk_type());
	  playDataEntity.setProvince(loadPlayerEntity.getProvince());
	  playDataEntity.setUid(loadPlayerEntity.getUid());
	  playDataEntity.setUserName(loadPlayerEntity.getUser_name());
	  
	  } else { playDataEntity.setBrowser_version(HAS_NO_LOADPLAYER_EVENT);
	  //playDataEntity.setBusinessID(HAS_NO_LOADPLAYER_EVENT);
	  playDataEntity.setCity(HAS_NO_LOADPLAYER_EVENT);
	  playDataEntity.setDevice_name(HAS_NO_LOADPLAYER_EVENT);
	  playDataEntity.setOpenType(HAS_NO_LOADPLAYER_EVENT);
	  playDataEntity.setOperation_guid(HAS_NO_LOADPLAYER_EVENT);
	  playDataEntity.setOs_type(HAS_NO_LOADPLAYER_EVENT);
	  playDataEntity.setOs_version(HAS_NO_LOADPLAYER_EVENT);
	  playDataEntity.setPlayer_sdk_type(HAS_NO_LOADPLAYER_EVENT);
	  playDataEntity.setProvince(HAS_NO_LOADPLAYER_EVENT);
	  playDataEntity.setUserName(HAS_NO_LOADPLAYER_EVENT); }
	  
	  if (AboutTime.toLongSSS(playDataEntity.getEnd_time()) >= startTime) {
	  playDataList.add(playDataEntity); }
	  
	  }
	  
	  long mergEnd = System.currentTimeMillis(); System.out.println("merge:" +
	  (mergEnd - mergStart));
	  
	  liveDTO.setTotalData(countLoad); liveDTO.setTotalPage(totalPageLoad);
	  liveDTO.setList(playDataList); } catch (Exception e) {
	  logger.error("统计中 查询findLive：", e); System.out.println(e); } finally {
	  conn.destory(mongo, db); } return liveDTO; }
	 */

	@Override
	public PlayListDto findLiveLog(long startTime, long endTime, String url, String cip, int pageSize, int currPage, boolean iscount) {
		PlayListDto liveDTO = new PlayListDto();
		List<PlayDataEntity> playDataList = new ArrayList<PlayDataEntity>();
		try {
			DBCollection employee = db.getCollection(LIVE_INFO_TAB);
			DBCursor curPlay = null;
			BasicDBObject dbo = new BasicDBObject("playtime", new BasicDBObject("$gte", startTime).append("$lt", endTime));

			if (url != null && !"".equals(url)) {
				dbo.append("url", url);
			}

			if (cip != null && !"".equals(cip)) {
				dbo.append("client_ip", cip);
			}

			BasicDBList eventList = new BasicDBList();
			eventList.add(new BasicDBObject("event_type", "play"));
			eventList.add(new BasicDBObject("event_type", "playlock"));
			dbo.append("$or", eventList);

			int skip = (currPage - 1) * pageSize;
			curPlay = employee.find(dbo).skip(skip).limit(pageSize);

			Map<String, PlayDataEntity> mapPlay = new ConcurrentHashMap<String, PlayDataEntity>();

			// 先进行装载
			DBCollection employee_time = db.getCollection(LOG_TIME_TAB);
			DBObject cur_time = null;
			while (curPlay.hasNext()) {
				DBObject playObject = curPlay.next();

				String uid = playObject.get("uid").toString();
				String operation_guid = playObject.get("operation_guid").toString();
				String url_Re = playObject.get("url").toString();

				BasicDBObject dbo_time = new BasicDBObject("uid", uid);
				dbo_time.append("operation_guid", operation_guid);
				dbo_time.append("url", url_Re);
				cur_time = employee_time.findOne(dbo_time);

				if (cur_time != null && System.currentTimeMillis() - Long.parseLong(cur_time.get("time").toString()) > 10 * 60 * 1000) {
					String key = uid + "~@~" + operation_guid + "~@~" + url_Re;

					if (mapPlay.get(key) == null) {
						mapPlay.put(key, new PlayDataEntity());
						mapPlay.get(key).setUid(uid);
						mapPlay.get(key).setOperation_guid(operation_guid);
						mapPlay.get(key).setUrl(url_Re);
						mapPlay.get(key).setLog(playObject.get("log").toString() + "<br/>------------------------------<br/>");
					} else {
						mapPlay.get(key).setLog(mapPlay.get(key).getLog() + playObject.get("log").toString() + "<br/>------------------------------<br/>");
					}
				}
			}

			for (String uid_guid_url : mapPlay.keySet()) {
				playDataList.add(mapPlay.get(uid_guid_url));
			}

			if (iscount) {
				int count = employee.find(dbo).count();
				int totalPage = count % pageSize == 0 ? count / pageSize : (count / pageSize) + 1;
				liveDTO.setCurrPage(currPage);
				liveDTO.setPageSize(pageSize);
				liveDTO.setTotalPage(totalPage);
				liveDTO.setTotalData(count);
			}
			liveDTO.setList(playDataList);
		} catch (Exception e) {
			logger.error("统计中 查询findLiveLog：", e);
			System.out.println(e);
		} finally {
			conn.destory(mongo, db);
		}
		return liveDTO;
	}

	@Override
	public PlayListDto findLiveIPByPicDuration(long startTime, long endTime, String url, double picDuration, String picDurationSelect, int pageSize,
			int currPage, boolean iscount) {
		PlayListDto liveDTO = new PlayListDto();
		List<PlayDataEntity> playDataList = new ArrayList<PlayDataEntity>();
		try {
			DBCollection employee = db.getCollection(LIVE_INFO_TAB);
			DBCursor curPlayType = null;
			BasicDBObject dbo = new BasicDBObject("playtime", new BasicDBObject("$gte", startTime).append("$lt", endTime));

			if (url != null && !"".equals(url)) {
				dbo.append("url", url);
			}

			dbo.append("event_type", "play");

			int skip = (currPage - 1) * pageSize;
			curPlayType = employee.find(dbo).skip(skip).limit(pageSize);
			Map<String, PlayDataEntity> mapPlay = new ConcurrentHashMap<String, PlayDataEntity>();

			DBCollection employee_time = db.getCollection(LOG_TIME_TAB);
			DBObject cur_time = null;
			while (curPlayType.hasNext()) {
				DBObject playObject = curPlayType.next();

				String uid = playObject.get("uid").toString();
				String operation_guid = playObject.get("operation_guid").toString();
				String url_Re = playObject.get("url").toString();

				BasicDBObject dbo_time = new BasicDBObject("uid", uid);
				dbo_time.append("operation_guid", operation_guid);
				dbo_time.append("url", url_Re);
				cur_time = employee_time.findOne(dbo_time);

				if (cur_time != null && System.currentTimeMillis() - Long.parseLong(cur_time.get("time").toString()) > 10 * 60 * 1000) {
					String key = uid + "~@~" + operation_guid + "~@~" + url_Re;

					int firstPicDuration = Integer.parseInt(playObject.get("first_pic_duration").toString());
					if (mapPlay.get(key) == null) {
						mapPlay.put(key, new PlayDataEntity());
						mapPlay.get(key).setUid(uid);
						mapPlay.get(key).setOperation_guid(operation_guid);
						mapPlay.get(key).setUrl(playObject.get("url").toString());
						mapPlay.get(key).setCip(playObject.get("client_ip").toString());
						mapPlay.get(key).getFirstPicDuration().add(firstPicDuration);
					} else {
						mapPlay.get(key).getFirstPicDuration().add(firstPicDuration);
					}
				}
			}

			if ("eq".equals(picDurationSelect)) {
				for (String uid_guid_url : mapPlay.keySet()) {
					List<Integer> firstPicDurationList = mapPlay.get(uid_guid_url).getFirstPicDuration();
					for (int i = 0; i < firstPicDurationList.size(); i++) {
						if (firstPicDurationList.get(i) == picDuration) {
							playDataList.add(mapPlay.get(uid_guid_url));
							break;
						}
					}
				}
			} else if ("gt".equals(picDurationSelect)) {
				for (String uid_guid_url : mapPlay.keySet()) {
					List<Integer> firstPicDurationList = mapPlay.get(uid_guid_url).getFirstPicDuration();
					for (int i = 0; i < firstPicDurationList.size(); i++) {
						if (firstPicDurationList.get(i) > picDuration) {
							playDataList.add(mapPlay.get(uid_guid_url));
							break;
						}
					}
				}
			} else if ("lt".equals(picDurationSelect)) {
				for (String uid_guid_url : mapPlay.keySet()) {
					List<Integer> firstPicDurationList = mapPlay.get(uid_guid_url).getFirstPicDuration();
					for (int i = 0; i < firstPicDurationList.size(); i++) {
						if (firstPicDurationList.get(i) < picDuration) {
							playDataList.add(mapPlay.get(uid_guid_url));
							break;
						}
					}
				}
			}

			if (iscount) {
				int count = employee.find(dbo).count();
				int totalPage = count % pageSize == 0 ? count / pageSize : (count / pageSize) + 1;
				liveDTO.setCurrPage(currPage);
				liveDTO.setPageSize(pageSize);
				liveDTO.setTotalPage(totalPage);
			}
			liveDTO.setList(playDataList);
		} catch (Exception e) {
			logger.error("统计中 查询端到端findLive：", e);
		} finally {
			conn.destory(mongo, db);
		}

		return liveDTO;
	}

	@Override
	public PlayListDto findLiveIPByResult(long startTime, long endTime, String url, String checkResult, int pageSize, int currPage, boolean iscount) {
		PlayListDto liveDTO = new PlayListDto();
		List<PlayDataEntity> playDataList = new ArrayList<PlayDataEntity>();
		try {
			DBCollection employee = db.getCollection(LIVE_INFO_TAB);
			DBCursor curPlayType = null;
			BasicDBObject dbo = new BasicDBObject("playtime", new BasicDBObject("$gte", startTime).append("$lt", endTime));

			if (url != null && !"".equals(url)) {
				dbo.append("url", url);
			}

			if (!"-10000".equals(checkResult)) {
				dbo.append("result", Integer.parseInt(checkResult));
			} else {
				BasicDBList resultList = new BasicDBList();
				resultList.add(200);
				resultList.add(0);
				BasicDBObject resultDbo = new BasicDBObject("$nin", resultList);
				dbo.put("result", resultDbo);
			}

			dbo.append("event_type", "play");

			int skip = (currPage - 1) * pageSize;
			curPlayType = employee.find(dbo).skip(skip).limit(pageSize);
			Map<String, PlayDataEntity> mapPlay = new ConcurrentHashMap<String, PlayDataEntity>();

			DBCollection employee_time = db.getCollection(LOG_TIME_TAB);
			DBObject cur_time = null;
			while (curPlayType.hasNext()) {
				DBObject playObject = curPlayType.next();

				String uid = playObject.get("uid").toString();
				String operation_guid = playObject.get("operation_guid").toString();
				String url_Re = playObject.get("url").toString();

				BasicDBObject dbo_time = new BasicDBObject("uid", uid);
				dbo_time.append("operation_guid", operation_guid);
				dbo_time.append("url", url_Re);
				cur_time = employee_time.findOne(dbo_time);

				if (cur_time != null && System.currentTimeMillis() - Long.parseLong(cur_time.get("time").toString()) > 10 * 60 * 1000) {
					String key = uid + "~@~" + operation_guid + "~@~" + url_Re;

					if (mapPlay.get(key) == null) {
						mapPlay.put(key, new PlayDataEntity());
						mapPlay.get(key).setUid(uid);
						mapPlay.get(key).setOperation_guid(operation_guid);
						mapPlay.get(key).setUrl(url_Re);
						mapPlay.get(key).setCip(playObject.get("client_ip").toString());
					}
				}
			}

			for (String uid_guid_url : mapPlay.keySet()) {
				playDataList.add(mapPlay.get(uid_guid_url));
			}

			if (iscount) {
				int count = employee.find(dbo).count();
				int totalPage = count % pageSize == 0 ? count / pageSize : (count / pageSize) + 1;
				liveDTO.setCurrPage(currPage);
				liveDTO.setPageSize(pageSize);
				liveDTO.setTotalPage(totalPage);
				liveDTO.setTotalData(count);
			}
			liveDTO.setList(playDataList);
		} catch (Exception e) {
			logger.error("统计中 查询端到端findLive：", e);
			System.out.println(e);
		} finally {
			conn.destory(mongo, db);
		}
		return liveDTO;
	}

	@Override
	public PlayListDto findLiveIPByLockCount(long startTime, long endTime, String url, int lockCount, String lockCountSelect, int pageSize, int currPage,
			boolean iscount) {
		PlayListDto liveDTO = new PlayListDto();
		List<PlayDataEntity> playDataList = new ArrayList<PlayDataEntity>();
		try {
			DBCollection employee = db.getCollection(LIVE_INFO_TAB);
			DBCursor curPlayType = null;
			BasicDBObject dbo = new BasicDBObject("playtime", new BasicDBObject("$gte", startTime).append("$lt", endTime));

			if (url != null && !"".equals(url)) {
				dbo.append("url", url);
			}

			dbo.append("event_type", "playlock");

			int skip = (currPage - 1) * pageSize;
			curPlayType = employee.find(dbo).skip(skip).limit(pageSize);
			Map<String, PlayDataEntity> mapPlay = new ConcurrentHashMap<String, PlayDataEntity>();

			DBCollection employee_time = db.getCollection(LOG_TIME_TAB);
			DBObject cur_time = null;
			while (curPlayType.hasNext()) {
				DBObject playObject = curPlayType.next();

				String uid = playObject.get("uid").toString();
				String operation_guid = playObject.get("operation_guid").toString();
				String url_Re = playObject.get("url").toString();

				BasicDBObject dbo_time = new BasicDBObject("uid", uid);
				dbo_time.append("operation_guid", operation_guid);
				dbo_time.append("url", url_Re);
				cur_time = employee_time.findOne(dbo_time);

				if (cur_time != null && System.currentTimeMillis() - Long.parseLong(cur_time.get("time").toString()) > 10 * 60 * 1000) {
					String key = uid + "~@~" + operation_guid + "~@~" + url_Re;

					int lockCountTotal = Integer.parseInt(playObject.get("lock_count").toString());
					if (mapPlay.get(key) == null) {
						mapPlay.put(key, new PlayDataEntity());
						mapPlay.get(key).setUid(uid);
						mapPlay.get(key).setOperation_guid(operation_guid);
						mapPlay.get(key).setUrl(url_Re);
						mapPlay.get(key).setCip(playObject.get("client_ip").toString());
						mapPlay.get(key).setLockCount(lockCountTotal);
					} else {
						if (mapPlay.get(key).getLockCount() < lockCountTotal) {
							mapPlay.get(key).setLockCount(lockCountTotal);
						}
					}
				}
			}

			if ("eq".equals(lockCountSelect)) {
				for (String uid_guid_url : mapPlay.keySet()) {
					if (mapPlay.get(uid_guid_url).getLockCount() == lockCount) {
						playDataList.add(mapPlay.get(uid_guid_url));
					}
				}
			} else if ("gt".equals(lockCountSelect)) {
				for (String uid_guid_url : mapPlay.keySet()) {
					if (mapPlay.get(uid_guid_url).getLockCount() > lockCount) {
						playDataList.add(mapPlay.get(uid_guid_url));
					}
				}
			} else if ("lt".equals(lockCountSelect)) {
				for (String uid_guid_url : mapPlay.keySet()) {
					if (mapPlay.get(uid_guid_url).getLockCount() < lockCount) {
						playDataList.add(mapPlay.get(uid_guid_url));
					}
				}
			}

			if (iscount) {
				int count = employee.find(dbo).count();
				int totalPage = count % pageSize == 0 ? count / pageSize : (count / pageSize) + 1;
				liveDTO.setCurrPage(currPage);
				liveDTO.setPageSize(pageSize);
				liveDTO.setTotalPage(totalPage);
			}
			liveDTO.setList(playDataList);
		} catch (Exception e) {
			logger.error("统计中 查询端到端findLive：", e);
		} finally {
			conn.destory(mongo, db);
		}

		return liveDTO;
	}

	@Override
	public LiveListDto findLiveAllFailedIPByResult(long startTime, long endTime, int pageSize, int currPage, boolean iscount) {
		LiveListDto liveDTO = new LiveListDto();
		return liveDTO;
	}

	@Override
	public PlayListDto findLiveIPByAll(long startTime, long endTime, String url, int lockCount, String lockCountSelect, double picDuration,
			String picDurationSelect, String result, int pageSize, int currPage, boolean iscount) {
		PlayListDto liveDTO = new PlayListDto();
		List<PlayDataEntity> playDataList = new ArrayList<PlayDataEntity>();
		try {
			DBCollection employee = db.getCollection(LIVE_INFO_TAB);
			DBCursor curPlayType = null;
			BasicDBObject dbo = new BasicDBObject("playtime", new BasicDBObject("$gte", startTime).append("$lt", endTime));

			if (url != null && !"".equals(url)) {
				dbo.append("url", url);
			}

			BasicDBList eventList = new BasicDBList();
			eventList.add(new BasicDBObject("event_type", "play"));
			eventList.add(new BasicDBObject("event_type", "playlock"));
			dbo.append("$or", eventList);

			int skip = (currPage - 1) * pageSize;
			curPlayType = employee.find(dbo).skip(skip).limit(pageSize);
			Map<String, PlayDataEntity> mapPlay = new ConcurrentHashMap<String, PlayDataEntity>();

			DBCollection employee_time = db.getCollection(LOG_TIME_TAB);
			DBObject cur_time = null;
			while (curPlayType.hasNext()) {
				DBObject playObject = curPlayType.next();

				String uid = playObject.get("uid").toString();
				String operation_guid = playObject.get("operation_guid").toString();
				String url_Re = playObject.get("url").toString();

				BasicDBObject dbo_time = new BasicDBObject("uid", uid);
				dbo_time.append("operation_guid", operation_guid);
				dbo_time.append("url", url_Re);
				cur_time = employee_time.findOne(dbo_time);

				if (cur_time != null && System.currentTimeMillis() - Long.parseLong(cur_time.get("time").toString()) > 10 * 60 * 1000) {
					String key = uid + "~@~" + operation_guid + "~@~" + url_Re;

					int lockCountTotal = Integer.parseInt(playObject.get("lock_count").toString());
					int firstPicDuration = Integer.parseInt(playObject.get("first_pic_duration").toString());
					int resultT = Integer.parseInt(playObject.get("result").toString());

					if (mapPlay.get(key) == null) {
						mapPlay.put(key, new PlayDataEntity());
						mapPlay.get(key).setUid(uid);
						mapPlay.get(key).setOperation_guid(operation_guid);
						mapPlay.get(key).setUrl(url_Re);
						mapPlay.get(key).setCip(playObject.get("client_ip").toString());
						mapPlay.get(key).setLockCount(lockCountTotal);
						mapPlay.get(key).getFirstPicDuration().add(firstPicDuration);
						mapPlay.get(key).getResult().add(resultT);
					} else {
						if (mapPlay.get(key).getLockCount() < lockCountTotal) {
							mapPlay.get(key).setLockCount(lockCountTotal);
						}
						mapPlay.get(key).getFirstPicDuration().add(firstPicDuration);
						mapPlay.get(key).getResult().add(resultT);
					}
				}
			}

			for (String uid_guid_url : mapPlay.keySet()) {

				boolean lockCountSit = false;
				boolean firstPicSit = false;
				boolean resultSit = false;

				if (lockCount >= 0) {
					if ("eq".equals(lockCountSelect)) {
						if (mapPlay.get(uid_guid_url).getLockCount() == lockCount) {
							lockCountSit = true;
						}
					} else if ("gt".equals(lockCountSelect)) {
						if (mapPlay.get(uid_guid_url).getLockCount() > lockCount) {
							lockCountSit = true;
						}
					} else if ("lt".equals(lockCountSelect)) {
						if (mapPlay.get(uid_guid_url).getLockCount() < lockCount) {
							lockCountSit = true;
						}
					}
				} else {
					lockCountSit = true;
				}

				if (picDuration >= 0) {
					if ("eq".equals(picDurationSelect)) {
						List<Integer> firstPicDurationList = mapPlay.get(uid_guid_url).getFirstPicDuration();
						for (int i = 0; i < firstPicDurationList.size(); i++) {
							if (firstPicDurationList.get(i) == picDuration) {
								firstPicSit = true;
								break;
							}
						}
					} else if ("gt".equals(picDurationSelect)) {
						List<Integer> firstPicDurationList = mapPlay.get(uid_guid_url).getFirstPicDuration();
						for (int i = 0; i < firstPicDurationList.size(); i++) {
							if (firstPicDurationList.get(i) > picDuration) {
								firstPicSit = true;
								break;
							}
						}
					} else if ("lt".equals(picDurationSelect)) {
						List<Integer> firstPicDurationList = mapPlay.get(uid_guid_url).getFirstPicDuration();
						for (int i = 0; i < firstPicDurationList.size(); i++) {
							if (firstPicDurationList.get(i) < picDuration) {
								firstPicSit = true;
								break;
							}
						}
					}
				} else {
					firstPicSit = true;
				}

				if (!"-10000".equals(result)) {
					List<Integer> resultList = mapPlay.get(uid_guid_url).getResult();
					for (int i = 0; i < resultList.size(); i++) {
						if (resultList.get(i) == Integer.parseInt(result)) {
							resultSit = true;
							break;
						}
					}
				} else {
					resultSit = true;
				}

				if (lockCountSit == true && firstPicSit == true && resultSit == true) {
					playDataList.add(mapPlay.get(uid_guid_url));
				}

			}

			if (iscount) {
				int count = employee.find(dbo).count();
				int totalPage = count % pageSize == 0 ? count / pageSize : (count / pageSize) + 1;
				liveDTO.setCurrPage(currPage);
				liveDTO.setPageSize(pageSize);
				liveDTO.setTotalPage(totalPage);
			}
			liveDTO.setList(playDataList);
		} catch (Exception e) {
			logger.error("统计中 查询端到端findLive：", e);
		} finally {
			conn.destory(mongo, db);
		}
		return liveDTO;
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

	@Override
	public List<JSONObject> findLivePopular(long startTime, long endTime, String domainName, String topN,int pageSize, int currPage, boolean iscount) {
		
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
