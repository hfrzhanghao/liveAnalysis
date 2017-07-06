package com.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllLogDto extends DTO{

	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	int result;
	
	public List<Map<String, String>> getList() {
		return list;
	}
	public void setList(List<Map<String, String>> list) {
		this.list = list;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	
	
}
