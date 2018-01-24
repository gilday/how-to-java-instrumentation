package com.github.gilday.junit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Servlet containers running on Java 8
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Java8 {

    /**
     * Tomcat 7 on Java 8
     */
    public static class Tomcat7 implements ServletContainerExecutionMetadataProvider {
        @Override
        public ServletContainerExecutionMetadata get() {
            return ServletContainerExecutionMetadata.builder()
                .name("JRE8 - Tomcat 7")
                .image("tomcat:7-jre8")
                .port(8080)
                .build();
        }
    }

    /**
     * Tomcat 8 on Java 8
     */
    public static class Tomcat8 implements ServletContainerExecutionMetadataProvider {
        @Override
        public ServletContainerExecutionMetadata get() {
            return ServletContainerExecutionMetadata.builder()
                .name("JRE8 - Tomcat 8")
                .image("tomcat:8-jre8")
                .port(8080)
                .build();
        }
    }

    /**
     * Tomcat 9 on Java 8
     */
    public static class Tomcat9 implements ServletContainerExecutionMetadataProvider {
        @Override
        public ServletContainerExecutionMetadata get() {
            return ServletContainerExecutionMetadata.builder()
                .name("JRE8 - Tomcat 9")
                .image("tomcat:9-jre8")
                .port(8080)
                .build();
        }
    }

    /**
     * Jetty 9.4 on Java 8
     */
    public static class Jetty9_4 implements ServletContainerExecutionMetadataProvider {
        @Override
        public ServletContainerExecutionMetadata get() {
            return ServletContainerExecutionMetadata.builder()
                .name("JRE8 - Jetty 9.4")
                .image("jetty:9.4-jre8")
                .port(8080)
                .javaOptsEnvVariableName("JAVA_OPTIONS")
                .build();
        }
    }

}
