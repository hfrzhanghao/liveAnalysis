/**
 * 南京青牛通讯技术有限公司
 * 日期：$Date: 2016-07-25 15:39:47 +0800 (周一, 25 七月 2016) $
 * 作者：$Author: zhoulin $
 * 版本：$Rev: 124323 $
 * 版权：All rights reserved.
 */
package com.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils
{
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    
    public static String sendGet(String url)
    {
        String result = "";
        BufferedReader in = null;
        try
        {
            URL U = new URL(url);
            URLConnection connection = U.openConnection();
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
            in.close();
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
        }
        finally
        {
            if (in != null)
            {
                try 
                {
                    in.close();
                } 
                catch (IOException e) 
                {
                	logger.error("Exception", e);
                } 
            }
        }
        
        return result;
    }

    public static String sendPost(String url, String param)
    {
        String result = "";
        BufferedReader in = null;
        try
        {
            URL httpurl = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection) httpurl.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            PrintWriter out = new PrintWriter(httpConn.getOutputStream());
            out.print(param);
            out.flush();
            out.close();
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
        }
        catch (Exception e)
        {
        	logger.error("Exception", e);
        }
        finally
        {
            if (in != null)
            {
                try 
                {
                    in.close();
                } 
                catch (IOException e) 
                {
                	logger.error("Exception", e);
                } 
            }
        }
        
        return result;
    }
    
	public static void main(String[] args) {
		System.err.println(sendGet("http://account.96189.com/index.php/api/user/openid.json?ids=56810&type=weixin"));
	}
}
