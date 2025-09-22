package com.weatherapp.controller;

import java.time.Instant;

/**
 * Represents a managed server instance in the dashboard.
 */
public class ServerEntry {
    private final String id;
    private final ServerManager manager;
    private int requestedPort;
    private Instant startedAt;
    private int clientCount = 0;

    public ServerEntry(String id, ServerManager manager, int requestedPort) {
        this.id = id;
        this.manager = manager;
        this.requestedPort = requestedPort;
    }

    public String getId() {
        return id;
    }

    public ServerManager getManager() {
        return manager;
    }

    public int getRequestedPort() {
        return requestedPort;
    }

    public void setRequestedPort(int requestedPort) {
        this.requestedPort = requestedPort;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void markStarted() {
        this.startedAt = Instant.now();
    }

    public boolean isRunning() {
        return manager != null && manager.isRunning();
    }

    public int getClientCount() {
        return clientCount;
    }

    public void setClientCount(int clientCount) {
        this.clientCount = clientCount;
    }
}
