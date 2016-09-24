package io.gs.barchack.userbot.banking;

import java.util.HashMap;
import java.util.Map;

public class InRamTransactionStore implements TransactionStore {
	private Map<String, IBCTransaction> transactions = new HashMap<String, IBCTransaction>();
	
	@Override
	public IBCTransaction getTransaction(String id) {
		return transactions.get(id);
	}

	@Override
	public void addTransaction(IBCTransaction t) {
		transactions.put(t.getId(), t);
	}
}
