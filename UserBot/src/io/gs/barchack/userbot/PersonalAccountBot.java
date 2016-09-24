package io.gs.barchack.userbot;

import io.gs.barchack.userbot.banking.IBCTransaction;
import io.gs.barchack.userbot.messaging.Context;
import io.gs.barchack.userbot.messaging.Message;

public interface PersonalAccountBot {
	public void performTransaction(IBCTransaction t);
	
	public void handleMessage(Context ctx, Message msg);
	public void handleOtherIBC(Context ctx, Message msg);
}
