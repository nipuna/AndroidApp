package com.tekpub.models;

import java.util.ArrayList;
import java.util.List;

public class Production {
	
	private String title; 
	private String slug; 
	private double price; 
	private boolean can_watch; 
	private String description; 
	private List<EpisodeContainer> episodes = new ArrayList<EpisodeContainer>();
	private List<Episode> episodeList = new ArrayList<Episode>(); 

	private long id; 
	
	
	public String getTitle() {
		return title;
	}
	public String getSlug() {
		return slug;
	}
	public double getPrice() {
		return price;
	}
	public boolean getCan_watch() {
		return can_watch;
	}
	public String getDescription() {
		return description;
	}
	public List<Episode> getEpisodes() {
		if(episodeList.size() == 0) {
			for(EpisodeContainer ep : episodes) {
				episodeList.add(ep.getEpisode());
			}
		}
		return episodeList;
	}  
	public void setTitle(String title) {
		this.title = title;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public void setCan_watch(boolean canWatch) {
		can_watch = canWatch;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setEpisodes(List<Episode> episodes) {
		this.episodeList = episodes;
	}
	public void setId(long id) {
		this.id = id; 
	}
	public long getId() { 
		return id; 
	}
	
	/**
	 * Gets the episode with the specified episode id
	 * @param episodeRowId
	 * @return
	 */
	public Episode getEpisode(int episodeRowId) {
		for(Episode episode : getEpisodes()) {
			if(episode.getRowId() == episodeRowId) {
				return episode; 
			}
		}
		return null;
	}
	
	
}
