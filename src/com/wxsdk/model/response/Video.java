package com.wxsdk.model.response;


public class Video {
	private String MediaId;
	private String Title;
	private String Description;

	public Video(String mediaId, String title, String description) {
		super();
		MediaId = mediaId;
		Title = title;
		Description = description;
	}

	public Video() {
		// TODO Auto-generated constructor stub
		super();
	}

	public String getMediaId() {
		return MediaId;
	}

	public void setMediaId(String mediaId) {
		MediaId = mediaId;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

}
