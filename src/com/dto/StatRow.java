package com.dto;

import java.util.List;

import com.dto.StatCol;

public class StatRow {
	private String name;
	private String description;
	private List<StatCol> cols;

	public String getName() {
		return name;
	}

	public List<StatCol> getCols() {
		return cols;
	}

	public void setCols(List<StatCol> cols) {
		this.cols = cols;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
