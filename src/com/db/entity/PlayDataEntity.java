package com.db.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class PlayDataEntity implements Serializable {
	
	private static final long serialVersionUID = -4343485293799439952L;
	
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
	
	private String player_sdk_type;
	
	private String browser_version;
	
	private Integer duration = -1;
	
	private Integer lockCount = -1;
	
	private String start_time;
	
	private String end_time;
	
	private String log;
	
	private List<Integer> result = new ArrayList<Integer>();
	
	private List<Integer> firstPicDuration = new ArrayList<Integer>();
	
	private boolean isPauseLong = false;
	
	private boolean isPause = false;
	
	private Long pauseTime = -1L; 
	
	public PlayDataEntity() {
		
	}
	
	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getLockCount() {
		return lockCount;
	}

	public void setLockCount(Integer lockCount) {
		this.lockCount = lockCount;
	}

	public List<Integer> getFirstPicDuration() {
		return firstPicDuration;
	}

	public void setFirstPicDuration(List<Integer> firstPicDuration) {
		this.firstPicDuration = firstPicDuration;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Integer> getResult() {
		return result;
	}

	public void setResult(List<Integer> result) {
		this.result = result;
	}

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

	public void setCip(String cip) {
		this.cip = cip;
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

	public String getOs_type() {
		return os_type;
	}

	public void setOs_type(String os_type) {
		this.os_type = os_type;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public String getOs_version() {
		return os_version;
	}

	public void setOs_version(String os_version) {
		this.os_version = os_version;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public boolean isPauseLong() {
		return isPauseLong;
	}

	public void setPauseLong(boolean isPauseLong) {
		this.isPauseLong = isPauseLong;
	}

	public Long getPauseTime() {
		return pauseTime;
	}

	public void setPauseTime(Long pauseTime) {
		this.pauseTime = pauseTime;
	}

	public boolean isPause() {
		return isPause;
	}

	public void setPause(boolean isPause) {
		this.isPause = isPause;
	}
	
	
	
}
