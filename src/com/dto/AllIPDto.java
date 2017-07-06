package com.dto;

import java.util.ArrayList;
import java.util.List;

public class AllIPDto extends DTO{
	List<String> list = new ArrayList<String>();
	int result;
	
	public List<String> getList() {
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
}
