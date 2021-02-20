package com.jdbc2.preparedstatement.crud;
/*
 * 
			 * 针对于表的字段名与类的属性名不相同的情况：❤❤
			 * 1.必须声明SQL语句时，使用类的属性名来命名字段的别名❤❤	
			 * 2.在使用ResultSetMetaData时，需要使用getColumnLabel()来替换getColumnName()方法，
			 * 	获取列的别名。
			 * 	说明：如果SQL中没有给字段起别名，getColumnLabel()获取的就是列名
 * 
 */
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import org.junit.Test;

import com.jdbc.bean.Order;
import com.jdbc.util.JdbcUtils;

public class OrderForQuery {

	@Test
	public void testOrderForQuery() {
		
		String sql = "select `order_id` orderId,`order_name` orderName,`order_date` orderDate from `order` where `order_id` = ?";
		orderForQuery(sql, 1);
		
		testQuery1();
		
	}

	/**
	 * 
	 * @Description 针对order表的通用操作
	 * @author LSH QQ:1945773750
	 * @version
	 * @throws Exception
	 * @date 2021-1-17 22:42:55
	 *
	 */

	public Order orderForQuery(String sql, Object... args) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 获取连接
			conn = JdbcUtils.getConnection();
			// 预编译SQL语句，返回PreparedStatement的实例化对象
			ps = conn.prepareStatement(sql);
			// 填充占位符
			for (int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]);
			}
			// 执行，返回结果集
			rs = ps.executeQuery();
			while (rs.next()) {
				Order order = new Order();
				// 获取结果集的元数据
				ResultSetMetaData rsmd = rs.getMetaData();
				// 获取列数
				int columnCount = rsmd.getColumnCount();
				for (int i = 0; i < columnCount; i++) {
					// 获取每一列的列值 : 通过ResultSet
					Object columnValue = rs.getObject(i + 1); // 参数易错！！！
					// 通过ResultSetMetaData
					// 获取列的列名:	getColumnName()
					// 获取列的别名或列名:	getColumnLabel()
					// String columnName = rsmd.getColumnName(i + 1);//---不推荐使用
					String columnLabel = rsmd.getColumnLabel(i + 1); // 参数易错！！！
					// 通过反射，将对象指定属性名columnName的属性赋值给指定值columnValue
					Field field = Order.class.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(order, columnValue);
				}
				return order;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		JdbcUtils.closeSource(conn, ps, rs);
		return null;

	}

	@Test
	public void testQuery1() {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = JdbcUtils.getConnection();
			String sql = "select `order_id`,`order_name`,`order_date` from `order` where `order_id` = ?";
			ps = connection.prepareStatement(sql);
			ps.setObject(1, 1);
			rs = ps.executeQuery();
			if (rs.next()) {
				int id = (int) rs.getObject(1);
				String name = (String) rs.getObject(2);
				Date date = (Date) rs.getObject(3);
				Order order = new Order(id, name, date);

				System.out.println(order);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeSource(connection, ps, rs);
		}

	}

}
