package com.github.gilday.junit;

import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Servlet containers running on Java 8
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Java8 {

    public static Stream<ServletContainerExecutionMetadata> jettys() { return Stream.of("9.3", "9.4").map(Java8::jetty); }

    public static Stream<ServletContainerExecutionMetadata> jetty9_3() { return Stream.of(jetty("9.3")); }

    public static Stream<ServletContainerExecutionMetadata> jetty9_4() { return Stream.of(jetty("9.4")); }

    private static ServletContainerExecutionMetadata jetty(final String version) {
        return ServletContainerExecutionMetadata.builder()
            .name("JRE8 - Jetty " + version)
            .image("jetty:" + version + "-jre8")
            .port(8080)
            .javaOptsEnvVariableName("JAVA_OPTIONS")
            .build();
    }

    public static Stream<ServletContainerExecutionMetadata> tomcats() { return Stream.of("7", "8", "9").map(Java8::tomcat); }

    public static Stream<ServletContainerExecutionMetadata> tomcat7() { return Stream.of(tomcat("7")); }

    public static Stream<ServletContainerExecutionMetadata> tomcat8() { return Stream.of(tomcat("8")); }

    public static Stream<ServletContainerExecutionMetadata> tomcat9() { return Stream.of(tomcat("9")); }

    private static ServletContainerExecutionMetadata tomcat(final String version) {
        return ServletContainerExecutionMetadata.builder()
            .name("JRE8 - Tomcat " + version)
            .image("tomcat:" + version + "-jre8")
            .port(8080)
            .build();
    }
}
