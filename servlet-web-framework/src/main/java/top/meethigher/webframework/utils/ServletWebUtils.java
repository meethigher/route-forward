package top.meethigher.webframework.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import top.meethigher.webframework.annotation.Required;
import top.meethigher.webframework.exception.ServletWebException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 基于fastjson的工具类
 *
 * @author chenchuancheng
 * @since 2023/5/21 10:25
 */
public class ServletWebUtils {


    /**
     * FastJSON使用中两个问题
     * 1. value为null的字段，自动省略
     * 2. key值默认不带引号
     * 写法过于麻烦，所以提出一个方法
     *
     * @param o 对象
     * @return 将o转换后的json字符串
     */
    public static String toJSONString(Object o) {
        return JSON.toJSONString(o, SerializerFeature.WRITE_MAP_NULL_FEATURES, SerializerFeature.QuoteFieldNames);
    }

    /**
     * 截取字符串
     *
     * @param content 内容
     * @param start   开始字符串
     * @param to      结束字符串
     * @return 截取后的字符串
     */
    private static String subString(String content, String start, String to) {
        int indexFrom = start == null ? 0 : content.indexOf(start);
        int indexTo = to == null ? content.length() : content.indexOf(to);
        if (indexFrom < 0 || indexTo < 0 || indexFrom > indexTo) {
            return null;
        }
        if (null != start) {
            indexFrom += start.length();
        }
        return content.substring(indexFrom, indexTo);
    }

    /**
     * 这边建议使用Map&lt;String,Object&gt;，本身流反序列化后，就是有类型的。
     * 如果传过来的value是Long、Integer、Boolean等类型，强转String会报错
     *
     * @param url 带参url。如/interface?name=1&age=2
     * @return 参数Map
     */
    public static Map<String, Object> getParameters(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            return Collections.<String, Object>emptyMap();
        }

        String start = "?";
        String parametersStr = subString(url, start, null);
        if (parametersStr == null || parametersStr.length() == 0) {
            return Collections.<String, Object>emptyMap();
        }

        String[] parametersArray = parametersStr.split("&");
        Map<String, Object> parameters = new LinkedHashMap<String, Object>();

        for (String parameterStr : parametersArray) {
            int index = parameterStr.indexOf("=");
            if (index <= 0) {
                continue;
            }

            String name = parameterStr.substring(0, index);
            String value = parameterStr.substring(index + 1);
            parameters.put(name, value);
        }
        return parameters;
    }

    /**
     * 流转字符串
     *
     * @param inputStream 流
     * @return 字符串
     */
    public static String convertInputStreamToString(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 转换为Java对象
     *
     * @param args  key-value参数
     * @param clazz class
     * @param <T>   类型
     * @return 转换后的对象
     * @throws ServletWebException 异常
     */
    public static <T> T toObject(Map<String, Object> args, Class<T> clazz) throws ServletWebException {
        T t = JSON.toJavaObject((JSON) JSON.toJSON(args), clazz);
        checkRequired(t);
        return t;
    }

    /**
     * 校验必填
     *
     * @param t   对象
     * @param <T> 对象类型
     * @throws ServletWebException 校验不通过丢异常
     */
    private static <T> void checkRequired(T t) throws ServletWebException {
        List<Field> requiredFields = new ArrayList<>();
        Class<?> clazz = t.getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Required.class)) {
                    requiredFields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        for (Field field : requiredFields) {
            field.setAccessible(true);
            try {
                if (field.get(t) == null) {
                    throw new ServletWebException("参数 " + field.getName() + " 不能为空! ");
                }
            } catch (Exception e) {
                throw new ServletWebException(e.getMessage());
            }
        }
    }
}
