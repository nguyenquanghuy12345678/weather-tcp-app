package com.weatherapp.controller;
import com.weatherapp.service.WeatherService;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller to manage multiple server instances (start/stop/list) for the dashboard.
 */
public class ServerAdminController {
    private final Map<String, ServerEntry> servers = new LinkedHashMap<>();

    public synchronized ServerEntry createServer(int port) throws Exception {
        String id = UUID.randomUUID().toString();
        ServerManager mgr = new ServerManager(new WeatherService());
        // wire listeners to update entry state
        ServerEntry entry = new ServerEntry(id, mgr, port);
        mgr.addListener(new ServerEventListener() {
            @Override public void onClientConnected(int p, java.net.SocketAddress remote) {
                entry.setClientCount(Math.max(0, entry.getClientCount() + 1));
            }
            @Override public void onClientDisconnected(int p, java.net.SocketAddress remote) {
                entry.setClientCount(Math.max(0, entry.getClientCount() - 1));
            }
            @Override public void onRequest(int p, String city) { entry.logRequest(city); }
        });
        // start might auto-select when port==0
        mgr.start(port);
        entry.markStarted();
        servers.put(id, entry);
        return entry;
    }

    public synchronized void stopServer(String id) {
        ServerEntry e = servers.get(id);
        if (e != null) {
            e.getManager().stop();
        }
    }

    public synchronized void removeServer(String id) {
        stopServer(id);
        servers.remove(id);
    }

    public synchronized Map<String, ServerEntry> listServers() {
        return Collections.unmodifiableMap(servers);
    }

    public synchronized Optional<ServerEntry> getServer(String id) {
        return Optional.ofNullable(servers.get(id));
    }

    // CRUD-like helpers for users
    public synchronized void addUser(String serverId, String username, String role) {
        ServerEntry e = servers.get(serverId);
        if (e != null) e.addUser(new ServerUser(username, role));
    }

    public synchronized void removeUser(String serverId, String username) {
        ServerEntry e = servers.get(serverId);
        if (e != null) e.removeUser(username);
    }

    public synchronized Map<String, ServerEntry> findByPort(int port) {
        Map<String, ServerEntry> res = new LinkedHashMap<>();
        for (Map.Entry<String, ServerEntry> en : servers.entrySet()) {
            ServerEntry se = en.getValue();
            try { if (se.getManager().getPort() == port) res.put(en.getKey(), se); } catch (Exception ignored) {}
        }
        return Collections.unmodifiableMap(res);
    }
}
