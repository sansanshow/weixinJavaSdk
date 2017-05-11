package com.wxsdk.util;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MyUtils {
	// 默认除法运算精度,即保留小数点多少位
	private static final int DEFAULT_DIV_SCALE = 10;

	public static final String number = "0123456789";
	private static final String[] weekdayShort = new String[] { "周一", "周二",
			"周三", "周四", "周五", "周六", "周日" };

	public static boolean hasPurview(int purview, int offset) {
		return (purview & offset) == offset;
	}

	public static String getDateString(Timestamp time) {
		String formatStr = "yyyy-MM-dd";
		SimpleDateFormat myFormat = new SimpleDateFormat(formatStr);
		return myFormat.format(time);
	}

	/**
	 * 获取指定时间前n天的时间，返回日期的时间为00:00:00
	 * 
	 * @param endDate
	 *            结束日期
	 * @param day
	 *            结束日期之前的天数
	 * @return
	 */
	public static Date getStartDateOfzero(Date endDate, int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - day);
		Date date = c.getTime();
		// System.out.println(MyUtils.formatDate("yyyy-MM-dd", date));
		return MyUtils.convertString2DateSpecifyFormat(
				MyUtils.formatDate("yyyy-MM-dd", date) + " 00:00:00",
				"yyyy-MM-dd HH:mm:ss");
	}

	public static Timestamp getTimestamp(String pattern, String timeStr) {
		// pattern格式：如yyyy-MM-dd HH:mm:ss，可自由定义
		SimpleDateFormat tsFormat = new SimpleDateFormat(pattern);
		Date date;
		try {
			date = tsFormat.parse(timeStr);
			return new Timestamp(date.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取指定日期星期几，星期天返回0
	 * 
	 * @param dt
	 * @return
	 */
	public static int getWeekOfDate(Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return w;
	}

	/**
	 * 获取2个时间之间相隔的天数，小时数，分钟数,type:day,天数、hour小时数、minute 分钟数,默认返回天数
	 * 
	 * @param dateA
	 * @param dateB
	 * @return
	 */
	public static long getBetweenNumber(Date dateA, Date dateB, String type) {
		long dayNumber = 0;
		Long v = 0L;
		if (type.equals("day")) {
			v = 24L * 60L * 60L * 1000L;
		} else if (type.equals("hour")) {
			v = 60L * 60L * 1000L;
		} else if (type.equals("minute")) {
			v = 60L * 1000L;
		} else if (type.equals("seconds")) {
			v = 1000L;
		}
		try {
			dayNumber = (dateB.getTime() - dateA.getTime()) / v;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dayNumber;
	}

	public static String[] getHoursBetween(Date date1, Date date2,
			String formate) {
		long hs = 0;

		if (date1 != null && date1 != null) {
			hs = (date2.getTime() - date1.getTime()) / (1 * 60L * 60L * 1000L);
		}

		String[] dates = new String[(int) hs + 1];
		for (int i = 0; i < dates.length; i++) {
			Calendar c = Calendar.getInstance();
			c.setTime(date1);
			c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + i);
			dates[i] = getStringDate(c.getTime(), formate);
		}
		return dates;
	}

	public static String[] getDatesBetween(Date date1, Date date2,
			String dateFormat) {
		long days = 0;
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		Date d1;
		Date d2;
		try {
			d1 = df.parse(getStringDate(date1, dateFormat));
			d2 = df.parse(getStringDate(date2, dateFormat));
		} catch (ParseException e) {
			d1 = d2 = null;
		}
		if (d1 != null && d2 != null) {
			days = (d2.getTime() - d1.getTime()) / (24L * 60L * 60L * 1000L);
		}

		String[] dates = new String[(int) days + 1];
		for (int i = 0; i < dates.length; i++) {
			Calendar c = Calendar.getInstance();
			c.setTime(d1);
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + i);
			dates[i] = getStringDate(c.getTime(), dateFormat);
		}
		return dates;
	}

	public static String getStringDate(Date date, String dateFormat) {
		SimpleDateFormat f = new SimpleDateFormat(dateFormat);
		return f.format(date);
	}

	/***************************************************************************
	 * 获取day天前的时间
	 * 
	 * @param day
	 *            天数
	 * @return
	 */
	public static Date getTimeBeforeDay(int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - day);
		return c.getTime();
	}

	/**
	 * 把字符串转换成日期时间格式
	 * 
	 * @param time
	 * @return
	 */
	public static Date convertString2DateSpecifyFormat(String time,
			String format) {
		if (time == null)
			return null;
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date cdate;
		try {
			cdate = formatter.parse(time);
		} catch (ParseException e) {
			return null;
		}
		return cdate;
	}

	public static String getTimeString(Timestamp time) {
		String formatStr = "yyyy-MM-dd HH:mm";
		SimpleDateFormat myFormat = new SimpleDateFormat(formatStr);
		return myFormat.format(time);
	}

	public static Date getTimeBeforeMinute(Date date, int minutes) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) - minutes);
		return c.getTime();
	}

	public static String getTimeString(Date time) {
		String formatStr = "yyyy-MM-dd HH:mm";
		SimpleDateFormat myFormat = new SimpleDateFormat(formatStr);
		return myFormat.format(time);
	}

	public static boolean isNumber(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (number.indexOf(str.charAt(i)) == -1) {
				return false;
			}
		}
		return true;
	}

	public static Node getElementByTagName(Element ele, String tagName) {
		NodeList list = ele.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {
			return list.item(0);
		}
		return null;
	}

	public static NodeList getNodeListByTagName(Element ele, String tagName) {
		NodeList list = ele.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {
			return list;
		}
		return null;
	}

	public static String getElementValueByTagName(Element ele, String tagName) {
		NodeList list = ele.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {
			Node node = list.item(0);
			if (node.getTextContent() != null
					&& node.getTextContent().trim().length() > 0) {
				return node.getTextContent().trim();
			}
		}
		return null;
	}

	public static Node transXml2Node(String xmlstr) throws Exception {
		if (xmlstr == null)
			return null;
		xmlstr = xmlstr.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");
		TransformerFactory tranFactory = TransformerFactory.newInstance();
		Transformer tran = tranFactory.newTransformer();
		StreamSource strSrc = new StreamSource(new CharArrayReader(
				xmlstr.toCharArray()));
		DOMResult domRes = new DOMResult();

		tran.transform(strSrc, domRes);
		Node rootNode = domRes.getNode();
		return rootNode;
	}

	/***************************************************************************
	 * 获取day天后的时间
	 * 
	 * @param day
	 *            天数
	 * @return
	 */
	public static Date getTimeAfterDay(int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + day);
		return c.getTime();
	}

	public static Date getTimeAfterDay(int day, Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + day);
		return c.getTime();
	}

	public static Date getTimeAfterSeconds(int seconds, Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.SECOND, c.get(Calendar.SECOND) + seconds);
		return c.getTime();
	}

	public static long getDays(String date1, String date2) {
		if (date1 == null || date1.equals(""))
			return 0;
		if (date2 == null || date2.equals(""))
			return 0;
		// 转换为标准时间
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = null;
		java.util.Date mydate = null;
		try {
			date = myFormatter.parse(date1);
			mydate = myFormatter.parse(date2);
		} catch (Exception e) {
		}
		long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		return day;
	}

	public static String getDecimalFormat(double d, String format) {
		DecimalFormat fmt = new DecimalFormat(format);
		String outStr = null;
		try {
			outStr = fmt.format(d);
		} catch (Exception e) {
			outStr = String.valueOf(d);
		}
		if (outStr.startsWith("."))
			outStr = "0" + outStr;
		return outStr;
	}

	/***************************************************************************
	 * 得到二个日期间的间隔天数
	 * 
	 * @param sj1
	 * @param sj2
	 * @return
	 */
	public static String getTwoDay(String sj1, String sj2) {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		long day = 0;
		try {
			java.util.Date date = myFormatter.parse(sj1);
			java.util.Date mydate = myFormatter.parse(sj2);
			day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			return "";
		}
		return day + "";
	}

	/***************************************************************************
	 * 把日期格式化为文本
	 * 
	 * @param format
	 *            格式
	 * @param date
	 *            日期
	 * @return
	 */
	public static String formatDate(String format, Date date) {
		try {
			SimpleDateFormat ss = new SimpleDateFormat(format);
			return ss.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	/***************************************************************************
	 * 日期字符串转化为Date对象
	 * 
	 * @param format
	 * @param dateString
	 * @return
	 */
	public static Date stringToDate(String format, String dateString) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			return df.parse(dateString);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return new Date();
	}

	/***************************************************************************
	 * 去除字符串中的回车,换行符,制表符
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		try {
			// Pattern p1 = Pattern.compile("\\s*|\t|\r|\n");
			Pattern p1 = Pattern.compile("\t|\r|\n");
			Matcher m1 = p1.matcher(str);
			return m1.replaceAll("");
		} catch (Exception e) {
			return str;
		}
	}

	/**
	 * 输入代表某一期间的字符串 返回开始时间和结束时间的list
	 * 
	 * @param dateType
	 * @return List<string> index 0=starttime,index 1=endtime
	 */

	public static List<String> converDateTypeToDateString(String dateType) {
		int iDateType = Integer.parseInt(dateType);
		List<String> list = new ArrayList<String>();
		String starttime = "";
		String endtime = "";
		String today = MyUtils.formatDate("yyyy-MM-dd", new Date());

		Calendar c = Calendar.getInstance();
		if (iDateType == 1) {
			starttime = today;
			endtime = today;

		} else if (iDateType == 2) {
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
			starttime = MyUtils.formatDate("yyyy-MM-dd", c.getTime());
			endtime = starttime;

		} else if (iDateType == 3) {

			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 7);
			starttime = MyUtils.formatDate("yyyy-MM-dd", c.getTime());
			endtime = today;

		} else if (iDateType == 4) {
			int month = c.get(Calendar.MONTH) + 1;
			String strMonth = "";
			if (month < 10) {
				strMonth = "0" + month;
			} else {
				strMonth = String.valueOf(month);
			}

			starttime = c.get(Calendar.YEAR) + "-" + strMonth + "-" + "01";
			endtime = today;
		} else if (iDateType == 5) {
			// 上个月
			c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
			Calendar c1 = (Calendar) c.clone();
			// 得到本月第一天
			c.set(Calendar.DAY_OF_MONTH, getMonthFirstDay(c));
			// 本月最后一天
			c1.set(Calendar.DAY_OF_MONTH, getMonthLastDay(c1));

			starttime = MyUtils.formatDate("yyyy-MM-dd", c.getTime());
			endtime = MyUtils.formatDate("yyyy-MM-dd", c1.getTime());
		}
		list.add(starttime);
		list.add(endtime);
		return list;
	}

	public static int getMonthFirstDay(Calendar c) {

		return c.getActualMinimum(Calendar.DAY_OF_MONTH);
	}

	public static int getMonthLastDay(Calendar c) {

		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 取的几个月前的日期
	 * 
	 * @param cdate
	 * @param months
	 * @return
	 */
	public static Date getDateBeforeMonth(Date cdate, int months) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(cdate);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - months);
		return cal.getTime();
	}

	/**
	 * 获取某月第一天几号
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	public static String analyseDomain(String starturl) {
		try {
			// logger.debug("url" + starturl);
			URL url = new URL(starturl);
			return url.getHost().toString();

		} catch (MalformedURLException e) {
			return starturl;
		}
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return (b1.add(b2)).doubleValue();
	}

	/**
	 * 提供精确的减法运算。
	 * 
	 * @param v1
	 *            被减数
	 * @param v2
	 *            减数
	 * @return 两个参数的差
	 */
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return (b1.subtract(b2)).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 * 
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return (b1.multiply(b2)).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后多少位，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			System.err.println("除法精度必须大于0!");
			return 0;
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return (b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP)).doubleValue();
	}

	public static int strToInt(String s) {
		try {
			return Integer.valueOf(s);
		} catch (Exception e) {
			return 0;
		}
	}

	public static Long strToLong(String s) {
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return new Long("0");
		}
	}

	public static BigDecimal strToBigDecimal(String s) {
		try {
			return new BigDecimal(s);
		} catch (Exception e) {
			return new BigDecimal(0);
		}
	}

	public static Long doubleToLong(Double num) {
		try {
			BigDecimal b = new BigDecimal(num);
			return b.setScale(2, BigDecimal.ROUND_HALF_UP).longValue();
		} catch (Exception e) {
			return new Long(0);
		}
	}

	/***************************************************************************
	 * MD5 32加密
	 * 
	 * @param src
	 * @return
	 */
	public static String md5ByHex(String src) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte b[] = src.getBytes();
			md.reset();
			md.update(b);
			byte hash[] = md.digest();
			String hs = "";
			String stmp = "";
			for (int i = 0; i < hash.length; i++) {
				stmp = Integer.toHexString(hash[i] & 0xff);
				if (stmp.length() == 1)
					hs = hs + "0" + stmp;
				else
					hs = hs + stmp;
			}

			return hs.toUpperCase();
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean isStringEmpty(String str) {
		boolean ret = false;
		if (str == null || "".equals(str.trim())) {
			ret = true;
		}
		return ret;
	}

	public static String getHTMLByUrl(String url, String encoding) {
		if (encoding == null)
			encoding = "utf-8";
		HttpClient client = new HttpClient();
		HttpMethod result = new GetMethod(url);
		try {
			result.getParams()
					.setParameter(
							HttpMethodParams.USER_AGENT,
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36 AlexaToolbar/alxg-3.1");
			HttpConnectionManagerParams managerParams = client
					.getHttpConnectionManager().getParams();

			// 设置连接超时时间(单位毫秒)
			managerParams.setConnectionTimeout(20000);
			// 设置读数据超时时间(单位毫秒)
			// managerParams.setSoTimeout(120000);
			client.executeMethod(result);

			InputStream resStream = result.getResponseBodyAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					resStream, encoding));
			StringBuffer resBuffer = new StringBuffer();
			char[] buffer = new char[256];
			int len = -1;
			while ((len = br.read(buffer, 0, buffer.length)) != -1) {

				resBuffer.append(buffer, 0, len);
			}
			return resBuffer.toString();

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

	public static String sendDataTo51bi(String backurl) {
		HttpClient client = new HttpClient();
		HttpMethod result = new GetMethod(backurl);
		try {
			HttpConnectionManagerParams managerParams = client
					.getHttpConnectionManager().getParams();
			// 设置连接超时时间(单位毫秒)
			managerParams.setConnectionTimeout(20000);
			client.executeMethod(result);
			InputStream resStream = result.getResponseBodyAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					resStream));
			StringBuffer resBuffer = new StringBuffer();
			String resTemp = "";
			while ((resTemp = br.readLine()) != null) {
				resBuffer.append(resTemp);
			}
			String r = resBuffer.toString();
			if (r == null)
				return "-1";
			else
				return r;
		} catch (HttpException e) {
			StringBuffer errstr = new StringBuffer();
			errstr.append(e.toString()).append("\n");
			StackTraceElement[] stack = e.getStackTrace();
			if (stack != null) {
				int loop = 5;
				if (stack.length < 5)
					loop = stack.length;
				for (int i = 0; i < loop; i++) {
					errstr.append(stack[i].toString()).append("\n");
				}

			}
			e.printStackTrace();
			return "-1";
		} catch (IOException e) {
			StringBuffer errstr = new StringBuffer();
			errstr.append(e.toString()).append("\n");
			StackTraceElement[] stack = e.getStackTrace();
			if (stack != null) {
				int loop = 5;
				if (stack.length < 5)
					loop = stack.length;
				for (int i = 0; i < loop; i++) {
					errstr.append(stack[i].toString()).append("\n");
				}
			}
			e.printStackTrace();
			return "-1";
		} finally {
			result.releaseConnection();
		}
	}

	public static int getRandomInScope(int a, int b) {
		if (b == 0)
			return 0;
		int temp = 0;
		try {
			if (a > b) {
				temp = new Random().nextInt(a - b);
				return temp + b;
			} else {
				temp = new Random().nextInt(b - a);
				return temp + a;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp + a;
	}

	public static Date getPastTwoMonth() {
		Date date = getDateBeforeMonth(new Date(), 1);
		Date fdate = getFirstDayOfMonth(date);
		return convertString2DateSpecifyFormat(formatDate("yyyy-MM-dd", fdate)
				+ " 00:00:00", "yyyy-MM-dd HH:mm:ss");

	}

	/**
	 * 获得过去X分钟的时间
	 * 
	 * @param minute
	 * @return
	 */
	public static Date getPastMinute(int minute) {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - minute);
		return cal.getTime();
	}

	/**
	 * 获得过去几天的时间
	 * 
	 * @param day天数
	 * @return
	 */
	public static Date getPastDay(int day) {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - day);
		return cal.getTime();
	}

	/**
	 * 获得过去几天的时间
	 * 
	 * @param day天数
	 * @return
	 */
	public static Date getPastDay(int day, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - day);
		return cal.getTime();
	}

	public static String sendGetConn(String url, String encode,
			boolean isRequestHeader) {
		StringBuffer htmlString = new StringBuffer();// 返回的JSON字符串
		BufferedReader reader = null;
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;
		try {
			URL httpUrl = new URL(url);
			URLConnection connection = httpUrl.openConnection();

			httpURLConnection = (HttpURLConnection) connection;
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setReadTimeout(20000);// 无响应20秒，断开会话
			httpURLConnection.setConnectTimeout(20000);
			httpURLConnection.setRequestMethod("GET");
			if (isRequestHeader) {
				String domain = analyseDomain(url);
				httpURLConnection.setRequestProperty("Referer", "http://"
						+ domain);
				httpURLConnection
						.setRequestProperty("Accept-Language", "zh-cn");
				httpURLConnection
						.setRequestProperty("User-Agent",
								"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)");
			}
			httpURLConnection.connect();
			inputStream = httpURLConnection.getInputStream();// 发送连接
			if (encode != null)
				reader = new BufferedReader(new InputStreamReader(inputStream,
						encode));
			else
				reader = new BufferedReader(new InputStreamReader(inputStream,
						"utf-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				htmlString.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (inputStream != null)
					inputStream.close();
				if (httpURLConnection != null)
					httpURLConnection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return htmlString.toString().trim();
	}

	/**
	 * 获取https地址内容
	 * 
	 * @param httpsurl
	 * @return
	 */
	public static String getHTMLByHttpsUrl(String httpsurl) {
		String json = null;
		try {

			DefaultHttpClient httpclient = new DefaultHttpClient();
			SSLContext ctx = SSLContext.getInstance("SSL");
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ClientConnectionManager ccm = httpclient.getConnectionManager();
			// register https protocol in httpclient's scheme registry
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 443, ssf));

			HttpGet httpget = new HttpGet(httpsurl);
			HttpParams params = httpclient.getParams();
			// 设定代理
			// HttpHost proxy = new HttpHost("127.0.0.1", 8888);
			// params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

			HttpProtocolParams
					.setUserAgent(
							params,
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.143 Safari/537.36");
			httpget.setParams(params);

			HttpResponse response = httpclient.execute(httpget);
			json = paseResponse(response);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json;
	}

	private static String paseResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		String body = null;
		try {
			body = EntityUtils.toString(entity);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return body;
	}

	public static void main(String[] args) {
		String aa = "http://58.221.72.184:5868/orderback.jhtml?ordergetFlag=1&bid=2706522&leagueflag=gome&cost=92.0&oid=8612431199&jumpinfo=489631157&adid=9763&adposid=231&ip=219.138.76.118&actid=70822&outadid=0&clickfrom=WL&fromPage=prod99superfanappsdjl&channel=baidustore&opsys=apk,apk&uid=364456639&cback=2.76&ordertime=2015-12-03+12%3A39%3A05.000&mcode=416DD7C43E3EF8F440E31DF916C6A31D&cates=cat10000074&comms=2.76&pp=92.0&storeostatus=M&userinfo=&scbldid=&sjumptime=";

		System.out.println(MyUtils.sendDataTo51bi(aa));
	}

	public static String getRemoteHost(
			javax.servlet.http.HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}

	public static boolean notEmptyList(java.util.List list) {
		return list != null && list.size() > 0;
	}

	/**
	 * 毫秒转化成当前时间 字符串 HH:mm
	 * 
	 * @param millisecond
	 *            相对于1970年的的毫秒数
	 * @return
	 */
	public static String formatMilliToTime(Long millisecond) {
		Date date = new Date(millisecond);

		String html = "00:00";
		if (millisecond != null) {
			long s = millisecond;
			String format;
			Object[] array;
			Integer hours = date.getHours();
			Integer minutes = date.getMinutes();
			format = "%1$,02d:%2$,02d";
			array = new Object[] { hours, minutes };
			html = String.format(format, array);
		}

		return html;
	}

	/**
	 * 毫秒转换成分钟 --xx分钟
	 * 
	 * @param millisecond
	 * @return
	 */
	public static int convertMilliToMin(Long millisecond) {
		Integer minutes = 0;
		if (millisecond != null) {
			minutes = (int) (millisecond / (60 * 1000));
		}

		return minutes;
	}

	/**
	 * 毫秒转换成分钟 --xx分钟
	 * 
	 * @param millisecond
	 * @return
	 */
	public static int convertMilliToSec(Long millisecond) {
		Integer minutes = 0;
		if (millisecond != null) {
			minutes = (int) (millisecond / 1000);
		}
		return minutes;
	}

	/**
	 * 毫秒转换成分钟 --xx分钟
	 * 
	 * @param millisecond
	 * @return
	 */
	public static String convertMilliToMinStr(Long millisecond) {
		return convertMilliToMin(millisecond) > 0 ? convertMilliToMin(millisecond)
				+ "分钟"
				: "少于1分钟";
	}
	/**
	 * 毫秒转换成xx小时xx分钟
	 * 
	 * @param millisecond
	 * @return
	 */
	public static String convertMilliToHHMM(Long millisecond) {
		String html = "0分钟";
		if (millisecond != null) {
			long s = millisecond;
			String format;
			Object[] array;
			Integer hours = (int) (s / (60 * 60 * 1000));
			Integer minutes = (int) (s / (60 * 1000) - hours * 60);
			System.out.println("hours:"+hours+",minutes:"+minutes);
			if(hours>0){
				System.out.println("ssssss");
				format = "%1$,02d小时%2$,02d分钟";
				array = new Object[] { hours, minutes };
			}else {
				System.out.println("sssss2222");
				format = "%1$,02d分钟";
				array = new Object[] { minutes };
			}
			
			html = String.format(format, array);
		}

		return html;
	}

	public static String getRepeatDayStr(int repeat) {
		String repStr = "";
		if (repeat == 127) {
			return "周一,周二,周三,周四,周五,周六,周日";
		}
		if (repeat == 96) {
			return "周六,周日";

		}
		if (repeat == 31) {
			return "周一,周二,周三,周四,周五";
		}
		int count = 0;
		for (int i = 0; i < weekdayShort.length; i++) {
			if (MyUtils.hasPurview(repeat, 1 << i)) {
				if (count == 0) {
					repStr += weekdayShort[i];
				} else {
					repStr += "," + weekdayShort[i];
				}
				count++;
			}
		}
		return repStr;
	}
	public static String getRepeatDayVal(int repeat) {
		String repStr = "";
		if (repeat == 127) {
			return "1,2,3,4,5,6,7";
		}
		if (repeat == 96) {
			return "6,7";

		}
		if (repeat == 31) {
			return "1,2,3,4,5";
		}
		int count = 0;
		for (int i = 0; i < weekdayShort.length; i++) {
			if (MyUtils.hasPurview(repeat, 1 << i)) {
				if (count == 0) {
					repStr += "" + (i+1);
				} else {
					repStr += "," + (i+1);
				}
				count++;
			}
		}
		return repStr;
	}
	/**
	 * 毫秒转化成当前时间 字符串 HH:mm
	 * 
	 * @param millisecond
	 *            相对于00:00的毫秒数
	 * @return
	 */
	public static String formatMilliToTimeStr(Long millisecond) {
		String html = "00:00";
		if (millisecond != null) {
			long s = millisecond;
			String format;
			Object[] array;
			Integer hours = (int) (s / (60 * 60 * 1000));
			Integer minutes = (int) (s / (60 * 1000) - hours * 60);
			format = "%1$,02d:%2$,02d";
			array = new Object[] { hours, minutes };
			html = String.format(format, array);
		}

		return html;
	}

	/**
	 * 时间字符串 HH:mm 转化成 毫秒
	 * 
	 * @param millisecond
	 *            相对于00:00的毫秒数
	 * @return
	 */
	public static long parseStringTimeToLong(String HH_mm) {
		String eL = "[0-9]{2}:[0-9]{2}";
		Pattern p = Pattern.compile(eL);
		Matcher m = p.matcher(HH_mm);
		boolean dateFlag = m.matches();
		if (!dateFlag) {
			System.out.println("格式错误");
		}
		System.out.println("格式正确");
		try {
			Date d;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			d = sdf.parse("2014-10-24 " + HH_mm);

			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
			Date d2 = sdf2.parse("2014-10-24 0:0:0,0");
			long s = d.getTime() - d2.getTime();
			System.out.println("你要的结果:" + s); // 0
			return s;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0L;
	}
}
