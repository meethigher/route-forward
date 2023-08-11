package top.meethigher.webframework.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * Servlet请求工具类
 *
 * @author chenchuancheng
 * @date 2023/08/12 20:47
 */
public class ServletRequestUtils {

    private static final ThreadLocal<HttpServletRequest> httpServletRequestThreadLocal = new ThreadLocal<>();

    /**
     * 将http请求与当前线程绑定
     *
     * @param request http请求
     */
    public static void setRequest(HttpServletRequest request) {
        httpServletRequestThreadLocal.set(request);
    }

    /**
     * 通过当前线程获取绑定http请求
     *
     * @return 绑定的http请求
     */
    public static HttpServletRequest getRequest() {
        return httpServletRequestThreadLocal.get();
    }

    /**
     * 移除当前线程绑定的http请求
     */
    public static void removeRequest() {
        httpServletRequestThreadLocal.remove();
    }
}
