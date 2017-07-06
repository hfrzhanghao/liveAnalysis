/**
 * 南京青牛通讯技术有限公司
 * 日期：$$Date: 2016-08-17 11:27:03 +0800 (周三, 17 八月 2016) $$
 * 作者：$$Author: zhoulin $$
 * 版本：$$Rev: 127899 $$
 * 版权：All rights reserved.
 */
package com.external.common.dto;

import java.util.Date;

import com.util.AppDateUtils;

public class MessageInfoDto
{
	/**
	 * 返回状态
	 */
    private int state;
    
	/**
	 * 返回消息
	 */
    private String message;
    
    /**
	 * 返回状态
	 */
    private Date currTime = AppDateUtils.getDate();
    
    public MessageInfoDto()
    {
    }
    
    public MessageInfoDto(int pState, String pMessage)
    {
		this.state = pState;
		this.message = pMessage;
    }

	/**
	 * getter method
	 * @return Returns the state.
	 */
	public int getState() {
		return state;
	}

	/**
	 * setter method
	 * @param state The state to set.
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * getter method
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * setter method
	 * @param message The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * getter method
	 * @return Returns the currTime.
	 */
	public Date getCurrTime() {
		return currTime;
	}

	/**
	 * setter method
	 * @param currTime The currTime to set.
	 */
	public void setCurrTime(Date currTime) {
		this.currTime = currTime;
	}

	/**
	 * setter method
	 * @param state The state to set.
	 */
	public void setMessageInfo(MessageInfoDto messageInfoDto) {
		this.state = messageInfoDto.getState();
		this.message = messageInfoDto.getMessage();
	}
}
