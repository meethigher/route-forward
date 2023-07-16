package top.meethigher.webframework.annotation;

import java.lang.annotation.*;

/**
 * GET请求映射路径
 *
 * @author chenchuancheng
 * @since 2023/5/19 10:01
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Get {

    String value() default "/get";

    String desc() default "";
}
