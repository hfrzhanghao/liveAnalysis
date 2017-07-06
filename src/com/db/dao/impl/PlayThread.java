package com.db.dao.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.AboutTime;
import com.db.dao.BaseDao;
import com.db.entity.PlayDataEntity;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class PlayThread extends BaseDao implements Runnable {

	private Logger logger = Logger.getLogger(this.getClass());
	final String LIVE_INFO_TAB = "live_data";

	long startTime_static;
	long endTime_static;
	String url_static;
	String businessID_static;
	String durationSelect_static;
	String duration_static;
	String firstPicDurationSelect_static;
	String firstPicDuration_static;
	int pageSize_static;
	int currPage_static;
	boolean iscount_static;

	Map<String, PlayDataEntity> mapPlay = new ConcurrentHashMap<String, PlayDataEntity>();

	boolean playComplete = false;

	public PlayThread(long start, long end, String url, String buss, String durationSelect, String duration, String firstPicDurationSelect,
			String firstPicDuration, int pageSize, int currPage, boolean iscount) {
		this.startTime_static = start;
		this.endTime_static = end;
		this.url_static = url;
		this.businessID_static = buss;
		this.durationSelect_static = durationSelect;
		this.duration_static = duration;
		this.firstPicDurationSelect_static = firstPicDurationSelect;
		this.firstPicDuration_static = firstPicDuration;
		this.pageSize_static = pageSize;
		this.currPage_static = currPage;
		this.iscount_static = iscount;

	}

	@Override
	public void run() {
		try {

			long playStart = System.currentTimeMillis();

			DBCollection employee = db.getCollection(LIVE_INFO_TAB);
			DBCursor curPlay = null;

			BasicDBObject dboPlay = new BasicDBObject("playtime", new BasicDBObject("$gte", startTime_static - 60 * 60 * 1000).append("$lt", endTime_static));

			BasicDBList urlList = new BasicDBList();
			if (url_static != null && !url_static.equals("多条以英文逗号隔开") && !url_static.equals("")) {
				if (url_static.split(",").length > 0) {
					String[] urlArray = url_static.split(",");
					for (int i = 0; i < urlArray.length; i++) {
						urlList.add(new BasicDBObject("content", urlArray[i]));
					}
					dboPlay.put("$or", urlList);
				} else {
					dboPlay.append("content", url_static);
				}
			} else {
				if (businessID_static != null) {
					dboPlay.append("business_id", businessID_static);
				}
			}
			dboPlay.append("event_type", new BasicDBObject("$ne", "LoadPlayer"));

			int countPlay = employee.find(dboPlay).count();
			int totalPagePlay = countPlay % pageSize_static == 0 ? countPlay / pageSize_static : (countPlay / pageSize_static) + 1;

			int countPlay1 = 0;

			for (int currPg = 1; currPg <= totalPagePlay; currPg++) {
				int skip = (currPg - 1) * pageSize_static;
				curPlay = employee.find(dboPlay).skip(skip).limit(pageSize_static);

				while (curPlay.hasNext()) {
					countPlay1++;
					DBObject playObject = curPlay.next();

					if (playObject.get("uid") != null && !playObject.get("uid").equals("null") && playObject.get("operation_guid") != null) {

						String playBusinessID = playObject.get("business_id").toString();
						if (playBusinessID.equals("") || playBusinessID.equals("null")) {
							playBusinessID = "未知";
						}

						String uid_guid_bid_url = playObject.get("uid").toString() + "_" + playObject.get("operation_guid").toString() + "_" + playBusinessID
								+ "_" + playObject.get("url").toString();

						if (mapPlay.get(uid_guid_bid_url) != null) {

							PlayDataEntity playDataEntity = mapPlay.get(uid_guid_bid_url);

							if (!playDataEntity.isPauseLong()) {

								int oldDuration = playDataEntity.getDuration();
								int newDuration = Integer.parseInt(playObject.get("duration").toString());

								long oldTime = AboutTime.toLongSSS(playDataEntity.getEnd_time());
								long newTime = AboutTime.toLongSSS(playObject.get("time").toString());

								if (playObject.get("event_type").toString().equals("playlock") && oldDuration == newDuration) {
									if (!playDataEntity.isPause()) {
										playDataEntity.setPause(true);
										playDataEntity.setPauseTime(oldTime);
									} else {
										if ((newTime - playDataEntity.getPauseTime()) > 600000) {
											playDataEntity.setPauseLong(true);
											continue;
										}
									}
								}

								if (newTime < AboutTime.toLongSSS(playDataEntity.getStart_time())) {
									playDataEntity.setStart_time(playObject.get("time").toString());
								}
								if (newTime > AboutTime.toLongSSS(playDataEntity.getEnd_time())) {
									playDataEntity.setEnd_time(playObject.get("time").toString());
								}
								if (playObject.get("event_type").toString().equals("play")) {
									if (!playObject.get("sub_type").equals("pause")) {
										playDataEntity.setPauseTime(-1L);
										playDataEntity.setPause(false);
										playDataEntity.getFirstPicDuration().add(Integer.parseInt(playObject.get("first_pic_duration").toString()));
									}
									playDataEntity.getResult().add(Integer.parseInt(playObject.get("result").toString()));
								} else if (playObject.get("event_type").toString().equals("playlock")) {
									if (Integer.parseInt(playObject.get("lock_count").toString()) > playDataEntity.getLockCount()) {
										playDataEntity.setLockCount(Integer.parseInt(playObject.get("lock_count").toString()));
									}
									if (newDuration > oldDuration) {
										playDataEntity.setDuration(newDuration);
									}
								}
							}
						} else {
							PlayDataEntity playDataEntity = new PlayDataEntity();
							playDataEntity.setUid(playObject.get("uid").toString());
							playDataEntity.setOperation_guid(playObject.get("operation_guid").toString());
							playDataEntity.setBusinessID(playObject.get("business_id").toString());
							playDataEntity.setUrl(playObject.get("url").toString());
							playDataEntity.setStart_time(playObject.get("time").toString());
							playDataEntity.setEnd_time(playObject.get("time").toString());

							if (playObject.get("event_type").toString().equals("play")) {
								if (!playObject.get("sub_type").equals("pause")) {
									playDataEntity.getFirstPicDuration().add(Integer.parseInt(playObject.get("first_pic_duration").toString()));
								}
								playDataEntity.getResult().add(Integer.parseInt(playObject.get("result").toString()));
							} else if (playObject.get("event_type").toString().equals("playlock")) {
								playDataEntity.setLockCount(Integer.parseInt(playObject.get("lock_count").toString()));
								playDataEntity.setDuration(Integer.parseInt(playObject.get("duration").toString()));
							}
							mapPlay.put(uid_guid_bid_url, playDataEntity);
						}
					}
				}
			}

			// 不符合条件的数据，删除
			if (duration_static != null && !duration_static.equals("")) {
				for (String urlkey : mapPlay.keySet()) {
					int duratinInt = Integer.parseInt(duration_static);
					if (durationSelect_static == null) {
						if (mapPlay.get(urlkey).getDuration() < duratinInt) {
							mapPlay.remove(urlkey);
						}
					} else if (durationSelect_static.equals("lt")) {
						if (mapPlay.get(urlkey).getDuration() > duratinInt) {
							mapPlay.remove(urlkey);
						}
					} else if (durationSelect_static.equals("gt")) {
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

			if (firstPicDuration_static != null && !firstPicDuration_static.equals("")) {
				// 如果过滤条件中有出画面时长，而此条数据中没有出画面时长数据，删除
				for (String urlkey : mapPlay.keySet()) {
					if (mapPlay.get(urlkey).getFirstPicDuration().size() == 0) {
						mapPlay.remove(urlkey);
					}
				}
				// 不符合条件的数据，删除
				for (String urlkey : mapPlay.keySet()) {
					int firstPicDurationFromPage = Integer.parseInt(firstPicDuration_static);
					boolean isDisplay = false;
					if (firstPicDurationSelect_static == null) {
						for (int fir : mapPlay.get(urlkey).getFirstPicDuration()) {
							if (fir > firstPicDurationFromPage) {
								isDisplay = true;
								break;
							}
						}
					} else if (firstPicDurationSelect_static.equals("lt")) {
						for (int fir : mapPlay.get(urlkey).getFirstPicDuration()) {
							if (fir < firstPicDurationFromPage) {
								isDisplay = true;
								break;
							}
						}
					} else if (firstPicDurationSelect_static.equals("gt")) {
						for (int fir : mapPlay.get(urlkey).getFirstPicDuration()) {
							if (fir > firstPicDurationFromPage) {
								isDisplay = true;
								break;
							}
						}
					} else {
						for (int fir : mapPlay.get(urlkey).getFirstPicDuration()) {
							if (fir == firstPicDurationFromPage) {
								isDisplay = true;
								break;
							}
						}
					}
					if (!isDisplay) {
						mapPlay.remove(urlkey);
					}
				}
			}

			long playEnd = System.currentTimeMillis();
			System.out.println("play:" + (playEnd - playStart) + "执行了hasNext" + countPlay1 + "次" + "mapPlay的size:" + mapPlay.keySet().size());
			playComplete = true;

		} catch (Exception e) {
			logger.error("统计中 PlayTread：", e);
			System.out.println(e);
		} finally {
			conn.destory(mongo, db);
		}
	}

}
