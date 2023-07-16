package top.meethigher.webframework.annotation;

import java.lang.annotation.*;

/**
 * 用于接收GET请求拼参形式请求参数
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/6/8 23:21
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    String value() default "paramName";

    String desc() default "";

    boolean required() default true;
}
