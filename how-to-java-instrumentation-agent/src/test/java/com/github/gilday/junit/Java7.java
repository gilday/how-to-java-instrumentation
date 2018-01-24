package com.github.gilday.junit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Servlet containers running on Java 7
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Java7 {

    /**
     * Tomcat 7 on Java 7
     */
    public static class Tomcat7 implements ServletContainerExecutionMetadataProvider {
        @Override
        public ServletContainerExecutionMetadata get() {
            return ServletContainerExecutionMetadata.builder()
                .name("JRE7 - Tomcat 7")
                .image("tomcat:7")
                .port(8080)
                .build();
        }
    }
}
