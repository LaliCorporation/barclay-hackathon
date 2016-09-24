package io.gs.barchack.userbot;

import io.gs.barchack.userbot.banking.DummyBaseBot;
import io.gs.barchack.userbot.banking.InRamTransactionStore;
import io.gs.barchack.userbot.banking.TransactionStore;

public class Application {
	private static Application app;
	
	static {
		app = new Application();
	}
	
	public static Application getInstance() {
		return app;
	}
	
	private PersonalAccountBot nirBot;
	private TransactionStore store;
	
	private Application() {
		this.store = new InRamTransactionStore();
		nirBot = new SimpleValidatingBot(
				new DummyBaseBot(),
				null,
				store);
	}
	
	public PersonalAccountBot getUserBot(String username) {
		return nirBot;
	}
	public TransactionStore getStore() {
		return store;
	}
}
