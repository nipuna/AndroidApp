package com.tekpub.services;

import roboguice.service.RoboIntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public abstract class WakefulntentService extends RoboIntentService {
	
	abstract void doWork(Intent intent);
	
	public static final String LOCK_NAME_STATIC="com.tekpub.services.wakefulintentservice";
	private static PowerManager.WakeLock lockStatic=null;
	
	public static void acquireStaticLock(Context context) {
		getLock(context).acquire();
	}
	
	synchronized private static PowerManager.WakeLock getLock(Context context) {
		if (lockStatic==null) {
			PowerManager mgr=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
			lockStatic=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
			lockStatic.setReferenceCounted(true);
		}
		return(lockStatic);
	}
	
	public WakefulntentService(String name) {
		super(name);
	}
	
	@Override
	final protected void onHandleIntent(Intent intent) {
		try {
			doWork(intent);
		}
		finally {
			getLock(this).release();
		}
	}
}
