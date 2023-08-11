package top.meethigher.webframework.exception;

/**
 * 基本web servlet异常
 *
 * @author chenchuancheng
 * @date 2023/08/12 22:19
 */
public class BasicServletWebException extends ServletWebException{
    public BasicServletWebException(String message) {
        super(message);
    }
}
