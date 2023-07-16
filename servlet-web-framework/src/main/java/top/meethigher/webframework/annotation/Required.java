package top.meethigher.webframework.annotation;

import java.lang.annotation.*;

/**
 * 必填注解
 *
 * @author chenchuancheng
 * @since 2023/5/21 10:33
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Required {
}
