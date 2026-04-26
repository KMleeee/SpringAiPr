package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.IOException;

@SpringBootTest
class GrafanaCaptureServiceTest {

    @Autowired
    private GrafanaCaptureService captureService;

    @Test
    void testCaptureAndSave() {
        // ⭐️ Step 3에서 복사해둔 내 로컬 그라파나 대시보드 URL을 여기에 넣으세요!
        String targetUrl = "http://localhost:3000/d/adpq248/new-dashboard?orgId=1&from=now-6h&to=now&timezone=browser";

        System.out.println("캡처 테스트 시작...");
        byte[] imageBytes = captureService.captureDashboard(targetUrl);

        // 결과물을 실제 이미지 파일로 저장해보기
        try (FileOutputStream fos = new FileOutputStream("test-capture2.png")) {
            fos.write(imageBytes);
            System.out.println("✅ 캡처 완료! 프로젝트 폴더에 test-capture.png 파일이 생성되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
