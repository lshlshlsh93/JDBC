package com.jdbc.exercise;

/*
 * 
 * 从控制台向customer表中插入一条数据
 */
import java.sql.*;
import java.util.Scanner;

import org.junit.Test;
import com.jdbc.util.JdbcUtils;

public class ExerInsert {

	@Test
	public void testInsert() {

		Scanner scanner = new Scanner(System.in);
		System.out.print("请输入姓名:");
		String name = scanner.next();
		System.out.print("请输入邮箱:");
		String email = scanner.next();
		System.out.print("请输入生日:");
		String date = scanner.next(); // YYYY-MM-DD
		String sql = "insert into customer (name,email,birth) values(?,?,?);";
		int insertCount = update(sql, name, email, date);
		if (insertCount > 0) {
			System.out.println("插入成功！");
		} else {
			System.out.println("插入失败！");
		}
		scanner.close();
	}

	/**
	 * 
	 *
	 * @Description 通用的增删改操作
	 * @author LSH
	 * @date 2021-1-18 14:11:09
	 * @param sql  SQL语句
	 * @param args 占位符
	 *
	 */
	public static int update(String sql, Object... args) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			// 1.获取数据库连接
			conn = JdbcUtils.getConnection();
			// 2.预编译SQL语句，返回PreparedStatement的实例化对象
			ps = conn.prepareStatement(sql);
			// 3.填充占位符
			for (int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]); // 参数易错
			}
			// 4.执行SQL语句
			/*
			 * execute() 如果执行的是增删改操作，没有返回结果集，则此方法返回false 如果执行的是查询操作，返回结果集，则此方法返回true
			 * Returns: true if the first result is a ResultSetobject; false if the first
			 * result is an update count or there is no result
			 * 
			 */

			// ps.execute();

			/*
			 * executeUpdate(); Returns: either (1) the row count for SQL Data Manipulation
			 * Language (DML) statementsor (2) 0 for SQL statements that return nothing
			 */
			return ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 5.关闭资源
			JdbcUtils.closeSource(conn, ps);
		}
		return 0;
	}

}
