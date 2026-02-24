package com.example.demo.services;

import com.example.demo.model.Article;
import com.example.demo.model.ArticleReply;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IArticleReplyService {
    /**
     * 帖子回复
     * @param articleReply
     */
    @Transactional
    void create(ArticleReply articleReply);
    List<ArticleReply> selectByArticleId( Long articleId);
}
