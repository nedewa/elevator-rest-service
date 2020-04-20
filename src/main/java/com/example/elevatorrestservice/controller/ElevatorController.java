package com.example.elevatorrestservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.elevatorrestservice.model.ElevatorState;
import com.example.elevatorrestservice.model.Passenger;
import com.example.elevatorrestservice.service.ElevatorService;

@RestController
public class ElevatorController {

	private final AtomicLong worldClock = new AtomicLong();

	@Autowired
	ElevatorService elevatorService;

	@PostMapping("/reset")
	public List<ElevatorState> reset() {
		worldClock.set(0); // Reset world clock

		List<ElevatorState> elevatorStates = elevatorService.reset();
		
		return elevatorStates;
	}

	@PostMapping("/workload")
	public List<ElevatorState> workload(@RequestBody List<Passenger> passengers) {
		worldClock.incrementAndGet(); // Increase world clock

		List<ElevatorState> elevatorStates = elevatorService.lowCostSchedule(passengers);
		
		return elevatorStates;
	}
	
}