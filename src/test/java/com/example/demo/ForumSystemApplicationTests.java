package com.example.demo;

import com.example.demo.dao.UserMapper;
import com.example.demo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class ForumSystemApplicationTests {
	@Resource
	private UserMapper userMapper;
	@Test
	public void testMybatis () {
		User user = userMapper.selectByPrimaryKey(1l);
		System.out.println(user.toString());
		System.out.println(user.getUsername());
	}
	@Resource
	private DataSource dataSource;
	/*@Test
	void testConnection() throws SQLException {
		System.out.println("dataSource ="+ dataSource.getClass());
		//获取数据库连接
		Connection connection = dataSource.getConnection();
		System.out.println("connection ="+ connection);
	}*/

	@Test
	void contextLoads() {
	}

}
