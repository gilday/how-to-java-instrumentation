package com.github.gilday.junit.containers;

import java.util.stream.Stream;

/**
 * A necessarily round-about way of identifying a {@link Stream} of {@link ServletContainerExecutionMetadata} to be used
 * in a {@link ServletContainersTest}
 */
public interface ServletContainerExecutionMetadataProvider {
     Stream<ServletContainerExecutionMetadata> get();
}
