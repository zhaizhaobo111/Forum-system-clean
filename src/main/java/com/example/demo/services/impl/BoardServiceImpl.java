package com.example.demo.services.impl;

import com.example.demo.common.AppResult;
import com.example.demo.common.ResultCode;
import com.example.demo.dao.BoardMapper;
import com.example.demo.exception.ApplicationException;
import com.example.demo.model.Board;
import com.example.demo.services.IBoradService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Slf4j
@Service
public class BoardServiceImpl implements IBoradService {
    @Resource
    private BoardMapper boardMapper;
    @Override
    public List<Board> selectByNum(Integer num) {
        //非空校验
        if (num<=0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw  new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //注入dao查询数据库信息
        List<Board> result = boardMapper.selectByNum(num);
        return result;
    }

    @Override
    public Board selectById(Long id) {
        if(id==null||id<=0){
            log.info(ResultCode.FAILED_USER_BOARD_APRICLR_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_BOARD_APRICLR_COUNT));
        }
        Board board = boardMapper.selectByPrimaryKey(id);
        return board;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        if(id==null||id<=0){
            log.info(ResultCode.FAILED_USER_BOARD_APRICLR_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_BOARD_APRICLR_COUNT));
        }
        //查询对应的板块
        Board board = boardMapper.selectByPrimaryKey(id);
        if(board==null){
            log.warn(ResultCode.ERROR_IS_NULL.toString()+" board id:"+id);
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        //更新帖子数量
        Board updateBoard =new Board();
        updateBoard.setId(board.getId());
        updateBoard.setArticleCount(board.getArticleCount()+1);
        boardMapper.updateByPrimaryKeySelective(updateBoard);
        //执行dao，实时更新
        int row =boardMapper.updateByPrimaryKeySelective(updateBoard);
        //受影响的行数不等于1
        if(row!=1){
            log.warn(ResultCode.FAILED.toString()+"受影响的行数不等于1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void subOneArticleCountById(Long id) {
        if(id==null||id<=0){
            log.info(ResultCode.FAILED_USER_BOARD_APRICLR_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_BOARD_APRICLR_COUNT));
        }
        Board board = boardMapper.selectByPrimaryKey(id);
        if(board==null){
            log.warn(ResultCode.ERROR_IS_NULL.toString()+" board id:"+id);
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        //更新帖子数量
        Board updateBoard =new Board();
        updateBoard.setId(board.getId());
        updateBoard.setArticleCount(board.getArticleCount()-1);
        boardMapper.updateByPrimaryKeySelective(updateBoard);
        //执行dao，实时更新
        int row =boardMapper.updateByPrimaryKeySelective(updateBoard);
        //受影响的行数不等于1
        if(row!=1){
            log.warn(ResultCode.FAILED.toString()+"受影响的行数不等于1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }
}
