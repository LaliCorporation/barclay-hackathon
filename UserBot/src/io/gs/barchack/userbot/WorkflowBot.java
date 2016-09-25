package io.gs.barchack.userbot;

import org.json.JSONArray;
import org.json.JSONObject;

import io.gs.barchack.userbot.banking.BaseAccountBot;
import io.gs.barchack.userbot.banking.IBCTransaction;
import io.gs.barchack.userbot.banking.RequestFunds;
import io.gs.barchack.userbot.banking.TransactionStore;

public class WorkflowBot extends BaseValidatingBot {
	private JSONObject userBotConversation;
	private JSONObject adminBotConversation;
	
	public WorkflowBot(BaseAccountBot baseBot, TransactionStore pers, String botname) {
		super(baseBot, pers, botname);
	}
	
	private JSONObject getAppropriateContext(int amount) {
		if(amount > 100)
			return adminBotConversation;
		return userBotConversation;
	}
	
	public void sendApprovalMessage(String message, String id, int amount) {
		JSONObject ctx = getAppropriateContext(amount);
		
		if(ctx == null)
			return;
		
		System.out.println("Have user msg...");
		
		JSONObject msgobj = new JSONObject();
		msgobj.put("type", "survey");
		msgobj.put("question", message);
		JSONArray optarr = new JSONArray();
		msgobj.put("options", optarr);
		
		optarr.put("approve " + id);
		optarr.put("reject " + id);
		sendMessage(msgobj.toString(), userBotConversation);

		
		String text = message 
				+ " Reply [approve " + id + "] to approve"
				+ " or [reject " + id + "] to reject.";
		
	}
	
	private void notify(String message, int amount) {
		JSONObject ctx = getAppropriateContext(amount);
		
		if(ctx == null)
			return;
		sendMessage(message, ctx);
	}
	private boolean isApprovedByUser(JSONObject msg) {
		String txt = msg.getString("text").toLowerCase();
		if(txt.startsWith("approve"))
			return true;
		return false;
	}
	

	@Override
	public String handleMessage(JSONObject ctx, JSONObject sdr, JSONObject msg) {
		if(msg.getString("text").trim().toLowerCase().equals("register-user")) {
			this.userBotConversation = ctx;
			System.out.println("Registered...");
			return "Registered as user.";
		}
		if(msg.getString("text").trim().toLowerCase().equals("register-admin")) {
			this.adminBotConversation = ctx;
			System.out.println("Registered...");
			return "Registered as admin";
		}
		
		IBCTransaction transaction = getTransactionFromUserMsg(ctx, msg);
		if(transaction == null || transaction.isComplete())
			return "";
		cancelTimeout(transaction.getRequestUUID());
		if(isApprovedByUser(msg)) {
			base.performTransaction(transaction, this);
		} else {
			transaction.cancel("rejected by user");			
			notifyTransactionResults(transaction);
		}
		return "";
	}
	
	@Override
	protected void notifyTransactionResults(IBCTransaction t) {
		super.notifyTransactionResults(t);
		String usermsg;
		if(t.status().equals("failed")) {
			if(t.reason().equals("rejected by user")) {
				usermsg = t.getRequestDisplay()
						+ " requested by "
						+ t.getRequestingEntityDisplay()
						+ " rejected as desired.";
			} else {
				usermsg = t.getRequestDisplay()
						+ " requested by "
						+ t.getRequestingEntityDisplay()
						+ " rejected due to "
						+ t.reason()
						+ ".";
			}
		} else if(t.status().equals("success")) {
			usermsg = t.getRequestDisplay()
					+ " requested by "
					+ t.getRequestingEntityDisplay()
					+ " fulfilled successfully.";
		} else {
			usermsg = t.getRequestDisplay()
					+ " requested by "
					+ t.getRequestingEntityDisplay()
					+ " completed abnormally.";
		}
		notify(usermsg, ((RequestFunds)t).amount());
	}

	@Override
	public void performTransaction(IBCTransaction t) {
		System.out.println("Got transaction:" + t.getRequestDisplay() + ", from:" + t.getRequestingEntityDisplay());
		sendApprovalMessage(getTransactionApprovalMessage(t), t.getRequestUUID(), ((RequestFunds)t).amount());
		setTimeout(t.getRequestUUID(), this.timeout);
	}
}
