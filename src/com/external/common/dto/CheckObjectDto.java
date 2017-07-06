/**
 * 南京青牛通讯技术有限公司
 * 日期：$$Date: 2016-07-13 18:18:30 +0800 (周三, 13 七月 2016) $$
 * 作者：$$Author: zhoulin $$
 * 版本：$$Rev: 122644 $$
 * 版权：All rights reserved.
 */
package com.external.common.dto;

import net.sf.json.JSONObject;

/**
 * 请求参数校验
 */
public class CheckObjectDto
{
    /**
     * 校验标识
     */
    private boolean flag;

    /**
     * 失败时，返回json对象
     */
    private JSONObject jsonObject;

    /**
     * @return the flag
     */
    public boolean isFlag()
    {
        return flag;
    }

    /**
     * @param flag
     *            the flag to set
     */
    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }

    /**
     * @return the jsonObject
     */
    public JSONObject getJsonObject()
    {
        return jsonObject;
    }

    /**
     * @param jsonObject
     *            the jsonObject to set
     */
    public void setJsonObject(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }
}
