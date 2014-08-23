package com.tekpub.models;

public class Episode {
	
	private int row_id; 
	public String created_at; 
	public int downloads; 
	private String description; 
	private int duration; 
	private double height; 
	private int id; 
	private String notes; 
	private int number; 
	public String override_url; 
	private int production_id; 
	private String released_at; 
	private String slug; 
	private String channel_slug; 
	private String title;
	private String updated_at; 
	private double width;
	private int views; 
	private boolean is_downloading;

	
	public void setDescription(String description) {
		this.description = description;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public void setProduction_id(int productionId) {
		production_id = productionId;
	}
	public void setReleased_at(String releasedAt) {
		released_at = releasedAt;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public String getDescription() {
		return description;
	}
	public int getDuration() {
		return duration;
	}
	public double getHeight() {
		return height;
	}
	public int getId() {
		return id;
	}
	public String getNotes() {
		return notes;
	}
	public int getNumber() {
		return number;
	}
	public int getProduction_id() {
		return production_id;
	}
	public String getReleased_at() {
		return released_at;
	}
	public String getSlug() {
		return slug;
	}
	public String getTitle() {
		return title;
	}
	public double getWidth() {
		return width;
	}


	public String getCreatedAt() {
		return created_at;
	}
	public void setCreatedAt(String createdAt) {
		created_at = createdAt;
	}
	public int getDownloads() {
		return downloads;
	}
	public void setDownloads(int downloads) {
		this.downloads = downloads;
	}
	public String getOverrideUrl() {
		return override_url;
	}
	public void setOverrideUrl(String overrideUrl) {
		override_url = overrideUrl;
	}
	public String getUpdatedAt() {
		return updated_at;
	}
	public void setUpdatedAt(String updatedAt) {
		updated_at = updatedAt;
	}
	public int getViews() {
		return views;
	}
	public void setViews(int views) {
		this.views = views;
	}
	public void setChannelSlug(String channel_slug) {
		this.channel_slug = channel_slug;
	}
	
	public String getChannelSlug() {
		return channel_slug;
	}
	public void setIsDownloading(boolean is_downloading) {
		this.is_downloading = is_downloading;
	}
	public boolean getIsDownloading() {
		return is_downloading;
	}
	public void setRowId(int row_id) {
		this.row_id = row_id;
	}
	public int getRowId() {
		return row_id;
	}
	
}
