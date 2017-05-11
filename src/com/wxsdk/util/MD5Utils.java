package com.wxsdk.util;


import java.security.MessageDigest;


public class MD5Utils {
	public static String md5ByHex4Checksum(String authToken, long timestamp,
			String version) {
		// md5ByHex(authToken + version + (timestamp<<3))
		String md5Str = authToken + version + (timestamp << 3);
		// return Md5Utils.md5Digest(md5Str.getBytes());
		return md5ByHex(md5Str);
	}

	public static String md5ByHex4Sign(String authToken, String sign,
			long timestamp) {
		// md5(md5(token) + sign + Timestamp<<5)
		String md5Str = md5ByHex(authToken) + sign + (timestamp << 5);
		return md5ByHex(md5Str);
		// return Md5Utils.md5Digest(md5Str.getBytes());
	}

	public static String createChildInfo(String authToken, long parentId,
			long childId, long timestamp) {
		// 家长ID$儿童ID$当前时间毫秒$md5(家长ID<<1,儿童ID<<2,当前时间毫秒<<3,md5(家长Token))(8,15)
		String childinfo = Long.toString(parentId) + "$"
				+ Long.toString(childId) + "$" + Long.toString(timestamp);
		childinfo += "$" + md5ByHex(Long.toString(parentId << 1)
								+ Long.toString(childId << 2)
								+ Long.toString(timestamp << 3)
								+ md5ByHex(authToken)).substring(8, 16);
		return childinfo;
	}
	public static String createChildInfo4Wx(String md5token, long parentId,
			long childId, long timestamp) {
		System.out.println("----createChildInfo4Wx--md5token-"+md5token);
		// 家长ID$儿童ID$当前时间毫秒$md5(家长ID<<1,儿童ID<<2,当前时间毫秒<<3,md5(家长Token))(8,15)
		String childinfo = Long.toString(parentId) + "$"
				+ Long.toString(childId) + "$" + Long.toString(timestamp);
		childinfo += "$" + md5ByHex(Long.toString(parentId << 1)
				+ Long.toString(childId << 2)
				+ Long.toString(timestamp << 3)
				+ md5token).substring(8, 16);
		return childinfo;
	}
	public static String createChildInfoForServer(String authToken, long parentId,
			long childId, long timestamp) {
		// 家长ID$儿童ID$当前时间毫秒$md5(家长ID<<1,儿童ID<<2,当前时间毫秒<<3,md5(家长Token))(8,15)
		String childinfo = Long.toString(parentId) + "$"
				+ Long.toString(childId) + "$" + Long.toString(timestamp);
		childinfo += "$" + md5ByHex(Long.toString(parentId << 1)
				+ Long.toString(childId << 2)
				+ Long.toString(timestamp << 3)
				+ authToken).substring(8, 16);
		return childinfo;
	}

	public static String md5ByHex(String src) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] b = src.getBytes();
			md.reset();
			md.update(b);
			byte[] hash = md.digest();
			String hs = "";
			String stmp = "";
			for (int i = 0; i < hash.length; i++) {
				stmp = Integer.toHexString(hash[i] & 0xFF);
				if (stmp.length() == 1)
					hs = hs + "0" + stmp;
				else {
					hs = hs + stmp;
				}
			}
			return hs.toUpperCase();
		} catch (Exception e) {
		}
		return "";
	}
}
