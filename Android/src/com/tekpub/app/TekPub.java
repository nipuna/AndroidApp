package com.tekpub.app;

import java.util.List;

import roboguice.application.RoboApplication;

import com.google.inject.Module;
import com.tekpub.ioc.*;


public class TekPub extends RoboApplication {
	
	// Intent
	public static final String VIEW_PRODUCTION = "view_video_category";
	public static final String VIEW_SELECTED_EPISODE = "view_video";
	public static final String MESSAGE_KEY = "view_message";
	public static final String MESSAGE_TYPE = "message_type";
	public static final String EPISODE_NUMBER = "episode_number";
	public static final String EPISODE_SLUG = "episode_slug";

	public static final String EXTRA_RELOAD_PRODUCTIONS = "reload_productions";
	public static final String ACTION_AUTHENTICATOR_LOGIN = "com.tekpub.authenticator.LOGIN";
	
	public static final String LOGIN_PREFERENCES = "login_preferences"; 
	public static final String PREFERENCE_QUERY_TO_LOGIN = "pref_query_to_login"; 
	
	private static TekPub instance = null; 
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this; 
	}
	
	@Override
	protected void addApplicationModules(List<Module> modules) {
		modules.add(new DefaultModule());
	}
	
	public static TekPub getInstance() {
		if(instance != null) {
			return instance;
		} else {
			return new TekPub();
		}
	}
	
	
}
