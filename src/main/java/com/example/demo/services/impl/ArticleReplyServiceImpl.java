package com.example.demo.services.impl;

import com.example.demo.common.AppResult;
import com.example.demo.common.ResultCode;
import com.example.demo.dao.ArticleReplyMapper;
import com.example.demo.exception.ApplicationException;
import com.example.demo.model.ArticleReply;
import com.example.demo.services.IArticleReplyService;
import com.example.demo.services.IArticleService;
import com.example.demo.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ArticleReplyServiceImpl implements IArticleReplyService {
     @Resource
   private ArticleReplyMapper articleReplyMapper;
     @Resource
     private IArticleService articleService;
    @Override
    public void create(ArticleReply articleReply) {
        if(articleReply==null||articleReply.getArticleId()==null
            ||articleReply.getPostUserId()==null
            || StringUtil.isempty(articleReply.getContent())){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
            articleReply.setReplyId(null);
            articleReply.setReplyUserId(null);
            articleReply.setLikeCount(0);
            articleReply.setState((byte) 0);
            articleReply.setDeleteState((byte) 0);
            Date date=new Date();
            articleReply.setCreateTime(date);
            articleReply.setUpdateTime(date);
            int row = articleReplyMapper.insertSelective(articleReply);
            if(row!=1){
                log.warn(ResultCode.ERROR_SERVICES.toString());
                throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
            }
            //更新帖子标回复数量
        articleService.addOneReplyCountById(articleReply.getArticleId());
            log.info("回复成功，article id="+articleReply.getArticleId()+"user id="+articleReply.getPostUserId());
    }

    @Override
    public List<ArticleReply> selectByArticleId(Long articleId) {
        if(articleId==null||articleId<=0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        List<ArticleReply> results = articleReplyMapper.selectByArticleId(articleId);

        return results;
    }


}
