package com.jdbc2.preparedstatement.crud;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.jdbc.bean.Customer;
import com.jdbc.bean.Order;
import com.jdbc.util.JdbcUtils;

/**
 * 
 * @Description 使用PreparedStatement实现对不同表的通用查询操作
 * @author LSH QQ:1945773750
 * @version
 * @date 2021-1-18 12:36:36
 *
 */
public class PreparedStatementQuery {
	
	@Test
	/**
	 * 
	 *
	 * @Description 对通用查询表的一条记录的测试
	 * @author LSH
	 * @date 2021-1-18 13:18:10
	 *
	 */
	public void testGetInstance() {

		// 对customer表的查询
		//String sql = "select `id`,`name`,`email`,`birth` from customer where id = ? ";
		//Customer customer = getInstance(Customer.class, sql, 7);
		//System.out.println(customer);

		// 对order表的查询
		//String sql1 = "select `order_id` orderId,`order_name` orderName,`order_date` orderDate from `order` where `order_id` = ? ";
		//Order order = getInstance(Order.class, sql1, 2);
		//System.out.println(order);
		// String SQL = "select `order_id` orderId,`order_name` orderName,`order_date`
		// orderDate from `order` where `order_id` = ?";

	}

	/**
	 * 
	 *
	 * @Description 针对不同表的查询方法,返回表中的一条记录
	 * @author LSH
	 * @date 2021-1-18 12:49:21
	 * @param <T>
	 * @param clazz 传入的类
	 * @param sql   SQL语句
	 * @param args  填充可变参数也就是占位符
	 * @return
	 *
	 */
	public <T> T getInstance(Class<T> clazz, String sql, Object... args) {
		
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

				T t = clazz.newInstance();
				// 处理结果集每一行数据的每一个列
				for (int i = 0; i < columnCount; i++) {
					Object columnValue = rs.getObject(i + 1); // 参数易错

					/*
					 * 难点
					 */

					// 获取每个列的列名
					// String columnName = rsmd.getColumnName(i + 1);
					String columnLabel = rsmd.getColumnLabel(i + 1); // 参数易错
					// 给t对象指定的 columnName 属性 ，赋值为columnValue ：通过反射
					Field field = clazz.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(t, columnValue);
				}
				return t;
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
	  * @Description 对通用查询表的多条记录的测试
	  * @author LSH
	  * @date 2021-1-18 13:18:03
	  *
	  */ 
	@Test
	public void testGetForList() {
		// 对customer表的查询
		 String sql = "select `id`,`name`,`email`,`birth` from customer where id < ? ";
		 List<Customer> list = getForList(Customer.class,sql,6);
		 list.forEach(System.out::println);
		 String sql1 = "select `id`,`name`,`email`,`birth` from customer ";
		 List<Customer> list1 = getForList(Customer.class,sql1);
		 list1.forEach(System.out::println);
		 
		// 对order表的查询
		 String sql2 = "select `order_id` orderId,`order_name` orderName,`order_date` orderDate from `order`  ";
		 List<Order> orderList = getForList(Order.class,sql2);
		 orderList.forEach(System.out::println);
	}
	
	
	
	/**
	 * 
	 * 
	 * @Description   对多表的查询
	 * @author LSH
	 * @date 2021-1-18 13:14:03
	 * @param <T>
	 * @param clazz 传入的类
	 * @param sql   SQL语句
	 * @param args  填充可变参数也就是占位符
	 * @return
	 *
	 */
	public  static <T> List<T> getForList(Class<T> clazz, String sql, Object... args) {

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

			// 创建集合对象
			ArrayList<T> list = new ArrayList<T>();
			while (rs.next()) {

				T t = clazz.newInstance();
				// 处理结果集每一行数据的每一个列;给t对象指定的属性赋值
				for (int i = 0; i < columnCount; i++) {
					// 获取列值
					Object columnValue = rs.getObject(i + 1); // 参数易错

					/*
					 * 难点
					 */

					// 获取每个列的列名
					// String columnName = rsmd.getColumnName(i + 1);
					String columnLabel = rsmd.getColumnLabel(i + 1); // 参数易错
					// 给t对象指定的 columnName 属性 ，赋值为columnValue ：通过反射
					Field field = clazz.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(t, columnValue);
				}
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭资源
			JdbcUtils.closeSource(conn, ps, rs);
		}
		return null;

	}

	
	
	
}
