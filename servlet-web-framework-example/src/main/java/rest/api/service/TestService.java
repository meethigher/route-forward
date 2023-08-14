package rest.api.service;

import rest.api.exception.DIYException;
import rest.api.model.Person;
import top.meethigher.webframework.annotation.Body;
import top.meethigher.webframework.annotation.Get;
import top.meethigher.webframework.annotation.Param;
import top.meethigher.webframework.annotation.Post;
import top.meethigher.webframework.exception.ServletWebException;
import top.meethigher.webframework.utils.Resp;

import java.util.Map;

/**
 * @author chenchuancheng github.com/meethigher
 * @since 2023/08/12 22:49
 */
public class TestService {

    public Resp api1(Map<String, Object> args) {
        return Resp.getSuccessResp(args);
    }


    public Resp api2(String name, Integer age, Double desc) {
        return Resp.getSuccessResp(new Person(name, age, desc));
    }


    public Resp api3(Map<String, Object> args) {
        return Resp.getSuccessResp(args);
    }


    public Resp api4(@Body Person person) {
        return Resp.getSuccessResp(person);
    }

    public Resp api5() throws ServletWebException {
        throw new DIYException("模拟出错");
    }

    public Resp api6() throws ServletWebException {
        //意外出错
        int i = 1 / 0;
        return Resp.getSuccessResp();
    }
}
