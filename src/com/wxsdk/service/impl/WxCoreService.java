package com.wxsdk.service.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.wxsdk.model.AccessToken;
import com.wxsdk.model.JsapiTicket;
import com.wxsdk.model.WxConfig;
import com.wxsdk.model.response.TextMessage;
import com.wxsdk.service.IWxCoreService;
import com.wxsdk.util.MessageUtils;
import com.wxsdk.util.Sign;
import com.wxsdk.util.WeixinUtil;


public class WxCoreService implements IWxCoreService {
	private static final Logger logger = Logger.getLogger(WxCoreService.class);
	private WxConfig wxConfig;

	/**
	 * 处理微信发来的请求
	 * 
	 * @param request
	 * @return
	 */
	public String processRequest(HttpServletRequest request) {

		String respMessage = null;
		try {
			// 默认返回的文本消息内容
			String respContent = "欢迎关注";

			// xml请求解析
			Map<String, String> requestMap = MessageUtils.doparseXml(request); //doparseXml(request);
			
			// 发送方帐号（open_id）
			String fromUserName = requestMap.get("FromUserName");
			// 公众帐号
			String toUserName = requestMap.get("ToUserName");
			// 消息类型
			String msgType = requestMap.get("MsgType");
			// 消息内容
			String msgContent = requestMap.get("Content");
			// 回复文本消息
			TextMessage textMessage = new TextMessage();
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(new Date().getTime());
			textMessage.setMsgType(MessageUtils.RESP_MESSAGE_TYPE_TEXT);
			textMessage.setFuncFlag(0);

			// 文本消息
			if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_TEXT)) {
				textMessage.setContent(respContent);
				respMessage = MessageUtils.textMessageToXml(textMessage);
			}
			// 图片消息
			else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_IMAGE)) {
				textMessage.setContent(respContent);
				respMessage = MessageUtils.textMessageToXml(textMessage);
			}
			// 地理位置消息
			else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_LOCATION)) {
				textMessage.setContent(respContent);
				respMessage = MessageUtils.textMessageToXml(textMessage);
			}
			// 链接消息
			else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_LINK)) {
				textMessage.setContent(respContent);
				respMessage = MessageUtils.textMessageToXml(textMessage);
			}
			// 音频消息
			else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_VOICE)) {
				textMessage.setContent(respContent);
				respMessage = MessageUtils.textMessageToXml(textMessage);
			}
			// 事件推送
			else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_EVENT)) {
				// 事件类型
				String eventType = requestMap.get("Event");
				String openid = requestMap.get("FromUserName");
				String sceneid = requestMap.get("EventKey");
				// 订阅
				if (eventType.equals(MessageUtils.EVENT_TYPE_SUBSCRIBE)) {
//					
				} else if (eventType.equals(MessageUtils.EVENT_TYPE_SCAN)) {// 扫描进入粉丝号
					respContent = "";
					textMessage.setContent(respContent);
					respMessage = MessageUtils.textMessageToXml(textMessage);
				}
				// 取消订阅
				else if (eventType.equals(MessageUtils.EVENT_TYPE_UNSUBSCRIBE)) {
//					textMessage.setContent("取消关注");
//					respMessage = MessageUtil.textMessageToXml(textMessage);
				}
				// 自定义菜单点击事件
				else if (eventType.equals(MessageUtils.EVENT_TYPE_CLICK)) {
					// TODO 自定义菜单权没有开放，暂不处理该类消息
					String eventKey = requestMap.get("EventKey");
					int key = Integer.parseInt(eventKey);
					switch (key) {
					case 1011:
						textMessage.setContent(respContent);
						respMessage = MessageUtils.textMessageToXml(textMessage);
						break;
					case 1012:
						textMessage.setContent(respContent);
						respMessage = MessageUtils.textMessageToXml(textMessage);
						break;
					default:
						break;
					}
				}
			}
//			textMessage.setContent(respContent);
//			respMessage = MessageUtil.textMessageToXml(textMessage);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.toString());
		}
//		logger.info("---返回消息---start");
		logger.info(respMessage);
//		
//		logger.info("---返回消息---end~");
		return respMessage;
	
	}


//	/**
//	 * xiaoqrobot的主菜单
//	 * 
//	 * @return
//	 */
//	public String getMainMenu_(int type) {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append(
//				"您好，亲爱的家长，欢迎" + (type == 1 ? "关注" : "使用")
//						+ "“苗苗”!\n您可以点击菜单选择服务，或者回复数字选择服务：").append("\n\n");
//		buffer.append("1  家长端APP下载").append("\n");
//		buffer.append("2 儿童端APP下载").append("\n");
//		buffer.append("3 <a href=\"http://www.kidmate.cn/help.html\">服务与帮助</a>")
//				.append("\n");
//		// buffer.append("4 更多").append("\n");
//		buffer.append("\n");
//		buffer.append("回复“?”显示此帮助菜单");
//		return buffer.toString();
//	}

	/**
	 * emoji表情转换(hex -> utf-16)
	 * 
	 * @param hexEmoji
	 * @return
	 */
	public String emoji(int hexEmoji) {
		return String.valueOf(Character.toChars(hexEmoji));
	}

	/**
	 * 判断字符是否为数字
	 * 
	 * @author san
	 * @param str
	 * @return boolean
	 */
	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 获取持久二维码
	 * 
	 * @param sceneId
	 * @return
	 */
	private JSONObject getPersistentQrcode(String sceneId) {
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, Object> action_info = new HashMap<String, Object>();
		Map<String, Object> scene = new HashMap<String, Object>();
		// {"action_name": "QR_LIMIT_STR_SCENE", "action_info": {"scene":
		// {"scene_str": "123"}}}
		param.put("action_name", "QR_LIMIT_STR_SCENE");
		scene.put("scene_str", sceneId);
		action_info.put("scene", scene);
		param.put("action_info", action_info);
		JSONObject ticket = null;
		// JSONObject ticket =
		// getTicket(JSONObject.fromObject(param).toString(),
		// wxConfigService.getAccessToken().getToken());
		return ticket;
	}

	/**
	 * 判断用户是否关注公众号
	 * @author mawq
	 * @param openid
	 * @param token
	 * @return
	 */
	public boolean judgeIsSubscribe(String openid,String token){
		JSONObject userInfo = WeixinUtil.getUserInfoByOpenId(openid, token);
		if(userInfo.containsKey("subscribe")){ // subscribe 为1 的时候关注；0表示未关注
			return userInfo.getInt("subscribe") == 1;
		}
		return false;
	}
	

	public Map<String, String> getJsConfig(String urlString, String appid,String appsecret) {
		// TODO Auto-generated method stub
		JsapiTicket ticket = WeixinUtil.getJsApiTicket(appid, appsecret);
		Map<String, String> config = new HashMap<String, String>();
		config = Sign.sign(ticket.getTicket(), urlString);
		config.put("appId", appid);
		config.put("result", "success");
		return config;
	}
	
	@Override
	public JSONObject getOpenidByCode(String code,String appid,String appsecret) {
		JSONObject token_info = WeixinUtil.getAccessTokenByCode(appid,
				appsecret, code);
		if (token_info.containsKey("errcode")) { // 如果包含errcode，说明该接口调用失败
			logger.info(token_info.toString());
			return token_info;
		}
		return token_info;
	}


	@Override
	public AccessToken getCacheToken(String appid, String appsecret) {
		AccessToken token = WeixinUtil.getAccessToken(appid, appsecret);
		return token;
	}
}
