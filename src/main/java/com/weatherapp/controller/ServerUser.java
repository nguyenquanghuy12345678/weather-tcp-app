package com.weatherapp.controller;

public class ServerUser {
	private final String username;
	private String role;

	public ServerUser(String username, String role) {
		this.username = username;
		this.role = role == null ? "user" : role;
	}

	public String getUsername() { return username; }
	public String getRole() { return role; }
	public void setRole(String role) { this.role = role; }
}


