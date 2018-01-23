package com.github.gilday;

import static net.bytebuddy.matcher.ElementMatchers.hasMethodName;
import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;
import static net.bytebuddy.matcher.ElementMatchers.isSubTypeOf;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarFile;

import javax.servlet.Servlet;

import com.github.gilday.context.RegisterRequestContextServletAdvice;
import com.github.gilday.stringcount.StringCounterAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
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
        try(final InputStream is = Agent.class.getResourceAsStream("/lib/bootstrap.jar")) {
            if (is == null) {
                throw new InitializationException("Failed to find /bootstrap.jar on the agent classpath");
            }
            final File file = File.createTempFile("bootstrap", "jar");
            Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            final JarFile jarfile = new JarFile(file);
            instrumentation.appendToBootstrapClassLoaderSearch(jarfile);
        } catch (IOException e) {
            throw new InitializationException("Failed to append bootstrap.jar to the JVM bootstrap classloader", e);
        }
    }

    /**
     * Uses Byte Buddy to instrument String constructors with counters
     */
    private static void instrumentClasses(final Instrumentation instrumentation) {
        new AgentBuilder.Default()
            .ignore(none())
            .disableClassFormatChanges()
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
            .type(is(String.class))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                .include(StringCounterAdvice.class.getClassLoader())
                .advice(isConstructor(), StringCounterAdvice.class.getName())
            )
            .type(hasSuperType(named("javax.servlet.Servlet")))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                .include(RegisterRequestContextServletAdvice.class.getClassLoader())
                .advice(hasMethodName("service"), RegisterRequestContextServletAdvice.class.getName())
            )
            .installOn(instrumentation);
    }
}
