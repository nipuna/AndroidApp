package com.tekpub.storage;

import android.os.Environment;
import android.os.StatFs;

public class SdCardManager implements ISdCardManager {

	
	private static final String FILE_EXTENSION = ".mp4"; 
	
	@Override
	public String getAppExternalDataDirectory(String packageName) {
		return String.format("%s/Android/data/%s/files/", Environment.getExternalStorageDirectory(), packageName);
	}

	@Override
	public String getEpisodeFileName(String episodeId) {
		return episodeId + FILE_EXTENSION;
	}

	@Override
	public boolean isExternalDirectoryWriteable() {
		String state = Environment.getExternalStorageState();
		 if (Environment.MEDIA_MOUNTED.equals(state)) {
		        return true;
		    } else  {
		        return false;
		    }
	}

	@Override
	public boolean isSpaceAvailableFor(long requestedByteLength) {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
		
		return requestedByteLength > bytesAvailable; 

	}
	
}
