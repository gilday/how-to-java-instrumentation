package com.github.gilday.junit;

/**
 * A class with a no-args ctor that creates a {@link ServletContainerExecutionMetadata}
 */
public interface ServletContainerExecutionMetadataProvider {
    ServletContainerExecutionMetadata get();
}
