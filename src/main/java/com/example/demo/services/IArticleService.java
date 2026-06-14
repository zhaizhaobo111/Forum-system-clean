package com.example.demo.services;

import com.example.demo.model.Article;
import com.example.demo.model.ArticleReply;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IArticleService {
    /**
     * 发布帖子
     * @param article 要发布的帖子
     */
    @Transactional//当前方法中的执行过程会被事务管理起来
    void create(Article article);

    List<Article> selectAll();

    List<Article>selectAllByBoardId(Long boardId);

    /**
     * 根据帖子id查询帖子详情
     * @param id 帖子id
     * @return 帖子详情
     */
    Article selectDetailById(Long id);

    /**
     * 根据帖子id查询记录
     * @param id 帖子id
     * @return 记录
     */
    Article selectById(Long id);


    /**
     * 编辑帖子
     * @param id 帖子id
     * @param title 帖子标题
     * @param content 帖子内容
     */
     void modify(Long id,String title,String content);

    /**
     * 点赞帖子
     * @param id 帖子id
     */
    void thumbsUpById(Long id);

    /**
     * 删除帖子
     * @param id 帖子id
     */
    @Transactional
    void deleteById(Long id);

    /**
     * 更新文章回复数量+1
     * @param id 板块id
     */
    void addOneReplyCountById(Long id);
    /**
     * 根据用户id查询帖子列表
     * @param userId 用户Id
     * @return 帖子列表
     */
    List<Article>selectByUserId(Long userId);
    void generateSummary(Long id);
    /**
     *   生成帖子智能摘要
     */

}
