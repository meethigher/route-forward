package top.meethigher.webframework.utils;

/**
 * 状态码
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2022/12/08 22:07
 */
public enum RespStatus {
    SUCCESS(0,"成功"),
    FAILURE(1,"失败"),
    ERROR(500,"服务器内部错误")
    ;

    public final int code;

    public final String desc;

    RespStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
