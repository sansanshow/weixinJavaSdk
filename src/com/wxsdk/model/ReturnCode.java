package com.wxsdk.model;

import java.io.Serializable;

public class ReturnCode implements Serializable{
	private static final long serialVersionUID = 1L;
	private String errcode;
	private String errmsg;
	public ReturnCode() {
		super();
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
	
}
