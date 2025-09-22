package com.weatherapp.dashboard;

import com.weatherapp.controller.ServerEntry;
import com.weatherapp.controller.ServerManager;
import com.weatherapp.service.WeatherService;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Simple manager that keeps track of multiple ServerManager instances.
 */
public class ServerAdminController {
    private final Map<String, ServerEntry> servers = new LinkedHashMap<>();

    public synchronized ServerEntry createServer(int port) throws Exception {
        String id = UUID.randomUUID().toString();
        ServerManager mgr = new ServerManager(new WeatherService());
        // start might auto-select when port==0
        mgr.start(port);
        ServerEntry entry = new ServerEntry(id, mgr, port);
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
}
