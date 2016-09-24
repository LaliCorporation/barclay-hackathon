package io.gs.barchack.userbot;

import java.util.HashMap;
import java.util.Map;

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
	
	private Map<String, PersonalAccountBot> simpleBots = new HashMap<>();
	private Map<String, PersonalAccountBot> autoBots = new HashMap<>();
	private Map<String, PersonalAccountBot> workflowBots = new HashMap<>();
	
	private TransactionStore store;
	
	private Application() {
		this.store = new InRamTransactionStore();
	}
	
	public PersonalAccountBot getSimpleBot(String username, String botname) {
		if(simpleBots.containsKey(username))
			return simpleBots.get(username);
		PersonalAccountBot nirBot1 = null;
		nirBot1 = new SimpleBot(
				new DummyBaseBot(),
				store,
				botname
				);
		simpleBots.put(username, nirBot1);
		return nirBot1;
	}
	public PersonalAccountBot getWorkflowBot(String username, String botname) {
		PersonalAccountBot nirBot1 = null;
		nirBot1 = new WorkflowBot(
				new DummyBaseBot(),
				store,
				botname
				);
		workflowBots.put(username, nirBot1);
		return nirBot1;
	}
	public PersonalAccountBot getAutoapprovalBot(String username, String botname) {
		PersonalAccountBot nirBot1 = null;
		nirBot1 = new AutoapprovalBot(
				new DummyBaseBot(),
				store,
				botname
				);
		autoBots.put(username, nirBot1);
		return nirBot1;
	}
	public TransactionStore getStore() {
		return store;
	}
}
