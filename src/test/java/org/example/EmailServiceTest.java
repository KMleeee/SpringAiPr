package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
public class EmailServiceTest {
    @Autowired
    private EmailService emailService;

    @Test
    void testSendEmail() {
        try {
            byte[] imageBytes = Files.readAllBytes(Paths.get("test-capture.png"));

            String toEmail = "dlrkdals6813@gmail.com";

            String subject = "[테스트] 그라파나 대시보드 캡처 리포트";
            String text = "그라파나 대시보드 캡처 테스트입니다.";
            String filename = "dashboard-report.png";

            emailService.sendEmailWithAttachment(toEmail, subject, text, imageBytes, filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
