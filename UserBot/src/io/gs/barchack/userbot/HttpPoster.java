package io.gs.barchack.userbot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpPoster {
	// HTTP GET request
	public static void sendMessage(String botname, String context, String message) throws Exception {
		System.out.println("Making API call...");
		
		String url = "https://dev-api.gupshup.io/sm/api/bot/"+botname+"/msg";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("apikey", "2d045dc742134d98cd475cd63196c6c1");
		
		String urlParameters = "context="
				+ URLEncoder.encode(context, "UTF-8")
				+ "&botname="
				+ URLEncoder.encode(botname, "UTF-8")
				+ "&message="
				+ URLEncoder.encode(message, "UTF-8")
				;
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());

	}
	public static void createBot(String botname) throws Exception {
		System.out.println("Making API call...");
		
		String url = "https://dev-api.gupshup.io/sm/api/bot/"+botname+"/create";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("PUT");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("apikey", "2d045dc742134d98cd475cd63196c6c1");
		
		con.setDoOutput(false);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'PUT' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());

	}
	public static void setCallback(String botname, String callback) throws Exception {
		System.out.println("Making API call...");
		
		String url = "https://dev-api.gupshup.io/sm/api/v1/bot/"+botname+"/settings/type/callback-get";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("apikey", "2d045dc742134d98cd475cd63196c6c1");
		
		String urlParameters = "url="
				+ URLEncoder.encode(callback, "UTF-8")
				;
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());

	}
	public static JSONObject makeTransaction(String accid, String merchant, double amount) {
		System.out.println("Making API call...");
		JSONObject toret = new JSONObject();
		
		try {
			String url = "https://c3f3c77d.ngrok.io/BarclaysAPIs/accounts/trans/" + accid;
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	
			//add reuqest header
			con.setRequestMethod("POST");
			
			String urlParameters = "toId="
					+ URLEncoder.encode(merchant, "UTF-8")
					+ "&amount="
					+ URLEncoder.encode(amount + "", "UTF-8")
					;
			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
	
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);
	
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());
			
			try {
				JSONObject jo = new JSONObject(response.toString());
				if(jo.has("err")) {
					toret.put("success", false);
					toret.put("msg", "Bank response malformed");
					return toret;
				} else {
					toret.put("success", true);
				}
			} catch (JSONException ex) {
				toret.put("success", false);
				toret.put("msg", "Bank response malformed");
			}
		} catch (IOException e) {
			toret.put("success", false);
			toret.put("msg", "Connection Problem");
		}
		return toret;
	}
	public static String getCustomerId(String phone) {
		System.out.println("Making API call...");
		
		try {
			String url = "https://c3f3c77d.ngrok.io/BarclaysAPIs/customers/phone/" + phone;
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	
			//add reuqest header
			con.setRequestMethod("GET");			
			con.setDoOutput(false);
	
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
	
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());
			
			try {
				JSONObject jo = new JSONObject(response.toString());
				return jo.getString("id");
			} catch (Exception e) {}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getAccountId(String customerid) {
		System.out.println("Making API call...");
		
		try {
			String url = "https://c3f3c77d.ngrok.io/BarclaysAPIs/accounts/custid/" + customerid;
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	
			//add reuqest header
			con.setRequestMethod("GET");			
			con.setDoOutput(false);
	
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
	
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());
			
			try {
				JSONObject jo = new JSONObject(response.toString());
				return jo.getString("id");
			} catch (Exception e) {}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		createBot("hahabot");
		setCallback("hahabot", "https://c3f3c77d.ngrok.io/UserBot/simplebot/nir1");
	}
}
