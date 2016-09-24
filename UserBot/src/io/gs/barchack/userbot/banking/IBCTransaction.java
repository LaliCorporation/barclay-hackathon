package io.gs.barchack.userbot.banking;

import org.json.JSONObject;

public interface IBCTransaction {
	public boolean isComplete();
	public String status();
	public String reason();
	
	public void cancel(String reason);
	public void complete(String data);
	
	public String getIBCResponseData();
	
	public String getRequestUUID();
	
	public String getRequestingEntityDisplay();
	public String getRequestDisplay();
	
	public JSONObject getRequestingContext();
	
	public String getId();
	
	public String merchant();
}
