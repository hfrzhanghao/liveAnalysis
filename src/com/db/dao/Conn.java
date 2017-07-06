package com.db.dao;

import java.util.List;

import com.CommStats;
import com.external.common.CommonConstants;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

public class Conn {
	static List<ServerAddress> callDBUrlList = CommStats.serverListCall(CommStats.callmongohost);
	private static Conn conn = null;

	private Conn() {
	}

	public static Conn getInstace() {
		if (conn == null) {
			conn = new Conn();
		}
		return conn;
	}

	private static Mongo mongo = null;

	public Mongo getMongo() {
		try {
			MongoOptions options=new MongoOptions();
			options.socketTimeout=CommonConstants.SOCKED_TIMEOUT;
			
			if (mongo == null) {
				mongo = new Mongo(callDBUrlList,options);
			}
			return mongo;
		} catch (Exception e) {
			if (mongo != null) {
				mongo.close();
			}
			e.printStackTrace();
		}
		return null;
	}

	public DB getDB(Mongo mongo) {
		try {
			DB db = mongo.getDB(CommStats.callmongodatabase);
			/* 数据库用户名密码，因为mongo集群，用密码影响性能，所以暂时去掉 */
			// db.authenticate(CommStats.username,
			// CommStats.password.toCharArray());
			return db;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void destory(Mongo mongo, DB db) {
		/*
		 * if(mongo != null){ mongo.close(); }
		 */
		db = null;
	}
}
