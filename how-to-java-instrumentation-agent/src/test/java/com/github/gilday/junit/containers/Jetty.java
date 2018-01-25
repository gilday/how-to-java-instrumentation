package com.github.gilday.junit.containers;


import java.util.stream.Stream;

class Jetty {

    /**
     * @return Jetty 9.2 on JRE 7, and Jetty 9.3 and 9.4 on JRE 8
     */
    static Stream<ServletContainerExecutionMetadata> all() {
        return Stream.concat(
            Stream.of(jetty(7, "9.2")),
            Stream.of("9.3", "9.4").map(version -> jetty(8, version))
        );
    }
    /**
     * Factory for creating {@link ServletContainerExecutionMetadata} to describe <a href="https://hub.docker.com/_/jetty/">jetty
     * images</a>
     */
    private static ServletContainerExecutionMetadata jetty(final int jre, final String version) {
        return ServletContainerExecutionMetadata.builder()
            .name("JRE" + jre + " - Jetty " + version)
            .image("jetty:" + version + "-jre" + jre)
            .port(8080)
            .javaOptsEnvVariableName("JAVA_OPTIONS")
            .build();
    }
}
