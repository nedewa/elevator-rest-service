package com.example.elevatorrestservice.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import com.example.elevatorrestservice.model.Elevator.DIRECTION;

public class MultiElevatorScheduler implements Runnable {

	private Elevator elevatorGroup[];
	private Floor floors[];
	private int start;

	public MultiElevatorScheduler(Elevator[] elevatorGroup, Floor[] floors) {
		super();

		this.elevatorGroup = elevatorGroup;
		this.floors = floors;
		this.start = 0;
	}

	@Override
	public void run() {
		try {
			while (true) {
				scheduler();
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			System.out.println("GroupElevatorController thread failed!");
		}
	}

	/**
	 * Scan the floors array, looks for a floor with at least one passenger. Based
	 * on the algorithm, assigns a Passenger to one of the elevators from the
	 * elevatorGroup array.
	 */
	private void scheduler() throws InterruptedException {

		int chosenElevator = 0;
		boolean foundPassenger = false;

		Passenger tempPassenger = null; // Create a dummy Passenger object

		// Look for a floor with at least one passenger
		for (int i = this.start; i < floors.length; ++i) {

			Floor floor = floors[i];

			if (floor.getPassengerRequests().size() > 0) {

				tempPassenger = floor.getPassengerRequests().take(); // Remove the passenger from queue

				// Remembers from which index to start scanning next time
				if (i == (floors.length - 1)) {
					this.start = 0;
				} else {
					this.start = i + 1;
				}

				foundPassenger = true;
				break;
			}

			// Remembers from which index to start scanning next time
			// even though no passenger was found
			if (i == (floors.length - 1)) {
				this.start = 0;
			} else {
				this.start = i + 1;
			}
		}

		if (foundPassenger) {
			chosenElevator = chooseLowCostElevator(elevatorGroup, tempPassenger); 
			this.elevatorGroup[chosenElevator].assignPassenger(tempPassenger); // Assign a passenger to the chosen elevator
		}
		
		displayElevators(elevatorGroup);
	}
	
	private void displayElevators(Elevator[] elevatorGroup) {
		for (int i = 0; i < elevatorGroup.length; i++) {
			for (int j = 1; j <= Elevator.MAX_FLOOR; j++) {
				if (j == elevatorGroup[i].getCurrentFloor()) {
					System.out.print(" == ");
				} else {
					System.out.printf(" %d ", j);
				}
			}
			System.out.println("---Elevator[" + elevatorGroup[i].getID() + "] " + elevatorGroup[i].getDirection());
		}
		System.out.println();
	}
	
	public List<ElevatorState> getElevatorStates() {
		List<ElevatorState> elevatorStates = new ArrayList<>();

		for (int i = 0; i < elevatorGroup.length; i++) {
			List<String> users = new ArrayList<>();
			for (PassengerRequest request : elevatorGroup[i].getSequence()) {
				users.add(request.getID());
			}
			elevatorStates.add(new ElevatorState(elevatorGroup[i].getID(), elevatorGroup[i].getCurrentFloor(), users));
		}
		return elevatorStates;
	}


	/**
	 * Algorithm description: the new requests will be is assigned to the elevator with
	 * the lowest costs.
	 *
	 * Calculate and compare the costs among the different elevators. Including time cost and energy cost.
	 * The main data structure used: PriorityBlockingQueue<PassengerRequest> CopyOnWriteArrayList<PassengerRequest>
	 * for controlling passenger requests. And I think the algorithm 
	 *
	 * Stop costs are static, it`s 1 time unit, for passengers get in/out, assume
	 * there is no other cost when get in/out.
	 */
	public int chooseLowCostElevator(Elevator[] elevatorGroup, Passenger passenger) {

		int pick = 0;
		boolean flag = true;
		double cost = Double.MAX_VALUE;

		while (flag) {

			// Find the elevator with lowest cost
			for (Elevator elevator : elevatorGroup) {

				int totalRequests = elevator.getSequence().size(); // Current number of requests in sequence
				int elevatorCost = (totalRequests + 1) * (Elevator.SPEED + Elevator.STOP_TIME_UNIT); // Total cost of all requests plus new call
				double newRequestEnergyCost = 0;
				
				PassengerRequest tempRequest = passenger.getInRequest();
				if (elevator.getDirection() == DIRECTION.UP) {
					if ((tempRequest.getFloor() > elevator.getCurrentFloor()) && (tempRequest.getDirection() == elevator.getDirection())) {
						newRequestEnergyCost += Elevator.ENERGY_UP * Math.abs(elevator.getCurrentFloor() - tempRequest.getFloor());
					} else if ((tempRequest.getFloor() < elevator.getCurrentFloor())
							&& (tempRequest.getDirection() == elevator.getDirection())) {
						newRequestEnergyCost += Elevator.ENERGY_UP * Math.abs(elevator.getSequence().size()) + Elevator.ENERGY_DOWN * Math.abs(elevator.getSequence().size());
					} else {
						newRequestEnergyCost += Elevator.ENERGY_UP * Math.abs(elevator.getSequence().size()) * 2 + Elevator.ENERGY_DOWN * Math.abs(elevator.getSequence().size()) * 2;
					}
				} else {
					if ((tempRequest.getFloor() < elevator.getCurrentFloor()) && (tempRequest.getDirection() == elevator.getDirection())) {
						newRequestEnergyCost += Elevator.ENERGY_UP * Math.abs(elevator.getCurrentFloor() - tempRequest.getFloor());
					} else if ((tempRequest.getFloor() > elevator.getCurrentFloor())
							&& (tempRequest.getDirection() == elevator.getDirection())) {
						newRequestEnergyCost += Elevator.ENERGY_UP * Math.abs(elevator.getSequence().size()) + Elevator.ENERGY_DOWN * Math.abs(elevator.getSequence().size());
					} else {
						newRequestEnergyCost += Elevator.ENERGY_UP * Math.abs(elevator.getSequence().size()) * 2 + Elevator.ENERGY_DOWN * Math.abs(elevator.getSequence().size()) * 2;
					}
				}
				elevatorCost += newRequestEnergyCost;
				
				if (elevatorCost < cost) {
					cost = elevatorCost;
					pick = elevator.getID() - 1;
				}
			}
			
			 // Check if thresholds is not reached
            if (elevatorGroup[pick].getSequence().size() < Elevator.CAPACITY) {
                flag = false;
            }
		}
		return pick;
	}
	
	public List<ElevatorState> reset() {
		List<ElevatorState> elevatorStates = new ArrayList<>();
	
		for (int i = 0; i < elevatorGroup.length; i++) {
			elevatorGroup[i].reset();
			
			List<String> users = new ArrayList<>();
			for (PassengerRequest request : elevatorGroup[i].getSequence()) {
				users.add(request.getID());
			}
			elevatorStates.add(new ElevatorState(elevatorGroup[i].getID(), elevatorGroup[i].getCurrentFloor(), users));
		}
		return elevatorStates;
	}

}
