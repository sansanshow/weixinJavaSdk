package com.wxsdk.model;

import java.io.Serializable;
import java.util.Date;


/**
 * 微信通用接口凭证
 * 
 * @author 
 * @date 2013-08-08
 */
public class AccessToken extends ReturnCode implements Serializable{

	private static final long serialVersionUID = -5346004704204316650L;

	// 获取到的凭证
	private String access_token;
	// 凭证有效时间，单位：秒
	private long expires_in;
	private Long createtime;  //创建时间
	
	public AccessToken(){
		createtime=new Date().getTime();
	}


	public String getAccess_token() {
		return access_token;
	}


	public void setAccess_token(String access_token) {
		this.access_token = access_token;
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
		return "AccessToken:[token:"+this.access_token+",expiresIn:"+this.expires_in+"]";
	}
}
