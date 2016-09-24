package io.gs.barchack.userbot;

import org.json.JSONObject;

import io.gs.barchack.userbot.banking.BaseAccountBot;
import io.gs.barchack.userbot.banking.IBCTransaction;
import io.gs.barchack.userbot.banking.TransactionStore;

public class SimpleValidatingBot implements PersonalAccountBot, TransactionNotifier {
	private BaseAccountBot base;
	private JSONObject userBotConversation;
	private TransactionStore pers;

	private long timeout;
	private String timeoutbot;

	public SimpleValidatingBot(BaseAccountBot baseBot, JSONObject userBotConversation, TransactionStore pers) {
		this.base = baseBot;
		this.userBotConversation = userBotConversation;
		this.pers = pers;
		this.timeout = 1000 * 3600L * 24; //one day approval window
		this.timeoutbot = "timeoutbot";
	}
	
	private void sendMessage(String message, JSONObject context) {
		//TODO
	}
	private JSONObject getTimeoutContext() {
		//TODO
		return null;
	}

	private IBCTransaction getTransactionFromUserMsg(JSONObject ctx, JSONObject msg) {
		String text = msg.getString("text");
		String[] comps = text.split(" ");
		if(comps.length != 2)
			return null;
		if(! (comps[0].equals("approve") || comps[0].equals("reject")))
			return null;
		
		return pers.getTransaction(comps[1]);
	}
	private IBCTransaction getTransactionFromTimeoutMsg(JSONObject ctx, JSONObject msg) {
		String text = msg.getString("text");
		String[] comps = text.split(" ");
		if(comps.length != 2)
			return null;
		if(comps[0].equals("timeout"))
			return null;
		
		return pers.getTransaction(comps[1]);
	}

	private void notifyOwner(String message) {
		if(userBotConversation == null)
			return;
		sendMessage(message, userBotConversation);
	}
	private void sendApprovalMessageToOwner(String message, String id) {
		if(userBotConversation == null)
			return;
		
		String text = message 
				+ " Reply [approve " + id + "] to approve"
				+ " or [reject " + id + "] to reject.";
		sendMessage(text, userBotConversation);
	}
	private void setTimeout(String id, long timeout) {
		sendMessage("notify " + timeout + " " + id, getTimeoutContext());
	}
	private void cancelTimeout(String id) {
		sendMessage("cancel " + id, getTimeoutContext());
	}
	private boolean isApprovedByUser(JSONObject msg) {
		String txt = msg.getString("text").toLowerCase();
		if(txt.startsWith("approve"))
			return true;
		return false;
	}

	private String getTransactionApprovalMessage(IBCTransaction transaction) {
		return transaction.getRequestingEntityDisplay()
				+ " requests "
				+ transaction.getRequestDisplay()
				+ ".";
	}
	private void notifyTransactionResults(IBCTransaction t) {
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
		sendMessage(t.getIBCResponseData(), t.getRequestingContext());
	}

	@Override
	public void onTransactionComplete(IBCTransaction t) {
		notifyTransactionResults(t);
	}
	
	@Override
	public void performTransaction(IBCTransaction t) {
		System.out.println("Got transaction:" + t.getRequestDisplay() + ", from:" + t.getRequestingEntityDisplay());
		
		sendApprovalMessageToOwner(getTransactionApprovalMessage(t), t.getRequestUUID());
		setTimeout(t.getRequestUUID(), this.timeout);
	}
	
	@Override
	public void handleOtherIBC(JSONObject ctx, JSONObject sdr, JSONObject msg) {
		if(! sdr.getString("channelid").equals(timeoutbot)) //TODO
			return;
		
		IBCTransaction transaction = getTransactionFromTimeoutMsg(ctx, msg);
		if(transaction == null || transaction.isComplete())
			return;
		transaction.cancel("timeout");
		notifyTransactionResults(transaction);
	}
	
	@Override
	public void handleMessage(JSONObject ctx, JSONObject sdr, JSONObject msg) {
		if(msg.getString("text").trim().toLowerCase().equals("register")) {
			this.userBotConversation = ctx;
			return;
		}
		
		IBCTransaction transaction = getTransactionFromUserMsg(ctx, msg);
		if(transaction == null || transaction.isComplete())
			return;
		cancelTimeout(transaction.getRequestUUID());
		if(isApprovedByUser(msg)) {
			base.performTransaction(transaction, this);
			return;
		}
		transaction.cancel("rejected by user");
		notifyTransactionResults(transaction);
	}
}
