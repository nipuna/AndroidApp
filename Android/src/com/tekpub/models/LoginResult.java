package com.tekpub.models;

import android.text.TextUtils;

public class LoginResult {
	private String token;
	private int errorCode; 
	private String errorMessage; 
	
	public LoginResult(String token) {
		this.token = token; 
	}
	
	public LoginResult(int errorCode, String errorMessage) {
		this.errorMessage = errorMessage;
		this.errorCode = errorCode; 
	}
	
	public boolean wasSuccess() {
		return !TextUtils.isEmpty(token);
	}
	
	public String getToken() {
		return token; 
	}
	
	public int getErrorCode() {
		return errorCode; 
	}

	public String getErrorMessage() {
		return errorMessage; 
	}
}
