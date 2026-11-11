package com.primeroute.logistics.exception;

public class AddressInUseException extends RuntimeException {
    public AddressInUseException(String message) {
        super(message);
    }
}
