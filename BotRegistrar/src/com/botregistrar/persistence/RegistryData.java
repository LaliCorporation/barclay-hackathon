package com.botregistrar.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RegistryData {
	private static final String OTP_TABLE_CREATE = "CREATE TABLE otp ("
			+ "id INT NOT NULL AUTO_INCREMENT, "
			+ "phone VARCHAR(64), "
			+ "type VARCHAR(64), "
			+ "botname VARCHAR(64), "
			+ "seqid VARCHAR(64), "
			+ "otp VARCHAR(64), "
			+ "reqts DATETIME, "
			+ "PRIMARY KEY ( id ));";
	private static final String MAPPINGS_TABLE_CREATE = "CREATE TABLE mappings ("
			+ "id INT NOT NULL AUTO_INCREMENT, "
			+ "phone VARCHAR(64), "
			+ "type VARCHAR(64), "
			+ "botname VARCHAR(64), "
			+ "PRIMARY KEY ( id ));";
	
	private final String MAKE_OTP_ENTRY="INSERT into otp"
			+ "(phone,type,botname,seqid,otp,reqts) values(?,?,?,?,?,?)";
	

	private final String VALIDATE_OTP = "SELECT type,botname FROM otp WHERE phone = ? AND seqid = ? AND otp = ?";
	private final String DELETE_OTP = "DELETE FROM otp WHERE phone = ? AND seqid = ? AND otp = ?";
	
	private final String DELETE_MAPPINGS = "DELETE FROM mappings WHERE phone = ? AND type = ?";
	private final String MAKE_BOT_ENTRY="INSERT into mappings"
			+ "(phone,type,botname) values(?,?,?)";
	private final String GET_BOTNAME = "SELECT botname FROM mappings WHERE phone=? AND type=?";

//	private final String MAKE_ENTRY="insert into botregistry(mobile,type,botname,seqid,otp) values(?,?,?,?,?) on DUPLICATE KEY update type=?,botname=?,isValid=false,seqid=?,otp=?";
//	private final String VALIDATE_OTP = "update botregistry set isValid = true where mobile = ? AND seqid = ? AND otp = ?";
//	private final String GET_BOTNAME = "select botname from botregistry where mobile=? AND type=? AND isValid=true";
	
	private void tryClose(Connection connection) {
		if (connection != null) {
			try {
				connection.commit();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean makeEntry(String mobile,String type, String botname, String seqid,String otp ){
		Connection connection = null;
		PreparedStatement prepstatement = null;
		try {
			connection = MySqlDataSource.getInstance().getConnection();
			
			prepstatement = connection.prepareStatement(MAKE_OTP_ENTRY);
			prepstatement.setString(1, mobile);
			prepstatement.setString(2, type);
			prepstatement.setString(3, botname);
			prepstatement.setString(4, seqid);
			prepstatement.setString(5, otp);
			prepstatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			
			int rowsInserted = prepstatement.executeUpdate();
			
			if (rowsInserted > 0)
				return true;
		} catch(Exception e){
			e.printStackTrace();
		}
		finally {
			tryClose(connection);
		}
		return false;
	}
	
	public boolean validateOtp(String mobile,String otp,String seqid){
		Connection connection = null;
		PreparedStatement prepstatement = null;
		try {
			connection = MySqlDataSource.getInstance().getConnection();
			
			prepstatement = connection.prepareStatement(VALIDATE_OTP);
			prepstatement.setString(1, mobile);
			prepstatement.setString(2, seqid);
			prepstatement.setString(3, otp);
			
			ResultSet rs = prepstatement.executeQuery();
			
			if(! rs.next())
				return false;
			
			String type = rs.getString(1);
			String botname = rs.getString(2);
			
			prepstatement = connection.prepareStatement(DELETE_OTP);
			prepstatement.setString(1, mobile);
			prepstatement.setString(2, seqid);
			prepstatement.setString(3, otp);
			prepstatement.executeUpdate();
			
			prepstatement = connection.prepareStatement(DELETE_MAPPINGS);
			prepstatement.setString(1, mobile);
			prepstatement.setString(2, type);
			prepstatement.executeUpdate();

			prepstatement = connection.prepareStatement(MAKE_BOT_ENTRY);
			prepstatement.setString(1, mobile);
			prepstatement.setString(2, type);
			prepstatement.setString(3, botname);
			prepstatement.executeUpdate();
			
		} catch(Exception e){
			e.printStackTrace();
		}
		finally {
			tryClose(connection);
		}
		return false;
	}
	
	public String getBotname(String mobile,String type){
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepstatement = null;
		try {
			connection = MySqlDataSource.getInstance().getConnection();
			prepstatement = connection.prepareStatement(GET_BOTNAME);
			prepstatement.setString(1, mobile);
			prepstatement.setString(2, type);
			resultSet = prepstatement.executeQuery();
			
			if (resultSet.next()) {
				String botName = resultSet.getString("botname");
				return botName;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		finally {
			tryClose(connection);
		}
		return null;
	}
}
