package com.wxsdk.config;

public class WeixinConfig {
	private String appid;
	private String appsecret;
	private String xcx_appid;
	private String xcx_appsecret;
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

	public String getXcx_appid() {
		return xcx_appid;
	}

	public void setXcx_appid(String xcx_appid) {
		this.xcx_appid = xcx_appid;
	}

	public String getXcx_appsecret() {
		return xcx_appsecret;
	}

	public void setXcx_appsecret(String xcx_appsecret) {
		this.xcx_appsecret = xcx_appsecret;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[appid:" + this.appid + ",appsecret:" + this.appsecret + "]";
	}

}
