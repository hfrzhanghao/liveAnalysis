/**
 * 南京青牛通讯技术有限公司
 * 日期：$$Date: 2016-08-23 16:32:25 +0800 (周二, 23 八月 2016) $$
 * 作者：$$Author: zhuzhengling $$
 * 版本：$$Rev: 128527 $$
 * 版权：All rights reserved.
 */
package com.external.common.dto;

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

}
