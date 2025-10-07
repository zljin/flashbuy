package com.zljin.flashbuy.model;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PermitAdmit {

    String name() default "";

    boolean check() default true;

}
