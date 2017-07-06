package com.external.dto;

import java.util.List;

/**
 * <dl>
 * <dt>GetContributionListDto.java</dt>
 * <dd>Description:3.22 获取节目列表DTO类</dd>
 * <dd>Copyright: Copyright (C) 2015</dd>
 * <dd>Company: 北京红云融通技术有限公司</dd>
 * <dd>CreateDate: 2016年7月21日</dd>
 * </dl>
 * 
 * @author cyril
 *
 */
public class GetAlbumListDto {

	private long totalCount;
	
	private List<AlbumDto> rows;

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public List<AlbumDto> getRows() {
		return rows;
	}

	public void setRows(List<AlbumDto> rows) {
		this.rows = rows;
	}
	
}
