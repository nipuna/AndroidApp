package com.tekpub.player;

import java.util.List;

import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectExtra;
import roboguice.util.RoboAsyncTask;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.inject.Inject;
import com.google.inject.internal.Nullable;
import com.tekpub.app.TekPub;
import com.tekpub.messaging.NotificationUtil;
import com.tekpub.models.Production;
import com.tekpub.storage.IProductionRepository;

public class MainActivity extends RoboListActivity {

	private static final String TAG = MainActivity.class.getName(); 
	
	private static final int LOADING_DIALOG = 1; 
	private static final int LOGOUT_LOADING_DIALOG = 2; 
	private static final int LOGOUT_CONFIRMATION_DIALOG = 3; 
	
	List<Production> mProductions = null;  
	
	@Inject IProductionRepository mProductionRepository; 
	
	@InjectExtra(value = TekPub.EXTRA_RELOAD_PRODUCTIONS ,optional = true)
	@Nullable
	Boolean mReloadProductions = false; 
	
	private ProductionAdapter mAdapter; 
	
	private boolean mHasAccount = false; 

	private GetEpisodesTask getEpisodesTask;
	private RefreshProductionsTask refreshTask; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        if(mReloadProductions.equals(true)) {
        	// We need to reload the productions because some part of the app requested a reload
        	// usually this is a login. 
        	refreshTask = new RefreshProductionsTask(); 
        	refreshTask.execute(); 
        } else {
        	// 
        	getEpisodesTask = new GetEpisodesTask(); 
        	getEpisodesTask.execute();
        }
        
    }
    
	@Override
	protected void onPause() {
		// Cancel any running tasks on pause (such as screen rotation)
		if(getEpisodesTask != null) {
			getEpisodesTask.cancel(true); 
		}
		
		if(refreshTask != null) {
			refreshTask.cancel(true); 
		}
		
		super.onPause();

	}
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	// Check the account manager each time this activity is 
    	// resumed. Because the user could have gone to the accounts & sync page
    	// in the settings and removed the account. 
    	AccountManager am = AccountManager.get(this); 
        Account[] accounts = am.getAccountsByType(getString(R.string.ACCOUNT_TYPE)); 
        if(accounts.length > 0) {
        	mHasAccount = true; 
        } else {
        	mHasAccount = false;
        }
        	
    }
    
 
    @Override
    protected void onListItemClick(android.widget.ListView l, View v, int position, long id) {
    	Log.d("TekPub", "MA - Position: " + position); 
    	Intent i = new Intent(MainActivity.this, VideoListActivity.class); 
    	long productionId = mProductions.get(position).getId();
    	i.putExtra(TekPub.VIEW_PRODUCTION, productionId); 
    	startActivity(i); 
    };
    
    class ProductionAdapter extends ArrayAdapter<Production> {
    	
    	ProductionAdapter() { 
    		super(MainActivity.this, R.layout.productionrow, R.id.label, mProductions);
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		View row;
    		
    		if(convertView == null) {
					LayoutInflater inflater = getLayoutInflater();
					convertView = inflater.inflate(R.layout.productionrow, parent, false);
			} 
		    
    		row = convertView; 
			
		    // Update the name of the label 
		    TextView label=(TextView)row.findViewById(R.id.label);
		    label.setText(getProductions().get(position).getTitle());
		
		    // Update description
		    TextView details = (TextView)row.findViewById(R.id.details_line);
		    String description = getProductions().get(position).getDescription();
		    details.setText(description == null ? "" : description); 
		  
		    return(row);
    	}
    	
    	
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    		case LOADING_DIALOG: 
    			return NotificationUtil.getLoadingDialog(this); 
    		case LOGOUT_LOADING_DIALOG: 
    			return NotificationUtil.getLogoutDialog(this);
    		case LOGOUT_CONFIRMATION_DIALOG: 
    			return getLogoutDialog(); 
    	}
    	return super.onCreateDialog(id);
    }



	private Dialog getLogoutDialog() {
		return NotificationUtil.getConfirmatinDialog(MainActivity.this, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Yes was clicked
				dialog.dismiss(); 
				
				// User wants to be logged out. 
				new LogoutTask().execute();  
			}
			
		}, 
		new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// No was clicked
				dialog.dismiss(); 
			}
		});
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater(); 
    	inflater.inflate(R.menu.mainactivity_menu, menu);
    	
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if(mHasAccount) {
    		menu.findItem(R.id.menu_login).setVisible(false); 
    		menu.findItem(R.id.menu_logout).setVisible(true);
    	} else {
    		menu.findItem(R.id.menu_login).setVisible(true); 
    		menu.findItem(R.id.menu_logout).setVisible(false);
    	}
    	return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()) { 
    		case R.id.main_activity_menu_about: 
    			NotificationUtil.getGenericMessageWithOk(MainActivity.this, getString(R.string.about), getString(R.string.about_message)).show();
    			return true; 
    		case R.id.main_activity_menu_refresh: 
    			refresh(); 
    			return true;
    		case R.id.menu_logout:
    			logout();
    			return true; 
    		case R.id.menu_login:
    			startActivity(new Intent(this, LoginActivity.class));
    			return true; 
    		case R.id.main_activity_menu_help: 
    			startHelp(); 
    			return true; 
    		case R.id.main_activity_report_bug: 
    			reportBug(); 
    			return true; 
    	}
    	 
    	return super.onMenuItemSelected(featureId, item);
    }
   
    private void reportBug() {
		Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tekpub/IntroductiontoAndroid/issues"));
		startActivity(i);
	}
    
	private void startHelp() {
		Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("http://help.tekpub.com"));
		startActivity(Intent.createChooser(i, getString(R.string.chooser_open_help_with)));
	}

	private void logout() {
		showDialog(LOGOUT_CONFIRMATION_DIALOG); 
	}



	private void refresh() {
		new RefreshProductionsTask().execute(); 
	}


	public void setProductions(List<Production> productions) {
    	mProductions = productions; 
    	
    }
    
    public List<Production> getProductions() {
    	return mProductions; 
    }
    
	private void setProductionAdapter(List<Production> result) {
		if(result != null) {
			setProductions(result); 
			if(mAdapter == null) { mAdapter = new ProductionAdapter(); } 
			setListAdapter(mAdapter);
		} else {
			// Notify user of problem and ask them if they want to retry. 
			Log.d(TAG, "No productions were found in the list.");
		}
	}
    
    class RefreshProductionsTask extends RoboAsyncTask<List<Production>> {

    	@Override
    	protected void onPreExecute() throws Exception {
    		showDialog(LOADING_DIALOG); 
    		super.onPreExecute();
    	}
    	
		@Override
		public List<Production> call() throws Exception {
			mProductionRepository.deleteAllProductions();
			List<Production> result = mProductionRepository.getProductions(); 	
			return result; 
		}
		
		@Override
		protected void onSuccess(List<Production> result) throws Exception {
			setProductionAdapter(result);
		}
		
		@Override
		protected void onException(Exception e) throws RuntimeException {
			Log.e(TAG, e.getMessage(), e); 
			NotificationUtil.getExceptionDialog(MainActivity.this, "Error", e.getMessage()).show();  
			
		}
		
		@Override
		protected void onFinally() throws RuntimeException {
			dismissDialog(LOADING_DIALOG); 
			super.onFinally();
		}
    	
    }
    
    /**
     * Responsible for logging out the user as well as reloading the productions list.
     */
    class LogoutTask extends RoboAsyncTask<Boolean> {

    	@Override
    	protected void onPreExecute() throws Exception {
    		showDialog(LOGOUT_LOADING_DIALOG); 
    		super.onPreExecute();
    	}
    	
		@Override
		public Boolean call() throws Exception {
			// Create Logout Task 
	    	AccountManager am = AccountManager.get(MainActivity.this); 
	    	Account[] accounts = am.getAccountsByType(getString(R.string.ACCOUNT_TYPE));
	    	
	    	boolean accountWasRemoved = false; 
	    	
	    	// We can only have one account.
	    	if(accounts.length > 0) {
	    		accountWasRemoved = am.removeAccount(accounts[0], null, null).getResult(); 
	    	}
	    	return accountWasRemoved; 
		}
		
		@Override
		protected void onException(Exception e) throws RuntimeException {
			Log.e(TAG, e.getMessage(), e);
			NotificationUtil.getExceptionDialog(MainActivity.this, MainActivity.this.getString(R.string.exception_logout_title), e.getMessage()); 
			super.onException(e);
		}
    	
		@Override
		protected void onSuccess(Boolean accountWasRemoved) throws Exception {
			if(accountWasRemoved){
				new RefreshProductionsTask().execute();
				mHasAccount = false; 
			} else {
				
			}
			 
			super.onSuccess(accountWasRemoved);
		}
		
		@Override
		protected void onFinally() throws RuntimeException {
			dismissDialog(LOGOUT_LOADING_DIALOG); 
			super.onFinally();
		}
		
    }
    
    class GetEpisodesTask extends RoboAsyncTask<List<Production>> {
    	
    	@Override
    	protected void onPreExecute() throws Exception {
    		showDialog(LOADING_DIALOG); 
    		super.onPreExecute();
    	}
    	
		@Override
		public List<Production> call() throws Exception {	
			Log.d(TAG,"In async task method, getting productions.");
			
			return mProductionRepository.getProductions(); 
		}

		@Override
		protected void onSuccess(List<Production> result) throws Exception {
			setProductionAdapter(result);
		}
		
		@Override
		protected void onException(Exception e) throws RuntimeException {
			Log.e(TAG, e.getMessage(), e);
			
			NotificationUtil.getExceptionDialog(MainActivity.this, "Error", e.getMessage()).show();
			
		}
		
		@Override
		protected void onFinally() throws RuntimeException {
			dismissDialog(LOADING_DIALOG); 
			super.onFinally();
		}
    	
    }
    
}
