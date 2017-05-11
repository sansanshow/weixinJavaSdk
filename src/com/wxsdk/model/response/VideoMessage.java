package com.wxsdk.model.response;

/**
 * 视频消息
 * 
 * @author Keaven
 * 
 */
public class VideoMessage extends BaseMessage {
	// 视频
	private Video Video;

	public Video getVideo() {
		return Video;
	}

	public void setVideo(Video video) {
		Video = video;
	}

}
