package com.example.demo.service;

public class InquiryNotFoundException extends RuntimeException {
    
    public InquiryNotFoundException(String message) {
        super(message);
    }
}
