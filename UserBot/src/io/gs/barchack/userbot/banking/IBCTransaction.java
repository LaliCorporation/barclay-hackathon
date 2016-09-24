package io.gs.barchack.userbot.banking;

public interface IBCTransaction {
	public boolean isComplete();
	public String status();
	public String reason();
	
	public void cancel(String reason);
	
	public String getIBCResponseData();
	
	public String getRequestUUID();
	
	public String getRequestingEntityDisplay();
	public String getRequestDisplay();
	
	public String getRequestingContext();
}
