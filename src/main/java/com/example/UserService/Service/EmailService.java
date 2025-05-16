package com.example.UserService.Service;

import com.example.UserService.Model.EmailDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    JavaMailSender javaMailSender;

    public void sendEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("name", emailDetail.getReceiver().getEmail());
            context.setVariable("button", "Go to home page");
            context.setVariable("link", emailDetail.getLink());

            String template = templateEngine.process("forgot-password", context);


            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom("nghialncse170125@fpt.edu.vn");
            mimeMessageHelper.setTo(emailDetail.getReceiver().getEmail());
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            mimeMessageHelper.setText(template, true);

            javaMailSender.send(mimeMessage);

        } catch (MailAuthenticationException e) {
            System.err.println("Authentication failed: " + e.getMessage());
        } catch (MessagingException e) {
            System.err.println("Messaging error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("General error: " + e.getMessage());
        }
    }
}