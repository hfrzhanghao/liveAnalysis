package com.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include = Inclusion.NON_NULL)
public class AllLiveDto extends DTO{
	private StatRowDto osType;// 总体链路质量统计
	private StatRowDto deviceName;// 总体链路质量统计
	private StatRowDto osVersion;// 总体链路质量统计
	private StatRowDto lockCount;// 总体链路质量统计
	private StatRowDto duration;// 总体链路质量统计
	private StatRowDto firstPicDuration;// 总体链路质量统计
	private StatRowDto province;// 总体链路质量统计
	private StatRowDto city;// 总体链路质量统计
	private StatRowDto playerSDK;
	private StatRowDto browserVersion;
	private StatRowDto userCome;
	private StatRowDto userLeave;
	private StatRowDto userOnline;
	private StatRowDto openType;
	private Integer totalCount;
	
	public StatRowDto getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(StatRowDto deviceName) {
		this.deviceName = deviceName;
	}
	
	public StatRowDto getOsType() {
		return osType;
	}
	public void setOsType(StatRowDto osType) {
		this.osType = osType;
	}
	public StatRowDto getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(StatRowDto osVersion) {
		this.osVersion = osVersion;
	}
	public StatRowDto getLockCount() {
		return lockCount;
	}
	public void setLockCount(StatRowDto lockCount) {
		this.lockCount = lockCount;
	}
	public StatRowDto getDuration() {
		return duration;
	}
	public void setDuration(StatRowDto duration) {
		this.duration = duration;
	}
	public StatRowDto getFirstPicDuration() {
		return firstPicDuration;
	}
	public void setFirstPicDuration(StatRowDto firstPicDuration) {
		this.firstPicDuration = firstPicDuration;
	}
	public StatRowDto getProvince() {
		return province;
	}
	public void setProvince(StatRowDto province) {
		this.province = province;
	}
	public StatRowDto getCity() {
		return city;
	}
	public void setCity(StatRowDto city) {
		this.city = city;
	}
	public StatRowDto getPlayerSDK() {
		return playerSDK;
	}
	public void setPlayerSDK(StatRowDto playerSDK) {
		this.playerSDK = playerSDK;
	}
	public StatRowDto getBrowserVersion() {
		return browserVersion;
	}
	public void setBrowserVersion(StatRowDto browserVersion) {
		this.browserVersion = browserVersion;
	}
	public StatRowDto getUserCome() {
		return userCome;
	}
	public void setUserCome(StatRowDto userCome) {
		this.userCome = userCome;
	}
	public StatRowDto getUserLeave() {
		return userLeave;
	}
	public void setUserLeave(StatRowDto userLeave) {
		this.userLeave = userLeave;
	}
	public StatRowDto getUserOnline() {
		return userOnline;
	}
	public void setUserOnline(StatRowDto userOnline) {
		this.userOnline = userOnline;
	}
	public StatRowDto getOpenType() {
		return openType;
	}
	public void setOpenType(StatRowDto openType) {
		this.openType = openType;
	}
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}


	
	
	
}
