package com.alex.demo.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendResetCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("alexpetru1804@gmail.com");
        message.setTo(to);
        message.setSubject("Password Reset Code");
        message.setText("Your password reset code is: " + code);

        mailSender.send(message);
    }

    public void sendPasswordChangeConfirmation(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Security Alert: Password Changed Successfully");
        message.setText("Hello,\n\n" +
                "This is an automated notification to inform you that the password for your account has been successfully updated.\n\n" +
                "If you did not make this change, please contact our support team immediately.\n\n" +
                "Best regards,\n" +
                "DemoApp Security Team");

        mailSender.send(message);
    }
}