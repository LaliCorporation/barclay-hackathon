package com.botregistrar.main;

import java.io.UnsupportedEncodingException;

import com.botregistrar.persistence.RegistryData;
import com.mashape.unirest.http.exceptions.UnirestException;

public class RegManager {
	
	public boolean makeEntry(String mobile,String type,String botname,String seqid){
		
		RegistryData registryData = new RegistryData();
		String otp ="9999";
		//String otp = Tools.generateOtp();
		
		if(registryData.makeEntry(mobile, type, botname, seqid,otp)){
			try {
				Tools.sendOTP(mobile, "Otp for bot registry : "+Tools.generateOtp());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				e.printStackTrace();
			} catch (UnirestException e) {
				e.printStackTrace();
			}
		}else{
			return false;
		}
		return true;
	}
	
	public boolean validateOtp(String mobile,String seqid,String otp){
		RegistryData registryData = new RegistryData();
		
		return registryData.validateOtp(mobile, otp, seqid);
	}
	
	public String getBotname(String mobile,String type){
		
		RegistryData registryData = new RegistryData();
		
		return registryData.getBotname(mobile, type);
	}
	

}
