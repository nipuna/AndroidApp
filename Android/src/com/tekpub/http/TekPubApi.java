package com.tekpub.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.tekpub.exception.TekPubApiException;
import com.tekpub.exception.TekPubFileNotFoundException;
import com.tekpub.exception.TekPubFreeSpaceException;
import com.tekpub.exception.TekPubLoginException;
import com.tekpub.io.StreamHelper;
import com.tekpub.mappers.IProductionListMapper;
import com.tekpub.messaging.IProgressNotifier;
import com.tekpub.models.LoginResult;
import com.tekpub.models.Production;
import com.tekpub.models.VideoResult;
import com.tekpub.player.R;
import com.tekpub.storage.ISdCardManager;

public class TekPubApi implements ITekPubApi {
	
	private IProductionListMapper mMapper; 
	private ISdCardManager mSdCardManager;
	private Context mContext; 
	
	
	private static final String TAG = "TekPub API"; 
	private static final String API_SCHEME = "http"; // "https";
	private static final String API_HOST = "tekpub.com"; // "secure.tekpub.com";
	private static final String API_ROOT = "/api/"; 
	private static final String API_AUTHENTICATE = API_ROOT + "authenticate"; 
	private static final String API_PRODUCTIONS = API_ROOT + "productions";
	private static final String API_VIEW_EPISODE = API_ROOT + "view/{slug}/{number}"; // View Episode: http://tekpub.com/api/view/slug/number
	private static final int API_PORT = -1; //443; 
	
	//private static final String API_BASE_URL = "https://secure.tekpub.com/api/";
	
	
	@Inject
	public TekPubApi(IProductionListMapper mapper, ISdCardManager sdCardManager, Provider<Application> provider) {
		mMapper = mapper; 
		mSdCardManager = sdCardManager;
		mContext = provider.get(); 
	}
	
	/**
	 * Gets the Auth Token from the TekPub api. The auth token is valid for 
	 * 30 days.
	 * @throws TekPubLoginException 
	 */
	public LoginResult getAuthToken(String email, String password) throws IOException, TekPubLoginException { 
		List<NameValuePair> params = new ArrayList<NameValuePair>(); 
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));  
		//String query = URLEncodedUtils.format(params, "UTF-8");
		HttpEntity entity; 
		entity = new UrlEncodedFormEntity(params);
		URI uri = null;
		try {
			// -1 port users default http port (80)
			uri = URIUtils.createURI(API_SCHEME, API_HOST, API_PORT, API_AUTHENTICATE, null, null);
		} catch (URISyntaxException e1) {
			// Shouldn't get here as the URL's are not generated. They are known routes.
			// But just in case we'll leave a trace statement. 
			Log.e(TAG, e1.getMessage());
		} 
		
		Log.d(TAG, "Setting up http post.");
		
		HttpPost post = new HttpPost(uri);
		post.setEntity(entity);
		HttpResponse response = null;
		DefaultHttpClient client = new DefaultHttpClient(); 
		
		LoginResult result = null; 
		
		try {
			response = client.execute(post); 
			
			int statusCode = response.getStatusLine().getStatusCode(); 
			switch(statusCode) {
				case HttpStatus.SC_OK: 
					// Get token
					InputStream is = response.getEntity().getContent();
					String httpResult = StreamHelper.convertInputStreamToString(is);
					if(httpResult.toLowerCase().equals("invalid login") || httpResult.toLowerCase().equals("not found")) {
						result = new LoginResult(403, mContext.getString(R.string.login_invalid)); 
						Log.e(TAG, "Could not get token: " + httpResult); 
					} else {
						result = new LoginResult(httpResult);
						Log.d(TAG, "Got token."); 
					}
					
					break; 
				case HttpStatus.SC_UNAUTHORIZED:  
					// Return a new TokenAPiResult with an error. 
					result = new LoginResult(403, mContext.getString(R.string.login_invalid));
					break; 
				default: 
					// Return a new token ApiResult with an error.
					String errorResult = StreamHelper.convertInputStreamToString(response.getEntity().getContent());
					throw new TekPubLoginException(errorResult, statusCode); 
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e; 
		}
		
		Log.d(TAG, "Login result success: " + result.wasSuccess()); 
		return result; 
	}
	
	
	public List<Production> getProductions() throws TekPubApiException, IllegalStateException, IOException, OperationCanceledException, AuthenticatorException {
		
		// Get the auth token (if there is one). 
		String tekPubTokenType = mContext.getString(R.string.ACCOUNT_TYPE); 
		AccountManager am = (AccountManager) mContext.getSystemService(Application.ACCOUNT_SERVICE);
		Account[] accounts = am.getAccountsByType(tekPubTokenType); 
		
		String authToken = null; 
		
		if(accounts.length > 0) { 
			// The user has a TekPub account, get the auth token for use.
			authToken = am.blockingGetAuthToken(accounts[0], tekPubTokenType, true); 
		}

	
		
		URI uri = null;  
		
		try {
			// -1 port users default http port (80)
			uri = URIUtils.createURI(API_SCHEME, API_HOST, API_PORT, API_PRODUCTIONS, null, null);
			Log.d(TAG, "Using Url: " + uri.toString()); 
		} catch (URISyntaxException e1) {
			// Shouldn't get here as we are working with known routes, not dynamic ones. 
			e1.printStackTrace();
		} 
		
		DefaultHttpClient client = new DefaultHttpClient(); 
		HttpPost post = new HttpPost(uri);
		
		if(TextUtils.isEmpty(authToken) == false) {
			// We have an auth token, supply it in the post so the user
			// can watch all of their valid videos that they have access to. 
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 
			params.add(new BasicNameValuePair("auth", authToken));
			HttpEntity authEntity = new UrlEncodedFormEntity(params);
			post.setEntity(authEntity);
		}
		
		HttpResponse response = null;
		try {
			response = client.execute(post);
		 
			int statusCode = response.getStatusLine().getStatusCode();
			
			switch(statusCode) {
				case HttpStatus.SC_OK: 
					InputStream is = response.getEntity().getContent();
					String json = StreamHelper.convertInputStreamToString(is);
					return mMapper.MapFrom(json);
				case HttpStatus.SC_UNAUTHORIZED:
				case HttpStatus.SC_NOT_FOUND:
				case HttpStatus.SC_INTERNAL_SERVER_ERROR: 
					throw new TekPubApiException(String.format("Error connecting to API. Status Code: %s. Info: %s", statusCode, response.getStatusLine().getReasonPhrase()));
				default: 
					throw new TekPubApiException(String.format("Could not connect to API. Status Code: %s. Info: %s", statusCode, response.getStatusLine().getReasonPhrase()));	
					
			}
		
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e; 
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e; 
		}
		
		
	}

	@Override
	public void downloadEpisode(String episodeId, Context context, IProgressNotifier progress) throws TekPubFreeSpaceException, TekPubFileNotFoundException, IOException {
		
		HttpURLConnection c = null;
		
		 try { 
			 	String destinationDirectory = mSdCardManager.getAppExternalDataDirectory(context.getPackageName()); 
			 	String fileName = mSdCardManager.getEpisodeFileName(episodeId); 
			 	
			    URL u = new URL(getEpisodeUrl(episodeId));

			    c = (HttpURLConnection) u.openConnection();
			    c.setRequestMethod("GET");
			    c.setDoOutput(true);
			    c.setRequestProperty("User-Agent","Android");
			    c.connect();

			    int responseCode = c.getResponseCode(); 
			    
			    switch (responseCode) {
			    	case HttpURLConnection.HTTP_OK:
			    		// File found, lets roll
			    		
			    	case HttpURLConnection.HTTP_NOT_FOUND: 
			    		// File not found. Throw. 
			    		String responseMessage = c.getResponseMessage(); 
			    		 Log.d(TAG, "Response Code: " + responseCode + ". Response Message: " + responseMessage);
			    		throw new TekPubFileNotFoundException(responseMessage); 
			    	case HttpURLConnection.HTTP_INTERNAL_ERROR: 
			    		// 500 Error, something went wrong. 
			    		
			    	default: 
			    		// Unknown. Throw with error code and response message. 
			    }
			    
			  
			    // Get the content-length header to determine how big our file is. 
			    // This will allow us to calculate download progress. 
			    int contentLengthValue = c.getContentLength();  
			    Log.d(TAG, "Content Length of attempted file to be downloaded: " + contentLengthValue);
			    long contentLength = Long.parseLong(c.getHeaderField("Content-Length"));
			   
			    if(mSdCardManager.isSpaceAvailableFor(contentLength) == false) {
			    	// There is not enough space available on the SD card to save the file. 
			    	// We cannot download it, therefore, let the user know. 
			    	throw new TekPubFreeSpaceException(context.getString(R.string.sdcard_space_unavailable)); 
			    }
			   
			    FileOutputStream f = new FileOutputStream(new File(destinationDirectory, fileName));
			    InputStream in = c.getInputStream();

			    byte[] buffer = new byte[1024];
			    int len1 = 0;
			    long bytesReadIn = 0; 
			    while ( (len1 = in.read(buffer)) != -1 ) {
			      f.write(buffer,0, len1);
			      bytesReadIn += len1; 
			      progress.publishProgress(((int)(bytesReadIn / contentLength)) * 100);
			    }

			    f.close();
			    
		 }  catch (IllegalStateException e) {
				Log.e(TAG, e.getMessage(), e);
		 } catch (TekPubFileNotFoundException e) {
			 	Log.e(TAG, e.getMessage(), e);
			 	throw e; 
		 } catch (TekPubApiException e) {
				Log.e(TAG, e.getMessage(), e); 		
		 } catch (IOException e) {
               Log.e(TAG, e.getMessage(), e);
               throw e; 
		 } finally {
			 if(c != null) 
				 c.disconnect(); 
		 }
	
       
		
		
	}
	
	private String getEpisodeUrl(String episodeId) throws IllegalStateException, IOException, TekPubApiException {
		DefaultHttpClient client = new DefaultHttpClient(); 
		
		String episodesUrl = String.format("http://tekpub.com/episodes/%s.json", episodeId); 
		Log.d(TAG, "Obtaining Episode URL from: " + episodesUrl); 
		HttpGet get = new HttpGet(episodesUrl);
		HttpResponse response = null;
	
		response = client.execute(get);
		
		 
		int statusCode = response.getStatusLine().getStatusCode();
		
		switch(statusCode) {
			case 200:
			case 201: 
				//InputStream is = response.getEntity().getContent();
				//String url = StreamHelper.convertInputStreamToString(is);
				

				
				
			case 401:
			case 404:
			case 500: 
				throw new TekPubApiException(String.format("Error connecting to API. Status Code: %s. Info: %s", statusCode, response.getStatusLine().getReasonPhrase()));
			default: 
				throw new TekPubApiException(String.format("Could not connect to API. Status Code: %s. Info: %s", statusCode, response.getStatusLine().getReasonPhrase()));
				
				
		}
		
	}

	@Override
	public VideoResult getVideo(String slug, int number) throws TekPubApiException, OperationCanceledException, AuthenticatorException, IOException {
		
		VideoResult videoResult = null; 
		boolean shouldTryAgain = true; 
		int retryAttempts = 0; 
		
		// Retry 
		do {
			
			Log.d(TAG, "Attempt #" + retryAttempts + " (zero based) of getting slug: " + slug + ", number: " + number);
			
			// Get the auth token (if there is one). 
			String tekPubTokenType = mContext.getString(R.string.ACCOUNT_TYPE); 
			AccountManager am = (AccountManager) mContext.getSystemService(Application.ACCOUNT_SERVICE);
			Account[] accounts = am.getAccountsByType(tekPubTokenType); 
			
			String authToken = null; 
			
			if(accounts.length > 0) { 
				// The user has a TekPub account, get the auth token for use.
				authToken = am.blockingGetAuthToken(accounts[0], tekPubTokenType, true); 
			}
			
			URI uri = null;  
			
			try {
				String viewEpisodeUrlFragment = API_VIEW_EPISODE.replace("{slug}", slug).replace("{number}", Integer.toString(number)); 
				// -1 port users default http port (80)
				uri = URIUtils.createURI(API_SCHEME, API_HOST, API_PORT, viewEpisodeUrlFragment, null, null);
				Log.d(TAG, "Using Url: " + uri.toString()); 
			} catch (URISyntaxException e1) {
				// Shouldn't get here as we are working with known routes, not dynamic ones. 
				e1.printStackTrace();
			} 
			
			DefaultHttpClient client = new DefaultHttpClient(); 
			HttpPost post = new HttpPost(uri);
			
			if(TextUtils.isEmpty(authToken) == false) {
				// We have an auth token, supply it in the post so request can be authorized
				List<NameValuePair> params = new ArrayList<NameValuePair>(); 
				params.add(new BasicNameValuePair("auth", authToken));
				HttpEntity authEntity = new UrlEncodedFormEntity(params);
				post.setEntity(authEntity);
			}
			
			HttpResponse response = null;
			try {
				response = client.execute(post);
			 
				int statusCode = response.getStatusLine().getStatusCode();
				 
				
				switch(statusCode) {
					case HttpStatus.SC_OK: 
						InputStream is = response.getEntity().getContent();
						String result = StreamHelper.convertInputStreamToString(is);
						Log.d(TAG, "Intial Url: " + result); 
						
						if(result.toLowerCase().equals("forbidden")) {
							videoResult = new VideoResult(mContext.getString(R.string.video_unauthorized), HttpStatus.SC_UNAUTHORIZED); 
						} else if(result.toLowerCase().equals("can't find that")) {
							videoResult = new VideoResult(mContext.getString(R.string.video_not_found), HttpStatus.SC_NOT_FOUND); 
						} else {
							// We found the video
							String url = result;
							
							
						   if(TextUtils.isEmpty(authToken) == false) {
								// We have an auth token, supply it in the post so request can be authorized
								List<NameValuePair> params = new ArrayList<NameValuePair>(); 
								params.add(new BasicNameValuePair("auth", authToken));
								//HttpEntity authEntity = new UrlEncodedFormEntity(params);
								url = url + "?" + URLEncodedUtils.format(params, "UTF-8");
		
							}
						   Log.d(TAG, "Final Url: " + url); 
						   //videoResult = new VideoResult(url); 
						   // Have to get a final url as the Android video player will not handle 302's that well. 
						   HttpGet httpGet = new HttpGet(url);
						   
					       HttpContext context = new BasicHttpContext(); 
					       HttpResponse finalResponse = client.execute(httpGet, context); 
					       if (finalResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
					           throw new IOException(finalResponse.getStatusLine().toString());
					       HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute( 
					               ExecutionContext.HTTP_REQUEST);
					       HttpHost currentHost = (HttpHost)  context.getAttribute( 
					               ExecutionContext.HTTP_TARGET_HOST);
					       String currentUrl = currentHost.toURI() + currentReq.getURI();
					       Log.d(TAG, "Final URL: " + currentUrl); 
					       videoResult = new VideoResult(currentUrl);
						}
						
						
						
						break; 
					case HttpStatus.SC_UNAUTHORIZED:
						videoResult = new VideoResult(mContext.getString(R.string.video_unauthorized), HttpStatus.SC_UNAUTHORIZED);
						break;
					case HttpStatus.SC_NOT_FOUND:
						videoResult = new VideoResult(mContext.getString(R.string.video_not_found), HttpStatus.SC_NOT_FOUND);
						break; 
					default: 
						throw new TekPubApiException(String.format("Could not connect to API. Status Code: %s. Info: %s", statusCode, response.getStatusLine().getReasonPhrase()));
						
						
				}
				
			 
			
			} catch (ClientProtocolException e) {
				Log.e(TAG, e.getMessage(), e);
				throw e; 
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
				throw e; 
			}
			
			// Only retry once. 
			if(retryAttempts >= 1 || videoResult.videoWasFound()) {
				shouldTryAgain = false; 
			} else {
				// Clear the existing auth token as it looks like we were not able to retry. 
				am.invalidateAuthToken(tekPubTokenType, authToken);
				retryAttempts = retryAttempts + 1; 
			}
			
						
		} while(shouldTryAgain);
			
		return videoResult;
	}
		
		



}
