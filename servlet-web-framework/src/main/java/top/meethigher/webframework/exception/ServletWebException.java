package top.meethigher.webframework.exception;

/**
 * ServletWeb框架捕获异常
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/08/12 23:55
 */
public abstract class ServletWebException extends Exception{
    public ServletWebException(String message) {
        super(message);
    }
}
