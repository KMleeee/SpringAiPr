package org.example;

import com.microsoft.playwright.*;
import org.springframework.stereotype.Service;
import com.microsoft.playwright.options.LoadState;

@Service
public class GrafanaCaptureService {
    private static final String GRAFANA_URL = "http://localhost:3001";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    public byte[] captureDashboard(String targetDashboardUrl) {
        // 1. Playwright 엔진 시작
        try (Playwright playwright = Playwright.create()) {
            // 2. 크롬(chromium) 브라우저 띄우기
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );

            // 3. 브라우저 컨텍스트 설정
            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions().setViewportSize(1920,1080)
            );

            // 4. 새로운 탭 열기
            Page page = context.newPage();

            try {
                // 5. 그라파나 로그인 페이지로 이동
                System.out.println("로그인 페이지로 이동 중...");
                page.navigate(GRAFANA_URL + "/login");

                // 6. 로그인 폼 채우기 및 제출
                page.fill("input[name='user']", USERNAME);
                page.fill("input[name='password']", PASSWORD);
                page.click("button[type='submit']");

                // 7. 로그이 완료되고 메인 화면이 뜰 때까지 대기
                page.waitForTimeout(3000);

                // 8. 캡처하려는 목표 대시보드 URL로 이동
                System.out.println("대시보드 페이지로 이동 중: " + targetDashboardUrl);
                page.navigate(targetDashboardUrl);

                // 9. 차트가 다 그려질 때까지 대기
                page.waitForLoadState(LoadState.NETWORKIDLE);

                System.out.println("스크린샷 캡처 완료");
                byte[] screenshotBytes = page.screenshot(
                        new Page.ScreenshotOptions().setFullPage(true)
                );

                return screenshotBytes;
            } catch (Exception e) {
                System.err.println("캡쳐 중 에러 발생: " + e.getMessage());
                throw new RuntimeException("Grafana 캡처 실패", e);
            }
        }
    }
}
