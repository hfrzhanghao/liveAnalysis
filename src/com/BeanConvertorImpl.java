package com;
/**
 * 对象属性拷贝
 * @author Administrator
 *
 * @param <T> 源对象
 * @param <E> 目标对象
 */
public class BeanConvertorImpl<T, E> implements BeanConvertor<T, E>
{

    @Override
    public void convertBean(T orig, E dest, Object... param)
    {
        BeanWrapperUtils.copyProperties(orig, dest);
    }

    @Override
    public void convertBean(T orig, E dest, String[] ignoreProperties, Object... param)
    {
        BeanWrapperUtils.copyProperties(orig, dest, ignoreProperties);
    }

}

