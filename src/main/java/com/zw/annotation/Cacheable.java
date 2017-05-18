package com.zw.annotation;

import com.zw.util.DateUnit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/5/18.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {

    // 类目 用于定义是以什么开头的
    String category() default "";

    // 要用来解释的key值
    String key();

    // 过期时间数值，默认-1为永久
    int expire() default -1;

    // 时间单位，默认为秒
    DateUnit dateUnit() default DateUnit.SECONDS;

}
