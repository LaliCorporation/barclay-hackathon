package com.barclays.utils;

import java.io.InputStream;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Data {
	
	private static JSONArray customers;
	private static JSONArray accounts;
	private static JSONArray transactions;
	private static Data data = null;
	
	private Data()
	{
		
	}
	
	public static synchronized Data getInstance()
	{
		if(data==null)
		{
			data = new Data();
			loadData();
		}
		return data;
	}
	

	
	
	public JSONArray getCustomers() {
		return customers;
	}

	public void setCustomers(JSONArray customers) {
		customers = customers;
	}

	public JSONArray getAccounts() {
		return accounts;
	}

	public void setAccounts(JSONArray accounts) {
		accounts = accounts;
	}

	public JSONArray getTransactions() {
		return transactions;
	}

	public void setTransactions(JSONArray transactions) {
		transactions = transactions;
	}

	private static void loadData() 
	{
		customers = loadFile("customers.json");
		accounts = loadFile("accounts.json");
	}
	
	private static JSONArray loadFile(String filename)
	{
		InputStream is = Data.class.getClassLoader().getResourceAsStream(filename);
		Scanner s = new Scanner(is);
		StringBuilder sb = new StringBuilder();
		while(s.hasNext())
		{
			sb.append(s.next());
		}
		try {
			return new JSONArray(sb.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	

}
