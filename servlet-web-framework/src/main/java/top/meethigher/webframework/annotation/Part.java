package top.meethigher.webframework.annotation;

/**
 * 用于支持post请求的multipart中的文件
 *
 * @author chenchuancheng
 * @since 2023/08/12 20:26
 */
public @interface Part {

    String value() default "paramName";

    String desc() default "";

    boolean required() default true;
}
