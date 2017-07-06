package com.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include = Inclusion.NON_EMPTY)
public class UserDto extends DTO{
	private String userName;
	private String openType;
	private String playerSDKType;
	private String deviceName;
	private String osVersion;
	private String province;
	private String city;
	private int lockCount;
	private double duration;
	private String businessID;
	private double first_pic_duration_avg;
	private String browserVersion;
	private String startTime;
	private String endTime;
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOpenType() {
		return openType;
	}
	public void setOpenType(String openType) {
		this.openType = openType;
	}
	public String getBrowserVersion() {
		return browserVersion;
	}
	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getPlayerSDKType() {
		return playerSDKType;
	}
	public void setPlayerSDKType(String playerSDKType) {
		this.playerSDKType = playerSDKType;
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
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public String getBusinessID() {
		return businessID;
	}
	public void setBusinessID(String businessID) {
		this.businessID = businessID;
	}
	public double getFirst_pic_duration_avg() {
		return first_pic_duration_avg;
	}
	public void setFirst_pic_duration_avg(double first_pic_duration_avg) {
		this.first_pic_duration_avg = first_pic_duration_avg;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public int getLockCount() {
		return lockCount;
	}
	public void setLockCount(int lockCount) {
		this.lockCount = lockCount;
	}

	
	
}
