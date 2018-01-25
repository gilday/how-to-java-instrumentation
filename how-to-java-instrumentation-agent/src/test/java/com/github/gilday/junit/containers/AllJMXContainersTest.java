package com.github.gilday.junit.containers;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.stream.Stream;

/**
 * {@link ServletContainersTest} meta-annotation for test templates which execute tests against {@link Jetty} and {@link
 * Tomcat} servlet containers. {@see ServletContainersTest}
 */
@Target({TYPE,METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ServletContainersTest(AllJMXContainersTest.ContainersProvider.class)
public @interface AllJMXContainersTest {

    class ContainersProvider implements ServletContainerExecutionMetadataProvider {
        @Override
        public Stream<ServletContainerExecutionMetadata> get() {
            return Stream.concat(
                Jetty.all(),
                Tomcat.allJMX()
            );
        }
    }
}
