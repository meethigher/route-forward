package rest.api.controlelr;

import rest.api.model.Person;
import rest.api.service.TestService;
import top.meethigher.webframework.annotation.*;
import top.meethigher.webframework.exception.ServletWebException;
import top.meethigher.webframework.utils.Resp;

import java.util.Map;

/**
 * 测试接口
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/7/16 15:39
 */
@Rest("/test")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }


    @Get("/api1")
    public Resp api1(Map<String, Object> args) {
        return testService.api1(args);
    }

    @Get("/api2")
    public Resp api2(@Param("name") String name, @Param("age") Integer age, @Param(value = "money", required = false) Double desc) {
        return testService.api2(name,age,desc);
    }

    @Post("/api3")
    public Resp api3(Map<String, Object> args) {
        return testService.api3(args);
    }

    @Post("/api4")
    public Resp api4(@Body Person person) {
        return testService.api4(person);
    }

    @Post("/api5")
    public Resp api5() throws ServletWebException {
        testService.api5();
        return Resp.getSuccessResp();
    }

    @Post("/api6")
    public Resp api6() throws ServletWebException {
        testService.api6();
        return Resp.getSuccessResp();
    }
}
