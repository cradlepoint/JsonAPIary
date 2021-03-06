package com.cradlepoint.jsonapiary.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonApiType {

    /**
     * Override for "type" value, otherwise the class name will be serliazed
     * @return
     */
    String value() default "";

}
