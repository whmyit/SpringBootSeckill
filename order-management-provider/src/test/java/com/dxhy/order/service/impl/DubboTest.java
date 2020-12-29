package com.dxhy.order.service.impl;

import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.context.BootstrapWith;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@BootstrapWith(SpringBootTestContextBootstrapper.class)
public @interface DubboTest {

    Class<?>[] value();
}
