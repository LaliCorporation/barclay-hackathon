package com.botregistrar.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.botregistrar.main.RegManager;

@WebServlet("/getbot")
public class GetData extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String mobile = request.getParameter("mobile");
		String type = request.getParameter("type");
		
		RegManager regManager = new RegManager();
		String botname  = regManager.getBotname(mobile, type);
		if(botname==null){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		else{
			response.getWriter().append(botname);
		}
	}
}
