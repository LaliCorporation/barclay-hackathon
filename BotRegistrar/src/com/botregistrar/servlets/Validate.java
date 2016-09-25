package com.botregistrar.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.botregistrar.main.RegManager;

/**
 * Servlet implementation class Validate
 */
@WebServlet("/validate")
public class Validate extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String mobile = request.getParameter("mobile");
		String seqid = request.getParameter("seqid");
		String otp = request.getParameter("otp");
		
		RegManager regManager = new RegManager();
		boolean isValid = regManager.validateOtp(mobile, seqid, otp);
		
		if(!isValid){
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		
		
	}

}
