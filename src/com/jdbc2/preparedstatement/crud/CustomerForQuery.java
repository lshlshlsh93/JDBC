package com.jdbc2.preparedstatement.crud;

import java.lang.reflect.Field;
import java.sql.*;
import org.junit.Test;
import com.jdbc.bean.Customer;
import com.jdbc.util.JdbcUtils;

/**
 * 
 * @Description 实现对customer表的查询操作
 * @author LSH QQ:1945773750
 * @version
 * @date 2021-1-17 20:43:23
 *
 */
public class CustomerForQuery {
	
	@Test
	public void testQueryForCustomer() {
		
		String sql = "select `id`,`name`,`email`,`birth` from customer where id = ? ";
		Object customer = queryForCustomer(sql, 1);
		System.out.println(customer);
	
	}
	
	
	/**
	 *
	 *
	 * @Description 针对customer表通用的查询  
	 * @author LSH
	 * @throws Exception
	 * @date 2021-1-17 21:42:25
	 *
	 */
	public Customer queryForCustomer(String sql, Object... args) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 获取数据库连接
			conn = JdbcUtils.getConnection();
			// 预编译SQL语句，返回PreparedStatement的实例化对象
			ps = conn.prepareStatement(sql);
			// 填充占位符
			for (int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]);
			}
			// 执行,返回结果集对象
			rs = ps.executeQuery();
			// **获取结果集的元数据ResultSetMetaData
			ResultSetMetaData rsmd = rs.getMetaData();
			// 通过ResultSetMetaData获取结果集中的列数
			int columnCount = rsmd.getColumnCount();

			if (rs.next()) {
				Customer customer = new Customer();
				// 处理结果集每一行数据的每一个列
				for (int i = 0; i < columnCount; i++) {
					Object columnValue = rs.getObject(i + 1); //参数易错
					/*
					 * 难点
					 */
					// 获取每个列的列名
					//String columnName = rsmd.getColumnName(i + 1);
					String columnLabel = rsmd.getColumnLabel(i + 1); //参数易错
					// 给customer对象指定的 columnName 属性 ，赋值为columnValue ：通过反射
					Field field = Customer.class.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(customer, columnValue);
				}
				return customer;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭资源
			JdbcUtils.closeSource(conn, ps, rs);
		}
		return null;

	}

	
	
	
	/**
	 * 
	 *
	 * @Description 测试    查询
	 * @author LSH
	 * @date 2021-1-17 22:38:56
	 *
	 */

	public void testQuery() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 1.获取数据库连接
			conn = JdbcUtils.getConnection();
			// 2.预编译SQL语句，返回PreparedStatement的实例化对象
			String sql = "select `id`,`name`,`email`,birth from customer where id = ? ";
			ps = conn.prepareStatement(sql);
			// 3.填充占位符
			ps.setObject(1, 2);
			// 4.执行,返回结果集对象
			rs = ps.executeQuery();
			// 5.处理结果集
			while (rs.next()) { // 判断结果集的下一条是否有数据，如果有返回true，指针下移，否则返回false，指针不下移

				int id = rs.getInt(1);
				String name = rs.getString(2);
				String email = rs.getString(3);
				Date birth = rs.getDate(4);
				// 处理数据
				// 方式一
				// System.out.println("id = "+ id + ",name = " + name + ",email = "+ email +
				// ",birth = " + birth );
				// 方式二
				// Object [] data = new Object[] {id, name, email, birth};

				// 方式三结果集封装到一个对象当中去(推荐)
				Customer cust = new Customer(id, name, email, birth);
				System.out.println(cust);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 6.关闭资源
			JdbcUtils.closeSource(conn, ps, rs);
		}
	}

}
