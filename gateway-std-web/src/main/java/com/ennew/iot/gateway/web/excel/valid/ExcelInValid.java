package com.ennew.iot.gateway.web.excel.valid;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExcelInValid {

    private ExcelInValid() {
    }

    /**
     * Excel导入字段校验
     *
     * @param object 校验的JavaBean 其属性须有自定义注解
     * @author linmaosheng
     */
    public static String valid(Object object) {
        List<String> errorMessages = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            //属性的值
            Object fieldValue = null;
            try {
                fieldValue = field.get(object);
            } catch (IllegalAccessException e) {
                errorMessages.add("数据异常");
            }

            //是否包含必填校验注解
            boolean isExcelValid = field.isAnnotationPresent(ExcelNotNullValid.class);
            if (isExcelValid && Objects.isNull(fieldValue)) {
                errorMessages.add(field.getAnnotation(ExcelNotNullValid.class).message());
            }

            //是否包含字符串长度校验注解
            boolean isExcelStrValid = field.isAnnotationPresent(ExcelStrLengthValid.class);
            if (isExcelStrValid && fieldValue != null) {
                String cellStr = fieldValue.toString();
                int length = field.getAnnotation(ExcelStrLengthValid.class).length();
                if (StringUtils.isNotBlank(cellStr) && cellStr.length() > length) {
                    errorMessages.add(field.getAnnotation(ExcelNotNullValid.class).message());
                }
            }

            //是否包含正则校验注解
            boolean isExcelPatternValid = field.isAnnotationPresent(ExcelPatternValid.class);
            if (isExcelPatternValid && fieldValue != null) {
                String cellStr = fieldValue.toString();
                String regexp = field.getAnnotation(ExcelPatternValid.class).regexp();
                if (StringUtils.isNotBlank(cellStr) && !cellStr.matches(regexp)) {
                    errorMessages.add(field.getAnnotation(ExcelPatternValid.class).message());
                }
            }

        }
        if (CollectionUtil.isNotEmpty(errorMessages)) {
            return String.join(",", errorMessages);
        }
        return "";
    }

}
