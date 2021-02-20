package com.jdbc.blob;

import java.sql.Connection;
import java.sql.PreparedStatement;
import org.junit.Test;
import com.jdbc.util.JdbcUtils;

/**
 * 
 * @Description 使用PrepareStatement实现批量操作
 * @author LSH QQ:1945773750
 * @version
 * @date 2021-1-19 13:49:02
 *
 * 
 */
/*
 * ` ` CREATE TABLE IF NOT EXISTS goods (id INT PRIMARY KEY AUTO_INCREMENT, NAME
 * VARCHAR(25) ); 向goods表中插入20000条数据
 * 
 * 方式一： 使用Statement Connection connection = JdbcUtils.getConnection(); Statement
 * state = connection.createStatement(); for( int i = 0; i < 20000;i++){ String
 * sql = "insert into goods(name) values('name_" + i + "');  ";
 * state.execute(sql); }
 * 
 */
public class InsertTest {
	/*
	 * 方式二： 使用PreparedStatement
	 */
	//@Test
	public void insert1() {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			long begin = System.currentTimeMillis();
			connection = JdbcUtils.getConnection();
			String sql = "insert into goods(name)values(?);";
			ps = connection.prepareStatement(sql);
			for (int i = 1; i <= 20000; i++) {
				ps.setObject(1, "name_" + i);
				ps.execute();
			}
			long end = System.currentTimeMillis();
			
			System.out.println("花费的时间为：" + (end - begin )+"毫秒"); //花费的时间为：26049
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeSource(connection, ps);
		}
	}
	/*
	 *  插入方式三： 
	 *  1.addBatch() , executeBatch() , clearBatch() 
	 *  2.MySQL服务器默认是关闭批处理的，我们需要通过一个参数，让MySQL开启批处理服务
	 *      ?rewriteBantchedStatements=true 写在配置文件的URL后面
	 */
	//@Test
	public void insert2() {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			long begin = System.currentTimeMillis();
			connection = JdbcUtils.getConnection();
			
	
			String sql = "insert into goods(name)values(?);";
			ps = connection.prepareStatement(sql);
			
			for (int i = 1; i <= 20000; i++) {
				ps.setObject(1, "name_" + i);
					//1. 攒SQL
				ps.addBatch();
				if(i % 500 == 0) {
					//2.执行-->Statement
					ps.executeBatch();
					//3.清空batch --> Statement
					ps.clearBatch();
				}	
			}
			
			long end = System.currentTimeMillis();
			
			System.out.println("花费的时间为：" + (end - begin )+"毫秒"); //花费的时间为：26049 --> 花费的时间为：23771毫秒
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeSource(connection, ps);
		}
	}
	/*
	 *  	插入方式四： 
	 */
	@Test
	public void insert3() {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			long begin = System.currentTimeMillis();
			connection = JdbcUtils.getConnection();
			
			//设置不允许自动提交数据
			connection.setAutoCommit(false);
			
			String sql = "insert into goods(name)values(?);";
			ps = connection.prepareStatement(sql);
			
			for (int i = 1; i <= 1000000; i++) {
				ps.setObject(1, "name_" + i);
					//1. 攒SQL
				ps.addBatch();
				if(i % 500 == 0) {
					//2.执行-->Statement
					ps.executeBatch();
					//3.清空batch --> Statement
					ps.clearBatch();
				}	
			}
			//提交数据
			connection.commit();
			long end = System.currentTimeMillis();
			
			System.out.println("花费的时间为：" + (end - begin )+"毫秒"); 
			//20000   花费的时间为：26049 --> 花费的时间为：23771毫秒   -->花费的时间为：2174毫秒
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeSource(connection, ps); //1000000 花费的时间为：85476毫秒
		}
	}
	
	
	
	
	
	
	
}
