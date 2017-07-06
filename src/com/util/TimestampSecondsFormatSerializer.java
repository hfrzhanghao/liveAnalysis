/**
 * 南京青牛通讯技术有限公司
 * 日期：$$Date: 2016-07-20 19:27:50 +0800 (周三, 20 七月 2016) $$
 * 作者：$$Author: zhoulin $$
 * 版本：$$Rev: 123721 $$
 * 版权：All rights reserved.
 */
package com.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

public final class TimestampSecondsFormatSerializer implements ObjectSerializer {

	/**
	 * 日期型字段转换为秒后输出。
	 * 
	 * @param serializer serializer
	 * @param object 对象值
	 * @param obj1 obj1对象
	 * @param type type对象
	 */
	@Override
	public void write(JSONSerializer serializer, Object object, Object obj1,
			Type type, int i) throws IOException {
		if (object == null)
		{
			serializer.getWriter().writeNull();
			return;
		}
		else 
		{
			Timestamp date = (Timestamp) object;
			serializer.write(AppDateUtils.getDateyyyyMMddHHmmss(new Date(date.getTime())));
			return;
		}
	}
}