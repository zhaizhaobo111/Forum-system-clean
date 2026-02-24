package com.example.demo.model;

import lombok.Data;

import java.util.Date;
@Data
public class ArticleReply {
    private Long id;

    private Long articleId;

    private Long postUserId;

    private Long replyId;

    private Long replyUserId;

    private String content;

    private Integer likeCount;

    private Byte state;

    private Byte deleteState;

    private Date createTime;

    private Date updateTime;
//回复的对象-回复的发布者
    private User user;

}