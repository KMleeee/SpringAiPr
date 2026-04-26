package org.example; // ⚠️ 본인의 패키지명으로 꼭 맞춰주세요!

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;

import java.io.IOException;

@SpringBootApplication
public class McpApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpApplication.class, args);
    }

    // 1. WebFlux 전용 CORS 필터 (유지)
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    // ⭐️ 2. 최강의 방어막: 스프링 전체를 통제하는 커스텀 ObjectMapper 강제 주입
    @Bean
    @Primary // "다른 번역기 다 무시하고 무조건 얘를 1순위로 써라!" 라는 뜻입니다.
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.createXmlMapper(false).build();

        // 핵심 1: 모르는 필드(elicitation 등)가 들어오면 무조건 에러 없이 무시!
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 핵심 2: 기존의 LoggingLevel 버그 우회 모듈도 여기에 직접 탑재
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LoggingLevel.class, new JsonDeserializer<LoggingLevel>() {
            @Override
            public LoggingLevel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                p.skipChildren();
                return LoggingLevel.INFO;
            }
        });
        mapper.registerModule(module);

        return mapper;
    }

    @Bean
    public ToolCallbackProvider grafanaToolProvider(GrafanaMcpTools grafanaMcpTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(grafanaMcpTools) // "이 객체 안에 있는 @Tool 들을 다 가져와!"
                .build();
    }
}