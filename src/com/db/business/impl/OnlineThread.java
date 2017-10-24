package com.db.business.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONObject;

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

public class OnlineThread extends BaseDao implements Runnable {

	private Logger logger = Logger.getLogger(this.getClass());
	final String LIVE_INFO_TAB = "live_online_data";

	long startTime_static;
	long endTime_static;
	String url_static;
	int pageSize_static;
	int currPage_static;
	boolean iscount_static;
	boolean complete = false;

	List<JSONObject> onlineObjList = new ArrayList<JSONObject>();

	int countPre;
	int totalPagePre;

	// 参数为搜索条件
	public OnlineThread(long start, long end, String url, int pageSize, int currPage, boolean iscount) {
		this.startTime_static = start;
		this.endTime_static = end;
		this.url_static = url;
		this.pageSize_static = pageSize;
		this.currPage_static = currPage;
		this.iscount_static = iscount;

	}

	@Override
	public void run() {

		// 分线程查找数据库
		Thread th = new Thread(new Thread() {
			public void run() {
				try {
					DBCollection employee = db.getCollection(LIVE_INFO_TAB);
					DBCursor curPre = null;
					BasicDBObject dboPre = new BasicDBObject("startTime", new BasicDBObject("$lt", endTime_static));
					dboPre.append("endTime", new BasicDBObject("$gt", startTime_static));
					// 如果设置了url过滤，优先按照url过滤，否则按照businessID过滤
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
						//dboPre.append("content", url_static);
					}
					dboPre.append("isTen", 1);

					countPre = employee.find(dboPre).count();
					totalPagePre = countPre % pageSize_static == 0 ? countPre / pageSize_static : (countPre / pageSize_static) + 1;

					for (int currPg = 1; currPg <= totalPagePre; currPg++) {
						int skip = (currPg - 1) * pageSize_static;
						curPre = employee.find(dboPre).skip(skip).limit(pageSize_static);
						while (curPre.hasNext()) {
							DBObject onlineObject = curPre.next();
							onlineObject.put("raw", 0);//非原始数据，而是10分钟的计算值
							onlineObjList.add(JSONObject.fromObject(onlineObject.toString()));
						}
					}
					complete = true;

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
