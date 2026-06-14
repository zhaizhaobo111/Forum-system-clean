package com.example.demo.controller;

import com.example.demo.common.AppCpnfig;
import com.example.demo.common.AppResult;
import com.example.demo.common.ResultCode;
import com.example.demo.model.Article;
import com.example.demo.model.Board;
import com.example.demo.model.User;
import com.example.demo.services.IArticleService;
import com.example.demo.services.IBoradService;
import com.fasterxml.jackson.core.sym.Name3;
import com.mysql.cj.Session;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "文章接口")
@Slf4j
@RestController
@RequestMapping("article")
public class ArticleController {
    @Resource
    private IArticleService articleService;
    @Resource
    private IBoradService boradService;
    /**
     * 发布新帖子
     * @param boardId 板块id
     * @param title 文章标题
     * @param content
     * @return 帖子内容
     */
    @ApiOperation("发布新帖")
    @PostMapping("/create")
    public AppResult create(HttpServletRequest request,
                            @ApiParam("板块id") @RequestParam("boardId")@NonNull Long boardId,
                            @ApiParam("文章标题") @RequestParam("title")@NonNull String title,
                            @ApiParam("帖子内容") @RequestParam("content")@NonNull String content){
        //校验用户是否禁言
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppCpnfig.USER_SESSION);
        if(user.getState()==1){
            //用户已禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //板块的校验
        Board board = boradService.selectById(boardId.longValue());
        if(board==null||board.getDeleteState()==1||board.getState()==1){
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_BOARD_BANNED);
        }
        //封装文章对象
        Article article=new Article();
        article.setContent(content);
        article.setTitle(title);
        article.setBoardId(boardId);
        article.setUserId(user.getId());
        articleService.create(article);

        return AppResult.success(ResultCode.SUCCESS);

    }
    /**
     * 根据版块Id查询帖子列表
     *
     * @param boardId 版块Id
     * @return 指定版块的帖子列表
     */
    @ApiOperation("获取帖子列表")
    @GetMapping("getAllByBoardId")
    public AppResult<List<Article>> getAllByBoardId(@ApiParam("板块Id") @RequestParam(value = "boardId",required = false) Long boardId){
       //定义返回的集合
        List<Article> articles;
        if(boardId== null){
            articles = articleService.selectAll();
        }else {
            articles = articleService.selectAllByBoardId(boardId);
        }
        if(articles==null){
            //如果结果为空，new一个集合
            articles=new ArrayList<>();
        }

        //响应结果
        return AppResult.success(articles);
    }
    @ApiOperation("根据帖子id获取帖子详情")
    @GetMapping("/details")
    public AppResult<Article> getDetails(HttpServletRequest request,
                                         @ApiParam("帖子id") @RequestParam("id") @NonNull Long id){
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppCpnfig.USER_SESSION);
        Article article = articleService.selectDetailById(id);
        if(article==null){
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
        }
        //判断是否为作者
        if(user.getId()==article.getUserId()){
            //表示为作者
            article.setOwn(true);
        }
        return AppResult.success(article);
    }
    @ApiOperation("修改帖子")
    @PostMapping("/modify")
    public AppResult modify(HttpServletRequest request,
                            @ApiParam("帖子Id")@RequestParam("id") @NonNull Long id,
                            @ApiParam("帖子标题")@RequestParam("title") @NonNull String title,
                            @ApiParam("帖子正文")@RequestParam("content") @NonNull String content) {
        //获取当前登录的用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppCpnfig.USER_SESSION);
        //校验用户状态
        if(user.getState()==1){
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }//查询帖子详情
        Article article = articleService.selectById(id);
        //检验帖子是否有效
        if(article==null ){
            return  AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        //校验用户是不是作者
        if(user.getId()!=article.getUserId()){
            return  AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        //判断帖子的状态
        if(article.getState()==1 ||article.getDeleteState()==1){
            return AppResult.failed(ResultCode. FAILED_ARTICLE_BANNED);
        }
        //调用service
        articleService.modify(id,title,content);
        log.info(("帖子更新成功.Article id="+id+"user id="+user.getId())+".");

        return AppResult.success();
    }
    @ApiOperation("点赞")
    @PostMapping("thumbsUp")
    public  AppResult thumbsUp(HttpServletRequest request,
                               @ApiParam("帖子Id")@RequestParam("id")@NonNull Long id){
        //检验用户状态
        HttpSession session = request.getSession(false);
        User user=(User) session.getAttribute(AppCpnfig.USER_SESSION);
        if(user.getState()==1){
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
         }
        articleService.thumbsUpById(id);
        return AppResult.success();
    }
    @ApiOperation("删除帖子")
    @PostMapping ("deleteById")
    public AppResult deleteById(HttpServletRequest request,@ApiParam("帖子id") @RequestParam("id") @NonNull Long id){
        HttpSession session = request.getSession(false);
        User user=(User)session.getAttribute(AppCpnfig.USER_SESSION);
        if(user.getState()==1){
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //查询帖子详情
        Article article = articleService.selectById(id);
        if(article==null||article.getDeleteState()==1){
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        //查看是否为作者
        if(user.getId()!=article.getUserId()){
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        //调用servic
        articleService.deleteById(id);
        return AppResult.success();
    }
    @ApiOperation("获取用户的帖子列表")
    @GetMapping("/getAllByUserId")
    public AppResult<List<Article>> getAllByUserId(HttpServletRequest request,
                                    @ApiParam("用户Id")@RequestParam(value = "userId",required = false) Long userId){
        //如果session为空，那么session中获取当前登录的用户id
        if(userId==null){
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(AppCpnfig.USER_SESSION);
            userId=user.getId();
        }
        List<Article> articles = articleService.selectByUserId(userId);
        return AppResult.success(articles);
    }

    @ApiOperation("生成帖子智能摘要")
    @PostMapping("/generateSummary")
    public AppResult generateSummary(
            @ApiParam("帖子Id") @RequestParam("id") @NonNull Long id) {
        articleService.generateSummary(id);
        return AppResult.success();
    }
}
