package com.tekpub.mappers;

import android.database.Cursor;

import com.tekpub.models.Production;
import com.tekpub.storage.DbConstants;

public class ProductionCursorMapper implements IProductionCursorMapper {
	
	@Override
	public Production MapFrom(Cursor c) {
		
		Production p = new Production(); 
		
		try { 
		
			//if(c != null && c.isAfterLast() == false) { 
			//	c.moveToFirst(); 
				p.setId(c.getLong(c.getColumnIndex(DbConstants.KEY_PROD_ROWID))); 
				p.setCan_watch(c.getInt(c.getColumnIndex(DbConstants.KEY_PROD_CAN_WATCH)) == 0 ? false : true); 
				p.setDescription(c.getString(c.getColumnIndex(DbConstants.KEY_PROD_DESCRIPTION))); 
				p.setPrice(c.getDouble(c.getColumnIndex(DbConstants.KEY_PROD_PRICE))); 
				p.setSlug(c.getString(c.getColumnIndex(DbConstants.KEY_PROD_SLUG))); 
				p.setTitle(c.getString(c.getColumnIndex(DbConstants.KEY_PROD_TITLE))); 
				
				//p.setEpisodes(mEpisodeRepository.getEpisodesFrom(c.getInt(c.getColumnIndex(DbConstants.KEY_PROD_ROWID)))); 
			//}	
		} finally { 
			//c.close();
		}
		 
		return p;
	}

}
