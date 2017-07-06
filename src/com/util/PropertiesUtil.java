/**
 * 南京青牛通讯技术有限公司
 * 日期：$$Date: 2016-07-13 18:32:40 +0800 (周三, 13 七月 2016) $$
 * 作者：$$Author: zhoulin $$
 * 版本：$$Rev: 122650 $$
 * 版权：All rights reserved.
 */
package com.util;

import java.util.Properties;

import com.external.common.ObjectFactory;
import com.external.common.dto.MessageInfoDto;

public class PropertiesUtil {
    
    private static MessageInfoDto successDto = getMessageInfoByKey("success");
    
    /**
     * 成功的返回码和返回码描述。
     * 
     * @param key 检索key
     * @return 返回码和返回码描述
     */
    public static MessageInfoDto getMessageInfoSuccess()
    {       
        return successDto;
    }
    
    /**
     * 根据key检索message文件得到返回码和返回码描述。
     * 
     * @param key 检索key
     * @return 返回码和返回码描述
     */
    public static MessageInfoDto getMessageInfoByKey(String key)
    {
        int state = Integer.parseInt(getMessageRcByKey(key));
        String message = getMessageRdByKey(key);
        
        return new MessageInfoDto(state, message);
    }
    
    /**
     * 根据key检索message文件得到返回码。
     * 
     * @param key 检索key
     * @return 返回码
     */
    public static String getMessageRcByKey(String key)
    {
        return MessagePropertiesConfigurer.getMessagePropertyByKey(key + ".rc");
    }
    
    /**
     * 根据key检索message文件得到返回码描述。
     * 
     * @param key 检索key
     * @return 返回码描述
     */
    public static String getMessageRdByKey(String key)
    {
        return MessagePropertiesConfigurer.getMessagePropertyByKey(key + ".rd");        
    }
    
    private static class MessagePropertiesConfigurer
    {
    
        /**
         * messageProperies配置取得的变量
         */
        private static Properties messageProperties = (Properties) ObjectFactory.getMessageProps();
        
        protected static String getMessagePropertyByKey(String key)
        {
            return messageProperties.getProperty(key);
        }
    }
}