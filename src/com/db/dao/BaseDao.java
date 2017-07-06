package com.db.dao;

import com.db.dao.Conn;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;

public class BaseDao {
	protected Conn conn = Conn.getInstace();
	protected Mongo mongo = null;
	protected DB db = null;
	public BaseDao(){
		mongo = conn.getMongo();
		/*MongoOptions options = mongo.getMongoOptions();
		options.socketTimeout = 2000;*/
		db = conn.getDB(mongo);
	}
}
