package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Service
@RequiredArgsConstructor
public class MailSenderService {
    private final JavaMailSender mailSender;
    public void sendMail(String to, String subject, String text) throws MailException {
       try {
           SimpleMailMessage message = new SimpleMailMessage();
           message.setTo(to);
           message.setSubject(subject);
           message.setText(text);
           mailSender.send(message);
           System.out.printf("Message sent to: %s\n", to);
       }
       catch (MailException e) {
           e.printStackTrace();
           System.out.printf("failed to send message to: %s\n", to);
       }
    }
}
