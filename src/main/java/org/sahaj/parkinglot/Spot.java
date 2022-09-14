package org.sahaj.parkinglot;

import java.util.UUID;

public class Spot {

    private final String id = UUID.randomUUID().toString();
    private Vehicle carType;

    public Spot(final Vehicle carType) {
        this.carType = carType;
    }

    public Vehicle getCarType() {
        return carType;
    }

    public String getId() {
        return id;
    }
}
