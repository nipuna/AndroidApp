package com.tekpub.models;

import android.text.TextUtils;

public class VideoResult {
	private String videoUrl; 
	private String errorMessage; 
	private int errorCode; 
	
	public VideoResult(String videoUrl) {
		this.videoUrl = videoUrl; 
	}
	
	public VideoResult(String errorMessage, int errorCode) {
		this.errorMessage = errorMessage; 
		this.errorCode = errorCode; 
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public boolean videoWasFound() {
		return TextUtils.isEmpty(videoUrl) == false; 
	}
	
	
}
