package com.jdbc.exercise;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;
import org.junit.Test;
import com.jdbc.bean.Student;
import com.jdbc.util.JdbcUtils;


public class UpdateStudent {
	
	 //@Test
	public void testInsert() {

		Scanner scanner = new Scanner(System.in);
		System.out.print("请输入4级/6级：");
		String type = scanner.next();
		System.out.print("请输入身份证号码：");
		String IDCard = scanner.next();
		System.out.print("请输入准考证号：");
		String examCard = scanner.next();
		System.out.print("请输入学生姓名：");
		String studentName = scanner.next();
		System.out.print("请输入城市：");
		String location = scanner.next();
		System.out.print("请输入成绩：");
		String grade = scanner.next();
		scanner.close();
		
		String sql = "insert into examstudent (`type`,`idcard`,`examcard`,`studentname`,`location`,`grade`) values(?,?,?,?,?,?);";
		int insertStudent = update(sql,type,IDCard,examCard,studentName,location,grade);
		
		if(insertStudent > 0) {
			System.out.println("插入成功！");
		}else {
			System.out.println("插入失败！");
		}
		
	}
	
	
	/**
	 * 
	 *
	 * @Description 通用的增删改操作
	 * @author LSH
	 * @date 2021-1-18 22:01:28
	 * @param sql SQL语句
	 * @param args 可变参数
	 * @return
	 *
	 */
	
	public static int update(String sql, Object... args) {// SQL语句中占位符的个数与可变参数的个数相同
		
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
				return ps.executeUpdate();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// 5.关闭资源
				JdbcUtils.closeSource(connection, ps);
			}
			return 0;
		}
	
	/*
	 * 			根据身份证号码或者准考证号查询学生成绩信息
	 */
	@Test
	public void queryWithIDCardOrExamCard() {
		
		System.out.println("请选择您要输入的类型：");
		System.out.println("a.准考证号");
		System.out.println("b.身份证号");
		
		Scanner scanner = new Scanner(System.in);
		String selection = scanner.next();
		
		if("a".equalsIgnoreCase(selection)) {
			
			System.out.println("请输入准考证号");
			String examCard= scanner.next();
			
			String sql = "select `FlowID` flowID,`Type` type,`IDCard`,`ExamCard` "
					+ "examCard,`StudentName` studentName,`Location` location,`Grade` grade "
					+ "from examstudent where `ExamCard` = ? ";
			
			Student student = getInstance(Student.class,sql,examCard);
			if (student != null) {
				System.out.println(student);
			}else {
				System.out.println("输入的准考证号有误！,请检查您的准考证号是否正确。");
			}
			
		}else if("b".equalsIgnoreCase(selection)) {
			System.out.println("请输入身份证号");
			String IDCard= scanner.next();
			
			String sql = "select `FlowID` flowID,`Type` type,`IDCard`,`ExamCard` "
					+ "examCard,`StudentName` studentName,`Location` location,`Grade` grade "
					+ "from examstudent where `IDCard` = ? ";
			
			Student student = getInstance(Student.class,sql,IDCard);
			if (student != null) {
				System.out.println(student);
			}else {
				System.out.println("输入的身份证号有误！,请检查您的准考证号是否正确。");
			}
		}else {
			System.out.println("您的输入有误，请重新进入程序 ");
		}
		scanner.close();
	}
	
	/**
	 * 
	 *
	 * @Description
	 * @author LSH
	 * @date 2021-1-18 22:47:43
	 * @param <T>  泛型
	 * @param clazz 类
	 * @param sql  SQL语句
	 * @param args 可变参数也就是占位符
	 * @return
	 *
	 */
	public static <T> T getInstance(Class<T> clazz, String sql, Object... args) {
		
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
					// 给t对象指定的 columnName 属性 ，赋值为columnValue ：通过反射的方式
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
	 * @Description 通过准考证号删除指定的学生信息
	 * @author LSH
	 * @date 2021-1-18 23:14:56
	 *
	 */
	@Test
	public void deleteByExamCard() {
		System.out.println("请输入学生的准考证号：");
		Scanner scanner = new Scanner(System.in);
		
		String examCard = scanner.next();
		String sql = "select `FlowID` flowID,`Type` type,`IDCard`,`ExamCard` "
				+ "examCard,`StudentName` studentName,`Location` location,`Grade` grade "
				+ "from examstudent where `ExamCard` = ? ";
		
		Student student = getInstance(Student.class,sql,examCard);
		
		if(student == null) {
			System.out.println("查无此人，请重新输入！");
		}else {
			String sql1 = "delete from examstudment where `ExamCard` = ? ;";
			int deleteCount = update(sql1,examCard );
			
			if( deleteCount > 0 ) {
				System.out.println("删除成功！");
			}		
		}
		scanner.close();
	}
	

	/**
	 * 
	 *
	 * @Description 优化以后的删除操作
	 * @author LSH
	 * @date 2021-1-18 23:14:56
	 * 
	 */
	@Test
	public void deleteByExamCard1() {
	
		System.out.println("请输入学生的准考证号：");
		Scanner scanner = new Scanner(System.in);
		String examCard = scanner.next();
		String sql1 = "delete from examstudent where `ExamCard` = ? ;";
		int deleteCount = update(sql1,examCard );
		
		if( deleteCount > 0 ) {
			System.out.println("删除成功！");
		}else {
			System.out.println("查无此人，请重新输入！");
		}
		scanner.close();
	}
	
	//------------------------------------
	}
	

