package com.result;

import java.util.List;

import com.dto.PopularDto;

public class PopularResult
{

    private int result;
    private List<PopularDto> data;
    
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public List<PopularDto> getData() {
		return data;
	}
	public void setData(List<PopularDto> data) {
		this.data = data;
	}
    
    
}