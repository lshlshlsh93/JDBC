package com.jdbc.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 
 * @Description 操作数据库的工具类
 * @author LSH QQ:1945773750
 * @version
 * @date 2021-1-17 19:49:22
 *
 */
public class JdbcUtils {

	/**
	 * 
	 *
	 * @Description 获取连接的方法
	 * @author LSH
	 * @date 2021-1-17 11:43:17
	 * @return
	 * @throws Exception
	 *
	 */
	public static Connection getConnection() throws Exception {

		Connection connection = null;
		PreparedStatement ps = null;

		// 1.读取配置文件
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");

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

		return connection;

	}

	/**
	 * 
	 *
	 * @Description 关闭资源的方法
	 * @author LSH
	 * @date 2021-1-17 11:42:46
	 * @param connection
	 * @param ps
	 *
	 */
	public static void closeSource(Connection connection, PreparedStatement ps) {

		try {
			if (ps != null)
				ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 *
	 * @Description 关闭资源的方法
	 * @author LSH
	 * @date 2021-1-17 11:43:03
	 * @param connection
	 * @param ps
	 * @param rs
	 *
	 */
	public static void closeSource(Connection connection, PreparedStatement ps, ResultSet rs) {

		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (ps != null)
				ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
