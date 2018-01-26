package com.github.gilday.bootstrap.context;

/**
 * type safe key for heterogeneous context containers. Type parameter {@link T} is unused, but present to enforce type
 * safety when Symbol is used as a type safe key
 */
@SuppressWarnings("unused")
public class Symbol<T> {

    public static <T>Symbol<T> of(final String name) { return new Symbol<>(name); }

    private final String name;

    private Symbol(final String name) { this.name = name; }

    @Override
    public String toString() { return "Symbol(" + name + ")"; }
}
