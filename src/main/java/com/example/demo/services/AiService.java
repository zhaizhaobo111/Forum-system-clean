package com.example.demo.services;


public interface AiService {
    /**
     * 生成帖子智能摘要
     */
    String generateSummary(String content);

    /**
     * 检查AI服务是否正常
     */
    boolean checkHealth();
}
