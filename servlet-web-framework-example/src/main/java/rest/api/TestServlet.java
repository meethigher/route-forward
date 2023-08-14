package rest.api;

import top.meethigher.webframework.servlet.ResourceHttpServlet;
import top.meethigher.webframework.worker.ControllerManager;

/**
 * 自定义Servlet
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/08/12 20:23
 */
public class TestServlet extends ResourceHttpServlet {

    public TestServlet(ControllerManager controllerManager) {
        super("test", "api", controllerManager);
    }

}
