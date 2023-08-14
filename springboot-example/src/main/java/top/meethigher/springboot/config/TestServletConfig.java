package top.meethigher.springboot.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rest.api.TestServlet;
import rest.api.controlelr.TestController;
import top.meethigher.webframework.worker.ControllerManager;

import javax.annotation.Resource;
import javax.servlet.Servlet;

/**
 * 注册Servlet接口
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/08/12 22:56
 */
@Configuration
public class TestServletConfig {

    @Resource
    private TestController testController;


    @Bean
    public ServletRegistrationBean workflowServlet() {
        ServletRegistrationBean<Servlet> bean = new ServletRegistrationBean<>();
        TestServlet servlet = new TestServlet(new ControllerManager());
        bean.setServlet(servlet);
        bean.addUrlMappings("/" + servlet.getServletPath() + "/*");
        servlet.registerController(testController);
        return bean;
    }

}
