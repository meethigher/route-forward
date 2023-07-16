package top.meethigher.webframework.worker;

import java.util.Map;

/**
 * 控制器内部方法调用者
 *
 * @author chenchuancheng  github.com/meethigher
 * @since 2023/5/21 03:04
 */
public interface ControllerMethodCaller {


    /**
     * 执行方法逻辑
     * 为何这边使用一个Map接? 而不是一个泛型对象。考虑GET时的请求参数，参数较少，完全不需要定义一个Model来接，所以用Map会方便一点
     *
     * @param map 参数
     * @return 执行结果
     * @throws Exception 执行异常
     */
    Object exec(Map<String, Object> map) throws Exception;
}
