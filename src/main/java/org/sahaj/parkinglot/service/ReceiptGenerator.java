package org.sahaj.parkinglot.service;

import org.sahaj.parkinglot.model.Receipt;
import org.sahaj.parkinglot.model.Ticket;
import org.sahaj.parkinglot.model.Vehicle;

public class ReceiptGenerator {

    private Integer baseParkingCharges;

    public ReceiptGenerator() {
        this.baseParkingCharges = 100;
    }

    public Receipt generate(final Ticket ticket) {
        return new Receipt(calculateFare(ticket.getVehicle()), ticket.getVehicle());
    }

    private Integer calculateFare(Vehicle vehicle) {
        switch (vehicle) {
            case CAR:
                return baseParkingCharges * 2;
            case TRUCK:
                return baseParkingCharges * 3;
            default:
                return baseParkingCharges;
        }
    }

}
