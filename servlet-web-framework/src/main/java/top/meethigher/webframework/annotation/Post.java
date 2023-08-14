package top.meethigher.webframework.annotation;

import java.lang.annotation.*;

/**
 * POST请求映射路径
 *
 * @author chenchuancheng
 * @since 2023/08/12 19:00
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Post {

    String value() default "/post";

    String desc() default "";
}
