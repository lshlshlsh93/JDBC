package com.jdbc2.preparedstatement.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import org.junit.Test;
import com.jdbc.util.JdbcUtils;

public class PrepareStatementUpdateTest {

	@Test
	public void testCommonUpdate() {
		 //String sql = "delete from customer where id = ?";
		//update(sql, 5);
		String sql = "update `order` set order_name = ? where order_id = ?";
		update(sql,"FF",2);
	}
	// 通用的增删改
	public void update(String sql, Object... args) {// SQL语句中占位符的个数与可变参数的个数相同
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			// 1 获取数据库连接
			connection = JdbcUtils.getConnection();
			// 2 预编译SQL语句，返回PreparedStatement的实例化对象
			ps = connection.prepareStatement(sql);
			// 3 填充占位符
			for (int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]); // 小心参数声明错误
			}
			// 4.执行SQL
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 5.关闭资源
			JdbcUtils.closeSource(connection, ps);
		}

	}

	// 修改customer表中的一条数据
	@Test
	public void UpdateTest() {

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			// 1.获取连接
			connection = JdbcUtils.getConnection();
			String sql = "update customer set name = ? where id = ?";
			// 2.预编译SQL语句，返回PreparedStatement的实例化对象
			ps = connection.prepareStatement(sql);
			// 2.1 填充占位符
			ps.setObject(1, "莫扎特");
			ps.setObject(2, 5);
			// 3.执行SQL语句
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 4.关闭资源
			JdbcUtils.closeSource(connection, ps);
		}

	}

}
