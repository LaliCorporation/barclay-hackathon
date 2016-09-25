package io.gs.barchack.userbot;

import org.eclipse.jdt.internal.compiler.apt.util.Options;
import org.json.JSONArray;
import org.json.JSONObject;

import io.gs.barchack.userbot.banking.BaseAccountBot;
import io.gs.barchack.userbot.banking.IBCTransaction;
import io.gs.barchack.userbot.banking.TransactionStore;

public class SimpleBot extends BaseValidatingBot {
	private JSONObject userBotConversation;
	
	public SimpleBot(BaseAccountBot baseBot, TransactionStore pers, String botname) {
		super(baseBot, pers, botname);
	}
	
	public void sendApprovalMessageToOwner(String message, String id) {
		System.out.println("Sending approval message...");
		if(userBotConversation == null)
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
	private void notifyOwner(String message) {
		if(userBotConversation == null)
			return;
		sendMessage(message, userBotConversation);
	}
	private boolean isApprovedByUser(JSONObject msg) {
		String txt = msg.getString("text").toLowerCase();
		if(txt.startsWith("approve"))
			return true;
		return false;
	}
	

	@Override
	public String handleMessage(JSONObject ctx, JSONObject sdr, JSONObject msg) {
		if(msg.getString("text").trim().toLowerCase().equals("register")) {
			this.userBotConversation = ctx;
			System.out.println("Registered...");
			return "Registered.";
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
		notifyOwner(usermsg);
	}

	@Override
	public void performTransaction(IBCTransaction t) {
		System.out.println("Got transaction:" + t.getRequestDisplay() + ", from:" + t.getRequestingEntityDisplay());
		
		sendApprovalMessageToOwner(getTransactionApprovalMessage(t), t.getRequestUUID());
		setTimeout(t.getRequestUUID(), this.timeout);
	}
}
