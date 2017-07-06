package com;

public interface BeanConvertor<T, E>
{

    /**
     * 用于在转换过程中传入参数，更加实用的进行转换
     * @param orig 源对象
     * @param dest 目标对象
     * @param param 需要用到的参数
     */
    void convertBean(final T orig, final E dest, final Object... param);

    /**
     * 用于在转换过程中传入参数，更加实用的进行转换
     * @param orig 源对象
     * @param dest 目标对象
     * @param ignoreProperties 忽略的属性列表
     * @param param 需要用到的参数
     */
    void convertBean(final T orig, final E dest, final String[] ignoreProperties, final Object... param);
}
