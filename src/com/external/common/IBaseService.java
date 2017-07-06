package com.external.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import com.external.common.dto.ResponseDto;

public interface IBaseService
{
    /**
     * process 处理POST请求返回结果，包括返回码和返回码描述
     * 
     * @param params 业务参数
     * @param request request对象
	 * @param response response对象
     * @return 返回码及描述
     */
    <T> ResponseDto<T> process(JSONObject params, 
        HttpServletRequest request, HttpServletResponse response);
}

