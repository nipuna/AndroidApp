package com.tekpub.messaging;

public interface IProgressNotifier {
	/**
	 * Publishes the progress of a download. The progress unit of 
	 * measure is 1/100. 
	 * @param progress
	 */
	void publishProgress(int progress); 
}
