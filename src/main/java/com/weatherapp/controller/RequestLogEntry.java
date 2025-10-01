package com.weatherapp.controller;

import java.time.Instant;

public class RequestLogEntry {
	private final Instant timestamp;
	private final String city;

	public RequestLogEntry(Instant timestamp, String city) {
		this.timestamp = timestamp;
		this.city = city;
	}

	public Instant getTimestamp() { return timestamp; }
	public String getCity() { return city; }
}


