/**
 * 南京青牛通讯技术有限公司
 * 日期：$$Date: 2016-08-23 16:32:25 +0800 (周二, 23 八月 2016) $$
 * 作者：$$Author: zhuzhengling $$
 * 版本：$$Rev: 128527 $$
 * 版权：All rights reserved.
 */
package com.external.common.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.util.AppDateUtils;
import com.util.StringUtil;

public class ResponseDto<T> extends MessageInfoDto
{

    /**
	 * 有效数据
	 */
    private T data;
    
    /**
	 * 透传数据
	 */
    private String  orignalRspStr;

	/**
	 * getter method
	 * @return Returns the data.
	 */
	public T getData() {
		return data;
	}

	/**
	 * setter method
	 * @param data The data to set.
	 */
	public void setData(T data) {
		this.data = data;
	}

	/**
	 * getter method
	 * @return Returns the orignalRspStr.
	 */
	public String getOrignalRspStr() {
		return orignalRspStr;
	}

	/**
	 * setter method
	 * @param orignalRspStr The orignalRspStr to set.
	 */
	public void setOrignalRspStr(String orignalRspStr) {
		setMessage(null);
		setState(-1);
		this.orignalRspStr = orignalRspStr;
	}

	/*@Override
    public String toString()
    {
		if (!StringUtil.isBlank(getMessage())  || StringUtil.isBlank(orignalRspStr))
		{
			orignalRspStr = null;
			return JSONObject.toJSONString(this, AppDateUtils.getSecondsFormatConfig());
		}
		else
		{
			JSONObject returnObj = new JSONObject();
			JSONObject dataObj = JSONObject.parseObject(orignalRspStr);
//			// code变化state 查询正常返回1而且没有message需要转换统一码
//			if(dataObj.containsKey("getConfig")){
//				dataObj.remove("getConfig");
//				return dataObj.toJSONString();
//			}
				
			// 结果处理
			returnObj.put("state", dataObj.getIntValue("state"));
			returnObj.put("message", dataObj.getString("message"));
			returnObj.put("currTime", AppDateUtils.getDateyyyyMMddHHmmss(AppDateUtils.getDate()));
			dataObj.remove("state");
			dataObj.remove("message");
			// 如果结果含有data数据，需要把data数据取出来和其它字段并行输出
			if (dataObj.containsKey("data"))
			{
				Object dataRstObj = dataObj.get("data");
				if (dataRstObj != null 
						&& dataRstObj instanceof JSONObject
						&& !((JSONObject) dataRstObj).isEmpty())
				{
					dataObj.putAll((JSONObject) dataRstObj);
				}
				else if (dataRstObj != null 
						&& dataRstObj instanceof JSONArray
						&& !((JSONArray) dataRstObj).isEmpty())
				{
                    JSONObject dataObj2 = JSONObject.parseObject(orignalRspStr);
                    //dataObj2.put("currTime", AppDateUtils.getDateyyyyMMddHHmmss(AppDateUtils.getDate()));
                    return dataObj2.toJSONString();
				}
				dataObj.remove("data");
			}
			if (!dataObj.isEmpty()) {
				returnObj.put("data", dataObj);
			}
			
			return returnObj.toJSONString();
		}
    }*/
}
