package top.meethigher.webframework.annotation;

import java.lang.annotation.*;

/**
 * 控制器映射路径
 *
 * @author chenchuancheng
 * @since 2023/08/12 22:59
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Rest {

    String value() default "/api";

    String desc() default "";
}
