package com.example.demo.services;

import java.util.Map;

public interface AiService {
    /**
     * 生成帖子智能摘要
     */
    String generateSummary(String content);

    /**
     * 检查AI服务是否正常
     */
    boolean checkHealth();

    /**
     * AI发帖助手
     * @param topic 文章主题
     * @return 生成的文章信息（标题、摘要、正文、标签）
     */
    Map<String, Object> agentWrite(String topic);
}
