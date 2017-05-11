package com.wxsdk.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wxsdk.model.AccessToken;


import net.sf.json.JSONObject;


public interface IWxCoreService {
	public String processRequest(HttpServletRequest request);
	public JSONObject getOpenidByCode(String code,String appid,String appsecret);
	public Map<String, String> getJsConfig(String urlString, String appid,String appsecret);
	public boolean judgeIsSubscribe(String openid,String token);
	public AccessToken getCacheToken(String appid,String appsecret);
}
