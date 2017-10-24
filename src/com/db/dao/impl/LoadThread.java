package com.db.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.db.dao.BaseDao;
import com.db.entity.LoadPlayerEntity;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.util.BeanUtil;
import com.util.DateUtil;

public class LoadThread extends BaseDao implements Runnable {

	private Logger logger = Logger.getLogger(this.getClass());
	final String LIVE_INFO_TAB = "live_data";

	long startTime_static;
	long endTime_static;
	String url_static;
	String domain_static;
	String isp_static;
	String openType_static;
	String businessID_static;
	String userName_static;
	int pageSize_static;
	int currPage_static;
	boolean iscount_static;

	Map<String, LoadPlayerEntity> mapLoad = new ConcurrentHashMap<String, LoadPlayerEntity>();

	int countLoad;
	int totalPageLoad;

	boolean loadComplete = false;

	// 参数为搜索条件
	public LoadThread(long start, long end, String url, String domain, String isp, String opentype, String buss, String username, int pageSize, int currPage,
			boolean iscount) {
		this.startTime_static = start;
		this.endTime_static = end;
		this.url_static = url;
		this.domain_static = domain;
		this.isp_static = isp;
		this.openType_static = opentype;
		this.businessID_static = buss;
		this.userName_static = username;
		this.pageSize_static = pageSize;
		this.currPage_static = currPage;
		this.iscount_static = iscount;

	}

	@Override
	public void run() {

		// 每个线程运行结束标识，初始都为false，线程结束后将对应的标识置为true
		final List<Boolean> completeList = new ArrayList<Boolean>();

		// 如果总的查询时长超过1天，则将每天夜里零点作为线程划分时间点。每到零点，开出一个线程
		List<Long> timeList = new ArrayList<Long>();
		
		// 每个零点的时间戳list
		timeList.add(startTime_static);

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

			completeList.add(false);

			final List<Long> timeF = timeList;
			final int f = i;

			// 分线程查找数据库
			Thread th = new Thread(new Thread() {
				public void run() {
					try {
						long loadStart = System.currentTimeMillis();
						DBCollection employee = db.getCollection(LIVE_INFO_TAB);
						DBCursor curLoad = null;
						BasicDBObject dboLoad = new BasicDBObject("playtime", new BasicDBObject("$gte", timeF.get(f)).append("$lt", timeF.get(f + 1)));
						// 如果设置了url过滤，优先按照url过滤，否则按照businessID过滤
						if (url_static == null) {
							if (businessID_static != null) {
								dboLoad.append("business_id", businessID_static);
							}
						}
						
						// 按照地区过滤
						if (domain_static != null) {
							dboLoad.append("province", domain_static);
						}
						
						// 按照运营商过滤
						if (isp_static != null) {
							if (!isp_static.equals("else")) {
								dboLoad.append("isp", isp_static);
							} else {
								BasicDBList values = new BasicDBList();
								values.add("电信");
								values.add("移动");
								values.add("联通");
								dboLoad.append("isp", new BasicDBObject("$nin", values));
							}
						}
						
						// 按照打开方式过滤
						if (openType_static != null) {
							dboLoad.append("open_type", openType_static);
						}
						
						// 企业号过滤
						if (userName_static != null) {
							dboLoad.append("user_name", userName_static);
						}
						dboLoad.append("event_type", "LoadPlayer");

						countLoad = employee.find(dboLoad).count();
						totalPageLoad = countLoad % pageSize_static == 0 ? countLoad / pageSize_static : (countLoad / pageSize_static) + 1;

						int countLoad1 = 0;

						for (int currPg = 1; currPg <= totalPageLoad; currPg++) {
							int skip = (currPg - 1) * pageSize_static;
							curLoad = employee.find(dboLoad).skip(skip).limit(pageSize_static);
							while (curLoad.hasNext()) {
								countLoad1++;
								DBObject loadObject = curLoad.next();
								if (loadObject.get("uid") != null && !loadObject.get("uid").equals("null") && loadObject.get("operation_guid") != null
										&& !loadObject.get("operation_guid").equals("null")) {

									String loadBusinessID = loadObject.get("business_id").toString();
									if (loadBusinessID.equals("") || loadBusinessID.equals("null")) {
										loadBusinessID = "未知";
									}

									String uid_guid_bid = loadObject.get("uid").toString() + "_" + loadObject.get("operation_guid").toString() + "_"
											+ loadBusinessID;
									try {
										mapLoad.put(uid_guid_bid, BeanUtil.dbObject2Bean(loadObject, new LoadPlayerEntity()));
									} catch (IllegalAccessException e) {
										logger.error("统计中LoadThread：", e);
										e.printStackTrace();
									} catch (InvocationTargetException e) {
										logger.error("统计中LoadThread：", e);
										e.printStackTrace();
									} catch (NoSuchMethodException e) {
										logger.error("统计中LoadThread：", e);
										e.printStackTrace();
									}
								}
							}
						}
						long loadEnd = System.currentTimeMillis();
						System.out.println("load:" + (loadEnd - loadStart) + "执行了hasNext" + countLoad1 + "次" + "mapload的size:" + mapLoad.keySet().size());
						completeList.set(f, true);

					} catch (Exception e) {
						logger.error("统计中LoadThread：", e);
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
				logger.error("统计中LoadThread：", e);
				e.printStackTrace();
			}
		}

		// 否则loadComplete置为true，表示全部执行完
		loadComplete = true;
	}

	public static void main(String[] args) {

		List<Long> timeList = new ArrayList<Long>();
		long st = 1500062400000L;
		long en = 1500656400000L;
		timeList.add(st);

		if (en - st > 24 * 60 * 60000) {
			int days = (int) ((en - st) / (24 * 60 * 60000));
			long firstNight = DateUtil.toLongSSSD(DateUtil.stampToDateD(st)) + 24 * 60 * 60000;
			for (int i = 0; i < days; i++) {
				timeList.add(firstNight + 24 * 60 * 60000 * i);
			}
			if (timeList.get(timeList.size() - 1) >= en) {
				timeList.remove(timeList.size() - 1);
			}
		}
		timeList.add(en);

		for (int i = 0; i < timeList.size() - 1; i++) {
			final List<Long> timeF = timeList;
			final int f = i;
			Thread th = new Thread(new Thread() {
				public void run() {
					System.out.println(timeF.get(f) + "---" + timeF.get(f + 1));// 这个值要求要打印出0、1、2、3、4、5、6、7、8、9

				}
			});
			th.start();

		}
	}

}
