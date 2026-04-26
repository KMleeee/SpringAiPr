package org.example;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class GrafanaMcpTools {
    private final GrafanaCaptureService captureService;
    private final EmailService emailService;

    public GrafanaMcpTools(GrafanaCaptureService captureService, EmailService emailService) {
        this.captureService = captureService;
        this.emailService = emailService;
    }

    @Tool(description = "특정 그라파나 대시보드 URL을 캡처하여 지정된 이메일로 발송합니다.")
    public String sendGrafanaReport(
            String dashboardUrl,
            String targetEmail
    ) {
        System.out.println("MCP Client로부터 요청 수신됨! URL: " + dashboardUrl + ", Email: " + targetEmail);

        try {
            // 1. 캡처 수행
            byte[] imageBytes = captureService.captureDashboard(dashboardUrl);

            // 2. 이메일 발송
            String subject = "[자동화 리포트] 요청하신 그라파나 대시보드 캡처입니다.";
            String text = "AI 에이전트(MCP)를 통해 자동 발송된 리포트입니다.";
            emailService.sendEmailWithAttachment(targetEmail, subject, text, imageBytes, "report.png");

            // 3. LLM에게 작업이 성공했음을 알림
            return "성공: " + targetEmail + "로 대시보드 캡처가 발송되었습니다.";
        } catch (Exception e) {
            return "실패 : 캡처 및 발송 중 에러가 발생했습니다. 원인 - " + e.getMessage();
        }
    }
}
