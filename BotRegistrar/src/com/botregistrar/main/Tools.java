package com.botregistrar.main;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

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
	
	public static void sendOtpByTwilio(String to,String text) throws TwilioRestException{
		   String accountSid = "";
		   String authTOken = "";
		
		   TwilioRestClient client = new TwilioRestClient(accountSid, authTOken);

		   // Build the parameters
		   List<NameValuePair> params = new ArrayList<NameValuePair>();
		   params.add(new BasicNameValuePair("To", "+91"+to));
		   params.add(new BasicNameValuePair("From", "+14159420699"));
		   params.add(new BasicNameValuePair("Body", text));
		   
		   MessageFactory messageFactory = client.getAccount().getMessageFactory();
		   Message message = messageFactory.create(params);
		   System.out.println("twilio send msg id : "+message.getSid());
	}

	public static String generateOtp() {
		Random r = new Random();
		r.nextInt(10);
		String otp = r.nextInt(10)+""+r.nextInt(10)+""+r.nextInt(10)+""+r.nextInt(10);
		return otp;
	}

}
