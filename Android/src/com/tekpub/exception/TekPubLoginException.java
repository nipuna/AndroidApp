package com.tekpub.exception;

public class TekPubLoginException extends TekPubApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 492242715860126770L;
	private int errorCode; 
	
	public TekPubLoginException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode; 
	}
	
	public int getErrorCode() {
		return this.errorCode; 
	}

}
