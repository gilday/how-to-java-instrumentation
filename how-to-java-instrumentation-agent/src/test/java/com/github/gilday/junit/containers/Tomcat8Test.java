package com.github.gilday.junit.containers;

import static com.github.gilday.junit.containers.Tomcat.tomcat;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.stream.Stream;

@Target({TYPE,METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ServletContainersTest(Tomcat8Test.ContainersProvider.class)
public @interface Tomcat8Test {

    /**
     * {@link ServletContainerExecutionMetadataProvider} which provides all Java 8 servlet containers
     */
    class ContainersProvider implements ServletContainerExecutionMetadataProvider {
        @Override
        public Stream<ServletContainerExecutionMetadata> get() {
            return Stream.of(tomcat(8, "8"));
        }
    }
}
