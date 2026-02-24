package com.example.demo.services.impl;

import com.example.demo.dao.BoardMapper;
import com.example.demo.model.Board;
import com.example.demo.services.IBoradService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class BoardServiceImplTest {
    @Resource
private IBoradService boradService;
    @Resource
    private ObjectMapper objectMapper;
    /*@Test
    void selectByNum() {
        List<Board> boards = boradService.selectByNum(5);
        System.out.println(boards);
    }*/

    @Test
    @Transactional//测试之后回滚数据库操作
    void addOneArticleCountById() {
        boradService.addOneArticleCountById(1L);
    }

    @Test
    void selectById() throws JsonProcessingException {
        Board board = boradService.selectById(1l);
        System.out.println(objectMapper.writeValueAsString(board));
    }

    @Test
    @Transactional
    void subOneArticleCountById() {
        boradService.subOneArticleCountById(1l);
        System.out.println("删除帖子成功");
    }
}