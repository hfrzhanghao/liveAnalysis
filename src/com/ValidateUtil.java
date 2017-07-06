package com;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil
{
    private static final String MOBILE_PATTERN = "^((13[0-9])|(14[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    /**
     * @breif 校验手机号码是否合法
     * @param mobiles
     * @return true 合法 false 不合法
     */
    public static boolean isMobileNO(String mobiles)
    {
        if (mobiles == null)
        {
            return false;
        }
        return match(MOBILE_PATTERN, mobiles);
    }
    
    private static boolean match(String pattern, String str)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
