
本人设计的一套轻量MVC框架，支持嵌入任何支持 Java Servlet 规范的框架，不局限于 Spring ，甚至国产的Web框架也支持嵌入。

一开始是为适用公司某些需求的产品做的轻量框架，经过多次整理后，决定小开源一下。个人能力和眼界有限，自然比不上如 [jooby](https://meethigher.top/blog/2023/jooby/) 那样优秀的轻量框架，不过够用。

**严格来说，这不算是个框架，顶多是便于开发和解耦的路由转发api，这也是为啥我开源的项目叫[ route-forward ](https://github.com/meethigher/route-forward)**

已实现的功能如下

1. 支持通过`@Rest`、`@GET`、`@POST`注解进行解耦开发，避免大量if...else...。目前请求方式也仅支持`GET`与`POST`

2. 传参支持直接使用`Map<String,Object>`，也支持注解解析

   * `GET`请求传参支持`@Param`解析，并支持参数校验

   * `POST`请求传参支持`@Body`解析，并支持参数校验

3. 全局异常抓取

4. 请求日志打印

明显的缺陷

1. POST请求主要支持任何Content-Type的**字符串请求体**。POST暂不支持类似GET的直接拼参、不支持文件流，不过也可以实现。
2. 目前不支持接入接口文档，不过可以实现，且注解都进行了适用生成文档的参数。不过可以直接使用代码零侵入的[Apifox框架](https://apifox.com/)。

# 一、介绍

## 1.1 项目结构说明

项目结构

* servlet-web-framework：核心。基于Servlet与注解的轻量MVC框架
* servlet-web-framework-example：示例。使用框架进行实际业务开发示例
* springboot-example：示例。嵌入到Spring框架示例

```
├─servlet-web-framework 基于Servlet与注解的轻量MVC框架
│  └─src
│      └─main
│          └─java
│              └─top
│                  └─meethigher
│                      └─webframework
│                          ├─annotation 类似SpringMVC的注解
│                          ├─exception 通用异常
│                          ├─servlet 接口请求入口
│                          ├─utils 工具类
│                          └─worker 注解的增强实现
├─servlet-web-framework-example 使用框架进行实际业务开发示例
│  └─src
│      └─main
│          ├─java
│          │  └─rest
│          │      └─api
│          │          ├─controlelr
│          │          ├─model
│          │          └─service
│          └─resources
│              └─test
│                  └─img
└─springboot-example 嵌入到Spring框架示例
    └─src
        └─main
            ├─java
            │  └─top
            │      └─meethigher
            │          └─springboot
            │              └─config
            └─resources

```

## 1.2 整体请求流程

以Servlet作为请求入口，这点类似于SpringMVC的DispatcherServlet

![](https://meethigher.top/blog/2023/route-forward/image-20230716180602655.png)

# 二、使用示例

## 2.1 maven依赖

已上传Maven中央仓库

```xml
<dependency>
    <groupId>top.meethigher</groupId>
    <artifactId>servlet-web-framework</artifactId>
    <version>1.0</version>
</dependency>
```

## 2.2 Spring使用示例

创建Controller

```java
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
```

注册Servlet到Spring容器

```java
class TestServlet extends ResourceHttpServlet {

    public TestServlet(ControllerManager controllerManager) {
        super("test", "api", controllerManager);
    }

}

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
```

请求示例

![](https://meethigher.top/blog/2023/route-forward/image-20230716182900269.png)

![](https://meethigher.top/blog/2023/route-forward/image-20230716182951542.png)
