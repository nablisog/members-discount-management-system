package com.springboot.membersdiscount.exception;

public class MembersNotFoundException extends RuntimeException {
    public MembersNotFoundException(String message) {
        super(message);
    }
}
