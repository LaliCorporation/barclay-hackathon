package com.barclays.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


public class RestClient {
	public HttpGet createGetRequest(String url, Map<String,String> headers, Map<String, String> params)
	{
		try {
			URIBuilder uriBuilder = new URIBuilder(url);

			if (params != null && params.size() > 0) {
				for (String p : params.keySet()) {
					uriBuilder.addParameter(p, params.get(p));
				}
			}

			HttpGet get;

			get = new HttpGet(uriBuilder.build());

			if (headers != null && headers.size() > 0) {
				for (String h : headers.keySet()) {
					get.addHeader(h, headers.get(h));
				}
			}
			return get;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public HttpPost createPostRequest(String url, Map<String,String> headers, Map<String, String> params)
	{
		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			HttpPost post;
			post = new HttpPost(uriBuilder.build());			
			
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();			

			if (params != null && params.size() > 0) {
				for (String p : params.keySet()) {
					formParams.add(new BasicNameValuePair(p, params.get(p)));
				}
			}
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
			post.setEntity(formEntity);

			if (headers != null && headers.size() > 0) {
				for (String h : headers.keySet()) {
					post.addHeader(h, headers.get(h));
				}
			}
			return post;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public HttpPut createPutRequest(String url, Map<String,String> headers, Map<String, String> params)
	{
		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			HttpPut put;
			put = new HttpPut(uriBuilder.build());			
			
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();			

			if (params != null && params.size() > 0) {
				for (String p : params.keySet()) {
					formParams.add(new BasicNameValuePair(p, params.get(p)));
				}
			}
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
			put.setEntity(formEntity);

			if (headers != null && headers.size() > 0) {
				for (String h : headers.keySet()) {
					put.addHeader(h, headers.get(h));
				}
			}
			return put;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	public HttpDelete createDeleteRequest(String url, Map<String,String> headers, Map<String, String> params)
	{
		try {
			URIBuilder uriBuilder = new URIBuilder(url);

			if (params != null && params.size() > 0) {
				for (String p : params.keySet()) {
					uriBuilder.addParameter(p, params.get(p));
				}
			}

			HttpDelete delete;

			delete = new HttpDelete(uriBuilder.build());

			if (headers != null && headers.size() > 0) {
				for (String h : headers.keySet()) {
					delete.addHeader(h, headers.get(h));
				}
			}
			return delete;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
		
	
	public HttpResponse getResponse(HttpUriRequest req)
	{
		try {
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(req);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public String extractBodyAsString(HttpResponse response)
	{
		try {
			HttpEntity he = response.getEntity();
			InputStreamReader isr = new InputStreamReader(he.getContent());
			BufferedReader rd = new BufferedReader(isr);
			StringBuilder sbr = new StringBuilder();
			while(true) {
				String s = rd.readLine();
				if(s == null)
					break;
				sbr.append(s);
				sbr.append("\n");
			}
			rd.close();
			isr.close();
			he.getContent().close();			
			EntityUtils.consume(he);			
			return sbr.toString();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject extractBodyAsJSON(HttpResponse response){
		try {
			return new JSONObject(extractBodyAsString(response));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
