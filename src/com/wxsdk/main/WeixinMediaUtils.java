package com.wxsdk.main;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.wxsdk.model.AccessToken;
import com.wxsdk.util.PropertyManager;
import com.wxsdk.util.WeixinUtil;


/**
 * 文件上传工具
 */
public class WeixinMediaUtils {
	public static void main(String[] args) {
		
//		try {
			// 第三方用户唯一凭证
			String appId = PropertyManager.getProperty("appid");
			// 第三方用户唯一凭证密钥
			String appSecret = PropertyManager.getProperty("appsecret");
			String uploap_url = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=TYPE";
			// 调用接口获取access_token
			AccessToken at = WeixinUtil.getAccessToken(appId, appSecret);
			String url = uploap_url.replace("ACCESS_TOKEN", at.getAccess_token()).replace("TYPE", "image");
			System.out.println(at);
			File file = new File("D:/child_download.jpg");
			System.out.println("==============uploadPermanentMedia2=========================");
			WeixinUtil.uploadPermanentMedia2(at.getAccess_token(), file, "儿童端二维码", "儿童端端二维码");
			//图片消息存储
//			WeixinUtil.upload("D:/child_site_barcode.png", at.getToken(), "image");
//			//视频消息存储
//			WeixinUtil.upload("E:/tmp/1466957730843.mp4", at.getToken(), "video");
//			//语音存储
//			WeixinUtil.upload("E:/tmp/ka_nong.mp3", at.getToken(), "voice");
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public String uploadImages(){
		return "";
	}

}
