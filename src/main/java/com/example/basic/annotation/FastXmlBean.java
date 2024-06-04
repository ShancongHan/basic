package com.example.basic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author han
 * @date 2024/4/12
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FastXmlBean {

    /**
     * 元素
     */
    String element() default "";

    /**
     * 属性
     */
    String attribute() default "";

    /**
     * 子节点
     */
    boolean subElement() default false;

    /**
     * 开始元素
     */
    String start() default "";

    /**
     * 结束元素
     */
    String stop() default "";

    /**
     * 自动闭合
     */
    boolean selfClose() default false;

    /**
     * 是否是list标志
     */
    boolean multi() default false;
}
