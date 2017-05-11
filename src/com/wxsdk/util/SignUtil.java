package com.wxsdk.util;


import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 请求校验工具类
 * 
 * @author liufeng
 * @date 2013-05-18
 */
public class SignUtil {
	// 与接口配置信息中的Token要一致---token:16lmUT6tGSO553vcbrAC
	private static String token = "kidmate";

	/**
	 * 验证签名
	 * 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public static boolean checkSignature(String signature, String timestamp, String nonce) {
		String[] arr = new String[] { token, timestamp, nonce };
		// 将token、timestamp、nonce三个参数进行字典序排序
		Arrays.sort(arr);
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			content.append(arr[i]);
		}
		MessageDigest md = null;
		String tmpStr = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
			// 将三个参数字符串拼接成一个字符串进行sha1加密
			byte[] digest = md.digest(content.toString().getBytes());
			tmpStr = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		content = null;
		// 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
		return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 * 
	 * @param byteArray
	 * @return
	 */
	private static String byteToStr(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}

	/**
	 * 将字节转换为十六进制字符串
	 * 
	 * @param mByte
	 * @return
	 */
	private static String byteToHexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];

		String s = new String(tempArr);
		return s;
	}
	
	 /**
     * 获取支付所需签名
     * @param ticket
     * @param timeStamp
     * @param card_id
     * @param code
     * @return
     * @throws Exception
     */
    public static String getPayCustomSign(Map<String, String> bizObj,String key) throws Exception {
        
        String bizString =FormatBizQueryParaMap(bizObj,false);
        String signStr = "";
        signStr = bizString + "&key=" + key;
        return MD5.MD5Encode(signStr);
    }
   /*
    * 将map按字母顺序排列
    */
    public static String FormatBizQueryParaMap(Map<String, String> paraMap,
            boolean urlencode) throws Exception {

        String buff = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(
                    paraMap.entrySet());

            Collections.sort(infoIds,
                    new Comparator<Map.Entry<String, String>>() {
                        public int compare(Map.Entry<String, String> o1,
                                Map.Entry<String, String> o2) {
                            return (o1.getKey()).toString().compareTo(
                                    o2.getKey());
                        }
                    });

            for (int i = 0; i < infoIds.size(); i++) {
                Map.Entry<String, String> item = infoIds.get(i);
                if (item.getKey() != "") {
                    
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlencode) {
                        val = URLEncoder.encode(val, "utf-8");

                    }
                    buff += key + "=" + val + "&";

                }
            }

            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return buff;
    }
    /** 
     * 微信支付:回调验证签名
     *  
     * @param map 
     * @return 
     */  
   public static boolean verifyWeixinNotify(Map<String, Object> map) {  
        SortedMap<String, String> parameterMap = new TreeMap<String, String>();  
        String sign = (String) map.get("sign");  
        for (Object keyValue : map.keySet()) {  
           if (!keyValue.toString().equals("sign")) {  
                parameterMap.put(keyValue.toString(), map.get(keyValue).toString());  
           }  
       }  
       String createSign = null;
	try {
		createSign = getPayCustomSign(parameterMap,"buyjustlixingkejiyaoyaolepay2017");
		if (createSign.equals(sign)) {  
               return true;  
           } else {  
                return false;  
            }  
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
        return false;
   }
}
