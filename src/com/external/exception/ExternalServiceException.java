package com.external.exception;

import com.exception.BaseRuntimeException;
import com.external.common.dto.MessageInfoDto;

/**
 * DAO操作的异常
 * 
 * @author zhangxuhua
 */
public class ExternalServiceException extends BaseRuntimeException
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2665640036436939863L;
	
	private MessageInfoDto messageInfo;

	public ExternalServiceException()
	{
		super();
	}

	public ExternalServiceException(MessageInfoDto messageInfo)
	{
		super(messageInfo.getMessage());
		this.messageInfo = messageInfo;
	}

	/**
	 * getter method
	 * @return Returns the messageInfo.
	 */
	public MessageInfoDto getMessageInfo() {
		return messageInfo;
	}

	/**
	 * setter method
	 * @param messageInfo The messageInfo to set.
	 */
	public void setMessageInfo(MessageInfoDto messageInfo) {
		this.messageInfo = messageInfo;
	}
}
