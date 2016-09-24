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
	
	private PersonalAccountBot nirBot1,nirBot2,nirBot3;
	private TransactionStore store;
	
	private Application() {
		this.store = new InRamTransactionStore();
		nirBot1 = new SimpleValidatingBot(
				new DummyBaseBot(),
				store,
				"bdemo1"
				);
		nirBot2 = new AutoapprovalBot(
				new DummyBaseBot(),
				store,
				"bdemo1"
				);
		nirBot3 = new AutoapprovalBot(
				new DummyBaseBot(),
				store,
				"bdemo1"
				);
	}
	
	public PersonalAccountBot getSimpleBot(String username) {
		return nirBot1;
	}
	public PersonalAccountBot getWorkflowBot(String username) {
		return nirBot2;
	}
	public PersonalAccountBot getAutoapprovalBot(String username) {
		return nirBot3;
	}
	public TransactionStore getStore() {
		return store;
	}
}
