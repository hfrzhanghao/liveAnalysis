package com.result;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.dto.AllLiveDto;
import com.dto.PopularDto;
import com.dto.StatRowDto;
import com.dto.DTO;
import com.dto.UserDto;

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