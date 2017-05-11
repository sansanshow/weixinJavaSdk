package com.wxsdk.model;

import java.io.Serializable;
import java.util.Date;

public class JsapiTicket implements Serializable{
	private String errcode;
	private String errmsg;
	// 获取到的ticket
	private String ticket;
	// 凭证有效时间，单位：秒
	private long expires_in;
	
	private Long createtime;
	public JsapiTicket() {
		this.createtime = new Date().getTime();
	}
	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getErrcode() {
		return errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public long getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(long expires_in) {
		this.expires_in = expires_in;
	}

	public Long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "JsapiTicket:[ticket:" + ticket + ",expiresIn:" + expires_in + "]";
	}
}
