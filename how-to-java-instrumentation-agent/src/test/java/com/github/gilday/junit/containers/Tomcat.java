package com.github.gilday.junit.containers;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Provides {@link ServletContainerExecutionMetadata} instances for Tomcat container images
 */
class Tomcat {

    /**
     * @return a cartesian product of tomcat images for JREs 7, 8 and Tomcat versions 7, 8, plus Tomcat 9 on JRE 8. Omit
     * Tomcat 6 on Java 6 because Java 6 provides no means for locking down the roaming RMI port; therefore, it is
     * impossible to know which ports to open when instantiating the container, so we cannot use JMX on Java 6 in
     * docker. http://hirt.se/blog/?p=289
     */
    static Stream<ServletContainerExecutionMetadata> allJMX() {
        return Stream.concat(
            IntStream.of(7, 8)
                .boxed()
                .flatMap(jre ->
                    IntStream.of(7, 8)
                        .mapToObj(String::valueOf)
                        .map(version -> tomcat(jre, version))
                ),
            Stream.of(
                tomcat(9, "8")
            )
        );
    }

    static Stream<ServletContainerExecutionMetadata> java6() {
        return Stream.of(
            ServletContainerExecutionMetadata.builder()
                .name("JRE 6 - Tomcat 6")
                .image("andreptb/tomcat:6-jdk6")
                .port(8080)
                .build()
        );
    }

    /**
     * Factory for creating {@link ServletContainerExecutionMetadata} to describe <a href="https://hub.docker.com/_/tomcat/">tomcat
     * images</a>
     */
    static ServletContainerExecutionMetadata tomcat(final int jre, final String version) {
        return ServletContainerExecutionMetadata.builder()
            .name("JRE" + jre + " - Tomcat " + version)
            .image("tomcat:" + version + "-jre" + jre)
            .port(8080)
            .build();
    }
}
