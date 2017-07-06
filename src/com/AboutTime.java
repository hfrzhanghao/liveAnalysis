package com;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AboutTime {
	public static Long toLong(String dateTime) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime));
			return c.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0L;
	}
	
	public static Long toLongWithMinite(String dateTime) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(new SimpleDateFormat("yyyy-MM-dd-HH:mm").parse(dateTime));
			return c.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	public static Long toLongSSS(String dateTime) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS").parse(dateTime));
			return c.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0L;
	}
}
