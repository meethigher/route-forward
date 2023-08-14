package rest.api.exception;

import top.meethigher.webframework.exception.ServletWebException;

/**
 * 自定义exception
 *
 * @author chenchuancheng
 * @since 2023/08/12 21:21
 */
public class DIYException extends ServletWebException {
    public DIYException(String message) {
        super(message);
    }
}
