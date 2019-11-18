package com.eastlanglearn.east.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceImpl implements EmailService {

    private final String SUBJECT = "EastLangLearn Confirm Registration";
    private final String HOSTNAME = "eastlanglearn.com";

    private final JavaMailSender emailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }


    @Override
    public void sendRegistrationMessage(String to, String id, String username) {
        new Thread(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(SUBJECT);
            String MESSAGE_TEXT = "Здравейте, " + username + "!\n" + "Моля потвърдете вашата регистрация, като цъкнете линка отдолу: \n" +
                    "https://www." + HOSTNAME + "/confirmEmail/" + id + "\n" + "Поздрави,\nЕкипът на EastLangLearn :)\n\n";
            MESSAGE_TEXT += "Hello, " + username + "!\n" + "Please confirm your email by clicking the link below: \n" +
                    "https://www." + HOSTNAME + "/confirmEmail/" + id + "\n" + "Best regards,\n Eastlang team :)";
            message.setText(MESSAGE_TEXT);
            emailSender.send(message);
        }).start();
    }
}