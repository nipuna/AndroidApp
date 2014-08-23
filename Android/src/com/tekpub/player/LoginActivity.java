package com.tekpub.player;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.google.inject.Inject;
import com.tekpub.app.TekPub;
import com.tekpub.authenticator.AccountAuthenticatorService;
import com.tekpub.http.ITekPubApi;
import com.tekpub.messaging.NotificationUtil;
import com.tekpub.models.LoginResult;

public class LoginActivity extends RoboActivity {
	
	@InjectView(R.id.sign_up_button) Button mSignUp; 
	@InjectView(R.id.sign_in_button) Button mSignIn; 
	
	@InjectView(R.id.username) EditText mEmail; 
	@InjectView(R.id.password) EditText mPassword; 
	
	@Inject ITekPubApi mApi; 
	
//	private String PREFS = this.getString(R.string.login_prefs); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		// Check to see if we already have an account logged in. 
		// If so, then forward them to the video listing page. 
		
		setContentView(R.layout.login);
		
		mPassword.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					mSignIn.performClick(); 
					return true; 
				}
				return false; 
			}
		});
		
		mSignIn.setOnClickListener(signInListener); 
		mSignUp.setOnClickListener(signUpListener); 
		
	}
	
	View.OnClickListener signInListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// Sign in with the new account.
			AddAccountTask task = new AddAccountTask(mEmail.getText().toString(), mPassword.getText().toString(), getString(R.string.loading_message));
			task.execute();
		}
	};
	

	View.OnClickListener signUpListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW); 
				i.setData(Uri.parse("http://tekpub.com/account/login"));
				startActivity(Intent.createChooser(i, "Sign Up")); 
		}
	};
	
	private class AddAccountTask extends RoboAsyncTask<LoginResult> {

		private String email; 
		private String password;
		private String loadingMessage;
		private ProgressDialog dialog;
		
		public AddAccountTask(String email, String password, String loadingMessage) {
			this.email = email; 
			this.password = password;
			this.loadingMessage = loadingMessage; 
			mSignIn.setEnabled(false); 
		}
		
		@Override
		protected void onPreExecute() throws Exception {			
			super.onPreExecute();
			
			dialog = ProgressDialog.show(LoginActivity.this, null, loadingMessage, true, false);  
		}
		
		@Override
		public LoginResult call() throws Exception {
			
			Parcelable authResponse = null;
			if(getIntent() != null && getIntent().getExtras() != null)
				authResponse = getIntent().getExtras().getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
			LoginResult result = mApi.getAuthToken(email, password);
			if(result.wasSuccess()) {
				// If we're good on the uid/pw front, add the account to the Android Account Manager. 
				AccountAuthenticatorService.addAccount(LoginActivity.this, email, password, authResponse);
				AccountManager am = AccountManager.get(LoginActivity.this);
				Account[] accounts = am.getAccountsByType(LoginActivity.this.getString(R.string.ACCOUNT_TYPE)); 
				if(accounts.length > 0) {
					Account acc = accounts[0];
					am.setAuthToken(acc, LoginActivity.this.getString(R.string.AUTH_TOKEN_TYPE), result.getToken());
					
					// User is logged in, therefore we don't want to ask them to login on the splash screen anymore. 
					NotificationUtil.setLoginQueryPreferencesAsSkip(LoginActivity.this); 
					
				}
			} 
			
			return result;
		}
		
		@Override
		protected void onSuccess(LoginResult result) throws Exception {
			super.onSuccess(result);
			
			if(result.wasSuccess()) {
	 			if(getIntent().getAction() != null && getIntent().getAction().equals(TekPub.ACTION_AUTHENTICATOR_LOGIN)) {
					// The request came from the authenticator. Finish the activity. 
					Intent i = getIntent(); 
					Bundle extras = i.getExtras();
					if (extras != null) {
						finish();
					}
				} else {
					// User logged in via the app, take them to the productions page
					Intent i = new Intent(LoginActivity.this, MainActivity.class); 
					i.putExtra(TekPub.EXTRA_RELOAD_PRODUCTIONS, true);
					startActivity(i);
				}
			} else {
				// Inform user that something went wrong with login. 
				Toast.makeText(LoginActivity.this, result.getErrorMessage(), Toast.LENGTH_LONG).show(); 
			}
				
			
		}
		
		@Override
		protected void onFinally() throws RuntimeException {
			super.onFinally();
			mSignIn.setEnabled(true);
			if(dialog.isShowing()) {
				dialog.dismiss(); 
			}
		}
		
	}

}
