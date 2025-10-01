package com.weatherapp.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a managed server instance in the dashboard.
 */
public class ServerEntry {
    private final String id;
    private final ServerManager manager;
    private int requestedPort;
    private Instant startedAt;
    private int clientCount = 0;
    private final List<RequestLogEntry> requestLogs = new ArrayList<>();
    private final List<ServerUser> users = new ArrayList<>();

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

    public synchronized void logRequest(String city) {
        requestLogs.add(new RequestLogEntry(Instant.now(), city));
        if (requestLogs.size() > 5000) requestLogs.remove(0);
    }

    public synchronized List<RequestLogEntry> getRequestLogs() {
        return Collections.unmodifiableList(requestLogs);
    }

    public synchronized List<ServerUser> getUsers() {
        return Collections.unmodifiableList(users);
    }

    public synchronized void addUser(ServerUser user) { if (user != null) users.add(user); }
    public synchronized void removeUser(String username) { users.removeIf(u -> u.getUsername().equalsIgnoreCase(username)); }
}
