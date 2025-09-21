package com.weatherapp.dashboard;

import java.net.Socket;
import java.time.Instant;

/**
 * Represents a connected client session (optional, lightweight record).
 */
public class ClientSession {
    private final String id;
    private final Socket socket;
    private final Instant connectedAt;

    public ClientSession(String id, Socket socket) {
        this.id = id;
        this.socket = socket;
        this.connectedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }

    public Instant getConnectedAt() {
        return connectedAt;
    }
}
