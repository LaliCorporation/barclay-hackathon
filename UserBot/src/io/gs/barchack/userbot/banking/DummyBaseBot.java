package io.gs.barchack.userbot.banking;

import io.gs.barchack.userbot.TransactionNotifier;

public class DummyBaseBot implements BaseAccountBot {
	@Override
	public void performTransaction(IBCTransaction t, TransactionNotifier n) {
		System.out.println("Performing transaction:" + t.getRequestDisplay() + " from " + t.getRequestingEntityDisplay());
		t.complete("Dummy Approved");
		n.onTransactionComplete(t);
	}
}
