package com.db.entity;

import java.util.HashMap;
import java.util.Map;

public class LivePreDataEntity {

	String province_key;
	
	String city_key;
	
	String isp_key;
	
	String openType_key;
	
	String url_key;
	
	String businessID_key;
	
	Map<String,Integer> deviceTypeName = new HashMap<String,Integer>();
	
	Map<String,Integer> browserVersion = new HashMap<String,Integer>();
	
	Map<String,Integer> province = new HashMap<String,Integer>();
	
	Map<String,Integer> city = new HashMap<String,Integer>();
	
	Map<String,Integer> openType = new HashMap<String,Integer>();
	
	Map<String,Integer> OSVersion = new HashMap<String,Integer>();
	
	Map<String,Integer> OSType = new HashMap<String,Integer>();
	
	Map<String,Integer> playerSDKType = new HashMap<String,Integer>();
	
	Map<String,Integer> duration = new HashMap<String,Integer>();
	
	Map<String,Integer> lockCount = new HashMap<String,Integer>();
	
	Map<String,Integer> firstPicDuration = new HashMap<String,Integer>();

	public String getProvince_key() {
		return province_key;
	}

	public void setProvince_key(String province_key) {
		this.province_key = province_key;
	}

	public String getCity_key() {
		return city_key;
	}

	public void setCity_key(String city_key) {
		this.city_key = city_key;
	}

	public String getIsp_key() {
		return isp_key;
	}

	public void setIsp_key(String isp_key) {
		this.isp_key = isp_key;
	}

	public String getOpenType_key() {
		return openType_key;
	}

	public void setOpenType_key(String openType_key) {
		this.openType_key = openType_key;
	}

	public String getUrl_key() {
		return url_key;
	}

	public void setUrl_key(String url_key) {
		this.url_key = url_key;
	}

	public String getBusinessID_key() {
		return businessID_key;
	}

	public void setBusinessID_key(String businessID_key) {
		this.businessID_key = businessID_key;
	}

	public Map<String, Integer> getDeviceTypeName() {
		return deviceTypeName;
	}

	public void setDeviceTypeName(Map<String, Integer> deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
	}

	public Map<String, Integer> getBrowserVersion() {
		return browserVersion;
	}

	public void setBrowserVersion(Map<String, Integer> browserVersion) {
		this.browserVersion = browserVersion;
	}

	public Map<String, Integer> getProvince() {
		return province;
	}

	public void setProvince(Map<String, Integer> province) {
		this.province = province;
	}

	public Map<String, Integer> getCity() {
		return city;
	}

	public void setCity(Map<String, Integer> city) {
		this.city = city;
	}

	public Map<String, Integer> getOpenType() {
		return openType;
	}

	public void setOpenType(Map<String, Integer> openType) {
		this.openType = openType;
	}

	public Map<String, Integer> getOSVersion() {
		return OSVersion;
	}

	public void setOSVersion(Map<String, Integer> oSVersion) {
		OSVersion = oSVersion;
	}

	public Map<String, Integer> getOSType() {
		return OSType;
	}

	public void setOSType(Map<String, Integer> oSType) {
		OSType = oSType;
	}

	public Map<String, Integer> getPlayerSDKType() {
		return playerSDKType;
	}

	public void setPlayerSDKType(Map<String, Integer> playerSDKType) {
		this.playerSDKType = playerSDKType;
	}

	public Map<String, Integer> getDuration() {
		return duration;
	}

	public void setDuration(Map<String, Integer> duration) {
		this.duration = duration;
	}

	public Map<String, Integer> getLockCount() {
		return lockCount;
	}

	public void setLockCount(Map<String, Integer> lockCount) {
		this.lockCount = lockCount;
	}

	public Map<String, Integer> getFirstPicDuration() {
		return firstPicDuration;
	}

	public void setFirstPicDuration(Map<String, Integer> firstPicDuration) {
		this.firstPicDuration = firstPicDuration;
	}
	
	

	
	
	
}
