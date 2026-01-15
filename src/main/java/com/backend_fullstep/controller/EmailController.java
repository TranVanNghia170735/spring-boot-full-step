package com.backend_fullstep.controller;

import com.backend_fullstep.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-CONTROLLER")
public class EmailController {
    private final EmailService emailService;

    @GetMapping("send-email")
    public ResponseEntity<Map<String, Object>>sendEmail(String to, String subject, String body){
        log.info("Sending email to {}", to);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", emailService.sendEmail(to, subject, body));
//        result.put("data", emailService.sendEmail(to, subject, body));
        log.info("Email send");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(result);

    }
}
