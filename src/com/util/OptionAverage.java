package com.util;

public class OptionAverage {
	
	public double m_total;
	public int m_samples;
	public double m_max;
	
	public void add(double lvalue) {
		if(-1 != lvalue){
			m_total += lvalue;
			m_samples += 1;
			m_max = Math.abs(m_max) > Math.abs(lvalue) ? m_max : lvalue ;
		}
	}
}
