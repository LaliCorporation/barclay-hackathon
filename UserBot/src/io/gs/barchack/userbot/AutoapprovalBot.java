package io.gs.barchack.userbot;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import io.gs.barchack.userbot.banking.BaseAccountBot;
import io.gs.barchack.userbot.banking.IBCTransaction;
import io.gs.barchack.userbot.banking.RequestFunds;
import io.gs.barchack.userbot.banking.TransactionStore;

public class AutoapprovalBot extends BaseValidatingBot {
	private JSONObject userBotConversation;
	
	private Set<String> preapproved = new HashSet<>();
	
	private boolean isApprovedMerchant(String id) {
		return preapproved.contains(id);
	}
	private void preapprove(String merchant) {
		preapproved.add(merchant);
	}
	
	public AutoapprovalBot(BaseAccountBot baseBot, TransactionStore pers, String botname) {
		super(baseBot, pers, botname);
	}
	
	public void sendApprovalMessageToOwner(String message, String id) {
		System.out.println("Sending approval message:" + this.getClass().getName());
		if(this.userBotConversation == null)
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
			System.out.println("CTX:" + ctx);
			return "Registered";
		}
		if(msg.getString("text").trim().toLowerCase().startsWith("preapprove ")) {
			String merchant = msg.getString("text").trim().split(" ")[1];
			String text = merchant + " preapproved.";
			preapprove(merchant);
			return text;
		}
		IBCTransaction transaction = getTransactionFromUserMsg(ctx, msg);
		if(transaction == null || transaction.isComplete())
			return "";
		cancelTimeout(transaction.getRequestUUID());
		if(isApprovedByUser(msg)) {
			base.performTransaction(transaction, this);
			
			//TODO
			if(! isApprovedMerchant(transaction.merchant())) {
				//Approval message
				String text = " To preapprove " 
					+ transaction.merchant() 
					+ ", send [preapprove " + transaction.merchant() + "]."
					+ " This will result in all transactions below $100 "
					+ "from " + transaction.merchant() + " being automically approved.";
				sendMessage(text, userBotConversation);
			}
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
		if(isApprovedMerchant(t.merchant()) && ((RequestFunds)t).amount() < 100) {
			base.performTransaction(t, this);
			return;
		}
		
		System.out.println("Got transaction:" + t.getRequestDisplay() + ", from:" + t.getRequestingEntityDisplay());
		
		
		sendApprovalMessageToOwner(getTransactionApprovalMessage(t), t.getRequestUUID());
		setTimeout(t.getRequestUUID(), this.timeout);
	}
}
