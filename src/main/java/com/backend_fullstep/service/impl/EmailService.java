package com.backend_fullstep.service.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final SendGrid sendGrid;

    @Value("${spring.sendgrid.from-mail}")
    private String mailFrom;

    public String sendEmail(String toEmail, String subject, String body) {
        Email from = new Email(mailFrom);
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            return response.getStatusCode() == 202 ?
                    "Email sent successfully!" : "Failed: " + response.getBody();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}
