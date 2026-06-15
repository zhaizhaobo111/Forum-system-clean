package com.example.demo.services.impl;
import com.example.demo.services.AiService;
import com.example.demo.common.AppResult;
import com.example.demo.common.ResultCode;
import com.example.demo.dao.ArticleMapper;
import com.example.demo.exception.ApplicationException;
import com.example.demo.model.Article;
import com.example.demo.model.ArticleReply;
import com.example.demo.model.Board;
import com.example.demo.model.User;
import com.example.demo.services.IArticleService;
import com.example.demo.services.IBoradService;
import com.example.demo.services.IUserService;
import com.example.demo.utils.StringUtil;
import com.mysql.cj.log.Log;
import com.mysql.cj.log.NullLogger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ArticleServiceImpl implements IArticleService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private AiService aiService;
    //用户和板块的操作
    @Resource
    private IBoradService boradService;
    @Resource
    private IUserService userService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Override
    public void create(Article article) {
        if(article==null||article.getUserId()==null
                || article.getBoardId()==null
                || StringUtil.isempty(article.getTitle())
                || StringUtil.isempty(article.getContent())){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //设置默认值
        article.setDeleteState((byte) 0);
        article.setLikeCount(0);
        article.setReplyCount(0);
        article.setVisitCount(0);
        article.setState((byte) 0);
        Date date=new Date();
        article.setCreateTime(date);
        article.setUpdateTime(date);
        //写入数据库
        int articleRow = articleMapper.insertSelective(article);
        if(articleRow<=0){
            log.warn(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        //获取用户信息
        User user = userService.selectById(article.getUserId());
        if(user==null){
            log.warn(ResultCode.FAILED_CREATE.toString()+"更新帖子失败，userid="+article.getUserId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        //更新用户的发帖数
        userService.addOneArticleCountById(user.getId());
        //获取板块信息
        Board board = boradService.selectById(article.getBoardId());
        //是否在数据库有对应的板块
        if(board==null){
            log.warn(ResultCode.FAILED_CREATE.toString()+"更新帖子失败，boardid"+article.getBoardId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        //更新板块中的帖子数量
        boradService.addOneArticleCountById(board.getId());
        log.info(ResultCode.SUCCESS.toString()+",userid=="+article.getUserId()+",boardid=="+ article.getBoardId()+",articleid=="+article.getId());
        }

    @Override
    public List<Article> selectAll() {
        //调用dao
        List<Article> articles=articleMapper.selectAll();
        return articles;
    }

    @Override
    public List<Article> selectAllByBoardId(Long boardId) {
        //非空校验
        if(boardId==null||boardId<=0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //校验板块是否存在
        Board board = boradService.selectById(boardId);
            if(board==null){
                log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString()+"boardi id="+board);
                throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
            }
            //调用dao查询
        List<Article> articles = articleMapper.selectAllByBoardId(boardId);
        return articles;
    }

    @Override
    public Article selectDetailById(Long id) {
        if(id==null||id<=0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Article article = articleMapper.selectDetailById(id);
        if(article==null){
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        Article updataArticle=new Article();
        updataArticle.setId(article.getId());
        updataArticle.setVisitCount(article.getVisitCount()+1);
        int row =articleMapper.updateByPrimaryKeySelective(updataArticle);
        if(row!=1){
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        //更新返回对象的访问次数
        article.setVisitCount(article.getVisitCount()+1);
        return article;
    }

    @Override
    public Article selectById(Long id) {
        if(id==null||id<=0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Article article = articleMapper.selectByPrimaryKey(id);
        return article;
    }

    @Override
    public void modify(Long id, String title, String content) {
        if(id==null||id<=0||StringUtil.isempty(title)||StringUtil.isempty(content)){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //构建要更新的帖子对象
        Article updataArticle=new Article();
        updataArticle.setId(id);
        updataArticle.setTitle(title);
        updataArticle.setContent(content);
        updataArticle.setUpdateTime(new Date());
        //调用dao
        int row = articleMapper.updateByPrimaryKeySelective(updataArticle);
        if(row!=1){
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public void thumbsUpById(Long id) {
        if(id==null||id<=0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //获取帖子详情
        Article article = articleMapper.selectByPrimaryKey(id);
        if(article==null||article.getDeleteState()==1){
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        if(article.getState()==1){
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }
        //构造要更新的对象
        Article updataArticle=new Article();
        updataArticle.setId(article.getId());
        updataArticle.setLikeCount(article.getLikeCount()+1);
        updataArticle.setUpdateTime(new Date());
        //dao
        int row = articleMapper.updateByPrimaryKeySelective(updataArticle);
        if(row!=1){
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public void deleteById(Long id) {
        if(id==null||id<=0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //根据id查询帖子信息
        Article article = articleMapper.selectByPrimaryKey(id);
        if(article==null||article.getDeleteState()==1){
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString()+"article id="+id);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        //构造中心对象
        Article updataArticle=new Article();
        updataArticle.setId(article.getId());
        updataArticle.setDeleteState((byte)1);
        int row = articleMapper.updateByPrimaryKeySelective(updataArticle);
        if(row!=1){
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }//更新板块帖子数量
        boradService.subOneArticleCountById(article.getBoardId());
        //更新用户帖子数量
        userService.subOneArticleCountById(article.getUserId());
        log.info("删除成功，article id="+article.getId()+"user id="+article.getUserId());
    }

    @Override
    public void addOneReplyCountById(Long id) {
        if(id==null||id<=0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //获取帖子记录
        Article article = articleMapper.selectByPrimaryKey(id);
        if(article==null||article.getDeleteState()==1){
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        if(article.getState()==1){
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }
        Article updataArticle=new Article();
        updataArticle.setId(article.getId());
        updataArticle.setReplyCount(article.getReplyCount()+1);
        updataArticle.setUpdateTime(new Date());
        int row = articleMapper.updateByPrimaryKeySelective(updataArticle);
        if(row!=1){
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public List<Article> selectByUserId(Long userId) {
        if(userId==null||userId<=0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }//检测用户是否存在
        User user = userService.selectById(userId);
        if(user==null){
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        //调用dao
        List<Article> articles = articleMapper.selectByUserId(userId);
        return articles;
    }
    @Override
    @Async
    public void generateSummary(Long id) {
        try {
            if (id == null || id <= 0) {
                log.warn("生成摘要失败：ID无效 id={}", id);
                return;
            }

            // 检查缓存中是否有摘要
            String cacheKey = "article:summary:" + id;
            String cachedSummary = redisTemplate.opsForValue().get(cacheKey);
            if (cachedSummary != null && !cachedSummary.isEmpty()) {
                log.info("从缓存获取摘要：id={}, summary={}", id, cachedSummary);
                return;
            }

            // 获取帖子详情
            Article article = articleMapper.selectByPrimaryKey(id);
            if (article == null || article.getDeleteState() == 1) {
                log.warn("生成摘要失败：帖子不存在 id={}", id);
                return;
            }

            // 调用AI服务生成摘要
            log.info("开始生成摘要：id={}, title={}", id, article.getTitle());
            String summary = aiService.generateSummary(article.getContent());
            log.info("摘要生成成功：id={}, summary={}", id, summary);

            // 保存到缓存（设置24小时过期）
            redisTemplate.opsForValue().set(cacheKey, summary, 24, TimeUnit.HOURS);
            log.info("摘要已保存到缓存：id={}", id);

            // 更新帖子摘要
            int row = articleMapper.updateSummaryById(id, summary);
            if (row != 1) {
                log.error("更新摘要失败：id={}", id);
            } else {
                log.info("摘要更新成功：id={}", id);
            }
        } catch (Exception e) {
            log.error("生成摘要异常：id={}", id, e);
        }
    }

}

