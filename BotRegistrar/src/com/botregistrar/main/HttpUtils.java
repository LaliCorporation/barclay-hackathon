package com.botregistrar.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;

public class HttpUtils {
	public static String makeGet(String baseUrl, String[] paramname, String[] paramvalues, String[] headername, String[] headervals) {
		StringBuilder urlstr = new StringBuilder(baseUrl);
		if(baseUrl.contains("?")) {
			if(! baseUrl.endsWith("&"))
				urlstr.append("&");
		} else {
			urlstr.append("?");
		}
		for(int i = 0; i < paramname.length; i++) {
			if(i > 0)
				urlstr.append("&");
			urlstr.append(paramname[i]);
			urlstr.append("=");
			urlstr.append(URLEncoder.encode(paramvalues[i]));
		}
		
		//System.out.println("Making GET:" + urlstr.toString());
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
		    URL url = new URL(urlstr.toString());

		    // (set connection and read timeouts on the connection)
		    conn = (HttpURLConnection)url.openConnection();
		    conn.setReadTimeout(30 * 1000);
		    conn.setConnectTimeout(30 * 1000);
		    
		    for(int i = 0; i < headername.length; i++)
		    	conn.setRequestProperty(headername[i], headervals[i]);
		    
		    String resp = convertStreamToString(conn.getInputStream());
		    return resp;
		} catch (Exception ex) {
			System.out.println("Making GET:" + urlstr.toString());
			ex.printStackTrace();
		} finally {
		    if (is != null) {
		        try {
		            is.close();
		        } catch (IOException e) {
		        }
		    }
		}
		return null;
	}

	public static String makePost(String baseUrl, String[] paramname, String[] paramvalues, String[] headername, String[] headervals) {
		StringBuilder paramstr = new StringBuilder();
		for(int i = 0; i < paramname.length; i++) {
			if(i > 0)
				paramstr.append("&");
			paramstr.append(paramname[i]);
			paramstr.append("=");
			paramstr.append(URLEncoder.encode(paramvalues[i]));
		}
		
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
		    URL url = new URL(baseUrl);

		    // (set connection and read timeouts on the connection)
		    conn = (HttpURLConnection)url.openConnection();
		    conn.setRequestMethod("POST");
		    conn.setReadTimeout(30 * 1000);
		    conn.setConnectTimeout(30 * 1000);
		    
		    for(int i = 0; i < headername.length; i++)
		    	conn.setRequestProperty(headername[i], headervals[i]);
		    conn.setRequestProperty("Content-Length", "" + Integer.toString(paramstr.toString().getBytes().length));
		    
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
		    
		    OutputStreamWriter connwriter = new OutputStreamWriter(conn.getOutputStream());
		    connwriter.write(paramstr.toString());
		    connwriter.flush();
		    connwriter.close();
			
		    String resp = convertStreamToString(conn.getInputStream());
		    return resp;
		} catch (Exception ex) {
			System.out.println("Making POST:" + baseUrl);
			ex.printStackTrace();
		} finally {
		    if (is != null) {
		        try {
		            is.close();
		        } catch (IOException e) {
		        }
		    }
		}
		return null;
	}
	
	public static String makePut(String baseUrl, String[] paramname, String[] paramvalues, String[] headername, String[] headervals) {
		StringBuilder paramstr = new StringBuilder();
		for(int i = 0; i < paramname.length; i++) {
			if(i > 0)
				paramstr.append("&");
			paramstr.append(paramname[i]);
			paramstr.append("=");
			paramstr.append(URLEncoder.encode(paramvalues[i]));
		}
		
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
		    URL url = new URL(baseUrl);

		    // (set connection and read timeouts on the connection)
		    conn = (HttpURLConnection)url.openConnection();
		    conn.setRequestMethod("PUT");
		    conn.setReadTimeout(30 * 1000);
		    conn.setConnectTimeout(30 * 1000);
		    
		    for(int i = 0; i < headername.length; i++)
		    	conn.setRequestProperty(headername[i], headervals[i]);
		    conn.setRequestProperty("Content-Length", "" + Integer.toString(paramstr.toString().getBytes().length));
		    
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
		    
		    OutputStreamWriter connwriter = new OutputStreamWriter(conn.getOutputStream());
		    connwriter.write(paramstr.toString());
		    connwriter.flush();
		    connwriter.close();
			
		    String resp = convertStreamToString(conn.getInputStream());
		    return resp;
		} catch (Exception ex) {
			System.out.println("Making POST:" + baseUrl);
			ex.printStackTrace();
		} finally {
		    if (is != null) {
		        try {
		            is.close();
		        } catch (IOException e) {
		        }
		    }
		}
		return null;
	}
	public static String convertStreamToString(java.io.InputStream is) throws IOException {
		return new String(readFully(is), "UTF-8");
	}
	
	private static byte[] readFully(InputStream inputStream)
	        throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int length = 0;
	    while ((length = inputStream.read(buffer)) != -1) {
	        baos.write(buffer, 0, length);
	    }
	    return baos.toByteArray();
	}
	
	public static String makePost(String baseUrl, String rawData, String[] headername, String[] headervals) {
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
		    URL url = new URL(baseUrl);

		    // (set connection and read timeouts on the connection)
		    conn = (HttpURLConnection)url.openConnection();
		    conn.setRequestMethod("POST");
		    conn.setReadTimeout(30 * 1000);
		    conn.setConnectTimeout(30 * 1000);
		    
		    
		    for(int i = 0; i < headername.length; i++)
		    	conn.setRequestProperty(headername[i], headervals[i]);
		    conn.setRequestProperty("Content-Length", "" + Integer.toString(rawData.getBytes().length));
		    
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
		    
		    OutputStreamWriter connwriter = new OutputStreamWriter(conn.getOutputStream());
		    connwriter.write(rawData);
		    connwriter.flush();
		    connwriter.close();
			
		    String resp = convertStreamToString(conn.getInputStream());
		    return resp;
		} catch (Exception ex) {
			System.out.println("Making POST:" + baseUrl);
			ex.printStackTrace();
		} finally {
		    if (is != null) {
		        try {
		            is.close();
		        } catch (IOException e) {
		        }
		    }
		}
		return null;
	}
	public static String generateBasicAuth(String username,String apikey)
	{
		String authString = username + ":" + apikey;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		//System.out.println("auth is "+"Basic "+authStringEnc);
		return "Basic "+authStringEnc;
	}
	public static int makeGetResponseCode(String baseUrl, String[] paramname, String[] paramvalues, String[] headername, String[] headervals) {
		StringBuilder urlstr = new StringBuilder(baseUrl);
		if(baseUrl.contains("?")) {
			if(! baseUrl.endsWith("&"))
				urlstr.append("&");
		} else {
			urlstr.append("?");
		}
		for(int i = 0; i < paramname.length; i++) {
			if(i > 0)
				urlstr.append("&");
			urlstr.append(paramname[i]);
			urlstr.append("=");
			urlstr.append(URLEncoder.encode(paramvalues[i]));
		}
		
		System.out.println("Making GET:" + urlstr.toString());
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
		    URL url = new URL(urlstr.toString());

		    // (set connection and read timeouts on the connection)
		    conn = (HttpURLConnection)url.openConnection();
		    conn.setReadTimeout(30 * 1000);
		    conn.setConnectTimeout(30 * 1000);
		    
		    for(int i = 0; i < headername.length; i++)
		    	conn.setRequestProperty(headername[i], headervals[i]);
		    conn.connect();
		    int respCode=conn.getResponseCode();
		    
		    return respCode;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
		    if (is != null) {
		        try {
		            is.close();
		        } catch (IOException e) {
		        }
		    }
		}
		return 0;
	}

	public static String makeDelete(String baseUrl, String[] paramname, String[] paramvalues, String[] headername, String[] headervals) {
		StringBuilder paramstr = new StringBuilder();
		for(int i = 0; i < paramname.length; i++) {
			if(i > 0)
				paramstr.append("&");
			paramstr.append(paramname[i]);
			paramstr.append("=");
			paramstr.append(URLEncoder.encode(paramvalues[i]));
		}
		
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
		    URL url = new URL(baseUrl);

		    // (set connection and read timeouts on the connection)
		    conn = (HttpURLConnection)url.openConnection();
		    conn.setRequestMethod("DELETE");
		    conn.setReadTimeout(30 * 1000);
		    conn.setConnectTimeout(30 * 1000);
		    
		    for(int i = 0; i < headername.length; i++)
		    	conn.setRequestProperty(headername[i], headervals[i]);
		    conn.setRequestProperty("Content-Length", "" + Integer.toString(paramstr.toString().getBytes().length));
		    
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
		    
		    OutputStreamWriter connwriter = new OutputStreamWriter(conn.getOutputStream());
		    connwriter.write(paramstr.toString());
		    connwriter.flush();
		    connwriter.close();
			
		    String resp = convertStreamToString(conn.getInputStream());
		    return resp;
		} catch (Exception ex) {
			System.out.println("Making DELETE:" + baseUrl);
			ex.printStackTrace();
		} finally {
		    if (is != null) {
		        try {
		            is.close();
		        } catch (IOException e) {
		        }
		    }
		}
		return null;
	}
}
