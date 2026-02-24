package com.example.demo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
//spring管理
@Configuration
@MapperScan("com.example.demo.dao")
public class MybatisConfig {
}
