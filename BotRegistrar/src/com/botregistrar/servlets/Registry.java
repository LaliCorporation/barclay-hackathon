package com.botregistrar.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.botregistrar.main.RegManager;

/**
 * Servlet implementation class Registry
 */
@WebServlet("/register")
public class Registry extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String mobile = request.getParameter("mobile");
			String type = request.getParameter("type");
			String botname = request.getParameter("botname");
			String seqid = request.getParameter("seqid");
			
			RegManager regManager = new RegManager();
			boolean entryDone = regManager.makeEntry(mobile, type, botname, seqid);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
