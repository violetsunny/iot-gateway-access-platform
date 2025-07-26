package com.ennew.iot.gateway.web.excel.valid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelStrLengthValid {

    int length() default 0;

    String message() default "字段超长";

}
