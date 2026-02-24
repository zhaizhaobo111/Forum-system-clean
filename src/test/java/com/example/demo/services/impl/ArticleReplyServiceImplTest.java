package com.example.demo.services.impl;

import com.example.demo.model.Article;
import com.example.demo.model.ArticleReply;
import com.example.demo.services.IArticleReplyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ArticleReplyServiceImplTest {
    @Resource
    private IArticleReplyService articleReplyService;
    @Resource
    private ObjectMapper objectMapper;

    @Test
    //@Transactional
    void create() {
        ArticleReply articleReply=new ArticleReply();
        articleReply.setArticleId(1l);
        articleReply.setPostUserId(1l);
        articleReply.setContent("单元测试回复");
        articleReplyService.create(articleReply);
        System.out.println("回复帖子成功");
    }

    @Test
    void selectByArticleId() throws JsonProcessingException {
        List<ArticleReply> results = articleReplyService.selectByArticleId(1l);
        System.out.println(objectMapper.writeValueAsString(results));
    }
}