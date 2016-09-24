package io.gs.barchack.userbot;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UserBotRegister
 */
@WebServlet("/register")
public class UserBotRegister extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserBotRegister() {
        super();
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userphone = request.getParameter("phone");
		String type = request.getParameter("type");
		
		String botname;
		String callback;
		
		if(type.equals("simple")) {
			botname = "simple-" + userphone;
			callback = "https://c3f3c77d.ngrok.io/UserBot/simplebot/" + userphone;
		} else if (type.equals("workflow")) {
			botname = "workflow-" + userphone;
			callback = "https://c3f3c77d.ngrok.io/UserBot/workflow/" + userphone;
		} else if (type.equals("auto")) {
			botname = "auto-" + userphone;
			callback = "https://c3f3c77d.ngrok.io/UserBot/autoapproval/" + userphone;
		} else {
			//TODO
			return;
		}
		
		try {
			HttpPoster.createBot(botname);
			HttpPoster.setCallback(botname, callback);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//save
		
		response.getWriter().println(botname);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
