package com.wxsdk.model.response;

/**
 * 语音消息
 * 
 * @author Keaven
 * 
 */
public class VoiceMessage extends BaseMessage {
	// 语音
	private Voice Voice;

	public Voice getVoice() {
		return Voice;
	}

	public void setVoice(Voice voice) {
		Voice = voice;
	}

}
