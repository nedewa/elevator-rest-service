package com.example.elevatorrestservice.model;

public class Passenger {

	private String user; // User ID
	private int startFloor;
	private int endFloor;

	private PassengerRequest getInRequest;
	private PassengerRequest getOutRequest;

	public Passenger() {
	}

	public Passenger(PassengerRequest getInRequest, PassengerRequest getOutRequest, String user) {
		this.getInRequest = getInRequest;
		this.getOutRequest = getOutRequest;
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getStartFloor() {
		return startFloor;
	}

	public void setStartFloor(int startFloor) {
		this.startFloor = startFloor;
	}

	public int getEndFloor() {
		return endFloor;
	}

	public void setEndFloor(int endFloor) {
		this.endFloor = endFloor;
	}

	public PassengerRequest getInRequest() {
		return getInRequest;
	}

	public PassengerRequest getOutRequest() {
		return getOutRequest;
	}

}
