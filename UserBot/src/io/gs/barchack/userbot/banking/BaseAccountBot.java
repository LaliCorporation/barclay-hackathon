package io.gs.barchack.userbot.banking;

import io.gs.barchack.userbot.TransactionNotifier;

public interface BaseAccountBot {
	public void performTransaction(IBCTransaction t, TransactionNotifier n);
}
