package com.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
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
	
	/* 
     * 将时间戳转换为时间
     */
    public static String stampToDate(long timestamp){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS");
        Date date = new Date(timestamp);
        res = simpleDateFormat.format(date);
        return res;
    }
    /* 
     * 将时间戳转换为时间天
     */
    public static String stampToDateD(long timestamp){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(timestamp);
        res = simpleDateFormat.format(date);
        return res;
    }
    /* 
     * 将时间天转换为时间戳
     */
    public static Long toLongSSSD(String dateTime) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateTime));
			return c.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0L;
	}
}
