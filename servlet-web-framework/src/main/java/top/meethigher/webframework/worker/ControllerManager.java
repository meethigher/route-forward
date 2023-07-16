package top.meethigher.webframework.worker;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.meethigher.webframework.annotation.*;
import top.meethigher.webframework.exception.ServletWebException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static top.meethigher.webframework.utils.ServletWebUtils.toObject;
import static top.meethigher.webframework.utils.ServletRequestUtils.getRequest;


/**
 * 控制器管理者
 * 可继承重写
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/5/21 03:04
 */
public class ControllerManager {
    private static final Logger log = LoggerFactory.getLogger(ControllerManager.class);

    /**
     * HTTP请求类型
     *
     * @author chenchuancheng
     * @since 2023/5/21 03:05
     */
    public enum HttpMethod {
        GET,
        POST
    }

    /**
     * GET请求映射Map
     */
    protected final Map<String, ControllerMethodCaller> getMethodMap = new HashMap<>();

    /**
     * POST请求映射MAP
     */
    protected final Map<String, ControllerMethodCaller> postMethodMap = new HashMap<>();


    /**
     * 将Service里的各个方法，提前注册到ControllerManager，借鉴Spring思想
     *
     * @param controller 控制器
     */
    public void registerController(Object controller) {
        Class<?> clazz = controller.getClass();
        Rest annotation = clazz.getAnnotation(Rest.class);
        if (annotation == null) {
            log.warn("未获取到注解 @RestController , 请检查代码!");
            return;
        }
        String controllerUrl = annotation.value();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Get get = method.getAnnotation(Get.class);
            Post post = method.getAnnotation(Post.class);
            if (get != null) {
                put(getMethodMap, controller, get.value(), controllerUrl, method);
            }
            if (post != null) {
                put(postMethodMap, controller, post.value(), controllerUrl, method);
            }
        }
        log.debug("注册 {} 到 ControllerManager", controller.getClass());
    }

    /**
     * 将请求地址url 以及 对应的执行逻辑，注册到map进行管理
     *
     * @param methodMap     请求映射Map
     * @param controller    控制器
     * @param methodUrl     方法地址
     * @param controllerUrl 控制器地址
     * @param method        方法
     */
    private void put(Map<String, ControllerMethodCaller> methodMap, Object controller, String methodUrl, String controllerUrl, Method method) {
        String url = controllerUrl + methodUrl;
        methodMap.put(url, args -> {
            try {
                Parameter[] parameters = method.getParameters();
                //参数为空
                if (args == null || parameters == null) {
                    return method.invoke(controller);
                }
                //参数为Map的处理方式
                if (parameters.length == 1 && parameters[0].getType().equals(Map.class)) {
                    return method.invoke(controller, args);
                }
                List<Object> objectList = new ArrayList<>();
                for (Parameter parameter : parameters) {
                    Param param = parameter.getAnnotation(Param.class);
                    Body body = parameter.getAnnotation(Body.class);
                    if (param != null) {
                        String o = (String) args.get(param.value());
                        //校验必填
                        if (param.required() && o == null) {
                            throw new ServletWebException("参数 " + param.value() + " 不能为空! ");
                        }
                        Object convert = convertType(o, parameter.getType());
                        objectList.add(convert);
                        continue;
                    }
                    if (body != null) {
                        Object o = toObject(args, parameter.getType());
                        objectList.add(o);
                    }
                }
                int i = parameters.length - objectList.size();
                if (i > 0) {
                    for (int j = 0; j < i; j++) {
                        objectList.add("");
                    }
                } else if (i < 0) {
                    throw new ServletWebException("参数数量多于预期数量");
                } else {

                }
                return method.invoke(controller, objectList.toArray());

            } catch (InvocationTargetException e) {
                e.getCause().printStackTrace();
                throw new ServletWebException(e.getCause().getMessage());
            } catch (Exception e) {
                throw new ServletWebException(e.getMessage());
            }
        });
    }

    /**
     * 转换类型
     *
     * @param o     字符串
     * @param clazz 需要转换成为的类型
     * @return 转换后的结果
     */
    private Object convertType(String o, Class<?> clazz) {
        //转换类型
        Object convert;
        if (clazz == Integer.class) {
            convert = o == null ? null : Integer.parseInt(o);
        } else if (clazz == int.class) {
            convert = o == null ? 0 : Integer.parseInt(o);
        } else if (clazz == Long.class) {
            convert = o == null ? null : Long.parseLong(o);
        } else if (clazz == long.class) {
            convert = o == null ? 0L : Long.parseLong(o);
        } else if (clazz == Short.class) {
            convert = o == null ? null : Short.parseShort(o);
        } else if (clazz == short.class) {
            convert = o == null ? (short) 0 : Short.parseShort(o);
        } else if (clazz == Byte.class) {
            convert = o == null ? null : Byte.parseByte(o);
        } else if (clazz == byte.class) {
            convert = o == null ? (byte) 0 : Byte.parseByte(o);
        } else if (clazz == Float.class) {
            convert = o == null ? null : Float.parseFloat(o);
        } else if (clazz == float.class) {
            convert = o == null ? 0f : Float.parseFloat(o);
        } else if (clazz == Double.class) {
            convert = o == null ? null : Double.parseDouble(o);
        } else if (clazz == double.class) {
            convert = o == null ? 0d : Double.parseDouble(o);
        } else if (clazz == Boolean.class) {
            convert = o == null ? null : Boolean.parseBoolean(o);
        } else if (clazz == boolean.class) {
            convert = o == null ? Boolean.FALSE : Boolean.parseBoolean(o);
        } else {
            convert = o;
        }
        return convert;
    }

    /**
     * 调用方法
     *
     * @param type 类型
     * @param url  路径
     * @param args 参数
     * @return 返回值
     * @throws Exception 异常
     */
    public Object call(HttpMethod type, String url, Map<String, Object> args) throws Exception {
        long startTime = System.currentTimeMillis();
        ControllerMethodCaller caller;
        switch (type) {
            case POST:
                caller = postMethodMap.get(url);
                break;
            case GET:
            default:
                caller = getMethodMap.get(url);
        }
        if (caller != null) {
            Object exec = caller.exec(args);
            //如需查看请求日志，需打开debug
            log.debug("请求响应信息如下:\n请求地址 => {}\n请求方式 => {}\n请求设备 => {}\n请求来源 => {}\n请求参数 => {}\n响应内容 => {}\n响应时间 => {}毫秒",
                    url,
                    type,
                    getRequest().getHeader("User-Agent"),
                    getRequest().getRemoteAddr(),
                    JSON.toJSONString(args),
                    JSON.toJSONString(exec),
                    System.currentTimeMillis() - startTime);
            return exec;
        } else {
            throw new ServletWebException("不存在该接口: " + url);
        }
    }


}
