package com.zero.core.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * baas日志注解
 * @author luofei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
public @interface BaasLogger {
	
	/**
	 * 查询是否记录日志
	 * @return
	 */
	boolean query() default false;
	/**
	 * 新增是否记录日志
	 * @return
	 */
	boolean add() default true;
	/**
	 * 更新是否记录日志
	 * @return
	 */
	boolean update() default true;
	/**
	 * 删除是否记录日志
	 * @return
	 */
	boolean delete() default true;

}
