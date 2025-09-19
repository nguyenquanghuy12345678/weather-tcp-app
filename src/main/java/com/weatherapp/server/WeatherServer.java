package com.weatherapp.server;

import com.weatherapp.service.WeatherService;

public class WeatherServer {
    public static final int PORT = 5555;

    public static void main(String[] args) throws Exception {
        int port = 5555;
        if (args != null && args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (Exception ignored) {}
        }
        WeatherService service = new WeatherService();
        ServerManager mgr = new ServerManager(service);
        System.out.println("Starting WeatherServer via ServerManager on port " + (port == 0 ? "auto" : port));
        mgr.start(port);
        System.out.println("WeatherServer running on port " + mgr.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down WeatherServer...");
            mgr.stop();
        }));
        while (mgr.isRunning()) {
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
        System.out.println("WeatherServer stopped");
    }
}