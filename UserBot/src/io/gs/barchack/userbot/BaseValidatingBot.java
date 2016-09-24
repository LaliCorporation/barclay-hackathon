package io.gs.barchack.userbot;

import org.json.JSONObject;

import io.gs.barchack.userbot.banking.BaseAccountBot;
import io.gs.barchack.userbot.banking.IBCTransaction;
import io.gs.barchack.userbot.banking.TransactionStore;

public abstract class BaseValidatingBot implements PersonalAccountBot, TransactionNotifier {
	protected BaseAccountBot base;
	private TransactionStore pers;

	protected long timeout;
	private String timeoutbot;
	private String mybotname;

	public BaseValidatingBot(BaseAccountBot baseBot, TransactionStore pers, String botname) {
		this.base = baseBot;
		this.pers = pers;
		this.timeout = 1000 * 3600L * 24; //one day approval window
		this.timeoutbot = "timeoutbot";
		this.mybotname = botname;
	}
	
	protected void sendMessage(String message, JSONObject context) {
		try {
			HttpPoster.sendMessage(mybotname, context.toString(), message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private JSONObject getTimeoutContext() {
		//TODO
		return null;
	}

	protected IBCTransaction getTransactionFromUserMsg(JSONObject ctx, JSONObject msg) {
		String text = msg.getString("text");
		String[] comps = text.split(" ");
		if(comps.length != 2)
			return null;
		if(! (comps[0].equals("approve") || comps[0].equals("reject")))
			return null;
		
		return pers.getTransaction(comps[1]);
	}
	protected IBCTransaction getTransactionFromTimeoutMsg(JSONObject ctx, JSONObject msg) {
		String text = msg.getString("text");
		String[] comps = text.split(" ");
		if(comps.length != 2)
			return null;
		if(comps[0].equals("timeout"))
			return null;
		
		return pers.getTransaction(comps[1]);
	}

	protected void setTimeout(String id, long timeout) {
		JSONObject ctx = getTimeoutContext();
		if(ctx == null)
			return;
		sendMessage("notify " + timeout + " " + id, ctx);
	}
	protected void cancelTimeout(String id) {
		JSONObject ctx = getTimeoutContext();
		if(ctx == null)
			return;		
		sendMessage("cancel " + id, ctx);
	}

	protected String getTransactionApprovalMessage(IBCTransaction transaction) {
		return transaction.getRequestingEntityDisplay()
				+ " requests "
				+ transaction.getRequestDisplay()
				+ ".";
	}
	protected void notifyTransactionResults(IBCTransaction t) {
		sendMessage(t.getIBCResponseData(), t.getRequestingContext());
	}

	@Override
	public void onTransactionComplete(IBCTransaction t) {
		notifyTransactionResults(t);
	}
	
	@Override
	public abstract void performTransaction(IBCTransaction t);
	
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
	
	public abstract void handleMessage(JSONObject ctx, JSONObject sdr, JSONObject msg);
}
