package com.backend_fullstep.service.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final SendGrid sendGrid;

    @Value("${spring.sendgrid.from-mail}")
    private String mailFrom;

    @Value("${spring.sendgrid.verificationLink}")
    private String verificationLink;

    @Value("${spring.sendgrid.templateId}")
    private String templateId;

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

    public String sendVerificationEmail(String to, String name) throws IOException {
        log.info("Sending verification email for name={}", name);

        Email fromEmail = new Email(mailFrom, "NghiaTV");
        Email toEmail = new Email(to);
        String subject ="Xác thực tài khoản";

        //Generate secret code and save to db
        String secretCode = UUID.randomUUID().toString();
        log.info("secretCode={}", secretCode);

        //TODO save secretCode to db

        // Tạo dynamic template data

        Map<String, String> dynamicTemplateData = new HashMap<>();
        dynamicTemplateData.put("name", name);
        dynamicTemplateData.put("verification_link", verificationLink +"?secretCode=" +secretCode);

        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setSubject(subject);
        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        //Add dynamic template data

        dynamicTemplateData.forEach(personalization::addDynamicTemplateData);
        mail.addPersonalization(personalization);
        mail.setTemplateId(templateId); //Template ID từ SendGrid

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sendGrid.api(request);

        if(response.getStatusCode()==202){
            log.info("Verification sent successfully");
            return "Verification sent successfully";
        } else {
            log.error("Verification sent failed");
            return "Verification sent failed";
        }


    }
}
