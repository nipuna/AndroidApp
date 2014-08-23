package com.tekpub.storage;

public interface ISdCardManager {
	boolean isExternalDirectoryWriteable(); 
	String getAppExternalDataDirectory(String packageName); 
	String getEpisodeFileName(String episodeId); 
	boolean isSpaceAvailableFor(long requestedByteLength);
}
