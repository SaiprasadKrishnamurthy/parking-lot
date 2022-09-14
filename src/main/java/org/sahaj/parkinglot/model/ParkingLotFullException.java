package org.sahaj.parkinglot.model;

/**
 * An exception to be raised when a parking is attempted on a fully occupied parking lot.
 *
 * @author Sai.
 */
public class ParkingLotFullException extends RuntimeException {
    private String msg;

    public ParkingLotFullException(final String msg) {
        super(msg);
    }
}
