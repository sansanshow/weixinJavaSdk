package com.wxsdk.model.response;


/**
 * 语音model
 * 
 * @author Keaven
 * 
 */
public class Voice {
	private String MediaId;

	public Voice() {
		super();
	}
	public Voice(String mediaId) {
		super();
		MediaId = mediaId;
	}

	public String getMediaId() {
		return MediaId;
	}

	public void setMediaId(String mediaId) {
		MediaId = mediaId;
	}
}
