/**
 * FileName: BaseRuntimeException.java
 */
package com.exception;

/**
 * <dl>
 * <dt>BaseRuntimeException</dt>
 * <dd>Description:运行时异常类</dd>
 * <dd>Copyright: Copyright (C) 2006</dd>
 * <dd>Company: 青牛（北京）技术有限公司</dd>
 * </dl>
 * 
 * @author zhangxuhua
 */
public class BaseRuntimeException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 618757937397146541L;

	public BaseRuntimeException()
	{
		super();
	}

	public BaseRuntimeException(String message)
	{
		super(message);
	}

	public BaseRuntimeException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public BaseRuntimeException(Throwable cause)
	{
		super(cause);
	}
}
