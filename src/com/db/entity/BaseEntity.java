package com.db.entity;

import java.io.Serializable;
import java.math.BigDecimal;


public class BaseEntity implements Serializable {
	
	private static final long serialVersionUID = -4343485293799439952L;
	private double value;
	private String unit;
	private String description;
	private String name;
	private String type;

	private String id;
	
	public BaseEntity() {
		
	}

	public BaseEntity(float value, String unit,String name, String description,String type) {
		this.value = value;
		this.unit = unit;
		this.description = description;
		this.name = name;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		//this.value = value;
		
			BigDecimal fec_avg_b = new BigDecimal(value);
			value = fec_avg_b.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
			this.value = value;
		

	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
