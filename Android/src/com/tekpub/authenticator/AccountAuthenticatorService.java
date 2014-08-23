package com.tekpub.authenticator;

import java.io.IOException;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.tekpub.app.TekPub;
import com.tekpub.exception.TekPubLoginException;
import com.tekpub.http.ITekPubApi;
import com.tekpub.models.LoginResult;
import com.tekpub.player.AccountFailActivity;
import com.tekpub.player.LoginActivity;
import com.tekpub.player.R;


public class AccountAuthenticatorService extends Service {
	
	
	
	private static final String TAG = AccountAuthenticatorService.class.getName();
	
	private static AccountAuthenticator sAccountAuthenticator = null;

	public AccountAuthenticatorService() {
		super();
		
	}

	public static void addAccount(Context ctx, String username, String password, Parcelable response) {
		AccountAuthenticatorResponse authResponse = (AccountAuthenticatorResponse)response;
		Bundle result = AccountAuthenticator.addAccount(ctx, username, password);
		if(authResponse != null)
			authResponse.onResult(result);
	}
	
	private static class AccountAuthenticator extends AbstractAccountAuthenticator {
 
		private ITekPubApi mApi;
		private Context mContext; 
		
		public AccountAuthenticator(Context context) {
			super(context);
			mContext = context; 
			
			mApi = TekPub.getInstance().getInjector().getInstance(ITekPubApi.class); 
		}
				
		public static Bundle addAccount(Context ctx, String username, String password) {
			Bundle result = null;
			
			Account account = new Account(username, ctx.getString(R.string.ACCOUNT_TYPE));
			AccountManager am = AccountManager.get(ctx);
			if (am.addAccountExplicitly(account, password, null)) {
				result = new Bundle();
				result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
				result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			}
			return result;
		}

		@Override
		public Bundle addAccount(AccountAuthenticatorResponse response,
				String accountType, String authTokenType,
				String[] requiredFeatures, Bundle options)
				throws NetworkErrorException {
			Bundle result; 
			if(accountExists(mContext)) {
				result = new Bundle();
				Intent i = new Intent(mContext, AccountFailActivity.class);
				i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
				result.putParcelable(AccountManager.KEY_INTENT, i);
			} else {
				result = new Bundle();
				Intent i = new Intent(mContext, LoginActivity.class);
				i.setAction(TekPub.ACTION_AUTHENTICATOR_LOGIN);
				i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
				result.putParcelable(AccountManager.KEY_INTENT, i);
			}
			return result; 
		}
		
		public static Boolean accountExists(Context ctx) {
			AccountManager am = AccountManager.get(ctx);
			Account[] accounts = am.getAccountsByType(ctx.getString(R.string.ACCOUNT_TYPE));
			if(accounts != null && accounts.length > 0)
				return true;
			else
				return false;
		}

		@Override
		public Bundle confirmCredentials(AccountAuthenticatorResponse response,
				Account account, Bundle options) throws NetworkErrorException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Bundle editProperties(AccountAuthenticatorResponse response,
				String accountType) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Bundle getAuthToken(AccountAuthenticatorResponse response,
				Account account, String authTokenType, Bundle options)
				throws NetworkErrorException {
			
			Log.d(TAG, "Getting auth token.");
			
			AccountManager am = AccountManager.get(TekPub.getInstance());
			String password = am.getPassword(account);
			
			String existingToken = am.peekAuthToken(account, authTokenType); 
			Log.d(TAG, "Found existing auth token: " + !TextUtils.isEmpty(existingToken));
			
			Bundle result = new Bundle();
			String token; 
			try {
				
				if(TextUtils.isEmpty(existingToken)) {
					LoginResult loginResult = mApi.getAuthToken(account.name, password);
					Log.d(TAG, "Login Result: " + loginResult.wasSuccess()); 
					if(loginResult.wasSuccess()) {
						// Were able to login and got a valid result. 
						am.setAuthToken(account, authTokenType, loginResult.getToken()); // Put the token into account manager cache. 
						result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
						result.putString(AccountManager.KEY_ACCOUNT_TYPE,  account.type);
						result.putString(AccountManager.KEY_AUTHTOKEN, loginResult.getToken());
					} else {
						// Could not login, set an error. 
						result.putInt(AccountManager.KEY_ERROR_CODE, loginResult.getErrorCode());
						result.putString(AccountManager.KEY_ERROR_MESSAGE, loginResult.getErrorMessage()); 
					}
					 
				} else {
					// Found an existing token, lets use that one. 
					// This logic path will always be chosen once the token is set in AccountManager cache. 
					Log.d(TAG, "Found existing token: " + existingToken);
					token = existingToken;
					result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
					result.putString(AccountManager.KEY_ACCOUNT_TYPE,  account.type);
					result.putString(AccountManager.KEY_AUTHTOKEN, token);
				}
				
				
				

				
			} catch (IOException e) {
				Log.e(TAG, "IOException Thrown.");
				result.putInt(AccountManager.KEY_ERROR_CODE, -100);
				result.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage()); 
				e.printStackTrace();
			} catch (TekPubLoginException e) {
				Log.e(TAG, "TekPubAPIException Thrown.");
				result.putInt(AccountManager.KEY_ERROR_CODE, e.getErrorCode());
				result.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage()); 
				e.printStackTrace();
			} 
			
			// Not sure if this is needed or not. 
			//result.putParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
			
			return result;
		}


		@Override
		public String getAuthTokenLabel(String authTokenType) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Bundle hasFeatures(AccountAuthenticatorResponse response,
				Account account, String[] features)
				throws NetworkErrorException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Bundle updateCredentials(AccountAuthenticatorResponse response,
				Account account, String authTokenType, Bundle options)
				throws NetworkErrorException {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	@Override
	public IBinder onBind(Intent intent) { 
		IBinder ret = null;
		if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT)) 
			ret = getAuthenticator().getIBinder();
		return ret;
	}
	
	private AccountAuthenticator getAuthenticator() { 
		if (sAccountAuthenticator == null)
			sAccountAuthenticator = new AccountAuthenticator(this);
		return sAccountAuthenticator;
	}
	

}
