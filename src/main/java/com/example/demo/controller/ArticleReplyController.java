package com.example.demo.controller;

import com.example.demo.common.AppCpnfig;
import com.example.demo.common.AppResult;
import com.example.demo.common.ResultCode;
import com.example.demo.model.Article;
import com.example.demo.model.ArticleReply;
import com.example.demo.model.User;
import com.example.demo.services.IArticleReplyService;
import com.example.demo.services.IArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Api(tags = "回复接口")
@Slf4j
@RestController
@RequestMapping("/reply")

public class ArticleReplyController {
    @Resource
    private IArticleReplyService articleReplyService;
    @Resource
    private IArticleService articleService;
    @ApiOperation("回复帖子")
    @PostMapping("create")
    public AppResult create(HttpServletRequest request,
                            @ApiParam("帖子Id")@RequestParam("articleId") @NonNull Long articleId,
                            @ApiParam("帖子内容")@RequestParam("content") @NonNull String content){
        HttpSession session = request.getSession(false);
        User user=(User) session.getAttribute(AppCpnfig.USER_SESSION);
        if(user.getState()==1){
            //用户禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED );
        }
        //获取想要回复的帖子对象
        Article article = articleService.selectById(articleId);
        //是否存在或已删除
        if(article==null||article.getDeleteState()==1){
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        //是否封贴
        if(article.getState()==1){
            return AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED);
        }
        //构建回复对象
        ArticleReply articleReply=new ArticleReply();
        articleReply.setArticleId(articleId);
        articleReply.setPostUserId(user.getId());
        articleReply.setContent(content);
        //写入回复
        articleReplyService.create(articleReply);

        return AppResult.success();
    }
    @ApiOperation("获取回复列表")
    @GetMapping("/getReplies")
    public AppResult<List<ArticleReply>>getRepliesByArticleId(@ApiParam("帖子id")@RequestParam("articleId")@NonNull Long articleId){
        Article article = articleService.selectById(articleId);
        if(article==null||article.getDeleteState()==1){
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        List<ArticleReply> articleReplies = articleReplyService.selectByArticleId(articleId);
        return AppResult.success(articleReplies);
    }
}
