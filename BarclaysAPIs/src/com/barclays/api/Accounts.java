package com.barclays.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.barclays.utils.Data;
import com.barclays.utils.Helper;

@WebServlet("/accounts/*")
public class Accounts extends HttpServlet {
	private JSONArray acct = new JSONArray();

	public Accounts() {
		try {
			acct = Data.getInstance().getAccounts();
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

		if (path == null || path.equals("/")) {
			out = acct;
		} else if (path.startsWith("/custid/")) {
			String[] args = path.split("/");
			String custId = args[2];
			out = Helper.filter(acct, "customerId", custId);
		} else if (path.startsWith("/id/")) {
			String[] args = path.split("/");
			String id = args[2];
			out = Helper.filter(acct, "id", id);
		} else if (path.startsWith("/trans/")) {
			try {
				String[] args = path.split("/");
				String drid = args[2];
				String crid = req.getParameter("toId");
				double amount = Double.parseDouble(req.getParameter("amount"));
				JSONObject dr = Helper.filter(acct, "id", drid);
				double drbal = dr.getDouble("currentBalance");
				JSONObject cr = Helper.filter(acct, "id", crid);
				double crbal = cr.getDouble("currentBalance");

				if (drbal > amount) {

					dr.put("currentBalance", drbal - amount);
					cr.put("currentBalance", crbal + amount);

					out = dr;
				} else {
					out = getErrorJSON("Inufficient fund");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (out != null)
			resp.getWriter().write(out.toString());
		else
			resp.getWriter().write(getErrorJSON("Not Found").toString());
	}
	
	private JSONObject getErrorJSON(String message)
	{

		JSONObject err = new JSONObject();
		try {
			err.put("err", message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return err;
	}

}
