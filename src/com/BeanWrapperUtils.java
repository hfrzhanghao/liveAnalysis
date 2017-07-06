package com;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * Bean 包装类（对象属性拷贝）
 */
public class BeanWrapperUtils
{
    protected static Log log = LogFactory.getLog(BeanWrapperUtils.class);

    /**
     * @param clazz
     * @param orig
     * @return 返回你所要的DO对象
     */
    public static <T> T copyProperties(Class<T> clazz, Object orig, String... ignorePropertis)
    {
        if (orig == null)
        {
            log.info("Source is null,Don't copy!");
            return null;
        }
        T dest = getInstance(clazz);
        copyProperties(orig, dest, ignorePropertis);
        return dest;
    }

    /**
     * 过滤属性copy
     * 
     * @param orig
     *            源对象
     * @param dest
     *            目标对象
     * @param ignorePropertis
     *            忽略的属性列表
     * @return
     */
    public static void copyProperties(Object orig, Object dest, String... ignorePropertis)
    {
        if (orig == null || dest == null)
        {
            log.info("Source or Dest is null,Ignore it!");
            return;
        }
        BeanUtils.copyProperties(orig, dest, ignorePropertis);
    }

    public static <T> T getInstance(Class<T> destClass)
    {
        T dest = null;
        try
        {
            dest = destClass.newInstance();
        }
        catch (InstantiationException e)
        {
            log.error(e.getMessage(), e);
        }
        catch (IllegalAccessException e)
        {
            log.error(e.getMessage(), e);
        }
        return dest;
    }

    public static <T, E> E convertBean(T source, Class<E> destClass, BeanConvertor<T, E> beanConvertor, Object... param)
    {
        return convertBean(source, destClass, null, beanConvertor, param);
    }

    public static <T, E> E convertBean(T source, Class<E> destClass, String[] ignoreProperties,
            BeanConvertor<T, E> beanConvertor, Object... param)
    {
        E dest = getInstance(destClass);
        if (source == null || dest == null)
        {
            log.info("Source or Dest is null,Ignore it!");
            return null;
        }
        Assert.notNull(beanConvertor);
        if (ignoreProperties == null)
        {
            beanConvertor.convertBean(source, dest, param);
        }
        else
        {
            beanConvertor.convertBean(source, dest, ignoreProperties, param);
        }
        return dest;
    }

    public static <T, E> List<E> convertList(List<T> list, Class<E> destClass)
    {
        return convertList(list, destClass, null);
    }

    public static <T, E> List<E> convertList(List<T> list, Class<E> destClass, BeanConvertor<T, E> beanConvertor,
            Object... param)
    {
        return convertList(list, destClass, null, beanConvertor, param);
    }

    public static <T, E> List<E> convertList(List<T> list, Class<E> destClass, String[] ignoreProperties,
            BeanConvertor<T, E> beanConvertor, Object... param)
    {
        List<E> destList = new ArrayList<E>();
        if (CollectionUtils.isNotEmpty(list))
        {
            if (beanConvertor == null)
            {
                convert(list, destClass, destList);
            }
            else
            {
                convert(list, destClass, ignoreProperties, beanConvertor, destList, param);
            }
        }
        return destList;
    }

    private static <E, T> void convert(List<T> list, Class<E> destClass, String[] ignoreProperties,
            BeanConvertor<T, E> beanConvertor, List<E> destList, Object... param)
    {
        for (T orig : list)
        {
            E dest = convertBean(orig, destClass, ignoreProperties, beanConvertor, param);
            if (dest != null)
            {
                destList.add(dest);
            }
        }
    }

    private static <E, T> void convert(List<T> list, Class<E> destClass, List<E> destList)
    {
        for (T orig : list)
        {
            E e = copyProperties(destClass, orig);
            if (e != null)
            {
                destList.add(e);
            }
        }
    }

}