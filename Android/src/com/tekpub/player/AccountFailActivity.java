package com.tekpub.player;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;
import android.widget.Toast;


public class AccountFailActivity extends AccountAuthenticatorActivity {
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Toast.makeText(this, R.string.one_account_only, Toast.LENGTH_LONG).show();
		finish();
	}
}
