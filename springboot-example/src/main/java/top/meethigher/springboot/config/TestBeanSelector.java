package top.meethigher.springboot.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * 批量创建bean选择器
 *
 * @author chenchuancheng
 * @since 2023/6/7 14:34
 */
public class TestBeanSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        ResourceBundle rb = ResourceBundle.getBundle("testBeanSelector");
        String controllerItems = rb.getString("controllerClass");
        String serviceImplItems = rb.getString("serviceImplClass");
        String[] controllerArr = controllerItems.split(",");
        String[] serviceImplArr = serviceImplItems.split(",");
        return mergeArrays(new String[0], serviceImplArr, controllerArr);
    }

    public String[] mergeArrays(String[] arr1, String[] arr2, String[] arr3) {
        String[] result = new String[arr1.length + arr2.length + arr3.length];
        System.arraycopy(arr1, 0, result, 0, arr1.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        System.arraycopy(arr3, 0, result, arr1.length + arr2.length, arr3.length);
        return result;
    }

    @Override
    public Predicate<String> getExclusionFilter() {
        return ImportSelector.super.getExclusionFilter();
    }
}