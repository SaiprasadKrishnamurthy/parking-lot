package org.sahaj.parkinglot.model;

import org.sahaj.parkinglot.service.ReceiptGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

public class ParkingLot {

    private Integer parkingSequenceNumber = 0;
    private final ReceiptGenerator receiptGenerator;
    private final Set<Spot> availableSlots;
    private final Set<Spot> occupiedSlots;

    private ParkingLot(final List<Spot> spots) {
        this.receiptGenerator = new ReceiptGenerator();
        this.availableSlots = ConcurrentHashMap.newKeySet();
        this.occupiedSlots = ConcurrentHashMap.newKeySet();
        this.availableSlots.addAll(spots);
    }

    public Integer totalSpotCount() {
        return this.availableSlots.size();
    }

    public Ticket park(final Vehicle vehicle) {
        return availableSlots.stream()
                .filter(spot -> spot.getCarType() == vehicle)
                .findFirst()
                .map(spot -> {
                    availableSlots.remove(spot);
                    occupiedSlots.add(spot);
                    return spot;
                }).map(spot -> new Ticket(parkingSequenceNumber++, spot, vehicle))
                .orElseThrow(() -> new ParkingLotFullException(String.format("All the %s slots are full", occupiedSlots.size())));
    }

    public Receipt unPark(final Ticket ticket) {
        // Only release the spot if it's not released already to avoid double counting.
        if (occupiedSlots.contains(ticket.getSpot())) {
            availableSlots.add(ticket.getSpot());
            occupiedSlots.remove(ticket.getSpot());
            return receiptGenerator.generate(ticket);
        } else if (availableSlots.contains(ticket.getSpot())) {
            // Return the Receipt using the 'as-is' ticket
            return receiptGenerator.generate(ticket);
        } else {
            throw new IllegalArgumentException("Invalid Ticket Passed");
        }
    }

    public static class Builder {

        public static final int DEFAULT_SPOT_COUNT = 10;
        private Integer smallParkingSpotCount = DEFAULT_SPOT_COUNT;
        private Integer mediumParkingSpotCount = DEFAULT_SPOT_COUNT;
        private Integer largeParkingSpotCount = DEFAULT_SPOT_COUNT;

        public Builder withSmallSpots(Integer count) {
            this.smallParkingSpotCount = count;
            return this;
        }

        public Builder withMediumSpots(Integer count) {
            this.mediumParkingSpotCount = count;
            return this;
        }

        public Builder withLargeSpots(Integer count) {
            this.largeParkingSpotCount = count;
            return this;
        }

        public ParkingLot build() {
            List<Spot> spots = new ArrayList<>();
            spots.addAll(IntStream.range(0, this.smallParkingSpotCount).mapToObj(i -> new Spot(Vehicle.BIKE)).collect(toSet()));
            spots.addAll(IntStream.range(0, this.mediumParkingSpotCount).mapToObj(i -> new Spot(Vehicle.CAR)).collect(toSet()));
            spots.addAll(IntStream.range(0, this.largeParkingSpotCount).mapToObj(i -> new Spot(Vehicle.TRUCK)).collect(toSet()));
            return new ParkingLot(spots);
        }

    }
}
