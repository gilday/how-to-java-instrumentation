package com.github.gilday.junit.containers;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

/**
 * Value object which contains metadata for launching a servlet container
 */
@AutoValue
public abstract class ServletContainerExecutionMetadata {

    /**
     * friendly name for reporting
     */
    public abstract String name();

    /**
     * docker image
     */
    public abstract String image();

    /**
     * docker entrypoint
     */
    public abstract ImmutableList<String> entrypoint();

    /**
     * docker CMD
     */
    public abstract ImmutableList<String> cmd();

    /**
     * HTTP port
     */
    public abstract int port();

    /**
     * java web application context path
     */
    public abstract String context();

    /**
     * Container images which use a java executable as the entry point should include the contents of the JAVA_OPTS
     * environment variable in the execution to java. Other container images that use a bash script as an entry point
     * will reference the JAVA_OPTS environment variable
     */
    public abstract boolean prependJavaOptsToCmd();

    /**
     * environment variable name for java opts configuration variable
     */
    public abstract String javaOptsEnvVariableName();

    public static Builder builder() {
        return new AutoValue_ServletContainerExecutionMetadata.Builder()
            .entrypoint(ImmutableList.of())
            .cmd(ImmutableList.of())
            .context("")
            .prependJavaOptsToCmd(false)
            .javaOptsEnvVariableName("JAVA_OPTS");
    }

    @AutoValue.Builder
    public static abstract class Builder {
        abstract Builder name(String value);
        abstract Builder image(String value);
        abstract Builder entrypoint(ImmutableList<String> value);
        abstract Builder cmd(ImmutableList<String> value);
        abstract Builder port(int value);
        abstract Builder context(String value);
        abstract Builder prependJavaOptsToCmd(boolean value);
        abstract Builder javaOptsEnvVariableName(String value);
        abstract ServletContainerExecutionMetadata build();
    }
}
