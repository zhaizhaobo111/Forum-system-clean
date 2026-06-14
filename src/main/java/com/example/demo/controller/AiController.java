package com.example.demo.controller;

import com.example.demo.common.AppResult;
import com.example.demo.services.AiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "AI接口")
@Slf4j
@RestController
@RequestMapping("ai")
public class AiController {

    @Resource
    private AiService aiService;

    @ApiOperation("生成帖子智能摘要")
    @PostMapping("/summary")
    public AppResult<String> generateSummary(
            @ApiParam("帖子内容") @RequestParam("content") String content) {
        String summary = aiService.generateSummary(content);
        return AppResult.success(summary);
    }


    @ApiOperation("AI服务健康检查")
    @GetMapping("/health")
    public AppResult<String> health() {
        log.info("开始检查AI服务健康状态");
        boolean healthy = aiService.checkHealth();
        log.info("AI服务健康状态: {}", healthy);
        if (healthy) {
            return AppResult.success("AI服务正常");
        } else {
            return AppResult.failed("AI服务异常");
        }
    }
}
