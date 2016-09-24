package io.gs.barchack.userbot;

import org.json.JSONObject;

import io.gs.barchack.userbot.banking.IBCTransaction;

public interface PersonalAccountBot {
	public void performTransaction(IBCTransaction t);
	
	public void handleMessage(JSONObject ctx, JSONObject sdr, JSONObject msg);
	public void handleOtherIBC(JSONObject ctx, JSONObject sdr, JSONObject msg);
}
