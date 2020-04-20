package com.example.elevatorrestservice.model;

import java.util.concurrent.LinkedBlockingQueue;

public class Floor {

	LinkedBlockingQueue<Passenger> passengerRequests; // Holds passengers at this floor
	private int ID;

	public Floor(int ID) {
		this.passengerRequests = new LinkedBlockingQueue<>();
		this.ID = ID;
	}

	public LinkedBlockingQueue<Passenger> getPassengerRequests() {
		return passengerRequests;
	}

	public int getID() {
		return ID;
	}

	/**
	 * Create a Passenger object (simulating a passenger arriving at a floor and
	 * pressing a button). Generate Passenger ID.
	 *
	 * Randomly select the direction in which the passenger wants to go from the
	 * getInRequest. Randomly select the floor number for getInRequest. - Type 1
	 *
	 * Set the direction of the exitFloor to be the same as the direction of the
	 * getInRequest. Randomly select the floor number for exitFloor, but make sure
	 * the floor number is in the direction of the exitFloor. - Type 0
	 *
	 * Remember to assign passage number to the getInRequest and exitFloor. Assign
	 * Passenger ID to each call.
	 */
	public void generatePassenger(Passenger passenger) throws InterruptedException {
		
		Elevator.DIRECTION direction = passenger.getStartFloor() > passenger.getEndFloor() ? Elevator.DIRECTION.DOWN : Elevator.DIRECTION.UP;
		
		PassengerRequest getInRequest = new PassengerRequest(PassengerRequest.REQUEST_TYPE.GET_IN_REQUEST, passenger.getStartFloor(), direction, passenger.getUser());

		PassengerRequest getOutRequest = new PassengerRequest(PassengerRequest.REQUEST_TYPE.GET_OUT_REQUEST, passenger.getEndFloor(), direction, passenger.getUser());

		this.passengerRequests.put(new Passenger(getInRequest, getOutRequest, passenger.getUser())); // Create a Passenger object and add it to the queue.																					// passengers array
	}
}
