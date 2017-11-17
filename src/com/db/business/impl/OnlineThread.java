package com.db.business.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.db.dao.BaseDao;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.util.DateUtil;

public class OnlineThread extends BaseDao implements Runnable {

	private Logger logger = Logger.getLogger(this.getClass());
	final String LIVE_INFO_TAB = "live_online_data";

	long startTime_static;
	long endTime_static;
	String url_static;
	String domainName_static;
	int pageSize_static;
	int currPage_static;
	boolean iscount_static;
	boolean complete = false;

	List<JSONObject> onlineObjList = new ArrayList<JSONObject>();

	int countPre;
	int totalPagePre;

	// 参数为搜索条件
	public OnlineThread(long start, long end, String url, String domainNameFilter, int pageSize, int currPage, boolean iscount) {
		this.startTime_static = start;
		this.endTime_static = end;
		this.url_static = url;
		this.domainName_static = domainNameFilter;
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

			completeList.add(false);
			final List<Long> timeF = timeList;
			final int f = i;

			// 分线程查找数据库
			Thread th = new Thread(new Thread() {
				public void run() {
					try {
						DBCollection employee = db.getCollection(LIVE_INFO_TAB);
						DBCursor curPre = null;
						BasicDBObject dboPre = new BasicDBObject("startTime", new BasicDBObject("$gte", timeF.get(f)).append("$lt", timeF.get(f + 1)));

						// 如果设置了url过滤，优先按照url过滤，否则按照domainName过滤
						if (url_static != null && !url_static.equals("多条以英文逗号隔开") && !url_static.equals("")) {
							if (url_static.split(",").length > 0) {
								BasicDBList urlList = new BasicDBList();
								String[] urlArray = url_static.split(",");
								for (int i = 0; i < urlArray.length; i++) {
									urlList.add(new BasicDBObject("content", urlArray[i]));
								}
								dboPre.put("$or", urlList);
							} else {
								dboPre.append("content", url_static);
							}
						}else{
							if (domainName_static != null) {
								dboPre.append("domainName", domainName_static);
							}
							
						}
						//统计10分钟颗粒度的预统计结果
						dboPre.append("isTen", 1);

						countPre = employee.find(dboPre).count();
						totalPagePre = countPre % pageSize_static == 0 ? countPre / pageSize_static : (countPre / pageSize_static) + 1;

						for (int currPg = 1; currPg <= totalPagePre; currPg++) {
							int skip = (currPg - 1) * pageSize_static;
							curPre = employee.find(dboPre).skip(skip).limit(pageSize_static);
							while (curPre.hasNext()) {
								DBObject onlineObject = curPre.next();
								onlineObject.put("raw", 0);// 非原始数据，而是10分钟的计算值
								onlineObjList.add(JSONObject.fromObject(onlineObject.toString()));
							}
						}
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
				logger.error("统计中PlayThread：", e);
				e.printStackTrace();
			}
		}
		// 否则playComplete置为true，表示全部执行完
		complete = true;
	}
}