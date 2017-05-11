package com.wxsdk.service.impl;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wxsdk.model.JsapiTicket;
import com.wxsdk.model.WxConfig;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;


@Service("WxConfigService")
public class WxConfigService implements IWxConfigService{
	private static final String WX_ACCESSTOKEN = "wx_accesstoken";
	private static final String WX_JSAPITICKET = "wx_jsapiticket";
	@Autowired
	private WxConfig config;
	
	@Resource(name="KmWxInfoDAO")
	private KmWxInfoDAO kmWxInfoDAO;
	
	@Resource(name="ShardedJedisPool")
	private ShardedJedisPool shardedJedisPool;
	
	@Override
	public AccessToken getAccessToken() {
		AccessToken accessToken = null;
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		if(shardedJedis.hexists(WX_ACCESSTOKEN, "Token") && shardedJedis.hexists(WX_ACCESSTOKEN, "updatetime")){
			String token = shardedJedis.hget(WX_ACCESSTOKEN, "Token");
			long updatetime = Long.parseLong(shardedJedis.hget(WX_ACCESSTOKEN, "updatetime"));
			if((System.currentTimeMillis() - updatetime) < 7000 * 1000){
				System.out.println("--getAccessToken-shardedJedis--");
				accessToken =new AccessToken();
				accessToken.setToken(token);
				accessToken.setExpiresIn((int)((System.currentTimeMillis() - updatetime)/1000));
				shardedJedisPool.returnResource(shardedJedis);
				return accessToken;
			}
		}
		KmWxInfo instance = new KmWxInfo();
		instance.setAppid(config.getAppid());
		instance.setType(1);
		
		List<KmWxInfo> tokens = kmWxInfoDAO.findByExample(instance);
		if(tokens!=null&&tokens.size()>0){
			System.err.println("accessToken==1");
			instance = tokens.get(0);
			if((System.currentTimeMillis() - tokens.get(0).getUpdatetime().getTime()) < 7000 * 1000){
				System.err.println("accessToken==1-1");
//				accessToken =new AccessToken();
//				accessToken.setToken(tokens.get(0).getContent());
//				accessToken.setExpiresIn((int)((System.currentTimeMillis() - tokens.get(0).getUpdatetime().getTime())/1000));
//				return accessToken;
			}else{
				System.err.println("accessToken==1-2");
				accessToken=WeixinUtil.getAccessToken(config.getAppid(), config.getAppsecret());
				instance.setContent(accessToken.getToken());
				instance.setUpdatetime(new Date(System.currentTimeMillis()));
				kmWxInfoDAO.attachDirty(instance);
			}
		} else {
			System.err.println("accessToken==2");
			accessToken=WeixinUtil.getAccessToken(config.getAppid(), config.getAppsecret());
			instance.setContent(accessToken.getToken());
			instance.setUpdatetime(new Date(System.currentTimeMillis()));
			instance.setCreatetime(new Date(System.currentTimeMillis()));
			kmWxInfoDAO.attachDirty(instance);
		}
		accessToken =new AccessToken();
		accessToken.setToken(instance.getContent());
		accessToken.setExpiresIn((int)((System.currentTimeMillis() - instance.getUpdatetime().getTime())/1000));
		shardedJedis.hset(WX_ACCESSTOKEN, "Token", instance.getContent());
		shardedJedis.hset(WX_ACCESSTOKEN, "updatetime", String.valueOf(instance.getUpdatetime().getTime()));
		shardedJedisPool.returnResource(shardedJedis);
		return accessToken;
//		System.err.println("accessToken==2");
//		accessToken=WeixinUtil.getAccessToken(config.getAppid(), config.getAppsecret());
//		if(tokens != null && tokens.size() > 0){
//			instance = tokens.get(0);
//		}else {
//			instance.setCreatetime(new Date(System.currentTimeMillis()));
//		}
//		instance.setContent(accessToken.getToken());
//		instance.setUpdatetime(new Date(System.currentTimeMillis()));
//		kmWxInfoDAO.save(instance);
//		System.out.println(accessToken);
//		return accessToken;
	}
	
	@Override
	public JsapiTicket getJSApiTicket() {
		
		JsapiTicket jsticket = null;
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		if(shardedJedis.hexists(WX_JSAPITICKET, "Ticket") && shardedJedis.hexists(WX_JSAPITICKET, "updatetime")){
			String ticket = shardedJedis.hget(WX_JSAPITICKET, "Ticket");
			long updatetime = Long.parseLong(shardedJedis.hget(WX_JSAPITICKET, "updatetime"));
			if((System.currentTimeMillis() - updatetime) < 7000 * 1000){
				System.out.println("--getJSApiTicket-shardedJedis--");
				jsticket =new JsapiTicket();
				jsticket.setTicket(ticket);
				jsticket.setExpiresIn((int)((System.currentTimeMillis() - updatetime)/1000));
				shardedJedisPool.returnResource(shardedJedis);
				return jsticket;
			}
		}
		
		KmWxInfo instance = new KmWxInfo();
		instance.setAppid(config.getAppid());
		instance.setType(2);
		List<KmWxInfo> tickets = kmWxInfoDAO.findByExample(instance);
		if(tickets != null && tickets.size() > 0){
			System.err.println("JsapiTicket==1");
			instance = tickets.get(0);
			if((System.currentTimeMillis() - tickets.get(0).getUpdatetime().getTime()) < 7000 * 1000){
//				jsticket =new JsapiTicket();
//				jsticket.setTicket(tickets.get(0).getContent());
//				jsticket.setExpiresIn((int)((System.currentTimeMillis() - tickets.get(0).getUpdatetime().getTime())/1000));
//				return jsticket;
			}else {
				System.err.println("JsapiTicket==2");
				jsticket=WeixinUtil.getJsApiTicket(this.getAccessToken().getToken());
				instance.setContent(jsticket.getTicket());
				instance.setUpdatetime(new Date(System.currentTimeMillis()));
				kmWxInfoDAO.attachDirty(instance);
			}
		} else {
			System.err.println("JsapiTicket==3");
			jsticket = WeixinUtil.getJsApiTicket(this.getAccessToken().getToken());
			instance.setContent(jsticket.getTicket());
			instance.setUpdatetime(new Date(System.currentTimeMillis()));
			instance.setCreatetime(new Date(System.currentTimeMillis()));
			kmWxInfoDAO.attachDirty(instance);
		}
		jsticket =new JsapiTicket();
		jsticket.setTicket(instance.getContent());
		jsticket.setExpiresIn((int)((System.currentTimeMillis() - instance.getUpdatetime().getTime())/1000));
		shardedJedis.hset(WX_JSAPITICKET, "Ticket", instance.getContent());
		shardedJedis.hset(WX_JSAPITICKET, "updatetime", String.valueOf(instance.getUpdatetime().getTime()));
		shardedJedisPool.returnResource(shardedJedis);
		return jsticket;
//		System.err.println("JsapiTicket==2");
//		jsticket=WeixinUtil.getJsApiTicket(this.getAccessToken().getToken());
//		if(tickets != null && tickets.size() > 0){
//			instance = tickets.get(0);
//			instance.setCreatetime(tickets.get(0).getCreatetime());
//		}else {
//			instance.setCreatetime(new Date(System.currentTimeMillis()));
//		}
//		instance.setContent(jsticket.getTicket());
//		instance.setUpdatetime(new Date(System.currentTimeMillis()));
//		kmWxInfoDAO.attachDirty(instance);
//		return jsticket;
	}

}
