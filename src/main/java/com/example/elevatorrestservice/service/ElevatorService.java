package com.example.elevatorrestservice.service;

import java.util.List;

import com.example.elevatorrestservice.model.ElevatorState;
import com.example.elevatorrestservice.model.Passenger;

public interface ElevatorService {
	public static final int TOTAL_FLOORS = 11; // Number of floors
	public static final int TOTAL_ELEVATORS = 3; // Number of elevators

	List<ElevatorState> lowCostSchedule(List<Passenger> passengers);
	
	List<ElevatorState> reset();

}
