package com.botregistrar.main;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Tools {
	public static void sendOTP(String to,String text) throws UnirestException, UnsupportedEncodingException{
		
		String url ="https://api.gupshup.io/sm/api/sms/msg";
		
		String paramname[] = {"source","destination","text"};
		String paramvalue[] = {"Botregistry","+91"+to,text};
		
		StringBuilder paramstr = new StringBuilder();
		for(int i = 0; i < paramname.length; i++) {
			if(i > 0)
				paramstr.append("&");
			paramstr.append(paramname[i]);
			paramstr.append("=");
			paramstr.append(URLEncoder.encode(paramvalue[i],"UTF-8"));
		}
		
		
		HttpResponse<String> response = Unirest.put("https://api.gupshup.io/sm/api/sms/msg")
				  .header("apikey", "45559405050540c5c03b978227cd144b")
				  .header("content-type", "application/x-www-form-urlencoded")
				  .body(paramstr.toString())
				  .asString();
		
	}

	public static String generateOtp() {
		Random r = new Random();
		r.nextInt(10);
		String otp = r.nextInt(10)+""+r.nextInt(10)+""+r.nextInt(10)+""+r.nextInt(10);
		return otp;
	}

}
