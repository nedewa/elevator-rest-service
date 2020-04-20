package com.example.elevatorrestservice.model;

public class PassengerRequest {

	public static enum REQUEST_TYPE {
		GET_OUT_REQUEST, // 0
		GET_IN_REQUEST; // 1
	}

	public static final int PASSAGE_SAME_DIRECTION_HIGHER = 1; // Same direction and higher than currentFloor (P1)
	public static final int PASSAGE_OPPOSITE_DIRECTION = 2; // Opposite direction (P2)
	public static final int PASSAGE_SAME_DIRECTION_LOWER = 3; // Same direction and lower than currentFloor (P3)

	private REQUEST_TYPE type;
	private int passage;
	private int floor; // Start from 0, 0 means the first floor.
	private Elevator.DIRECTION direction;
	private String ID;
	private boolean specialRequest; // The flag of upPeakRequest - requests in the high throughput

	public PassengerRequest(REQUEST_TYPE type, int floor, Elevator.DIRECTION direction, String ID) {
		this.type = type;
		this.floor = floor;
		this.direction = direction;
		this.ID = ID;
		this.specialRequest = false;
	}

	public void setPassage(int passage) {
		this.passage = passage;
	}

	public int getPassage() {
		return passage;
	}

	public REQUEST_TYPE getType() {
		return type;
	}

	public int getFloor() {
		return floor;
	}

	public Elevator.DIRECTION getDirection() {
		return direction;
	}

	public String getID() {
		return ID;
	}

	public void setSpecialRequest(boolean specialRequest) {
		this.specialRequest = specialRequest;
	}

	public boolean isSpecialRequest() {
		return specialRequest;
	}
}
