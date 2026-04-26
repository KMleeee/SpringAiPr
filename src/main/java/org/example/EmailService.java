package org.example;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailWithAttachment(String to, String subject, String text, byte[] imageBytes, String filename) {
        try {
            // 1. 첨부파일이 있는 메일은 단순 텍스트가 아닌 MimeMessage를 사용
            MimeMessage message = mailSender.createMimeMessage();

            // 2. MimeMessage를 쉽게 다루도록 해주는 Helper 객체 생성
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 3. 메일에 내용 적기
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            // 4. 사진 첨부하기
            // byte[] 형태의 이미지 데이터를 스프링이 읽을 수 있는 Resource로 변환하여 첨부
            ByteArrayResource imageResource = new ByteArrayResource(imageBytes);
            helper.addAttachment(filename, imageResource);

            // 5. 메일 발송
            System.out.println("메일 발송 중...");
            mailSender.send(message);
            System.out.println("메일 발송 완료.");

        } catch (MessagingException e) {
            System.err.println("메일 작성 또는 발송 중 에러가 발생했습니다.");
            throw new RuntimeException(e);
        }
    }
}
