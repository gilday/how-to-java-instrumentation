package com.github.gilday;

import static net.bytebuddy.matcher.ElementMatchers.hasMethodName;
import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;
import static net.bytebuddy.matcher.ElementMatchers.nameEndsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

import com.github.gilday.context.RegisterRequestContextServletAdvice;
import com.github.gilday.stringcount.StringCounterAdvice;
import com.google.common.io.ByteStreams;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.pmw.tinylog.Logger;

/**
 * how-to-java-instrument agent entry point. First, appends the embedded bootstrap.jar to the JVM's bootstrap class
 * loader to make critical instrumentation singletons available to all classes in the JVM. These critical classes cannot
 * be loaded before they are injected into the bootstrap class loader, therefore none of these classes may be referenced
 * in this class; instead, these classes are referenced in {@link Initialization} which services as a class loading
 * buffer. Next, use Byte Buddy to instrument system and user classes with new behavior
 */
public class Agent {

    public static void premain(final String args, final Instrumentation instrumentation) {
        appendBootstrapJarToClassLoader(instrumentation);
        Initialization.initialize();
        instrumentClasses(instrumentation);
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
        final InputStream is = Agent.class.getResourceAsStream("/lib/bootstrap.jar");
        if (is == null) {
            throw new InitializationException("Failed to find /lib/bootstrap.jar on the agent classpath");
        }
        return new BufferedInputStream(is);
    }

    /**
     * Uses Byte Buddy to instrument String constructors with counters
     */
    private static void instrumentClasses(final Instrumentation instrumentation) {
        new AgentBuilder.Default()
            .ignore(none())
            .disableClassFormatChanges()
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
            .type(
                is(String.class)
            )
            .transform(new AgentBuilder.Transformer.ForAdvice()
                .include(StringCounterAdvice.class.getClassLoader())
                .advice(isConstructor(), StringCounterAdvice.class.getName())
            )
            .type(
                hasSuperType(named("javax.servlet.Servlet").and(nameEndsWith("Servlet")))
            )
            .transform(new AgentBuilder.Transformer.ForAdvice()
                .advice(hasMethodName("service"), RegisterRequestContextServletAdvice.class.getName())
            )
            .type(
                hasSuperType(named("org.eclipse.jetty.server.Handler"))
            )
            .transform(new AgentBuilder.Transformer.ForAdvice()
                .advice(hasMethodName("handle"), RegisterRequestContextServletAdvice.class.getName())
            )
            .installOn(instrumentation);
    }
}
