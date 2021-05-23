package org.example.core.email.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.email.EmailService;
import org.example.core.security.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender javaMailSender;

    @Value("${host.name:http://localhost:8080}")
    private String hostName;
    @Value("${mail.username}")
    private String mailUsername;
    @Value("${mail.message.subject}")
    private String subject;

    @Override
    public void sendMessage(String email,String username,String activationCode) {
        SimpleMailMessage mailMessage;
        try{
            mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(mailUsername);
            mailMessage.setTo(email);
            mailMessage.setSubject(subject);
            mailMessage.setText("Dear " + username+ "\n Thanks for registering!"+
                    "\n Click here for the activation: "
                    +hostName+"/api/authenticate/activate/"+activationCode);
            javaMailSender.send(mailMessage);
        }catch (Exception e){
            log.error("Error to sent email: "+email+" "+e.getMessage());
            throw new AuthException("Failed to sent email to registration validation");
        }
    }
}