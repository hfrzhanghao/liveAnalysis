/**
 * 北京红云融通技术有限公司
 * 日期：$$Date: 2016-07-23 16:08:50 +0800 (周五, 22 七月 2016) $$
 * 作者：$$Author: Cyril $$
 * 版本：$$Rev: 124025 $$
 * 版权：All rights reserved.
 */
package com.external.dto;

import java.util.Date;

/**
 * 
 * @ClassName: ContributionDto
 * @Description:
 */
public class AlbumDto {

	// 专辑标识
	private String id;
	// 所属频道标识
	private String channelId;
	// 专辑名称
	private String albumName;
	// 专辑封面名称
	private String albumCover;
	// 专辑简介
	private String albumDesc;
	// 专辑创建时间
	private Date updateDate;
	// 专辑总观看次数
	private Integer totalViewCount;
	//专辑详情页面url地址
	private String detailUrl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public String getAlbumCover() {
		return albumCover;
	}

	public void setAlbumCover(String albumCover) {
		this.albumCover = albumCover;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getTotalViewCount() {
		return totalViewCount;
	}

	public String getAlbumDesc() {
		return albumDesc;
	}

	public void setAlbumDesc(String albumDesc) {
		this.albumDesc = albumDesc;
	}

	public void setTotalViewCount(Integer totalViewCount) {
		this.totalViewCount = totalViewCount;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

}
