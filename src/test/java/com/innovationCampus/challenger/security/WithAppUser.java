package com.innovationCampus.challenger.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAppUserSecurityContextFactory.class)
public @interface WithAppUser {

    String email() default "test@challenger.com";

    String username() default "testuser";

    String tag() default "test_tag";

    String password() default "password";

    long id() default 1L;
}
