package com.jdbc2.preparedstatement.crud;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.junit.Test;

/**
 * 
 * @Description 对数据库添加数据操作
 * @author LSH QQ:1945773750
 * @version
 * @date 2021-1-17 1:20:24
 *
 */
/*
 * 
 * 
 * 使用PreparedStatement替换Statement实现对数据库的增删改操作 增删改 -->返回一个数字 1代表成功 0 代表失败 查 -->
 * 返回一个结果集
 * 
 */
public class PrepareStatementInsertTest {

	@Test
	public void insertTest() {

		PreparedStatement ps = null;
		Connection connection = null;
		
		try {
			// 1.读取配置文件
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
			// 加载配置文件
			Properties pros = new Properties();
			pros.load(is);
			String user = pros.getProperty("user");
			String password = pros.getProperty("password");
			String url = pros.getProperty("url");
			String driver = pros.getProperty("driver");
			// 2.加载驱动
			Class.forName(driver);
			// 3.获取连接
			connection = DriverManager.getConnection(url, user, password);
			// System.out.println(connection);

			// 4.预编译SQL语句，返回PreparedStatement的实例
			ps = connection.prepareStatement(driver);
			String sql = "insert into people(name,date,email) values (?,?,?)"; // ?表示占位符
			connection.prepareStatement(sql);
			// 5.填充占位符
			ps.setString(1, "liming");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			java.sql.Date date = (java.sql.Date) sdf.parse("1000-11-11");
			ps.setDate(2, (java.sql.Date) new Date(date.getTime()));
			ps.setString(3, "lm@qq.com");

			// 6.执行SQL语句
			int executeUpdate = ps.executeUpdate(sql);
			System.out.println(executeUpdate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 7.关闭资源
		finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
