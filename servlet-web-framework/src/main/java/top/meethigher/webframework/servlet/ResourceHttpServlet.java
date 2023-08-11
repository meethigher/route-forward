package top.meethigher.webframework.servlet;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.meethigher.webframework.annotation.Rest;
import top.meethigher.webframework.exception.BasicServletWebException;
import top.meethigher.webframework.exception.ServletWebException;
import top.meethigher.webframework.utils.Resp;
import top.meethigher.webframework.utils.ServletWebUtils;
import top.meethigher.webframework.worker.ControllerManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static top.meethigher.webframework.utils.ServletRequestUtils.removeRequest;
import static top.meethigher.webframework.utils.ServletRequestUtils.setRequest;
import static top.meethigher.webframework.utils.ServletWebUtils.*;

/**
 * 处理静态资源Servlet
 *
 * @author chenchuancheng github.com/meethigher
 * @see <a href="https://github.com/alibaba/druid/blob/master/core/src/main/java/com/alibaba/druid/support/jakarta/ResourceServlet.java">阿里druid</a>
 * @date 2023/06/11 21:20
 */
public abstract class ResourceHttpServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ResourceHttpServlet.class);


    /**
     * 本地资源路径，以/开头
     */
    protected final String resourcePath;


    /**
     * 该servlet表示的接口路径
     */
    protected final String servletPath;

    /**
     * 控制器管理器
     */
    protected final ControllerManager controllerManager;

    public ResourceHttpServlet(String resourcePath, String servletPath, ControllerManager controllerManager) {
        this.resourcePath = resourcePath;
        this.servletPath = servletPath;
        this.controllerManager = controllerManager;
    }

    /**
     * 注册控制器，注意控制器需带有响应注解
     *
     * @param objs 控制器对象数组
     */
    public void registerController(Object... objs) {
        for (Object obj : objs) {
            if (obj.getClass().isAnnotationPresent(Rest.class)) {
                controllerManager.registerController(obj);
            }
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (method.equals("OPTIONS")) {
            doOptions(req, resp);
            return;
        }
        String servletPath = req.getServletPath();
        String contextPath = req.getContextPath();
        /**
         * 访问路径
         * 若contextPath为/aaa，访问接口/aaa/test/index.html，获取到即/test/index.html
         * 若contextPath未配置，访问接口/test/index.html，获取到即/test/index.html
         */
        String requestURI = req.getRequestURI();
        resp.setCharacterEncoding("utf-8");
        if (contextPath == null) {
            contextPath = "";
        }
        String uri = contextPath + servletPath;
        String path = requestURI.substring(contextPath.length() + servletPath.length());
        path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        if ("".equals(path)) {
            if (contextPath.equals("") || contextPath.equals("/")) {
                resp.sendRedirect("/" + this.servletPath + "/index.html");
            } else {
                resp.sendRedirect(this.servletPath + "index.html");
            }
            return;
        }
        if ("/".equals(path)) {
            resp.sendRedirect("index.html");
            return;
        }
        //若静态资源存在，则优先返回静态资源。否则才到接口调用
        if (returnResourceFile(path, uri, resp)) {
            return;
        }
        //接口转发
        forward(req, resp, method, path);
    }


    /**
     * 进行路由转发请求内容
     *
     * @param req    http请求
     * @param resp   http响应
     * @param method 请求类型 GET/POST
     * @param path   请求
     * @throws IOException 抛出异常
     */
    protected void forward(HttpServletRequest req, HttpServletResponse resp, String method, String path) throws IOException {
        try {
            path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
            resp.setContentType("application/json;charset=utf-8");
            Object result;
            Map<String, Object> args;
            try {
                setRequest(req);
                switch (method) {
                    case "GET":
                        args = getParameters(URLDecoder.decode(path + "?" + req.getQueryString(), StandardCharsets.UTF_8.name()));
                        result = controllerManager.call(ControllerManager.HttpMethod.GET, path, args);
                        break;
                    case "POST":
                        Enumeration<String> names = req.getParameterNames();
                        args = new HashMap<>();
                        if (req.getContentType() != null && req.getContentType().toLowerCase(Locale.ROOT).contains("application/json")) {
                            String read = convertInputStreamToString(req.getInputStream());
                            args = ServletWebUtils.stringToObject(read);
                        } else {
                            while (names.hasMoreElements()) {
                                String key = names.nextElement();
                                args.put(key, req.getParameter(key));
                            }
                        }
                        result = controllerManager.call(ControllerManager.HttpMethod.POST, path, args);
                        break;
                    default:
                        throw new BasicServletWebException("接口目前仅支持Get/Post请求");
                }
                String s = toJSONString(result);
                resp.getWriter().print(s);
            } finally {
                removeRequest();
            }
        } catch (ServletWebException failure) {//failure表示开发者故意丢出的异常，提示请求失败
            resp.getWriter().print(toJSONString(Resp.getFailureResp(failure.getMessage())));
        } catch (Exception error) {//error表示未预见的异常，提示服务器内部错误
            error.printStackTrace();
            resp.getWriter().print(toJSONString(Resp.getErrorResp(error.getCause() == null ? error.getMessage() : error.getCause().getMessage())));
        }
    }


    /**
     * 返回静态资源
     *
     * @param fileName 文件名
     * @param uri      访问路径
     * @param response HttpServletResponse
     * @return true表示返回静态资源
     * @throws IOException IO异常
     */
    protected boolean returnResourceFile(String fileName, String uri, HttpServletResponse response) throws IOException {
        String filePath = getFilePath(fileName);
        //设置响应类型
        if (filePath.endsWith(".html") || filePath.endsWith(".htm")) {
            response.setContentType("text/html; charset=utf-8");
        }
        if (fileName.endsWith(".css")) {
            response.setContentType("text/css;charset=utf-8");
        }
        if (fileName.endsWith(".js")) {
            response.setContentType("text/javascript;charset=utf-8");
        }
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource(filePath);
            if (url == null) {
                return false;
            }
            File file = new File(url.toURI());
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        //处理图片文件
        if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg")) {
            byte[] bytes = readByteArrayFromResource(filePath);
            if (bytes != null) {
                response.getOutputStream().write(bytes);
            }
        } else {//处理非图片文件
            String text = readFromResource(filePath);
            if (text == null) {
                return false;
            }
            response.getWriter().write(text);
        }
        return true;
    }


    /**
     * 读取文件并返回UTF-8字符串
     *
     * @param filePath 相对文件地址。注意，此处的文件地址为相对于类路径的文件地址
     * @return UTF-8字符串
     */
    protected String readFromResource(String filePath) {
        if (filePath == null || filePath.isEmpty() || filePath.contains("..") || filePath.contains("?") || filePath.contains(":")) {
            return null;
        }
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            if (is == null) {
                return null;
            }
            int b;
            while ((b = is.read()) != -1) {
                os.write(b);
            }
            os.flush();
            return os.toString(StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取文件并返回字节数组
     *
     * @param filePath 相对文件地址。注意，此处的文件地址为相对于类路径的文件地址
     * @return 字节数组
     */
    protected byte[] readByteArrayFromResource(String filePath) {
        if (filePath == null || filePath.isEmpty() || filePath.contains("..") || filePath.contains("?") || filePath.contains(":")) {
            return null;
        }

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            if (is == null) {
                return null;
            }
            int b;
            while ((b = is.read()) != -1) {
                os.write(b);
            }
            os.flush();
            return os.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 返回文件路径
     *
     * @param fileName 文件名
     * @return 文件路径
     */
    protected String getFilePath(String fileName) {
        return resourcePath + fileName;
    }

    public String getServletPath() {
        return servletPath;
    }
}
