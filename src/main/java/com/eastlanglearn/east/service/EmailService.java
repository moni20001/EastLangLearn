package com.eastlanglearn.east.service;

public interface EmailService {
    void sendRegistrationMessage(String to, String id, String username);
}
