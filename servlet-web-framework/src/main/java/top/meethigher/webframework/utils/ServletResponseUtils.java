package top.meethigher.webframework.utils;

import javax.servlet.http.HttpServletResponse;

/**
 * Servlet响应工具类
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/06/09 00:22
 */
public class ServletResponseUtils {

    private static final ThreadLocal<HttpServletResponse> httpServletResponseThreadLocal = new ThreadLocal<>();

    /**
     * 将http响应与当前线程绑定
     *
     * @param response http响应
     */
    public static void setResponse(HttpServletResponse response) {
        httpServletResponseThreadLocal.set(response);
    }

    /**
     * 通过当前线程获取绑定http响应
     *
     * @return 绑定的http响应
     */
    public static HttpServletResponse getResponse() {
        return httpServletResponseThreadLocal.get();
    }

    /**
     * 移除当前线程绑定的http响应
     */
    public static void removeResponse() {
        httpServletResponseThreadLocal.remove();
    }
}
