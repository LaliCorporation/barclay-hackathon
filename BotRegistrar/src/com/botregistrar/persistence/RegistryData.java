package com.botregistrar.persistence;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
public class RegistryData {
	
	private final String MAKE_ENTRY="insert into botregistry(mobile,type,botname,seqid,otp) values(?,?,?,?,?) on DUPLICATE KEY update type=?,botname=?,isValid=false,seqid=?,otp=?";
	private final String VALIDATE_OTP = "update botregistry set isValid = true where mobile = ? AND seqid = ? AND otp = ?";
	private final String GET_BOTNAME = "select botname from botregistry where mobile=? AND type=? AND isValid=true";
	
	public boolean makeEntry(String mobile,String type, String botname, String seqid,String otp ){
		Connection connection = null;
		PreparedStatement prepstatement = null;
		try {
			connection = MySqlDataSource.getInstance().getConnection();
			
			prepstatement = connection.prepareStatement(MAKE_ENTRY);
			prepstatement.setString(1, mobile);
			prepstatement.setString(2, type);
			prepstatement.setString(3, botname);
			prepstatement.setString(4, seqid);
			prepstatement.setString(5, otp);
			prepstatement.setString(6, type);
			prepstatement.setString(7, botname);
			prepstatement.setString(8, seqid);
			prepstatement.setString(9, otp);
			
			int rowsInserted = prepstatement.executeUpdate();
			if (rowsInserted > 0) {
				return true;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(prepstatement != null)
			{
				try
				{
					prepstatement.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
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
			
			int rowsInserted = prepstatement.executeUpdate();
			if (rowsInserted > 0) {
				return true;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(prepstatement != null)
			{
				try
				{
					prepstatement.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
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
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(prepstatement != null)
			{
				try
				{
					prepstatement.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return null;
		
	}

}
