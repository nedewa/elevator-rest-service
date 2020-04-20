package com.example.elevatorrestservice.model;

import java.util.ArrayList;
import java.util.List;

public class ElevatorState {
	private int id;
	private int floor;
	private List<String> users = new ArrayList<>();
	
	public ElevatorState(int id, int floor, List<String> users) {
		this.id = id;
		this.floor = floor;
		this.users = users;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}
}
