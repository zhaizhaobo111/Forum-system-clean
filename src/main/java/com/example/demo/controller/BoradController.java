package com.example.demo.controller;

import com.example.demo.common.AppResult;
import com.example.demo.common.ResultCode;
import com.example.demo.exception.ApplicationException;
import com.example.demo.model.Board;
import com.example.demo.services.IBoradService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(tags = "板块接口")
@RestController
@RequestMapping("/board")
public class BoradController {

    @Value("${one-forum.index.board-num:9}")
    private Integer indexBoardNum;
    @Resource
    private IBoradService boradService;
    /**
     * 查询首版本列表
     * @return
     */
    @ApiOperation("获取首页板块列表")
    @GetMapping("/topList")
    public AppResult<List<Board>> topList(){
        log.info("首页板块个数为："+ indexBoardNum);
        //调用service查询结果
        List<Board> boards = boradService.selectByNum(indexBoardNum);
        //判断是否为空
        if(boards==null){
            boards=new ArrayList<>();
        }
        return AppResult.success(boards);
    }
    @ApiOperation("获取板块信息")
    @GetMapping("/getById")
    public AppResult<Board> getId(@ApiParam("板块Id")@RequestParam("id")@NonNull Long id){
        //调用service
        Board board = boradService.selectById(id);
        //板块不存在或者已删除
        if(board==null||board.getDeleteState()==1){
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        return AppResult.success(board);
    }
}
