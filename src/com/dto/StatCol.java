package com.dto;

import java.util.Map;

import com.db.entity.BaseEntity;

public class StatCol {
	private String rowname;
	private String name;
	private String description;
	private Map<String,BaseEntity> values;

	public String getRowname() {
		return rowname;
	}

	public void setRowname(String rowname) {
		this.rowname = rowname;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, BaseEntity> getValues() {
		return values;
	}

	public void setValues(Map<String, BaseEntity> values) {
		this.values = values;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
