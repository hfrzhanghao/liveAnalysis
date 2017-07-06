package com.db.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LiveInfoEntity {

	private String uid;
	
	private String operation_guid;
	
	private String url;
	
	private String cip;
	
	private String openType;
	
	private String userName;
	
	private String businessID;

	private String os_type;
	
	private String device_name;
	
	private String os_version;
	
	private String province;
	
	private String city;
	
	private double duration;
	
	private double first_data_duration_avg;
	
	private double[] first_data_duration_inner;
	
	private double first_data_duration_max;
	
	private double first_pic_duration_avg;
	
	private List<Double> first_pic_duration_inner = new ArrayList<Double>();
	
	private double first_pic_duration_max;
	
	private long insertTime;
	
	private double lock_count_avg;
	
	private int[] lock_count_inner;
	
	private int lock_count_total;
	
	private String start_time;
	
	private String end_time;
	
	private String player_sdk_type;
	
	private String browser_version;
	
	private String log;
	
	private List<Map<String,String>> result = new ArrayList<Map<String,String>>();

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getOperation_guid() {
		return operation_guid;
	}

	public void setOperation_guid(String operation_guid) {
		this.operation_guid = operation_guid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCip() {
		return cip;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCip(String cip) {
		this.cip = cip;
	}

	public String getOs_type() {
		return os_type;
	}

	public void setOs_type(String os_type) {
		this.os_type = os_type;
	}

	

	public String getOs_version() {
		return os_version;
	}

	public void setOs_version(String os_version) {
		this.os_version = os_version;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public double getFirst_data_duration_avg() {
		return first_data_duration_avg;
	}

	public void setFirst_data_duration_avg(double first_data_duration_avg) {
		this.first_data_duration_avg = first_data_duration_avg;
	}

	public double[] getFirst_data_duration_inner() {
		return first_data_duration_inner;
	}

	public void setFirst_data_duration_inner(double[] first_data_duration_inner) {
		this.first_data_duration_inner = first_data_duration_inner;
	}

	public double getFirst_data_duration_max() {
		return first_data_duration_max;
	}

	public void setFirst_data_duration_max(double first_data_duration_max) {
		this.first_data_duration_max = first_data_duration_max;
	}

	public double getFirst_pic_duration_avg() {
		return first_pic_duration_avg;
	}

	public void setFirst_pic_duration_avg(double first_pic_duration_avg) {
		this.first_pic_duration_avg = first_pic_duration_avg;
	}

	public List<Double> getFirst_pic_duration_inner() {
		return first_pic_duration_inner;
	}

	public void setFirst_pic_duration_inner(List<Double> first_pic_duration_inner) {
		this.first_pic_duration_inner = first_pic_duration_inner;
	}

	public double getFirst_pic_duration_max() {
		return first_pic_duration_max;
	}

	public void setFirst_pic_duration_max(double first_pic_duration_max) {
		this.first_pic_duration_max = first_pic_duration_max;
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}

	public double getLock_count_avg() {
		return lock_count_avg;
	}

	public void setLock_count_avg(double lock_count_avg) {
		this.lock_count_avg = lock_count_avg;
	}

	public int[] getLock_count_inner() {
		return lock_count_inner;
	}

	public void setLock_count_inner(int[] lock_count_inner) {
		this.lock_count_inner = lock_count_inner;
	}

	public int getLock_count_total() {
		return lock_count_total;
	}

	public void setLock_count_total(int lock_count_total) {
		this.lock_count_total = lock_count_total;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public List<Map<String, String>> getResult() {
		return result;
	}

	public void setResult(List<Map<String, String>> result) {
		this.result = result;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPlayer_sdk_type() {
		return player_sdk_type;
	}

	public void setPlayer_sdk_type(String player_sdk_type) {
		this.player_sdk_type = player_sdk_type;
	}

	public String getBrowser_version() {
		return browser_version;
	}

	public void setBrowser_version(String browser_version) {
		this.browser_version = browser_version;
	}
	
	public String getOpenType() {
		return openType;
	}

	public void setOpenType(String openType) {
		this.openType = openType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBusinessID() {
		return businessID;
	}

	public void setBusinessID(String businessID) {
		this.businessID = businessID;
	}
}
