package top.meethigher.webframework.annotation;

import java.lang.annotation.*;

/**
 * 用于接收POST请求formdata形式数据，不支持文件流
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/06/08 23:25
 * @see Part 处理文件流
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Body {
}
