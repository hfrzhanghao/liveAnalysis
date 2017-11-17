package com.db.dao.impl;

import java.util.ArrayList;
import java.util.List;
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
import com.util.DateUtil;

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

		// 如果总的查询时长超过1天，则将每天夜里零点作为线程划分时间点。每到零点，开出一个线程
		List<Long> timeList = new ArrayList<Long>();

		// 每个零点的时间戳list
		timeList.add(startTime_static);

		// 每个线程运行结束标识，初始都为false，线程结束后将对应的标识置为true
		final List<Boolean> completeList = new ArrayList<Boolean>();

		long timeInterval = 24 * 60 * 60000;

		// 计算各个零点时间戳
		if (endTime_static - startTime_static > timeInterval) {
			int days = (int) ((endTime_static - startTime_static) / (timeInterval));
			long firstNight = DateUtil.toLongSSSD(DateUtil.stampToDateD(startTime_static)) + timeInterval;
			for (int i = 0; i < days; i++) {
				timeList.add(firstNight + timeInterval * i);
			}
			if (timeList.get(timeList.size() - 1) >= endTime_static) {
				timeList.remove(timeList.size() - 1);
			}
		}
		timeList.add(endTime_static);
		for (int i = 0; i < timeList.size() - 1; i++) {

			final List<Long> timeF = timeList;
			final int f = i;
			completeList.add(false);

			// 分线程查找数据库
			Thread th = new Thread(new Thread() {
				public void run() {
					try {

						long playStart = System.currentTimeMillis();

						DBCollection employee = db.getCollection(LIVE_INFO_TAB);
						DBCursor curPlay = null;

						BasicDBObject dboPlay = new BasicDBObject("playtime", new BasicDBObject("$gte", timeF.get(f)).append("$lt", timeF.get(f + 1)));


						BasicDBList urlList = new BasicDBList();
						if (url_static != null && !url_static.equals("多条以英文逗号隔开") && !url_static.equals("")) {

							// 如果设置了url过滤，优先按照url过滤，否则按照businessID过滤
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
								
								int hour = Integer.parseInt(playObject.get("time").toString().split("-")[3].split(":")[0]);
								if (playObject.get("event_type").toString().equals("playlock")
										&& hour > 1 && hour < 5 && Long.parseLong(playObject.get("duration").toString()) > 1 * 60 * 60) {
									continue;
								}

								if (playObject.get("uid") != null && !playObject.get("uid").equals("null") && playObject.get("operation_guid") != null) {

									String playBusinessID = playObject.get("business_id").toString();
									if (playBusinessID.equals("") || playBusinessID.equals("null")) {
										playBusinessID = "未知";
									}

									String uid_guid_bid_url = playObject.get("uid").toString() + "_" + playObject.get("operation_guid").toString() + "_"
											+ playBusinessID + "_" + playObject.get("url").toString();

									// 如果搜索到的数据的uid_guid_bid_url已经存在与mapPlay，则结合之前的记录形成新的记录
									if (mapPlay.get(uid_guid_bid_url) != null) {

										PlayDataEntity playDataEntity = mapPlay.get(uid_guid_bid_url);
										
										if (Math.abs(Long.parseLong(playObject.get("playtime").toString())
												- DateUtil.toLongSSS(mapPlay.get(uid_guid_bid_url).getEnd_time())) > 3600000) {
											playDataEntity.setPauseLong(true);
											continue;
										}

										if (!playDataEntity.isPauseLong()) {

											// 原有数据的播放时长
											int oldDuration = playDataEntity.getDuration();
											// 新来数据的播放时长
											int newDuration = Integer.parseInt(playObject.get("duration").toString());

											// 原有数据的开始时间
											long oldStartTime = AboutTime.toLongSSS(playDataEntity.getStart_time());
											// 原有数据的结束时间
											long oldEndTime = AboutTime.toLongSSS(playDataEntity.getEnd_time());
											// 新来数据的上报时间
											long newTime = AboutTime.toLongSSS(playObject.get("time").toString());

											if (playObject.get("event_type").toString().equals("playlock") && playDataEntity.isPause()) {
												long pauseTime = newTime - playDataEntity.getPauseTime();

												if (pauseTime > 600000) {
													// 暂停时间过长
													playDataEntity.setPauseLong(true);
													continue;
												}
											}

											// 如果新来的数据有着更早的time，那么这次播放的起始时间更新
											if (newTime < oldStartTime) {
												playDataEntity.setStart_time(playObject.get("time").toString());
											}

											// 如果新来的数据有着更晚的time，那么这次播放的结束时间更新
											if (newTime > oldEndTime) {
												playDataEntity.setEnd_time(playObject.get("time").toString());
											}

											// type为play的日志中有pause信息和first_pic_duration信息
											if (playObject.get("event_type").toString().equals("play")) {
												if (!playObject.get("sub_type").equals("pause")) {
													playDataEntity.setPauseTime(-1L);
													playDataEntity.setPause(false);
													playDataEntity.getFirstPicDuration().add(Integer.parseInt(playObject.get("first_pic_duration").toString()));
												} else {
													playDataEntity.setPause(true);
													if(Long.parseLong(playObject.get("playtime").toString()) > playDataEntity.getPauseTime()){
														playDataEntity.setPauseTime(Long.parseLong(playObject.get("playtime").toString()));
													}
												}
												playDataEntity.getResult().add(Integer.parseInt(playObject.get("result").toString()));

											} else if (playObject.get("event_type").toString().equals("playlock")) {

												// type为playlock的日志中有lock_count信息和duration信息
												// 如果新数据有着更多的卡顿记录，则更新卡顿次数
												if (Integer.parseInt(playObject.get("lock_count").toString()) > playDataEntity.getLockCount()) {
													playDataEntity.setLockCount(Integer.parseInt(playObject.get("lock_count").toString()));
												}

												// 如果新数据有着更长的播放时长，则更新播放时长
												if (newDuration > oldDuration) {
													playDataEntity.setDuration(newDuration);
												}
											}
										}
									} else {
										// 否则直接添加
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
						completeList.set(f, true);

					} catch (Exception e) {
						logger.error("统计中 PlayTread：", e);
						System.out.println(e);
					} finally {
						conn.destory(mongo, db);
					}

				}
			});
			th.start();
		}

		// 如果completeList还有false，表示线程还没有执行完，一直等待
		while (completeList.contains(false)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("统计中PlayThread：", e);
				e.printStackTrace();
			}
		}
		for (String key : mapPlay.keySet()) {
			PlayDataEntity playDataEntity = mapPlay.get(key);

			if (playDataEntity.isPauseLong()) {
				mapPlay.remove(playDataEntity);
			}
		}
		// 否则playComplete置为true，表示全部执行完
		playComplete = true;
	}
}
