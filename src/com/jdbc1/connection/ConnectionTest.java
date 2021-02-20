package com.jdbc1.connection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

public class ConnectionTest {

	// 连接方式一

	@Test

	public void getConnection1() throws SQLException {

		Driver driver = new com.mysql.jdbc.Driver();

		// String url =
		// "jdbc:mysql://localhost:3306/test?useUnicode=true&amp;characterEncoding=UTF-8&amp;useSSL=false";
		String url = "jdbc:mysql://localhost:3306/test?useSSL=false";
		// 将用户名和密码封装在properties中
		Properties info = new Properties();
		info.setProperty("user", "root");
		info.setProperty("password", "123456");

		Connection connect = driver.connect(url, info);
		System.out.println(connect);
	}

	// 连接方式二 对方式一的迭代 不出现第三方的API，使得程序具有更好的可移植性
	// 采用反射的方式
	@Test
	public void getConnection2() throws Exception {

		Class<?> forName = Class.forName("com.mysql.jdbc.Driver");
		Driver driver = (Driver) forName.newInstance();

		String url = "jdbc:mysql://localhost:3306/test?useSSL=false";

		Properties info = new Properties();
		info.setProperty("user", "root");
		info.setProperty("password", "123456");

		Connection connect = driver.connect(url, info);

		System.out.println(connect);

	}

	// 方式三 使用DriverManager替换Driver
	@Test
	public void getConnection3() throws Exception {
		/*
		 * 
		 * static void registerDriver(Driver driver)注册与给定的驱动程序 DriverManager 。
		 * 
		 * static Connection getConnection(String url, String user, String password)
		 * 尝试建立与给定数据库URL的连接。
		 */
		// 获取driver的实现类对象
		Class<?> forName = Class.forName("com.mysql.jdbc.Driver");
		Driver driver = (Driver) forName.newInstance();
		// 注册驱动
		DriverManager.registerDriver(driver);

		String url = "jdbc:mysql://localhost:3306/test?useSSL=false";
		String user = "root";
		String password = "123456";
		// 获取连接对象
		Connection connection = DriverManager.getConnection(url, user, password);

		System.out.println(connection);

	}

	// 方式四 
	@Test
	public void getConnection4() throws Exception {

		String url = "jdbc:mysql://localhost:3306/test?useSSL=false";
		String user = "root";
		String password = "123456";

		/*
		 * 
		 * static void registerDriver(Driver driver)注册与给定的驱动程序 DriverManager 。
		 * 
		 * static Connection getConnection(String url, String user, String password)
		 * 尝试建立与给定数据库URL的连接。
		 */

		// 获取driver的实现类对象
		Class.forName("com.mysql.jdbc.Driver");

		//Driver driver = (Driver) forName.newInstance();
		// 注册驱动
		// DriverManager.registerDriver(driver);

		/*        为什么可以省略上述操作呢？
		 * 			因为在MySQL的Driver实现类中，声明了如下操作
		 				static {
						        try {
						            java.sql.DriverManager.registerDriver(new Driver());
						        } catch (SQLException E) {
						            throw new RuntimeException("Can't register driver!");
						        }
						    }		 
		 * 
		 * 
		 */

		// 获取连接对象
		Connection connection = DriverManager.getConnection(url, user, password);

		System.out.println(connection);

	}
	
	//方式五（final) 将数据库连接需要的四个基本信息声明在配置文件中
	
	/*
	 * 	好处
	 * 1.实现了代码与数据的分离，实现了解耦
	 * 2.如果需要修改配置文件信息，可以避免程序重新打包
	 */
	
	@Test
	public void getConnection5() throws Exception {
		
		//读取配置文件
		InputStream is = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
		Properties pros = new Properties();
		pros.load(is);
		
		String user = pros.getProperty("user");
		String password = pros.getProperty("password");
		String url = pros.getProperty("url");
		String driver = pros.getProperty("driver");
		
		//加载驱动
		Class.forName(driver);
		//获取连接
		Connection connection = DriverManager.getConnection(url, user, password);
		System.out.println(connection);
		
		
	}

}
