package org.example.services;

import org.springframework.context.annotation.PropertySource;


public interface EmailService {

    void sendMessage(String email,String username,String activationCode);

}
