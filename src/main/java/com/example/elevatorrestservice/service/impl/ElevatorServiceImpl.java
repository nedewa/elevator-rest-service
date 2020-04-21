package com.example.elevatorrestservice.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.example.elevatorrestservice.model.Elevator;
import com.example.elevatorrestservice.model.ElevatorState;
import com.example.elevatorrestservice.model.Floor;
import com.example.elevatorrestservice.model.MultiElevatorScheduler;
import com.example.elevatorrestservice.model.Passenger;
import com.example.elevatorrestservice.service.ElevatorService;

@Service
public class ElevatorServiceImpl implements ElevatorService {
	
    private Elevator elevatorGroup[];
    private Floor floors[]; 
    private MultiElevatorScheduler elevatorScheduler;
	
    @PostConstruct
	public void init() {
        this.elevatorGroup = new Elevator[TOTAL_ELEVATORS];
        this.floors = new Floor[TOTAL_FLOORS];
        
        this.createFloors();
        this.createElevators();
        
        this.elevatorScheduler = new MultiElevatorScheduler(this.elevatorGroup, this.floors);
        
		new Thread(this.elevatorScheduler).start(); // Activates the GroupElevatorController to scan the floors array
    }
	
    /**
     * Creates Elevator objects in the elevatorGroup array.
     */
    private void createElevators() {
        for (int i = 0; i < TOTAL_ELEVATORS; ++i) {
            this.elevatorGroup[i] = new Elevator(i);
            this.elevatorGroup[i].setCurrentFloor(1); // Start from 1
            this.elevatorGroup[i].setDirection(Elevator.DIRECTION.UP);
        }

        // Create elevator threads
        for (int i = 0; i < TOTAL_ELEVATORS; ++i) {
            this.elevatorGroup[i].startRequestMonitor();
            this.elevatorGroup[i].startElevatorWorker();
        }
    }

    /**
     * Creates Floor objects in the floors array.
     */
    private void createFloors() {
        for (int i = 0; i < TOTAL_FLOORS; ++i) {
            this.floors[i] = new Floor(i);
        }
    }

    /**
     * Randomly selects a floor from the floors array and
     * calls the generatePassenger method on the Floor(randFloor) object.
     */
    private void generatePassenger(List<Passenger> passengers) throws InterruptedException {
    	for (Passenger passenger : passengers) {
    		System.out.println("passenger:" + passenger.getUser() + " start:" + passenger.getStartFloor() + " end:" + passenger.getEndFloor());
    		floors[passenger.getStartFloor()].generatePassenger(passenger);
    	}       
    }

	@Override
	public List<ElevatorState> lowCostSchedule(List<Passenger> passengers) {			
		try {
			this.generatePassenger(passengers);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return this.elevatorScheduler.getElevatorStates();
	}

	@Override
	public List<ElevatorState> reset() {
		return this.elevatorScheduler.reset();
	}

}
