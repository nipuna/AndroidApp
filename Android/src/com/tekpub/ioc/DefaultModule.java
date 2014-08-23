package com.tekpub.ioc;

import com.google.inject.AbstractModule;
import com.tekpub.http.ITekPubApi;
import com.tekpub.http.TekPubApi;
import com.tekpub.mappers.EpisodeCursorMapper;
import com.tekpub.mappers.IEpisodeCursorMapper;
import com.tekpub.mappers.IProductionCursorMapper;
import com.tekpub.mappers.IProductionListMapper;
import com.tekpub.mappers.ProductionCursorMapper;
import com.tekpub.mappers.ProductionListMapper;
import com.tekpub.messaging.INotificationService;
import com.tekpub.messaging.NotificationService;
import com.tekpub.storage.DbHelper;
import com.tekpub.storage.EpisodeRepository;
import com.tekpub.storage.IDbHelper;
import com.tekpub.storage.IEpisodeRepository;
import com.tekpub.storage.IProductionRepository;
import com.tekpub.storage.ISdCardManager;
import com.tekpub.storage.ProductionRepository;
import com.tekpub.storage.SdCardManager;

public class DefaultModule extends AbstractModule {

	@Override
	protected void configure() {
		
		// HTTP components 
		bind(ITekPubApi.class).to(TekPubApi.class); 
		
		// Database
		requestStaticInjection(DbHelper.class); 
		bind(IDbHelper.class).to(DbHelper.class);
		bind(IProductionRepository.class).to(ProductionRepository.class); 
		bind(IEpisodeRepository.class).to(EpisodeRepository.class); 
		
		// Mapping
		bind(IProductionListMapper.class).to(ProductionListMapper.class);
		bind(IProductionCursorMapper.class).to(ProductionCursorMapper.class); 
		bind(IEpisodeCursorMapper.class).to(EpisodeCursorMapper.class);
		
		// Notification
		bind(INotificationService.class).to(NotificationService.class); 
		
		// Storage
		bind(ISdCardManager.class).to(SdCardManager.class); 
		
	}

}
