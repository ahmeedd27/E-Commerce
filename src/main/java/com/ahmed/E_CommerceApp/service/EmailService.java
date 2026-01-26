package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.model.Order;
import com.ahmed.E_CommerceApp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
      private final JavaMailSender javaMailSender;

      @Value("${spring.mail.username}")
      private String fromEmail;

    public void sendOrderConfirmation(Order order){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(order.getUser().getEmail());
        message.setSubject("Order confirmation");
        message.setText("Your order has been confirmed. Order ID " + order.getId());
        javaMailSender.send(message);
    }

      public void sendConfirmationCode(User user){
          SimpleMailMessage message=new SimpleMailMessage();
          message.setFrom(fromEmail);
          message.setTo(user.getEmail());
          message.setSubject("Confirm your email");
          message.setText("Please confirm your email by entering this code " + user.getConfirmationCode());
          javaMailSender.send(message);
      }
}
