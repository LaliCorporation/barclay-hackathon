package io.gs.barchack.userbot.banking;

import org.json.JSONObject;

import io.gs.barchack.userbot.HttpPoster;
import io.gs.barchack.userbot.TransactionNotifier;

public class DummyBaseBot implements BaseAccountBot {
	@Override
	public void performTransaction(IBCTransaction t, TransactionNotifier n) {
		System.out.println("Performing transaction:" + t.getRequestDisplay() + " from " + t.getRequestingEntityDisplay());
		
		double amount = ((RequestFunds)t).amount();
		String phone = t.userPhone();
		String merchant = t.merchant();
		
		String userid = HttpPoster.getAccountId(phone);
		String accountid = HttpPoster.getAccountId(userid);
		JSONObject resp = HttpPoster.makeTransaction(accountid, merchant, amount);
		
		if(resp.getBoolean("success")) {
			t.complete("Approved");
			n.onTransactionComplete(t);
		} else {
			t.cancel("Transaction error - " + resp.getString("msg"));
			n.onTransactionComplete(t);
		}
	}
}
