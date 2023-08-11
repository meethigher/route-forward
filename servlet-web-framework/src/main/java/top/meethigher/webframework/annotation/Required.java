package top.meethigher.webframework.annotation;

import java.lang.annotation.*;

/**
 * 必填注解
 *
 * @author chenchuancheng
 * @date 2023/08/12 21:33
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Required {
}
