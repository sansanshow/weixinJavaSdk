package com.wxsdk.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;


public class Util {
	
	public static String getHTMLByUrl(String url,String encoding) {
		if(encoding == null)
			encoding = "utf-8";
		HttpClient client = new HttpClient();
		HttpMethod result = new GetMethod(url);
		try {
			HttpConnectionManagerParams managerParams = client
					.getHttpConnectionManager().getParams();
			// 设置连接超时时间(单位毫秒)
			managerParams.setConnectionTimeout(20000);
			// 设置读数据超时时间(单位毫秒)
			// managerParams.setSoTimeout(120000);
			client.executeMethod(result);
			
			InputStream resStream = result.getResponseBodyAsStream();   
	        BufferedReader br = new BufferedReader(new InputStreamReader(resStream,encoding));   
	        StringBuffer resBuffer = new StringBuffer();   
	        char[] buffer = new char[256];
	        int len = -1;
			while ((len = br.read(buffer, 0, buffer.length)) != -1) {
				
				resBuffer.append(buffer, 0,len);
			}
			return  resBuffer.toString(); 

		} catch (HttpException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (Exception e) {
			return null;
		} finally {
			result.releaseConnection();
		}
	}
	

}
