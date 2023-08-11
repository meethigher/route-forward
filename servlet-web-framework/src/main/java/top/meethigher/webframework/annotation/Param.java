package top.meethigher.webframework.annotation;

import java.lang.annotation.*;

/**
 * 用于接收GET/POST请求拼参形式请求参数
 *
 * @author chenchuancheng github.com/meethigher
 * @date 2023/06/08 23:21
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    String value() default "paramName";

    String desc() default "";

    boolean required() default true;
}
