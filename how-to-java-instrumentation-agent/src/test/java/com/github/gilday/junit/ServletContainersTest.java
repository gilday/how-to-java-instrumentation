package com.github.gilday.junit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestTemplate;

/**
 * {@link ServletContainersTest} is used to signal that the annotated method is a test template that should be called
 * with connections to a set of servlet containers that have been instrumented with the how-to-java-agent. The external
 * servlet containers are executed in docker containers
 */
@Target({TYPE,METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TestTemplate
public @interface ServletContainersTest {

    Class<? extends ServletContainerExecutionMetadataProvider> value();

    /**
     * A test method {@link String} parameter decorated with {@link Context} will resolve to a java web application
     * context path as defined by {@link ServletContainerExecutionMetadata#context()}
     */
    @Target(PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Context { }
}
