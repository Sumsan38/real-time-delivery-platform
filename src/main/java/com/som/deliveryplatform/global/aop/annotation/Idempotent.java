package com.som.deliveryplatform.global.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})     // 메서드에만 적용
@Retention(RetentionPolicy.RUNTIME) // 런타임 시점에 동작
//@Documented // 문서화 시 포함
public @interface Idempotent {
}
