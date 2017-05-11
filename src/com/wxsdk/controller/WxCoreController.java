package com.wxsdk.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.wxsdk.model.WxConfig;
import com.wxsdk.service.IWxCoreService;
import com.wxsdk.util.Sign;
import com.wxsdk.util.SignUtil;
import com.wxsdk.util.WeixinUtil;


/**
 * 微信Core Servlet
 * @author Keaven
 *
 */
@Controller
@RequestMapping(value = "/core")
public class WxCoreController {
	private static Logger logger = LoggerFactory
			.getLogger(WxCoreController.class);
	@Autowired
	private WxConfig config;
	
	@Resource(name="CoreServiceImpl")
	private IWxCoreService coreServiceImpl;
	
	// 接收微信公众号接收的消息，处理后再做相应的回复
	@RequestMapping(value = "/core.wx", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public void replyMesage(HttpServletRequest request,
			HttpServletResponse response) {
		PrintWriter out = null;
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			logger.info("----------weixin/core.do----replyMesage--------");
	
			logger.info("====doPost-----");
			logger.info("------------------start------------------");
			// 调用核心业务类接收消息、处理消息
			String respMessage = coreServiceImpl.processRequest(request);
	
			// 响应消息
			out.print(respMessage);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.close();
		out = null;
		
	}

	// 微信公众平台验证url是否有效使用的接口
	@RequestMapping(value = "/core.wx", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public void weixinCore(HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println(request);
		PrintWriter out = null;
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			String signature = request.getParameter("signature");
			String timestamp = request.getParameter("timestamp");
			String nonce = request.getParameter("nonce");
			String echostr = request.getParameter("echostr");
			logger.info("----------weixin/core.do----weixinCore--------");
			logger.info("--"+SignUtil.checkSignature(signature, timestamp, nonce));
			if (SignUtil.checkSignature(signature, timestamp, nonce)) {
				logger.info("token-success");
				out.print(echostr);
			} else {
				out.print("error");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.close();
		out = null;
	}

	@RequestMapping(value = "/access.wx")
	public void getUserInfo(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String type = request.getParameter("type");
		PrintWriter out = response.getWriter();
		String code = request.getParameter("code");
		logger.info("type:" + type + ",code:" + code);
		if ("ACCESS".equals(type.toUpperCase())) {
			logger.info("1111111");
			JSONObject token_info = WeixinUtil.getAccessTokenByCode(
					config.getAppid(), config.getAppsecret(), code);
			out.print(token_info);
		} else if ("USERINFO".equals(type.toUpperCase())) {
			JSONObject token_info = WeixinUtil.getAccessTokenByCode(
					config.getAppid(), config.getAppsecret(), code);
			logger.info("access_token:" + token_info);
			logger.info("222222" + config + token_info);
			String access_token = (String) token_info.get("access_token");
			String refresh_token = (String) token_info.get("refresh_token");
			String openid = (String) token_info.get("openid");
			JSONObject userinfo = WeixinUtil.getUserInfoByToken(access_token,
					openid);
			String ref_token = WeixinUtil.getRefreshToken(config.getAppid(),
					refresh_token);
			logger.info("第三步-refresh_token：" + ref_token);
			logger.info("userinfo:" + userinfo);
			out.print(userinfo.toString());
		} else if ("OPENID".equals(type.toUpperCase())) {

			logger.info("333333");
			JSONObject token_info = WeixinUtil.getAccessTokenByCode(
					config.getAppid(), config.getAppsecret(), code);
			logger.info("access_token---openid:"
					+ token_info.getString("openid"));
//			out.print(userService.getUserStatus(token_info.getString("openid")));
		}
		out.close();
		out = null;
	}
	
//	@RequestMapping(value = "/wxconfig.wx")
//	public void wxconfig(HttpServletRequest request,
//			HttpServletResponse response) throws IOException {
//		logger.info("---wxconfig.do---");
//		request.setCharacterEncoding("UTF-8");
//		response.setCharacterEncoding("UTF-8");
//		String urlString = request.getParameter("url");
//		logger.info("url:" + urlString);
//		// 调用核心业务类接收消息、处理消息
//		Map<String, String> ret = Sign.sign(coreServiceImpl.get, urlString);
//		ret.put("appId", config.getAppid());
//		String respMessage = JSONObject.fromObject(ret).toString();
//		logger.info("wxconfig_ticket:"+respMessage);
//		// 响应消息
//		PrintWriter out = response.getWriter();
//		out.print(respMessage);
//		out.close();
//		out = null;
//	}
	/**
	 * 页面js-sdk 参数
	 * @return
	 */
	@RequestMapping(value = "/wxconfig.wx")
	public void getJsConfig(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String urlString = request.getParameter("url");
		String stateStr = request.getParameter("state");
		logger.info("url:" + urlString);
		
		String appid = config.getAppid();
		String appsecret = config.getAppsecret();
		Map<String, String> ret = coreServiceImpl.getJsConfig(urlString, appid, appsecret);
		// 调用核心业务类接收消息、处理消息
		String respMessage = JSONObject.fromObject(ret).toString();
		request.setAttribute("result", respMessage);
	}
}
	
