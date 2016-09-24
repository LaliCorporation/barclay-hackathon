package com.barclays.utils;

import java.io.InputStream;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;

public class Data {
	private static Data data = null;
	
	private JSONArray customers;
	private JSONArray accounts;
	private JSONArray transactions;
	
	private Data() {}
	
	public static synchronized Data getInstance()
	{
		if(data==null)
		{
			data = new Data();
			data.loadData();
		}
		return data;
	}
	

	
	
	public JSONArray getCustomers() {
		return customers;
	}

	public void setCustomers(JSONArray customers) {
		this.customers = customers;
	}

	public JSONArray getAccounts() {
		return accounts;
	}

	public void setAccounts(JSONArray accounts) {
		this.accounts = accounts;
	}

	public JSONArray getTransactions() {
		return transactions;
	}

	public void setTransactions(JSONArray transactions) {
		this.transactions = transactions;
	}

	private void loadData() 
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
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				s.close();
			} catch (Exception e){}
		}
	}
}
