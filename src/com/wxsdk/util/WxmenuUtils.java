package com.wxsdk.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wxsdk.model.menu.Button;
import com.wxsdk.model.menu.CommonButton;
import com.wxsdk.model.menu.ComplexButton;
import com.wxsdk.model.menu.ViewButton;
import com.wxsdk.model.menu.WxMenu;



/**
 *  菜单管理器类
 */
public class WxmenuUtils {
	private static String urlPrefix = "http://m.51bi.com/";
	private static Logger log = LoggerFactory.getLogger(WxmenuUtils.class);
	public static void main(String[] args) throws UnsupportedEncodingException {
		deleteMenu(1);//HKS
		doCreateMenuHks();//HKS
	}
	
	private static void doCreateMenuHks() throws UnsupportedEncodingException{
		// 第三方用户唯一凭证
		String appId = "appId";
		// 第三方用户唯一凭证密钥
		String appSecret = "appSecret";

		// 调用接口获取access_token
		String token = getAccessToken(appId, appSecret).getString("access_token");

//		// 网络请求服务器token -- 防止本地请求导致token失效
//		String res = Util.getHTMLByUrl(urlPrefix + "yaoyaole/getToken.do?type=1000", "utf-8");
//		String token = net.sf.json.JSONObject.fromObject(res).getJSONObject("content").getString("access_token");
//		System.out.println(token);
		if (null != token) {
			// 删除菜单
			int ret = WeixinUtil.deleteMenu(token);
			if(ret == 0){
				// 调用接口创建菜单
				int result = WeixinUtil.createMenu(createMenuHks(), token);
		
				// 判断菜单创建结果
				if (0 == result)
					log.info("菜单创建成功！");
				else
					log.info("菜单创建失败，错误码：" + result);
			}
		} else {
			log.info("菜单创建失败：token获取失败");
		}
	}
	private static void doCreateMenuYyl() throws UnsupportedEncodingException{
//		// 第三方用户唯一凭证
//		String appId = "wx63bbf298061ea9a3";
//		// 第三方用户唯一凭证密钥
//		String appSecret = "3895dc80569a5bd6715dee5cb7884679";
//
//		// 调用接口获取access_token
//		String token = getAccessToken(appId, appSecret).getString("access_token");

		
		// 网络请求服务器token -- 防止本地请求导致token失效
		String res = Util.getHTMLByUrl(urlPrefix + "yaoyaole/getToken.do?type=2000", "utf-8");
		String token = net.sf.json.JSONObject.fromObject(res).getJSONObject("content").getString("access_token");
		System.out.println(token);
		if (null != token) {
			// 删除菜单
			int ret = WeixinUtil.deleteMenu(token);
			if(ret == 0){
				// 调用接口创建菜单
				int result = WeixinUtil.createMenu(createMenuYyl(), token);

				// 判断菜单创建结果
				if (0 == result)
					log.info("菜单创建成功！");
				else
					log.info("菜单创建失败，错误码：" + result);
			}
			
		} else {
			log.info("菜单创建失败：token获取失败");
		}
	}
	/**
	 * 组装菜单数据
	 * HKS
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private static WxMenu createMenuHks() throws UnsupportedEncodingException{
		String appid="wxe9341dcc8419a785";
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_userinfo&state=1000#wechat_redirect";
	/****************************正式服务菜单***************************************************************/
	String url2="";
		/*
		 * 菜单一
		 */
		CommonButton btn11 = new CommonButton();
		btn11.setName("活动一");
		btn11.setType("click");
		btn11.setKey("1011");
		
//		CommonButton btn12 = new CommonButton();
//		btn12.setName("活动二");
//		btn12.setType("click");
//		btn12.setKey("1012");
		
		ComplexButton mainBtn1 = new ComplexButton();
		mainBtn1.setName("今日通告");
		mainBtn1.setSub_button(new Button[] { btn11});
//		mainBtn1.setSub_button(new Button[] { btn11, btn12});
		/*
		 * 菜单二
		 */
		ViewButton btn21 = new ViewButton();
		btn21.setName("我的二维码");
		btn21.setType("view");
		url2= urlPrefix+"myqrcode/index.html";
		btn21.setUrl(url.replace("REDIRECT_URI", URLEncoder.encode(url2, "utf-8")));
		
		ViewButton btn22 = new ViewButton();
		btn22.setName("发红包");
		btn22.setType("view");
		url2= urlPrefix+"fahongbao/index.html";
		btn22.setUrl(url.replace("REDIRECT_URI", URLEncoder.encode(url2, "utf-8")));
		
		ViewButton btn23 = new ViewButton();
		btn23.setName("怎么赚钱");
		btn23.setType("view");
		url2= urlPrefix + "faq/index.html?stateshow=1";
//		btn23.setUrl(url.replace("REDIRECT_URI", url2));
		btn23.setUrl(url2);
		
		ComplexButton mainBtn2 = new ComplexButton();
		mainBtn2.setName("我要赚钱");
		mainBtn2.setSub_button(new Button[] { btn21, btn22, btn23});
		
		/*
		 * 菜单三
		 */
		ViewButton btn31 = new ViewButton();
		btn31.setName("账户");
		url2= urlPrefix+"my/index.html";
		btn31.setType("view");
		btn31.setUrl(url.replace("REDIRECT_URI", URLEncoder.encode(url2, "utf-8")));
		
		ViewButton btn32 = new ViewButton();
		btn32.setName("我的粉丝");
		url2= urlPrefix+"zhaopengyou/index.html#myFensi";
		btn32.setType("view");
		btn32.setUrl(url.replace("REDIRECT_URI", URLEncoder.encode(url2, "utf-8")));
		
		ViewButton btn33 = new ViewButton();
		btn33.setName("查看收益");
		btn33.setType("view");
		url2= urlPrefix+"shouyi/index.html";
		btn33.setUrl(url.replace("REDIRECT_URI", URLEncoder.encode(url2, "utf-8")));
		
		ViewButton btn34 = new ViewButton();
		btn34.setName("关于HKS");
		btn34.setType("view");
		url2 = urlPrefix + "about/index.html";
		btn34.setUrl(url2);
		
		ComplexButton mainBtn3 = new ComplexButton();
		mainBtn3.setName("我的");
		mainBtn3.setSub_button(new Button[] { btn31, btn32, btn33, btn34});
		/**
		 * 这是公众号xiaoqrobot目前的菜单结构，每个一级菜单都有二级菜单项<br>
		 * 
		 * 在某个一级菜单下没有二级菜单的情况，menu该如何定义呢？<br>
		 * 比如，第三个一级菜单项不是“更多体验”，而直接是“幽默笑话”，那么menu应该这样定义：<br>
		 * menu.setButton(new Button[] { mainBtn1, mainBtn2, btn33 });
		 */
		WxMenu menu = new WxMenu();
		menu.setButton(new Button[] { mainBtn1, mainBtn2, mainBtn3 });

		return menu;
	}
	/**
	 * 组装菜单数据
	 * 粉丝端
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private static WxMenu createMenuYyl() throws UnsupportedEncodingException{
	/****************************正式服务菜单***************************************************************/
		String appid="wx63bbf298061ea9a3";
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_userinfo&state=2000#wechat_redirect";
		String url2="";
		/*
		 * 菜单一
		 */
		
		ViewButton btn12 = new ViewButton();
		btn12.setName("抢红包");
		url2= urlPrefix+"/pengyouquan/index.html";
		btn12.setType("view");
		btn12.setUrl(url.replace("REDIRECT_URI", URLEncoder.encode(url2, "utf-8")));
		
		ViewButton btn13 = new ViewButton();
		btn13.setName("花余额");
		btn13.setType("view"); 
		url2= urlPrefix+"yaoyaole/dist/html/taobao/index.html";
		btn13.setUrl(url.replace("REDIRECT_URI", URLEncoder.encode(url2, "utf-8")));
		
		
		ComplexButton mainBtn1 = new ComplexButton();
		mainBtn1.setName("抢红包");
		mainBtn1.setSub_button(new Button[] {btn12, btn13});
		/*
		 * 菜单二 摇一摇
		 */
		ViewButton mainBtn2 = new ViewButton();
		mainBtn2.setName("摇一摇");
		mainBtn2.setType("view");
		url2= urlPrefix+"/index.html";
		mainBtn2.setUrl(url.replace("REDIRECT_URI", URLEncoder.encode(url2, "utf-8")));
	
		/*
		 * 菜单三
		 */
		// 账户
		ViewButton btn31 = new ViewButton();
		btn31.setName("账户");
		btn31.setType("view");
		url2= urlPrefix+"yaoyaole/dist/html/my/index.html";
		btn31.setUrl(url.replace("REDIRECT_URI", URLEncoder.encode(url2, "utf-8")));
		// z
		ViewButton btn32 = new ViewButton();
		btn32.setName("找朋友");
		btn32.setType("view");
		url2= urlPrefix+"yaoyaole/dist/html/zhaopengyou/index.html";
		btn32.setUrl(url.replace("REDIRECT_URI", URLEncoder.encode(url2, "utf-8")));
		
//		ViewButton btn33 = new ViewButton();
//		btn33.setName("怎么玩？");
//		btn33.setType("view");
//		url2= urlPrefix + "yaoyaole/dist/html/faq/index.html?stateshow=2";
//		btn33.setUrl(url2);
		
		ViewButton btn33 = new ViewButton();
		btn33.setName("怎么玩");
		btn33.setType("view");
		url2= urlPrefix + "yaoyaole/dist/html/faq2/index.html";
		btn33.setUrl(url2);
		
		ComplexButton mainBtn3 = new ComplexButton();
		mainBtn3.setName("我的");
		mainBtn3.setSub_button(new Button[] { btn31, btn32, btn33});
			/**
			 * 这是公众号xiaoqrobot目前的菜单结构，每个一级菜单都有二级菜单项<br>
			 * 
			 * 在某个一级菜单下没有二级菜单的情况，menu该如何定义呢？<br>
			 * 比如，第三个一级菜单项不是“更多体验”，而直接是“幽默笑话”，那么menu应该这样定义：<br>
			 * menu.setButton(new Button[] { mainBtn1, mainBtn2, btn33 });
			 */
			WxMenu menu = new WxMenu();
			menu.setButton(new Button[] { mainBtn1, mainBtn2, mainBtn3 });

			return menu;
	}
	
	public static JSONObject getAccessToken(String appid, String appsecret) {
		// 获取access_token的接口地址（GET） 限200（次/天）
		String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
		String requestUrl = ACCESS_TOKEN_URL.replace("APPID", appid).replace(
				"APPSECRET", appsecret);
		JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl, "GET", null);
		// 如果请求成功
		if (null != jsonObject) {
//			try {
//				System.out.println("获取access_token:限制2000次");
//				accessToken = new AccessToken();
//				accessToken.setToken(jsonObject.getString("access_token"));
//				accessToken.setExpiresIn(jsonObject.getInt("expires_in"));
//			} catch (Exception e) {
//				accessToken = null;
//				// 获取token失败
//				logger.error("获取token失败 errcode:{} errmsg:{}",
//						jsonObject.getInt("errcode"),
//						jsonObject.getString("errmsg"));
//			}
		}
		return jsonObject;
	}
	public static void deleteMenu(int type){
		if(type==1){ //HKS
			// 第三方用户唯一凭证
			String appId = "appId";//test
			// 第三方用户唯一凭证密钥
			String appSecret = "secret";
			
			String token = getAccessToken(appId, appSecret).getString("access_token");
			WeixinUtil.deleteMenu(token);
		}else{// 摇摇乐
			// 第三方用户唯一凭证
			String appId = "wxappId";
			// 第三方用户唯一凭证密钥
			String appSecret = "appSecret";
			String token = getAccessToken(appId, appSecret).getString("access_token");
			WeixinUtil.deleteMenu(token);
		}
		
	}
}
