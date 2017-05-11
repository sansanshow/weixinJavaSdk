package com.wxsdk.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletContext;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.wxsdk.model.AccessToken;
import com.wxsdk.model.JsapiTicket;
import com.wxsdk.model.menu.WxMenu;

/**
 * 公众平台通用接口工具类
 * 
 * @author liuyq
 * @date 2013-08-09
 */
public class WeixinUtil {
	private static final Logger logger = Logger.getLogger(WeixinUtil.class);
	private static String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";//新增临时素材
	private static String UPLOAD_URL_FOREVER = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN";//新增永久素材
	// 带渠道的二维码信息url
	private static String QR_CODE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN";
	/**
	 * 发起https请求并获取结果
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @param requestMethod
	 *            请求方式（GET、POST）
	 * @param outputStr
	 *            提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpRequest(String requestUrl,
			String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
					.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// 当有数据需要提交时
			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			jsonObject = JSONObject.fromObject(buffer.toString());
		} catch (ConnectException ce) {
			logger.error("Weixin server connection timed out.");
		} catch (Exception e) {
			logger.error("https request error:{}", e);
		}
		return jsonObject;
	}

	// 获取access_token的接口地址（GET） 限200（次/天）
	public final static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	// 获取access_token的接口地址（GET） 限200（次/天）
	public final static String JSAPI_TICKET_URL= "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";

	public static JSONObject getToken(String appid, String appsecret) {

		String requestUrl = ACCESS_TOKEN_URL.replace("APPID", appid).replace(
				"APPSECRET", appsecret);
		JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
		// 如果请求成功
		if (null != jsonObject && !jsonObject.containsKey("errcode")) {
			return jsonObject ;
		}
		return jsonObject;
	}
	
	// By Openid 获取 userInfo URL
	private static String USERINFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN"; // 用户管理
	/**
	 * 用户管理
	 * 公众号通过openid，token获取用户信息
	 * @param openid
	 * @param token 公众平台token
	 * @return
	 */
	public static JSONObject getUserInfoByOpenId(String openid,String token){
		String url = USERINFO_URL.replace("ACCESS_TOKEN", token).replace("OPENID", openid);
		JSONObject jsonObject = WeixinUtil.httpRequest(url, "GET", null);
		if (null != jsonObject) {
			if(!jsonObject.containsKey("errcode")){
				return jsonObject;
			}
		}
		return jsonObject;
	}
	
	private static String USERINFO_URL_HTML = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN"; //网页
	/**
	 * 网页授权拉取用户
	 * @param openid
	 * @param token : 网页授权code拉取的用户token
	 * @return
	 */
	public static JSONObject getUserInfoByOpenIdWeb(String openid,String token){
		String url = USERINFO_URL_HTML.replace("ACCESS_TOKEN", token).replace("OPENID", openid);
		JSONObject jsonObject = WeixinUtil.httpRequest(url, "GET", null);
		if (null != jsonObject) {
			if(!jsonObject.containsKey("errcode")){
				return jsonObject;
			}
		}
		return jsonObject;
	}
	//http请求方式: GET
	//https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
	//获取用户个人信息（UnionID机制）
	// openid,nickname,sex,headimgurl,unionid
	/**
	 * 公众平台用户管理
	 * 获取 userInfo By Openid 
	 * @param openid
	 * @param token : 公众平台的的token
	 * @return
	 */
	public static JSONObject getUserInfoByOpenIdApp(String openid,String token){
		String USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
		String url = USERINFO_URL.replace("ACCESS_TOKEN", token).replace("OPENID", openid);
		JSONObject jsonObject = WeixinUtil.httpRequest(url, "GET", null);
		if (null != jsonObject) {
			if(!jsonObject.containsKey("errcode")){
				return jsonObject;
			}
		}
		return jsonObject;
	}
	
	/**
	 * 检验token是否有效
	 * @param openid
	 * @param token
	 * @return
	 */
	public static boolean checkToken(String openid,String token){
		String USERINFO_URL = "https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid=OPENID";
		String url = USERINFO_URL.replace("ACCESS_TOKEN", token).replace("OPENID", openid);
		JSONObject jsonObject = WeixinUtil.httpRequest(url, "GET", null);
		if (null != jsonObject&& jsonObject.getInt("errcode") ==0) {
			return true;
		}
		return false;
	}
	
	//校验 token是否有效
	public static boolean checkAccessToken(AccessToken token){
		if(token==null){
			return true;
		}else{
			Long nowtime=new Date().getTime();
			if((nowtime.longValue()-token.getCreatetime().longValue()) > token.getExpires_in()){  //超时
				return true;
			}
		}
		return false;
	}
	//校验 ticket 是否有效
	public static boolean checkJsTicket(JsapiTicket ticket){
		if(ticket==null){
			return true;
		}else{
			Long nowtime=new Date().getTime();
			if((nowtime.longValue()-ticket.getCreatetime().longValue()) > ticket.getExpires_in()){  //超时
				return true;
			}
		}
		return false;
	}
	
	// 菜单创建（POST） 限100（次/天）
	public static String menu_create_url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	// 菜单删除（POST） 限100（次/天）
	public static String menu_delete_url = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";

	/**
	 * 创建菜单
	 * 
	 * @param menu
	 *            菜单实例
	 * @param accessToken
	 *            有效的access_token
	 * @return 0表示成功，其他值表示失败
	 */
	public static int createMenu(WxMenu menu, String accessToken) {
		int result = 0;
		// 拼装创建菜单的url
		String url = menu_create_url.replace("ACCESS_TOKEN", accessToken);
		// 将菜单对象转换成json字符串
		String jsonMenu = JSONObject.fromObject(menu).toString();
		// 调用接口创建菜单
		JSONObject jsonObject = httpRequest(url, "POST", jsonMenu);
		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				logger.info("创建菜单失败 errcode:"+jsonObject.getInt("errcode")+" errmsg:"+jsonObject.getString("errmsg"));
			}
		}

		return result;
	}

	/**
	 * 删除菜单
	 * 
	 * @param menu
	 *            菜单实例
	 * @param accessToken
	 *            有效的access_token
	 * @return 0表示成功，其他值表示失败
	 */
	public static int deleteMenu(String accessToken) {
		int result = 0;
		// 拼装创建菜单的url
		String url = menu_delete_url.replace("ACCESS_TOKEN", accessToken);
		// 调用接口创建菜单
		JSONObject jsonObject = httpRequest(url, "POST", null);

		if (null != jsonObject) {
			if (0 != jsonObject.getInt("errcode")) {
				result = jsonObject.getInt("errcode");
				logger.error("删除菜单失败 errcode:"+jsonObject.getInt("errcode")+" errmsg:"+jsonObject.getString("errmsg"));
			}else {
				logger.error("删除菜单ok");
			}
		}

		return result;
	}

	/**
	 * 调用微信JS接口的临时票据
	 * 
	 * @param access_token
	 *            接口访问凭证
	 * @return
	 */
	public static String getJsApiTicketString(String access_token) {
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
		String requestUrl = url.replace("ACCESS_TOKEN", access_token);
		// 发起GET请求获取凭证
		JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
		String ticket = null;
		if (null != jsonObject) {
			try {
				ticket = jsonObject.getString("ticket");
			} catch (JSONException e) {
				// 获取token失败
				logger.error("获取token失败 errcode:"+jsonObject.getInt("errcode")+" errmsg:"+jsonObject.getString("errmsg"));
			}
		}
		return ticket;
	}

	/**
	 * 获取js_ticket
	 * @param access_token
	 * @return
	 */
	public static JSONObject getJsApiTicket(String access_token) {

		String requestUrl = JSAPI_TICKET_URL.replace("ACCESS_TOKEN",
				access_token);
		JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
		// 如果请求成功
		if (null != jsonObject) {
			try {
				jsonObject.getString("ticket");
			} catch (Exception e) {
				// 获取token失败
				logger.error("获取token失败 errcode:"+jsonObject.getInt("errcode")+" errmsg:"+jsonObject.getString("errmsg"));
			}
		}
		return jsonObject;
	}
	/**
	 * 获取通信 token -- 缓存机制
	 * @return
	 */
	public static AccessToken getAccessToken(String appid,String appsecret){ 
		AccessToken token=null;
			//token=new Token("ZwSKl32BCCUNWcyAlLQjeTJ5Y2POJXaJrQpz4_J23skF5KA1NH_CEqnsDmrca6cj8eYNxfHjt6fp8V2yDy46xqZBwuqlmVMMtH50IyD8pfguhCvs6re0eZPIhJ2lZqJzZ38nWNohRaQKt4QXDzY5-A", "72000");
		String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+appsecret;
		JSONObject json=WeixinUtil.httpRequest(url, "GET", null);
		JSONObject jobj=JSONObject.fromObject(json);
		token=(AccessToken)JSONObject.toBean(jobj, AccessToken.class);
        return token;
    }
	/**
	 * 获取通信 Tikect -- 缓存机制
	 * @return
	 */
	public static JsapiTicket getJsApiTicket(String appid,String appsecret){ 
		JsapiTicket ticket = null;
		JSONObject token = getToken(appid, appsecret);
		JSONObject json = WeixinUtil.getJsApiTicket(token.getString("access_token"));
		JSONObject jobj = JSONObject.fromObject(json);
		ticket = (JsapiTicket)JSONObject.toBean(jobj, JsapiTicket.class);
        return ticket;
    }

	public static String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	public static String  xcx_url= "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=CODE&grant_type=authorization_code";

	public static JSONObject getAccessTokenByCode(String appid,
			String appsecret, String code) {
		String requestUrl = url.replace("APPID", appid)
				.replace("SECRET", appsecret).replace("CODE", code)+"&r="+ (int)100000 * Math.random();
		JSONObject jsonObject = httpRequest(requestUrl, "POST", null);
		return jsonObject;
	}
	

	/**
	 * 
	 * @author san
	 * @param appid
	 * @param appsecret
	 * @param code
	 * @return JSONObject
	 */
	public static String getOpenidByCode(String appid, String appsecret,
			String code) {
		String requestUrl = url.replace("APPID", appid)
				.replace("SECRET", appsecret).replace("CODE", code);
		JSONObject jsonObject = httpRequest(requestUrl, "POST", null);
		return jsonObject.getString("openid");
	}

	public static JSONObject getUserInfoByToken(String token, String openid) {
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
		String requestUrl = url.replace("ACCESS_TOKEN", token).replace(
				"OPENID", openid);
		JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
		return jsonObject;
	}

	
	public static String getRefreshToken(String appid, String token) {
		String r_url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
		String requestUrl = r_url.replace("APPID", appid).replace(
				"REFRESH_TOKEN", token);
		JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
		return jsonObject.toString();
	}

	/**
	 * 上传媒体文件
	 * @author san
	 */
	public static String upload(String filePath,String accessToken,String type) throws IOException,NoSuchAlgorithmException{
		File file =new File(filePath);
		if(!file.exists()||!file.isFile()){
			throw new IOException("文件不存在");
		}
		
		String url=UPLOAD_URL.replace("ACCESS_TOKEN",accessToken).replace("TYPE", type);
		URL urlObj=new URL(url);
		//链接
		HttpURLConnection con=(HttpURLConnection) urlObj.openConnection();
		con.setRequestMethod("POST");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		
		//设置请求头信息
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");
		//设置边界
		String BOUNDARY="----------"+System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");

		byte[] head = sb.toString().getBytes("utf-8");

		//获得输出流
		OutputStream out = new DataOutputStream(con.getOutputStream());
		//输出表头
		out.write(head);

		//文件正文部分
		//把文件已流文件的方式 推入到url中
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		in.close();

		//结尾部分
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//定义最后数据分隔线

		out.write(foot);

		out.flush();
		out.close();

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		String result = null;
		try {
			//定义BufferedReader输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		JSONObject jsonObj = JSONObject.fromObject(result);
		String typeName = "media_id";
//		if(!"image".equals(type)){
//			typeName = type + "_media_id";
//		}
		String mediaId = jsonObj.getString(typeName);
		return mediaId;
	}
	
	public static String uploadMedia(String filePath,String accessToken,String type) throws IOException,NoSuchAlgorithmException{
		File file =new File(filePath);
		if(!file.exists()||!file.isFile()){
			throw new IOException("文件不存在");
		}
		
		String url = UPLOAD_URL_FOREVER.replace("ACCESS_TOKEN",accessToken).replace("TYPE", type);
		URL urlObj=new URL(url);
		//链接
		HttpURLConnection con=(HttpURLConnection) urlObj.openConnection();
		con.setRequestMethod("POST");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		
		//设置请求头信息
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");
		//设置边界
		String BOUNDARY="----------"+System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");

		byte[] head = sb.toString().getBytes("utf-8");

		//获得输出流
		OutputStream out = new DataOutputStream(con.getOutputStream());
		//输出表头
		out.write(head);

		//文件正文部分
		//把文件已流文件的方式 推入到url中
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		in.close();

		//结尾部分
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//定义最后数据分隔线

		out.write(foot);

		out.flush();
		out.close();

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		String result = null;
		try {
			//定义BufferedReader输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		JSONObject jsonObj = JSONObject.fromObject(result);
//		logger.info(jsonObj);
		String typeName = "media_id";
//		if(!"image".equals(type)){
//			typeName = type + "_media_id";
//		}
		String mediaId = jsonObj.getString(typeName);
		return mediaId;
	}
	
	/**
	 * 新增永久素材
	 * @author san
	 */
	public static String formUpload(String urlStr, Map<String, String> textMap,
			Map<String, String> fileMap) {
		String res = "";
		HttpURLConnection conn = null;
		String BOUNDARY = "---------------------------"	+ System.currentTimeMillis(); // boundary就是request头和上传文件内容的分隔符
		try {

			URL url = new URL(urlStr);

			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);

			OutputStream out = new DataOutputStream(conn.getOutputStream());
			// text
			if (textMap != null) {
				StringBuffer strBuf = new StringBuffer();
				Iterator iter = textMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					strBuf.append("\r\n").append("--").append(BOUNDARY)
							.append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\""
							+ inputName + "\"\r\n\r\n");
					strBuf.append(inputValue);
				}
				out.write(strBuf.toString().getBytes("utf-8"));
			}

			// file
			if (fileMap != null) {
				Iterator iter = fileMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					File file = new File(inputValue);
					String filename = file.getName();
					String contentType = new MimetypesFileTypeMap()
							.getContentType(file);
					if (filename.endsWith(".png")) {
						contentType = "image/png";
					} else if (filename.endsWith(".jpg")) {
						contentType = "image/jpeg";
					} else if (filename.endsWith(".mp4")) {
						contentType = "video/mpeg4";
					}
					if (contentType == null || contentType.equals("")) {
						contentType = "application/octet-stream";
					}

					StringBuffer strBuf = new StringBuffer();
					strBuf.append("\r\n").append("--").append(BOUNDARY)
							.append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\""
							+ inputName + "\"; filename=\"" + filename
							+ "\"\r\n");
					strBuf.append("Content-Type:" + contentType + "\r\n\r\n");

					out.write(strBuf.toString().getBytes("utf-8"));

					DataInputStream in = new DataInputStream(
							new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];
					while ((bytes = in.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}
					in.close();
				}
			}

			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");
			out.write(endData);
			out.flush();
			out.close();

			// 读取返回数据
			StringBuffer strBuf = new StringBuffer();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuf.append(line).append("\n");
			}
			res = strBuf.toString();
			reader.close();
			reader = null;
		} catch (Exception e) {
			logger.info("发送POST请求出错。" + urlStr);
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}

		JSONObject jsonObj = JSONObject.fromObject(res);
		String mediaId = jsonObj.getString("media_id");
		return mediaId;
	}
	
	
	
	/**
	 * 上传永久素材--no
	 * @author san
	 */
	public static String postFile(String url, String filePath, String title,
			String introduction) {
		File file = new File(filePath);
		if (!file.exists())
			return null;
		String result = null;
		try {
			URL url1 = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Cache-Control", "no-cache");
			String boundary = "-----------------------------"
					+ System.currentTimeMillis();
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary);

			OutputStream output = conn.getOutputStream();
			output.write(("--" + boundary + "\r\n").getBytes());
			output.write(String
					.format("Content-Disposition: form-data; name=\"media\"; filename=\"%s\"\r\n",
							file.getName()).getBytes());
			output.write("Content-Type: video/mp4 \r\n\r\n".getBytes());
			byte[] data = new byte[1024];
			int len = 0;
			FileInputStream input = new FileInputStream(file);
			while ((len = input.read(data)) > -1) {
				output.write(data, 0, len);
			}
			output.write(("--" + boundary + "\r\n").getBytes());
			output.write("Content-Disposition: form-data; name=\"description\";\r\n\r\n"
					.getBytes());
			output.write(String.format(
					"{\"title\":\"%s\", \"introduction\":\"%s\"}", title,
					introduction).getBytes());
			output.write(("\r\n--" + boundary + "--\r\n\r\n").getBytes());
			output.flush();
			output.close();
			input.close();
			InputStream resp = conn.getInputStream();
			StringBuffer sb = new StringBuffer();
			while ((len = resp.read(data)) > -1)
				sb.append(new String(data, 0, len, "utf-8"));
			resp.close();
			result = sb.toString();
		} catch (IOException e) {
			logger.error("postFile数据传输失败", e);
		}
		logger.info(url + ": result=" + result);
		return result;
	}
			 
	public static void main(String[] args) {
		String url = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token="
				+ "U_0AIUrkfmVk0LXlaGu0lx5CXx-_4&type=video";
		postFile(url, "/Users/jlusoft/Desktop/test.mp4", "test", "dfd");
	}
	
	//上传永久素材
	public static void uploadPermanentMedia2(String accessToken, File file,
			String title, String introduction) {
		try {

			// 这块是用来处理如果上传的类型是video的类型的
			JSONObject j = new JSONObject();
			j.put("title", title);
			j.put("introduction", introduction);

			// 拼装请求地址
			String uploadMediaUrl = "http://api.weixin.qq.com/cgi-bin/material/add_material?access_token=##ACCESS_TOKEN##";
			uploadMediaUrl = uploadMediaUrl.replace("##ACCESS_TOKEN##",
					accessToken);

			URL url = new URL(uploadMediaUrl);
			String result = null;
			long filelength = file.length();
			String fileName = file.getName();
			String suffix = fileName.substring(fileName.lastIndexOf("."),
					fileName.length());
			String type = "video/mp4"; // 我这里写死
			/**
			 * 你们需要在这里根据文件后缀suffix将type的值设置成对应的mime类型的值
			 */
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false); // post方式不能使用缓存
			// 设置请求头信息
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");

			// 设置边界,这里的boundary是http协议里面的分割符，不懂的可惜百度(http 协议
			// boundary)，这里boundary 可以是任意的值(111,2222)都行
			String BOUNDARY = "----------" + System.currentTimeMillis();
			con.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			// 请求正文信息
			// 第一部分：

			StringBuilder sb = new StringBuilder();

			// 这块是post提交type的值也就是文件对应的mime类型值
			sb.append("--"); // 必须多两道线
								// 这里说明下，这两个横杠是http协议要求的，用来分隔提交的参数用的，不懂的可以看看http
								// 协议头
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"type\" \r\n\r\n"); // 这里是参数名，参数名和值之间要用两次
			sb.append(type + "\r\n"); // 参数的值

			// 这块是上传video是必须的参数，你们可以在这里根据文件类型做if/else 判断
			sb.append("--"); // 必须多两道线
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"description\" \r\n\r\n");
			sb.append(j.toString() + "\r\n");

			/**
			 * 这里重点说明下，上面两个参数完全可以卸载url地址后面 就想我们平时url地址传参一样，
			 * http://api.weixin.qq.
			 * com/cgi-bin/material/add_material?access_token
			 * =##ACCESS_TOKEN##&type=""&description={} 这样，如果写成这样，上面的
			 * 那两个参数的代码就不用写了，不过media参数能否这样提交我没有试，感兴趣的可以试试
			 */

			sb.append("--"); // 必须多两道线
			sb.append(BOUNDARY);
			sb.append("\r\n");
			// 这里是media参数相关的信息，这里是否能分开下我没有试，感兴趣的可以试试
			sb.append("Content-Disposition: form-data;name=\"media\";filename=\""
					+ fileName + "\";filelength=\"" + filelength + "\" \r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");
			byte[] head = sb.toString().getBytes("utf-8");
			// 获得输出流
			OutputStream out = new DataOutputStream(con.getOutputStream());
			// 输出表头
			out.write(head);
			// 文件正文部分
			// 把文件已流文件的方式 推入到url中
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			in.close();
			// 结尾部分，这里结尾表示整体的参数的结尾，结尾要用"--"作为结束，这些都是http协议的规定
			byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
			out.write(foot);
			out.flush();
			out.close();
			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = null;
			// 定义BufferedReader输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
			// 使用JSON-lib解析返回结果
			JSONObject jsonObject = JSONObject.fromObject(result);
//			if (jsonObject.has("media_id")) {
//				logger.info("media_id:"
//						+ jsonObject.getString("media_id"));
//			} else {
//				logger.info(jsonObject.toString());
//			}
//			logger.info("json:" + jsonObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}
	
	/**
	 * 获取临时二维码,ticket
	 * 
	 * @param sceneId 场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
	 * @return
	 * 临时二维码post参数
	 * {"expire_seconds": 604800, "action_name": "QR_SCENE", "action_info": {"scene": {"scene_id": 123}}}
	 * 
	 * 永久二维码
	 * POST数据例子：
	 * {"action_name": "QR_LIMIT_SCENE", "action_info": {"scene": {"scene_id": 123}}}
	 * 或者也可以使用以下POST数据创建字符串形式的二维码参数：
	 * {"action_name": "QR_LIMIT_STR_SCENE", "action_info": {"scene": {"scene_str": "123"}}}
	 */
	public static JSONObject getTempQrcode(String sceneId,String token) {
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, Object> action_info = new HashMap<String, Object>();
		Map<String, Object> scene = new HashMap<String, Object>();
		param.put("expire_seconds", "2592000");
		param.put("action_name", "QR_SCENE");
		scene.put("scene_id", sceneId);
		action_info.put("scene", scene);
		param.put("action_info", action_info);
		JSONObject ticket = getTicket(JSONObject.fromObject(param).toString(), token);
		logger.info(ticket.toString());
		return ticket;
	}
	
	/**
	 * 获取Ticket
	 * @param param
	 * @param accessToken
	 * @return
	 */
	private static JSONObject getTicket(String param, String accessToken) {
		// url
		String url = QR_CODE_URL.replace("TOKEN", accessToken);
		JSONObject jsonObject = WeixinUtil.httpRequest(url, "POST", param);
		if (null != jsonObject) {
			if(!jsonObject.containsKey("errcode")){
				return jsonObject;
			}
		}
		return jsonObject;
	}

	/** 获取模版消息 */
	public static JSONObject getTemplateList(String access_token){
		String url = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=ACCESS_TOKEN".replace("ACCESS_TOKEN", access_token);
		JSONObject jsonObject = WeixinUtil.httpRequest(url, "GET", null);
		return jsonObject;
	}
	/** 删除模版消息 */
	public static JSONObject deleteTemplate(String access_token,String template_id){
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("template_id", template_id);
		String param = JSONObject.fromObject(paramMap).toString();
		String url = "https://api.weixin.qq.com/cgi-bin/template/del_private_template?access_token=ACCESS_TOKEN".replace("ACCESS_TOKEN", access_token);
		JSONObject jsonObject = WeixinUtil.httpRequest(url, "POST", param);
		return jsonObject;
	}
	
	/** 发送模版消息 */
	public static JSONObject sendTemplate(String access_token, String openid, String template_id, String redirect_url, Object data){
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("touser", openid);
		paramMap.put("template_id", template_id);
		paramMap.put("url", redirect_url);
		paramMap.put("miniprogram", "");
//		paramMap.put("miniprogram", "{\"appid\":\"xiaochengxuappid12345\",\"pagepath\":\"index?foo=bar\"");
		paramMap.put("data", data);
		String param = JSONObject.fromObject(paramMap).toString();
		System.out.println(param);
		String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN".replace("ACCESS_TOKEN", access_token);
		JSONObject jsonObject = WeixinUtil.httpRequest(url, "POST", param);
		return jsonObject;
	}
	
	/** 获取自动回复列表 */
	public JSONObject getAutoReplyList(String access_token){
		String url = "https://api.weixin.qq.com/cgi-bin/get_current_autoreply_info?access_token=ACCESS_TOKEN".replace("ACCESS_TOKEN", access_token);
		JSONObject jsonObject = WeixinUtil.httpRequest(url, "GET", null);
		return jsonObject;
	}
	
	public static String getPermanentMedia2(String accessToken,String type,int offset,int count) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("type", type);
		paramMap.put("offset", offset);
		paramMap.put("count", count);
		String param = JSONObject.fromObject(paramMap).toString();
		String get_media_url = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=ACCESS_TOKEN".replace("ACCESS_TOKEN", accessToken);
		JSONObject jsonObject = httpRequest(get_media_url, "POST", param);
		return jsonObject.toString();
	} 
	/**
	 * 判断字符串是否不为空
	 * @param args
	 * @return
	 */
	public static boolean isNotEmpty(String args){
		return args!=null && args.length()>0;
	}
	/**
	 * 通过设定的minutes分钟，取得缓存到期时间
	 * @param minutes
	 * @return
	 */
	public static Date getMemcachedExpiry(int minutes){
		if(minutes == 0) 
			minutes = 120;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, minutes);
		return new Date(calendar.getTimeInMillis());
	}
}
