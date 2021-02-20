package com.jdbc.blob;

/**
 * 
 * @Description 测试使用PreparedStatement操作blob类型数据
 * @author LSH QQ:1945773750
 * @version
 * @date 2021-1-19 12:54:33
 *
 */

import java.io.*;
import java.sql.*;
import org.junit.Test;
import com.jdbc.bean.Customer;
import com.jdbc.util.JdbcUtils;

public class BlobTest {

	// 向customer插入blob类型的数据
	@Test
	public void testInsert() {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = JdbcUtils.getConnection();
			String sql = "insert into customer (`name`,`email`,`birth`,`photo`) values(?,?,?,?);";
			ps = connection.prepareStatement(sql);
			ps.setObject(1, "庄敏");
			ps.setObject(2, "zm@126.com");
			ps.setObject(3, "1979-01-16");
			FileInputStream is = new FileInputStream(new File("girl.jpg"));
			ps.setBlob(4, is);

			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeSource(connection, ps);
		}
	}

	// 查询
	//@Test
	public void testQuery() {
		InputStream is = null;
		FileOutputStream fos = null;
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = JdbcUtils.getConnection();
			String sql = "select `id`,`name`,`email`,`birth`,`photo` from customer where `id` = ?";
			ps = connection.prepareStatement(sql);
			ps.setObject(1, 13);
			rs = ps.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				Date birth = rs.getDate("birth");

				Customer customer = new Customer(id, name, email, birth);
				System.out.println(customer);
				// 将blob类型的字段以文件的方式保存在本地
				Blob photo = rs.getBlob("photo");
				is = photo.getBinaryStream();
				fos = new FileOutputStream("hhh.jpg");

				byte[] buffer = new byte[1024];
				int len;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			
			try {
				if(is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			JdbcUtils.closeSource(connection, ps, rs);
		}

	}

}
