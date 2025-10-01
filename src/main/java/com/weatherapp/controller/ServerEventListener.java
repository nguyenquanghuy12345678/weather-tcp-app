package com.weatherapp.controller;

import java.net.SocketAddress;

public interface ServerEventListener {
	default void onClientConnected(int port, SocketAddress remote) {}
	default void onClientDisconnected(int port, SocketAddress remote) {}
	default void onRequest(int port, String city) {}
}


