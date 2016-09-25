package io.gs.barchack.userbot.banking;

import org.json.JSONObject;

public class RequestFunds implements IBCTransaction {
	
	public RequestFunds(JSONObject ctx, String sender, int amount, String reason, String id, String phone) {
		this.ctx = ctx;
		this.sender = sender;
		this.display = reason;
		this.amount = amount;
		this.id = id;
		this.phone = phone;
	}
	
	private boolean complete = false;
	private String status = "inited";
	private String payload = "";
	
	private JSONObject ctx;
	private String sender, reason, display, id, phone;
	private int amount;
	
	public int amount() {
		return amount;
	}
	
	@Override
	public boolean isComplete() {
		return complete;
	}

	@Override
	public String status() {
		return status;
	}

	@Override
	public String reason() {
		return reason;
	}

	@Override
	public void cancel(String reason) {
		if(isComplete())
			return;
		this.complete = true;
		this.status = "failed";
		this.reason = reason;
	}

	@Override
	public void complete(String data) {
		if(isComplete())
			return;
		this.complete = true;
		this.status = "success";
		this.reason = "approved";
		this.payload = data;
	}

	@Override
	public String getIBCResponseData() {
		JSONObject jo = new JSONObject();
		
		jo.put("reqtype", "barcreq");
		jo.put("transaction-type", "reqfunds-response");
		jo.put("tid", this.getId());
		
		jo.put("status", this.status);
		jo.put("reason", this.reason);
		jo.put("payload", this.payload);
		return jo.toString();
	}

	@Override
	public String getRequestUUID() {
		return id;
	}

	@Override
	public String getRequestingEntityDisplay() {
		return sender;
	}

	@Override
	public String getRequestDisplay() {
		return "payment of $" + amount + " for " + display + ".";
	}

	@Override
	public JSONObject getRequestingContext() {
		return ctx;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String merchant() {
		if(sender.startsWith("merchant"))
			return sender.substring("merchant".length());
		return null;
	}

	@Override
	public String userPhone() {
		return phone;
	}
}
