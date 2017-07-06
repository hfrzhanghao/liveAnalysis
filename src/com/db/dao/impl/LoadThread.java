package com.db.dao.impl;

import java.lang.reflect.InvocationTargetException;
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
		try {

			long loadStart = System.currentTimeMillis();
			DBCollection employee = db.getCollection(LIVE_INFO_TAB);
			DBCursor curLoad = null;
			BasicDBObject dboLoad = new BasicDBObject("playtime", new BasicDBObject("$gte", startTime_static).append("$lt", endTime_static));
			if (url_static == null) {
				if (businessID_static != null) {
					dboLoad.append("business_id", businessID_static);
				}
			}

			if (domain_static != null) {
				dboLoad.append("province", domain_static);
			}
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
			if (openType_static != null) {
				dboLoad.append("open_type", openType_static);
			}
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
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			long loadEnd = System.currentTimeMillis();
			System.out.println("load:" + (loadEnd - loadStart) + "执行了hasNext" + countLoad1 + "次" + "mapload的size:" + mapLoad.keySet().size());
			loadComplete = true;

		} catch (Exception e) {
			logger.error("统计中LoadThread：", e);
			System.out.println(e);
		} finally {
			conn.destory(mongo, db);
		}
	}

}
