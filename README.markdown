# how-to-java-instrumentation

Reference project for using Java instrumentation techniques to build a Java
agent. Inspired by Contrast Security's [Join the Team
Challenge](https://github.com/Contrast-Security-OSS/join-the-team/blob/master/challenges.md),
this Java agent measures String allocations; it counts both Strings allocated
since the start of the JVM, and Strings allocated in the context of a web
request.

The agent exposes its String allocation metrics via JMX. Use JConsole to see
the system-wide String allocations, and browse the set of Strings allocated per
web request records.

![jconsole](./media/jconsole.png)


# Prequisites

* JDK 8 and Maven ☕️
* Docker 🐳

The JUnit tests expect that the user running the build has access to a local
docker daemon without the need for privilege escalation.


# Building

Build the agent and run full suite of tests. Note: the first run may take
significantly long if it needs to pull missing docker images for tests

    mvn install


# Usage

Configure the JVM to use the agent using command line flag `javaagent`. See
`java` usage. Use a JVM client like `jconsole` to browse the agent's metrics.

Try it out with Docker and jconsole. The following, executed from the project
root after a successful build (`mvn install`), launches a docker container with
an instrumented Tomcat JVM which exposes a JMX server on port 9010 and a web
server on port 8080. Note: the cumbersome JMX system properties are necessary to
allow a local JMX client to connect to the JMX server running in the docker
container

    docker run --rm -p 9010:9010 -p 8080:8080 -e 'JAVA_OPTS=-javaagent:/agent.jar -Dcom.sun.management.jmxremote.host=0.0.0.0 -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.rmi.port=9010 -Djava.rmi.server.hostname=0.0.0.0' -v $(pwd)/how-to-java-instrumentation-agent/target/how-to-java-instrumentation-agent-1.0-SNAPSHOT.jar:/agent.jar:ro tomcat:9-jre8

Then launch the jconsole GUI and connect to the JMX server at `localhost:9010`

    jconsole &


# Design

Some noteworthy design aspects


## Testing Framework

In addition to the usual Java unit test tools JUnit, AssertJ, and Mockito, this
project includes a custom JUnit Platform Extension for writing integration tests
which interact with an instrumented JVM running in a Docker container. The JUnit
Platform Extension uses the
[docker-java](https://github.com/docker-java/docker-java) API client to launch
new containers before a test, provides connection parameters for the web and JMX
ports exposed by the container to the test method, and cleans up the container
after the test concludes. The result is a single JUnit test method which is
repeated for any number of application servers with which the agent should be
tested against.

```java
@Java8ContainersTest
void it_records_per_request_string_allocation_count(final Endpoint endpoint, @ServletContainersTest.Context final String context, final MBeanServerConnection mBeanServerConnection) throws InterruptedException {
    // GIVEN a server that has not yet served any requests
    final StringsAllocatedGaugeMXBean stringsAllocatedGaugeMXBean = JMX.newMXBeanProxy(mBeanServerConnection, StringsAllocatedGauge.name(), StringsAllocatedGaugeMXBean.class);
    assumeTrue(stringsAllocatedGaugeMXBean.requests().length == 0);
    sleep(4000); // sleep a bit to make sure the web server is ready to serve requests

    // WHEN make an HTTP request to the server
    final HttpUrl url = new HttpUrl.Builder()
        .scheme("http")
        .host(endpoint.host().getHostName())
        .port(endpoint.port())
        .addPathSegment(context)
        .build();
    httpGET(url);

    // AND sleep a bit to make sure the server registers the count after serving the response
    sleep(200);

    // THEN server records a strings allocation count for the request
    final StringsAllocatedBean[] requests = stringsAllocatedGaugeMXBean.requests();
    assertThat(requests).hasSize(1);
    // AND surely some strings were allocated to serve the requests
    assertThat(requests).allMatch(r -> r.getCount() > 0);
}
```

![junit test run against multiple app servers](./media/junit-tests.png)


## Byte Buddy

The excellent [Byte Buddy](http://bytebuddy.net) library does the heavy lifting
for instrumeting users' bytecode. Here is how the agent uses the Byte Buddy
`AgentBuilder` to instrument java classes with new functionality

```java
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
```


# TODO

* Refactor dependency wiring code to use Dagger
* Investigate using Retrolambda, various backport libraries, and Animal Sniffer
  to migrate the agent code to support Java 6, 7, 8
* Investigate JMX limitations that prevent the container's JMX port from being a
  different port than the host port to which it is bound (this limitation
  prevents the containerized tests from running in parallel)
* Investigate migrating to sbt (or gradle)
