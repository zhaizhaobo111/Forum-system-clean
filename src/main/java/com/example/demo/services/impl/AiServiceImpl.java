package com.example.demo.services.impl;


import com.example.demo.services.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiServiceImpl implements AiService {

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String generateSummary(String content) {
        String url = aiServiceUrl + "/api/ai/summary";

        Map<String, String> request = new HashMap<>();
        request.put("content", content);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                    url, request, Map.class);

            if (response != null && response.containsKey("summary")) {
                return (String) response.get("summary");
            }
            return "摘要生成失败";
        } catch (Exception e) {
            return "AI服务调用失败：" + e.getMessage();
        }
    }

    @Override
    public boolean checkHealth() {
        try {
            String url = aiServiceUrl + "/api/ai/health";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(
                    url, Map.class);
            return response != null && "ok".equals(response.get("status"));
        } catch (Exception e) {
            return false;
        }
    }
}