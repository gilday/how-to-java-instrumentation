package com.github.gilday.junit.containers;

import static com.github.gilday.junit.containers.Java8.jettys;
import static com.github.gilday.junit.containers.Java8.tomcats;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.stream.Stream;

/**
 * {@link ServletContainersTest} meta-annotation for test templates which execute tests against {@link Java8} servlet
 * containers. {@see ServletContainersTest}
 */
@Target({TYPE,METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ServletContainersTest(Java8ContainersTest.ContainersProvider.class)
public @interface Java8ContainersTest {

    /**
     * {@link ServletContainerExecutionMetadataProvider} which provides all Java 8 servlet containers
     */
    class ContainersProvider implements ServletContainerExecutionMetadataProvider {
        @Override
        public Stream<ServletContainerExecutionMetadata> get() {
            return Stream.concat(
                jettys(),
                tomcats()
            );
        }
    }
}
