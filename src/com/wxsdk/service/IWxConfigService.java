package com.wxsdk.service;

import com.kidmate.wx.pojo.AccessToken;
import com.kidmate.wx.pojo.JsapiTicket;

public interface IWxConfigService {

	public AccessToken getAccessToken();

	public JsapiTicket getJSApiTicket();
}
