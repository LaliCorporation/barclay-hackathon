package io.gs.barchack.userbot.banking;

public interface TransactionStore {
	public IBCTransaction getTransaction(String id);
	public void addTransaction(IBCTransaction t);
}
