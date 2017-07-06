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
import com.dto.StatRowDto;
import com.dto.DTO;
import com.dto.UserDto;

public class UsersResult
{

    private int result;
    private List<UserDto> data;
    
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public List<UserDto> getData() {
		return data;
	}
	public void setData(List<UserDto> data) {
		this.data = data;
	}
    
    
}