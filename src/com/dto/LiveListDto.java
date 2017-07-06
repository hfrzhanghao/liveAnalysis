package com.dto;

import java.util.ArrayList;
import java.util.List;

import com.db.entity.LiveInfoEntity;

public class LiveListDto {

	List<LiveInfoEntity> list = new ArrayList<LiveInfoEntity>();

	/**
	 * 每页个数
	 */
	private int pageSize;
	/**
	 * 当前页码
	 */
	private int currPage;
	/**
	 * 总页码
	 */
	private int totalPage;
	
	/**
	 * 总数据量
	 */
	private int totalData;

	public List<LiveInfoEntity> getList() {
		return list;
	}

	public void setList(List<LiveInfoEntity> list) {
		this.list = list;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalData() {
		return totalData;
	}

	public void setTotalData(int totalData) {
		this.totalData = totalData;
	}

	
}
