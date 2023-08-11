package top.meethigher.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 批量创建Bean
 *
 * @author chenchuancheng github.com/meethigher
 * @date 2023/08/12 22:55
 */
@Configuration
@Import(TestBeanSelector.class)
public class TestBeanCreationConfig {
}
