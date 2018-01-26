package com.github.gilday;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

import com.google.common.io.ByteStreams;
import org.pmw.tinylog.Logger;

/**
 * how-to-java-instrument agent entry point. First, appends the embedded bootstrap.jar to the JVM's bootstrap class
 * loader to make critical instrumentation singletons available to all classes in the JVM. These critical classes cannot
 * be loaded before they are injected into the bootstrap class loader, therefore none of these classes may be referenced
 * in this class; instead, these classes are initialized in the {@link Agent}
 */
public class Main {

    public static void premain(final String args, final Instrumentation instrumentation) {
        appendBootstrapJarToClassLoader(instrumentation);
        Agent.run(instrumentation);
        Logger.info("how-to-java-instrumentation loaded");
    }

    /**
     * copies the bootstrap.jar from the classpath to the file system then adds it to the Bootstrap bootloader
     * classpath. This makes critical instrumentation singletons available to all class loaders in the JVM
     */
    private static void appendBootstrapJarToClassLoader(final Instrumentation instrumentation) {
        final File file;
        try {
            file = File.createTempFile("bootstrap", "jar");
        } catch (IOException e) {
            throw new InitializationException("Failed to create temporary file bootstrap.jar");
        }
        try(final BufferedInputStream is = readEmbeddedBootstrapJarOrDie();
            final FileOutputStream fos = new FileOutputStream(file)
        ) {
            ByteStreams.copy(is, fos);
        } catch (IOException e) {
            throw new InitializationException("Failed to append bootstrap.jar to the JVM bootstrap classloader", e);
        }
        final JarFile jarfile;
        try {
            jarfile = new JarFile(file);
        } catch (IOException e) {
            throw new InitializationException("Failed to read bootstrap.jar file from disk", e);
        }
        instrumentation.appendToBootstrapClassLoaderSearch(jarfile);
    }

    /**
     * @return a {@link BufferedInputStream} for the embedded bootstrap.jar
     */
    private static BufferedInputStream readEmbeddedBootstrapJarOrDie() {
        final InputStream is = Main.class.getResourceAsStream("/lib/bootstrap.jar");
        if (is == null) {
            throw new InitializationException("Failed to find /lib/bootstrap.jar on the agent classpath");
        }
        return new BufferedInputStream(is);
    }
}
