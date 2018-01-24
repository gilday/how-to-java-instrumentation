package com.github.gilday.bootstrap.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * type safe key for heterogeneous context containers. Type parameter {@link T} is unused, but present to enforce type
 * safety when Symbol is used as a type safe key
 */
@SuppressWarnings("unused")
@Accessors(fluent = true)
@Getter
@RequiredArgsConstructor(staticName = "of")
@ToString
public class Symbol<T> {
    private final String name;
}
