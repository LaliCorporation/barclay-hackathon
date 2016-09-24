package com.barclays.api;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.barclays.utils.Data;
import com.barclays.utils.Helper;

@WebServlet("/customers/*")
public class Customers extends HttpServlet {
	private JSONArray cust = new JSONArray();
	
	public Customers(){
		try {
			cust = Data.getInstance().getCustomers();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String path = req.getPathInfo();
		resp.setContentType("application/json");
		Object out = null;

		if(path == null || path.equals("/"))
		{
			out = cust;
		}
		else if(path.startsWith("/phone/")){
			String[] args = path.split("/");
			String phone = args[2];
			out = Helper.filter(cust, "mobileNo", phone);
		}
		else if(path.startsWith("/id/")){
			String[] args = path.split("/");
			String id = args[2];
			out = Helper.filter(cust, "id", id);
		}
	
		if(out != null)
			resp.getWriter().write(out.toString());
		else
			resp.getWriter().write("{\"error\":\"Not Found\"}\"");
	}
}
