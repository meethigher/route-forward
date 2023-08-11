package top.meethigher.webframework.annotation;

import java.lang.annotation.*;

/**
 * GET请求映射路径
 *
 * @author chenchuancheng
 * @date 2023/08/12 23:01
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Get {

    String value() default "/get";

    String desc() default "";
}
