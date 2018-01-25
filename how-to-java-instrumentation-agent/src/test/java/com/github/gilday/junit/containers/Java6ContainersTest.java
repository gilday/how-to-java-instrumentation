package com.github.gilday.junit.containers;

import static com.github.gilday.junit.containers.Tomcat.java6;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.stream.Stream;

/**
 * {@link ServletContainersTest} meta-annotation for test templates which execute tests against Java 6 containers
 */
@Target({TYPE,METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ServletContainersTest(value = Java6ContainersTest.ContainersProvider.class, jmx = false)
public @interface Java6ContainersTest {

    class ContainersProvider implements ServletContainerExecutionMetadataProvider {
        @Override
        public Stream<ServletContainerExecutionMetadata> get() { return java6(); }
    }
}
