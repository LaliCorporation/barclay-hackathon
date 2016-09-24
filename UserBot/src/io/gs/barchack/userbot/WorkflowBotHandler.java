package io.gs.barchack.userbot;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import io.gs.barchack.userbot.banking.RequestFunds;

/**
 * Servlet implementation class BotHandler
 */
@WebServlet("/workflow/*")
public class WorkflowBotHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final Application app = Application.getInstance();

    /**
     * Default constructor. 
     */
    public WorkflowBotHandler() {
        // TODO Auto-generated constructor stub
    }
    
    private String getUser(HttpServletRequest req) {
    	String path = req.getPathInfo();
    	return path.substring(1).trim();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Got req...");
		
		String user = getUser(request);
		String botname = "workflow-" + user;
		
		PersonalAccountBot bot = app.getWorkflowBot(user, botname);
		
		JSONObject ctx = new JSONObject(request.getParameter("contextobj"));
		JSONObject msg = new JSONObject(request.getParameter("messageobj"));
		JSONObject sdr = new JSONObject(request.getParameter("senderobj"));
		
		if(ctx.getString("channeltype").equals("ibc")) {
			try {
				String txt = msg.getString("text");
				System.out.println("Got IBC request:" + txt);
				JSONObject jo = new JSONObject(txt);
				System.out.println("JO:");
				if(jo.getString("reqtype").equals("barcreq")) {
					String ttype = jo.getString("transaction-type");
					if(ttype.equals("reqfunds")) {
						RequestFunds rf = new RequestFunds(
								ctx,
								sdr.getString("channelid"),
								jo.getInt("amount"),
								jo.getString("reason"),
								jo.getString("tid"));
						app.getStore().addTransaction(rf);
						bot.performTransaction(rf);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			//user message
			bot.handleMessage(ctx, sdr, msg);
			response.getWriter().append("Served at: ").append(request.getContextPath());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
