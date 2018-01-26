package com.github.gilday.junit.containers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import com.google.auto.value.AutoValue;

/**
 * Value object which contains the endpoint of an instrumented web application
 */
@AutoValue
public abstract class Endpoint {

    public static Endpoint of(final InetAddress host, final int port) {
        return new AutoValue_Endpoint(host, port);
    }

    public abstract InetAddress host();
    public abstract int port();

    public CompletableFuture<Void> pollForConnection() {
        return CompletableFuture.runAsync(() -> {
            while (!isListening()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private boolean isListening() {
        try (final Socket ignored = new Socket(host(), port())) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
