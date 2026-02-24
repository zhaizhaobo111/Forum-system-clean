package com.example.demo.services;

import com.example.demo.model.Board;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface IBoradService {
    /**
     * 查询num条记录
     * @param num 要查询的条数
     * @return
     */
    List<Board> selectByNum( Integer num);

    /**
     * 根据板块id查询板块信息
     * @param id 板块id
     * @return
     */
    Board selectById(Long id);

    /**
     * 更新板块中的帖子数量+1
     * @param id 板块id
     */
    void addOneArticleCountById(Long id);

    /**
     * 更新板块中的帖子数量-1
     * @param id 板块id
     */
    void subOneArticleCountById(Long id);
}
