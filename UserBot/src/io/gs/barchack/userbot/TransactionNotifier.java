package io.gs.barchack.userbot;

import io.gs.barchack.userbot.banking.IBCTransaction;

public interface TransactionNotifier {
	public void onTransactionComplete(IBCTransaction t);
}
