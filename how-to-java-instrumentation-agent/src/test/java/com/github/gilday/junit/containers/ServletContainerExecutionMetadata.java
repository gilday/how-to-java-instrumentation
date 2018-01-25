package com.github.gilday.junit.containers;

import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Value object which contains metadata for launching a servlet container
 */
@Accessors(fluent = true)
@Builder
@Value
public class ServletContainerExecutionMetadata {

    /**
     * friendly name for reporting
     */
    private final String name;

    /**
     * docker image
     */
    private final String image;

    /**
     * docker entrypoint
     */
    private final ImmutableList<String> entrypoint;

    /**
     * docker CMD
     */
    private final ImmutableList<String> cmd;

    /**
     * HTTP port
     */
    private final int port;

    /**
     * java web application context path
     */
    private final String context;

    /**
     * Container images which use a java executable as the entry point should include the contents of the JAVA_OPTS
     * environment variable in the execution to java. Other container images that use a bash script as an entry point
     * will reference the JAVA_OPTS environment variable
     */
    private final boolean prependJavaOptsToCmd;

    /**
     * environment variable name for java opts configuration variable
     */
    private final String javaOptsEnvVariableName;

    private ServletContainerExecutionMetadata(
        @NonNull final String name,
        @NonNull final String image,
        final ImmutableList<String> entrypoint,
        final ImmutableList<String> cmd,
        final int port,
        @Nullable final String context,
        final boolean prependJavaOptsToCmd,
        @Nullable final String javaOptsEnvVariableName
    ) {
        Preconditions.checkArgument(port > 0, "port must be greater than 0");
        this.name = name;
        this.image = image;
        this.entrypoint = coalesceToEmpty(entrypoint);
        this.cmd = coalesceToEmpty(cmd);
        this.port = port;
        this.context = context == null ? "" : context;
        this.prependJavaOptsToCmd = prependJavaOptsToCmd;
        this.javaOptsEnvVariableName = Optional.ofNullable(javaOptsEnvVariableName).orElse("JAVA_OPTS");
    }

    private static <T> ImmutableList<T> coalesceToEmpty(@Nullable final ImmutableList<T> list) {
        return Optional.ofNullable(list).orElse(ImmutableList.of());
    }
}
