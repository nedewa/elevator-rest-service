package com.example.elevatorrestservice.model;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;

public class Elevator {

	public static enum DIRECTION {
		UP, DOWN;
	}

	public static final int MAX_FLOOR = 11;
	public static final int CAPACITY = 20;
	public static final int STOP_TIME_UNIT = 1; // 1 time unit, stop for passenger get in/out
	public static final int SPEED = 1; // 1 time unit per floor
	public static final double ENERGY_UP = 0.6; 
	public static final double ENERGY_DOWN = 0.4; 

	private List<PassengerRequest> getInRequests; // Holds get in requests
	private List<PassengerRequest> getOutRequests; // Holds get out requests
	private PriorityBlockingQueue<PassengerRequest> sequence;

	private int ID; // ID start from 1
	private int currentFloor;
	private DIRECTION direction; // 0 - Down, 1- Up
	private boolean idle = true;

	public Elevator(int ID) {
		this.ID = ID + 1; // ID start from 1
		this.getInRequests = new CopyOnWriteArrayList<>();
		this.getOutRequests = new CopyOnWriteArrayList<>();
		this.sequence = new PriorityBlockingQueue<>(100, new Comparator<PassengerRequest>() {
			/**
			 * Sorts requests based on passage and floor number
			 */
			@Override
			public int compare(PassengerRequest x, PassengerRequest y) {

				// -1 The element pointed by x goes before the element pointed by y
				// 0 The element pointed by x is equivalent to the element pointed by y
				// 1 The element pointed by x goes after the element pointed by y
				// If x or y are up-peak requests
				if (x.isSpecialRequest() || y.isSpecialRequest()) {
					if (x.isSpecialRequest() && !y.isSpecialRequest()) {
						return -1;
					} else if (!x.isSpecialRequest() && y.isSpecialRequest()) {
						return 1;
					} else {
						return 0;
					}
				}

				if (x.getPassage() == y.getPassage()) {

					if ((x.getPassage() == 1) || (x.getPassage() == 3)) {

						if (x.getFloor() < y.getFloor()) {
							return -1;
						} else if (x.getFloor() > y.getFloor()) {
							return 1;
						}

						return 0;

					} else if (x.getPassage() == 2) {

						if (x.getFloor() > y.getFloor()) {
							return -1;
						} else if (x.getFloor() < y.getFloor()) {
							return 1;
						}

						return 0;

					}

				} else if (x.getPassage() > y.getPassage()) {
					return 1;
				}

				return -1;
			}
		});
	}

	public void startElevatorWorker() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						elevatorWorker();
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						System.out.println("Error in performJob thread.");
					}
				}
			}
		}).start();
	}

	public void startRequestMonitor() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {
					try {
						requestToQueue();
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						System.out.println("Error in elevatorController thread.");
					}
				}
			}
		}).start();
	}

	/**
	 * Checks the sequence queue to find any Requests that need to be removed or
	 * added.
	 */
	private void checkSequence(PassengerRequest tempRequest) throws InterruptedException {

		// Here we are looking for the getOutRequest of the current getInRequest
		if (tempRequest.getType() == PassengerRequest.REQUEST_TYPE.GET_IN_REQUEST
				&& tempRequest.getFloor() == this.currentFloor) {

			int removeIndex = 0;
			boolean foundGetOutRequest = false;

			// Traverse getOutFloors array to look for a
			// getOutRequest with the same ID as tempRequest
			for (int i = 0; i < this.getOutRequests.size(); ++i) {

				PassengerRequest tempGetOutRequest = this.getOutRequests.get(i);

				if (tempGetOutRequest.getID().equals(tempRequest.getID())) {

					removeIndex = i;

					// Assign passage to getOutRequest
					// Same direction and higher than currentFloor - P1
					// Opposite direction - P2

					if (this.direction == DIRECTION.UP) {
						if ((tempGetOutRequest.getFloor() > this.currentFloor)
								&& (tempGetOutRequest.getDirection() == this.direction)) {
							tempGetOutRequest.setPassage(1);
						} else {
							tempGetOutRequest.setPassage(2);
						}
					} else {
						if ((tempGetOutRequest.getFloor() < this.currentFloor)
								&& (tempGetOutRequest.getDirection() == this.direction)) {
							tempGetOutRequest.setPassage(1);
						} else {
							tempGetOutRequest.setPassage(2);
						}
					}

					// Add getOutRequest to sequence
					this.sequence.add(tempGetOutRequest);

					foundGetOutRequest = true;
					break;
				}
			}

			// Remove getOutRequest from getOutRequests array
			if (foundGetOutRequest) {
				this.getOutRequests.remove(removeIndex);
			}
		}

		// Check the Requests in the sequence, if the sequence is not empty
		// Here we are looking for all getOutRequests and getInRequests that can be
		// removed from sequence
		if (this.sequence.size() > 0) {

			// Traverse the Requests in the sequence to find out if
			// any Requests need to be remove, because their floor matches the currentFloor
			// of the elevator
			for (PassengerRequest request : sequence) {

				// Remove all getOutRequests whose floor is the current floor of the elevator
				// The passengers whose getOutRequest is the same as currentFloor have already
				// arrived
				if (request.getType() == PassengerRequest.REQUEST_TYPE.GET_OUT_REQUEST
						&& request.getFloor() == this.currentFloor) {
					this.sequence.remove(request);
				}

				// Remove all getInRequests whose floor is the current floor of the elevator,
				// and add getOutRequests with the same ID to the sequence
				// The passengers whose getInRequest is the same as currentFloor have boarded
				// the elevator
				// and pressed a button inside the elevator (made a getOutRequest)
				if (request.getType() == PassengerRequest.REQUEST_TYPE.GET_IN_REQUEST
						&& request.getFloor() == this.currentFloor) {

					int removeIndex = 0;
					boolean foundGetOutRequest = false;

					// Traverse getOutFloors array
					for (int i = 0; i < this.getOutRequests.size(); ++i) {

						PassengerRequest tempGetOutRequest = this.getOutRequests.get(i);

						if (tempGetOutRequest.getID().equals(request.getID())) {

							removeIndex = i;

							// Assign passage to getOutRequest
							if (this.direction == DIRECTION.UP) {
								if ((tempGetOutRequest.getFloor() > this.currentFloor)
										&& (tempGetOutRequest.getDirection() == this.direction)) {
									tempGetOutRequest.setPassage(PassengerRequest.PASSAGE_SAME_DIRECTION_HIGHER);
								} else {
									tempGetOutRequest.setPassage(PassengerRequest.PASSAGE_OPPOSITE_DIRECTION);
								}
							} else {
								if ((tempGetOutRequest.getFloor() < this.currentFloor)
										&& (tempGetOutRequest.getDirection() == this.direction)) {
									tempGetOutRequest.setPassage(PassengerRequest.PASSAGE_SAME_DIRECTION_HIGHER);
								} else {
									tempGetOutRequest.setPassage(PassengerRequest.PASSAGE_OPPOSITE_DIRECTION);
								}
							}

							// Add getOutRequest to sequence
							this.sequence.add(tempGetOutRequest);
							foundGetOutRequest = true;
							break;
						}
					}

					// Remove getOutRequest from getOutRequests array
					if (foundGetOutRequest) {
						this.getOutRequests.remove(removeIndex);
					}

					// Remove the getInRequest from the sequence
					this.sequence.remove(request);
				}

			}
		}
	}

	/**
	 * Assigns passage to requests in the sequence
	 */
	private void redefinePassage() {

		for (PassengerRequest tempRequest : sequence) {

			if (!tempRequest.isSpecialRequest()) {
				if (this.direction == DIRECTION.UP) {
					if ((tempRequest.getFloor() > this.currentFloor)
							&& (tempRequest.getDirection() == this.direction)) {
						tempRequest.setPassage(PassengerRequest.PASSAGE_SAME_DIRECTION_HIGHER);
					} else if ((tempRequest.getFloor() < this.currentFloor)
							&& (tempRequest.getDirection() == this.direction)) {
						tempRequest.setPassage(PassengerRequest.PASSAGE_SAME_DIRECTION_LOWER);
					} else {
						tempRequest.setPassage(PassengerRequest.PASSAGE_OPPOSITE_DIRECTION);
					}
				} else {
					if ((tempRequest.getFloor() < this.currentFloor)
							&& (tempRequest.getDirection() == this.direction)) {
						tempRequest.setPassage(PassengerRequest.PASSAGE_SAME_DIRECTION_HIGHER);
					} else if ((tempRequest.getFloor() > this.currentFloor)
							&& (tempRequest.getDirection() == this.direction)) {
						tempRequest.setPassage(PassengerRequest.PASSAGE_SAME_DIRECTION_LOWER);
					} else {
						tempRequest.setPassage(PassengerRequest.PASSAGE_OPPOSITE_DIRECTION);
					}
				}
			}
		}
	}

	/**
	 * Simulates the elevator moving through the shaft
	 */
	private void elevatorWorker() throws InterruptedException {

		if (this.sequence.size() > 0) {

			// Get PassengerRequest from sequence
			PassengerRequest tempRequest = this.sequence.take();

			if (tempRequest.getFloor() == this.currentFloor) {
				checkSequence(tempRequest);
			} else {

				// Update the direction of the elevator based
				// on the position of the current floor
				// Since the direction has changed, we must
				// reassign passage to all requests in the sequence
				if (tempRequest.getFloor() < this.currentFloor) {
					this.direction = DIRECTION.DOWN;
					redefinePassage();
				} else if (tempRequest.getFloor() > this.currentFloor) {
					this.direction = DIRECTION.UP;
					redefinePassage();
				}

				// Simulate elevator go through the floors of the building
				while ((this.currentFloor != tempRequest.getFloor()) && (this.currentFloor >= 1) && (this.currentFloor <= MAX_FLOOR)) {

					this.idle = false;

					// Direction is up
					if (this.direction == DIRECTION.UP && this.currentFloor != (MAX_FLOOR)) {

						this.currentFloor += 1;
						Thread.sleep(SPEED * 1000);

						checkSequence(tempRequest);

					} else if (this.direction == DIRECTION.DOWN && this.currentFloor != 1) {

						this.currentFloor -= 1;
						Thread.sleep(SPEED * 1000);
						
						checkSequence(tempRequest);

					} else {
						System.out.println("Exit: Elevator is out of range");
						System.exit(0);
					}
				}
			}

			this.idle = true;
		}
	}

	/**
	 * Responsible for sorting requests assigned by the MultiElevatorScheduler into
	 * the elevator’s internal sequence list.
	 */
	private void requestToQueue() throws InterruptedException {

		if (this.getInRequests.size() > 0) {

			PassengerRequest tempRequest = this.getInRequests.get(0);
			this.getInRequests.remove(0);

			if (this.direction == DIRECTION.UP) {
				if ((tempRequest.getFloor() > this.currentFloor) && (tempRequest.getDirection() == this.direction)) {
					tempRequest.setPassage(PassengerRequest.PASSAGE_SAME_DIRECTION_HIGHER);
				} else if ((tempRequest.getFloor() < this.currentFloor)
						&& (tempRequest.getDirection() == this.direction)) {
					tempRequest.setPassage(PassengerRequest.PASSAGE_SAME_DIRECTION_LOWER);
				} else {
					tempRequest.setPassage(PassengerRequest.PASSAGE_OPPOSITE_DIRECTION);
				}
			} else {
				if ((tempRequest.getFloor() < this.currentFloor) && (tempRequest.getDirection() == this.direction)) {
					tempRequest.setPassage(PassengerRequest.PASSAGE_SAME_DIRECTION_HIGHER);
				} else if ((tempRequest.getFloor() > this.currentFloor)
						&& (tempRequest.getDirection() == this.direction)) {
					tempRequest.setPassage(PassengerRequest.PASSAGE_SAME_DIRECTION_LOWER);
				} else {
					tempRequest.setPassage(PassengerRequest.PASSAGE_OPPOSITE_DIRECTION);
				}
			}

			this.sequence.add(tempRequest);
		}
	}
	
	/**
	 * Breaks apart the Passenger object. Puts Passenger.getInRequest to the
	 * getInRequests array. Puts Passenger.getOutRequest to the getOutRequests
	 * array.
	 */
	public void assignPassenger(Passenger temp) throws InterruptedException {

		PassengerRequest getInRequest = temp.getInRequest(); 
		PassengerRequest getOutRequest = temp.getOutRequest(); 

		this.getInRequests.add(getInRequest);
		this.getOutRequests.add(getOutRequest);
	}
	
	public void reset() {
		this.getInRequests.clear();
		this.getOutRequests.clear();
		this.getSequence().clear();
		this.currentFloor = 1;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public DIRECTION getDirection() {
		return this.direction;
	}

	public void setDirection(DIRECTION direction) {
		this.direction = direction;
	}

	public PriorityBlockingQueue<PassengerRequest> getSequence() {
		return sequence;
	}

	public boolean isIdle() {
		return idle;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public int getID() {
		return this.ID;
	}
}
