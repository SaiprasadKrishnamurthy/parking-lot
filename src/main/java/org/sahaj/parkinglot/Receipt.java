package org.sahaj.parkinglot;

import java.util.UUID;

public class Receipt {
    private String id = UUID.randomUUID().toString();
    private Double parkingCharges;
    private Vehicle vehicle;

    public Receipt(Integer parkingCharges, Vehicle vehicle) {
        this.parkingCharges = Double.valueOf(parkingCharges.toString());
        this.vehicle = vehicle;
    }

    public String getId() {
        return this.id;
    }

    public Vehicle forVehicle() {
        return this.vehicle;
    }

    public Double getParkingCharges() {
        return this.parkingCharges;
    }
}
