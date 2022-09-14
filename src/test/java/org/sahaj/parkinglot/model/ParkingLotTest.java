package org.sahaj.parkinglot.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sahaj.parkinglot.model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParkingLotTest {

    private ParkingLot.Builder parkingLotBuilder;

    @BeforeEach
    void setUp() {
        this.parkingLotBuilder = new ParkingLot.Builder().withLargeSpots(2).withMediumSpots(5).withSmallSpots(10);
    }

    @DisplayName("Should allocate a parking spot and return a ticket with an available spot.")
    @Test
    void shouldAllocateParkingSpotAndReturnTicketForACarWhenSpotAvailable() {
        ParkingLot parkingLot = this.parkingLotBuilder.build();
        Vehicle car = Vehicle.CAR;
        Ticket ticket = parkingLot.park(car);
        assertEquals(0, ticket.getId());
    }

    @DisplayName("Should unallocate a parking spot when a valid parking ticket is passed.")
    @Test
    void shouldUnParkAParkedCarFromAGivenTicket() {
        ParkingLot parkingLot = this.parkingLotBuilder.build();
        Vehicle car = Vehicle.CAR;
        Ticket ticket = parkingLot.park(car);
        assertEquals(car, parkingLot.unPark(ticket).forVehicle());
    }

    @DisplayName("Should fetch the spot occupancies by vehicle types.")
    @Test
    void shouldTrackSpotOccupancyByVehicleType() {
        ParkingLot parkingLot = this.parkingLotBuilder.withMediumSpots(1).withSmallSpots(1).build();
        Vehicle car = Vehicle.CAR;
        Vehicle bike = Vehicle.BIKE;
        Vehicle truck = Vehicle.TRUCK;
        assertEquals(0, parkingLot.park(car).getId());
        assertEquals(1, parkingLot.park(bike).getId());
        assertEquals(2, parkingLot.park(truck).getId());
    }

    @DisplayName("Should park and unpark fixed number of cars with unpark being idempotent operation.")
    @Test
    void shouldUnParkAndUnParkFixedNumberOfCars() {
        ParkingLot parkingLot = this.parkingLotBuilder.withMediumSpots(3).withSmallSpots(0).withLargeSpots(0).build();
        Ticket ticketOne = parkingLot.park(Vehicle.CAR);
        Ticket ticketTwo = parkingLot.park(Vehicle.CAR);
        Ticket ticketThree = parkingLot.park(Vehicle.CAR);
        parkingLot.unPark(ticketOne);
        parkingLot.unPark(ticketOne);
        parkingLot.unPark(ticketTwo);
        parkingLot.unPark(ticketThree);
        assertEquals(3, parkingLot.totalSpotCount());
    }

    @DisplayName("Should generate receipt fee for a vehicle when unparking.")
    @Test
    void shouldGenerateReceiptWithFlatFeeByVehicleTypeOnUnPark() {
        ParkingLot parkingLot = this.parkingLotBuilder.build();
        Vehicle car = Vehicle.CAR;
        Ticket ticket = parkingLot.park(car);
        Receipt receipt = parkingLot.unPark(ticket);
        assertEquals(200, receipt.getParkingCharges());
    }

    @DisplayName("Should generate receipt fee for all vehicles when unparking.")
    @Test
    void shouldApplyFlatRateMultiplierAcrossAllVehicleTypes() {
        ParkingLot parkingLot = this.parkingLotBuilder.build();
        Vehicle car = Vehicle.CAR;
        Vehicle bike = Vehicle.BIKE;
        Vehicle truck = Vehicle.TRUCK;
        Receipt carFare = parkingLot.unPark(parkingLot.park(car));
        Receipt truckFare = parkingLot.unPark(parkingLot.park(truck));
        Receipt bikeFare = parkingLot.unPark(parkingLot.park(bike));
        assertEquals(bikeFare.getParkingCharges() * 3, truckFare.getParkingCharges());
        assertEquals(bikeFare.getParkingCharges() * 2, carFare.getParkingCharges());
    }

    @DisplayName("When there are no parking slots available, if attempted to park an exception is raised")
    @Test
    void shouldRaiseAnErrorWhenAttemptedToParkWhenNoSlotsAvailable() {
        ParkingLot parkingLot = this.parkingLotBuilder.withMediumSpots(3).withSmallSpots(0).withLargeSpots(0).build();
        assertThrows(ParkingLotFullException.class, () -> {
            parkingLot.park(Vehicle.CAR);
            parkingLot.park(Vehicle.CAR);
            parkingLot.park(Vehicle.CAR);
            parkingLot.park(Vehicle.CAR);

        }, "Expected ParkingLotFullException but didn't");
    }

    @DisplayName("When an artificially created ticket is passed, an exception should be raised and the no of slots should not be impacted.")
    @Test
    void shouldRaiseAnErrorWhenAttemptedToUnparkWithAnInvalidTicket() {
        ParkingLot parkingLot = this.parkingLotBuilder.withMediumSpots(2).withSmallSpots(0).withLargeSpots(0).build();
        parkingLot.park(Vehicle.CAR); //ok
        parkingLot.park(Vehicle.CAR); // ok
        assertThrows(IllegalArgumentException.class, () -> {
            parkingLot.unPark(new Ticket(-1, new Spot(Vehicle.CAR), Vehicle.CAR));
        }, "Expected IllegalArgumentException but didn't");
    }
}