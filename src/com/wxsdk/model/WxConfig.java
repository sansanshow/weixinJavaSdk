package com.wxsdk.model;

import java.io.Serializable;

public class WxConfig implements Serializable{
	private static final long serialVersionUID = 3914936716193181470L;
	private String appid;
	private String appsecret;
	private String version;
	private int source;
	private boolean develop;
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getAppsecret() {
		return appsecret;
	}
	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public boolean isDevelop() {
		return develop;
	}
	public void setDevelop(boolean develop) {
		this.develop = develop;
	}


}
