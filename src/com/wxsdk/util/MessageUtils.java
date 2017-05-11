package com.wxsdk.util;

import java.io.InputStream;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.wxsdk.model.SendRedPackPo;
import com.wxsdk.model.response.Article;
import com.wxsdk.model.response.Image;
import com.wxsdk.model.response.ImageMessage;
import com.wxsdk.model.response.Music;
import com.wxsdk.model.response.MusicMessage;
import com.wxsdk.model.response.NewsMessage;
import com.wxsdk.model.response.TextMessage;
import com.wxsdk.model.response.Video;
import com.wxsdk.model.response.VideoMessage;
import com.wxsdk.model.response.Voice;
import com.wxsdk.model.response.VoiceMessage;

/**
 * 消息工具类
 * 
 * @author liufeng
 * @date 2013-05-19
 */
public class MessageUtils {
	private static final Logger logger = Logger.getLogger(MessageUtils.class);

	/** 返回消息类型：文本 */
	public static final String RESP_MESSAGE_TYPE_TEXT = "text";

	/** 返回消息类型：音乐 */
	public static final String RESP_MESSAGE_TYPE_MUSIC = "music";

	/** 返回消息类型：图片 */
	public static final String RESP_MESSAGE_TYPE_IMAGE = "image";

	/** 返回消息类型：视频 */
	public static final String RESP_MESSAGE_TYPE_VIDEO = "video";

	/** 返回消息类型：图文 */
	public static final String RESP_MESSAGE_TYPE_NEWS = "news";
	/** 返回消息类型：语音 */
	public static final String RESP_MESSAGE_TYPE_VOICE = "voice";

	/** 请求消息类型：文本 */
	public static final String REQ_MESSAGE_TYPE_TEXT = "text";

	/** 请求消息类型：图片 */
	public static final String REQ_MESSAGE_TYPE_IMAGE = "image";

	/** 请求消息类型：链接 */
	public static final String REQ_MESSAGE_TYPE_LINK = "link";

	/** 请求消息类型：地理位置 */
	public static final String REQ_MESSAGE_TYPE_LOCATION = "location";

	/** 请求消息类型：音频 */
	public static final String REQ_MESSAGE_TYPE_VOICE = "voice";

	/** 请求消息类型：推送 */
	public static final String REQ_MESSAGE_TYPE_EVENT = "event";

	/** 事件类型：subscribe(订阅) */
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

	/** 事件类型：SCAN(扫描进入) */
	public static final String EVENT_TYPE_SCAN = "SCAN";

	/** 事件类型：unsubscribe(取消订阅) */
	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

	/** 事件类型：CLICK(自定义菜单点击事件) */
	public static final String EVENT_TYPE_CLICK = "CLICK";

	private static XStream xstream = new XStream(new XppDriver() {
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {
				// 对所有xml节点的转换都增加CDATA标记
				boolean cdata = true;

				public void startNode(String name, Class clazz) {
					super.startNode(name, clazz);
				}

				protected void writeText(QuickWriter writer, String text) {
					if (cdata) {
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					} else {
						writer.write(text);
					}
				}
			};
		}
	});

	/**
	 * 解析微信发来的请求（XML）
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> doparseXml(HttpServletRequest request) {
		try {
			// 将解析结果存储在HashMap中
			Map<String, String> map = new HashMap<String, String>();
			// 从request中取得输入流
			InputStream inputStream = request.getInputStream();
			// 读取输入流
			SAXReader reader = new SAXReader();
			Document document = reader.read(inputStream);
			logger.info(document.toString());
			// 得到xml根元素
			Element root = document.getRootElement();
			// 得到根元素的所有子节点
			List<Element> elementList = root.elements();

			// 遍历所有子节点
			for (Element e : elementList)
				map.put(e.getName(), e.getText());

			// 释放资源
			inputStream.close();
			inputStream = null;

			return map;

		} catch (Exception e) {
			logger.info(e.toString());
		}
		return null;
	}

	/**
	 * 文本消息对象转换成xml
	 * 
	 * @param textMessage
	 *            文本消息对象
	 * @return xml
	 */
	public static String textMessageToXml(TextMessage textMessage) {
		xstream.alias("xml", textMessage.getClass());
		return xstream.toXML(textMessage);
	}

	/**
	 * 音乐消息对象转换成xml
	 * 
	 * @param musicMessage
	 *            音乐消息对象
	 * @return xml
	 */
	public static String musicMessageToXml(MusicMessage musicMessage) {
		xstream.alias("xml", musicMessage.getClass());
		xstream.alias("Music", new Music().getClass());
		return xstream.toXML(musicMessage);
	}

	/**
	 * 图文消息对象转换成xml
	 * 
	 * @param newsMessage
	 *            图文消息对象
	 * @return xml
	 */
	public static String newsMessageToXml(NewsMessage newsMessage) {
		xstream.alias("xml", newsMessage.getClass());
		xstream.alias("item", new Article().getClass());
		return xstream.toXML(newsMessage);
	}

	/**
	 * 语音消息对象转换成xml
	 * 
	 * @return xml
	 */
	public static String voiceMessageToXml(VoiceMessage voiceMessage) {
		xstream.alias("xml", voiceMessage.getClass());
		xstream.alias("Voice", new Voice().getClass());
		return xstream.toXML(voiceMessage);
	}

	/**
	 * 视频消息对象转换成xml
	 * 
	 * @param newsMessage
	 *            图文消息对象
	 * @return xml
	 */
	public static String videoMessageToXml(VideoMessage videoMessage) {
		xstream.alias("xml", videoMessage.getClass());
		xstream.alias("Video", new Video().getClass());
		return xstream.toXML(videoMessage);
	}

	/**
	 * 图片消息转为xml
	 * 
	 * @param imageMessage
	 * @return
	 */
	public static String imageMessageToXml(ImageMessage imageMessage) {
		XStream xstream = new XStream();
		xstream.alias("xml", imageMessage.getClass());
		return xstream.toXML(imageMessage);
	}

	public static boolean isQqFace(String content) {
		boolean result = false;

		// 判断QQ表情的正则表达式
		String qqfaceRegex = "/::\\)|/::~|/::B|/::\\||/:8-\\)|/::<|/::$|/::X|/::Z|/::'\\(|/::-\\||/::@|/::P|/::D|/::O|/::\\(|/::\\+|/:--b|/::Q|/::T|/:,@P|/:,@-D|/::d|/:,@o|/::g|/:\\|-\\)|/::!|/::L|/::>|/::,@|/:,@f|/::-S|/:\\?|/:,@x|/:,@@|/::8|/:,@!|/:!!!|/:xx|/:bye|/:wipe|/:dig|/:handclap|/:&-\\(|/:B-\\)|/:<@|/:@>|/::-O|/:>-\\||/:P-\\(|/::'\\||/:X-\\)|/::\\*|/:@x|/:8\\*|/:pd|/:<W>|/:beer|/:basketb|/:oo|/:coffee|/:eat|/:pig|/:rose|/:fade|/:showlove|/:heart|/:break|/:cake|/:li|/:bome|/:kn|/:footb|/:ladybug|/:shit|/:moon|/:sun|/:gift|/:hug|/:strong|/:weak|/:share|/:v|/:@\\)|/:jj|/:@@|/:bad|/:lvu|/:no|/:ok|/:love|/:<L>|/:jump|/:shake|/:<O>|/:circle|/:kotow|/:turn|/:skip|/:oY|/:#-0|/:hiphot|/:kiss|/:<&|/:&>";
		Pattern p = Pattern.compile(qqfaceRegex);
		Matcher m = p.matcher(content);
		if (m.matches()) {
			result = true;
		}
		return result;
	}

	public static String messageToXml(SendRedPackPo sendRedPackPo) {
		XStream xstream = new XStream();
		xstream.alias("xml", sendRedPackPo.getClass());
		return xstream.toXML(sendRedPackPo);
	}

	/**
	 * 图片消息
	 * 
	 * @author san
	 */
	public static String initImageMessage(String toUserName,
			String fromUserName, int type) {
		String message = null;
		String meidiaId = null;
		Image image = new Image(meidiaId);
		ImageMessage imageMessage = new ImageMessage();
		imageMessage.setFromUserName(toUserName);
		imageMessage.setToUserName(fromUserName);
		imageMessage.setMsgType(RESP_MESSAGE_TYPE_IMAGE);
		imageMessage.setCreateTime(new Date().getTime());
		imageMessage.setImage(image);
		message = imageMessageToXml(imageMessage);
		return message;
	}

	/**
	 * 视频消息
	 * 
	 * @author san
	 */
	public static String initVideoMessage(String toUserName, String fromUserName) {
		String message = null;
		String meidiaId = "tn9ew5GoV7ISsg0kzTFgGMHV13bZLKbFSdzmm6LHjRnwo5m-D76D9Mj854VEFRCw";
		Video video = new Video(meidiaId, "我不要上班", "一个美女上班的自述");
		VideoMessage videoMessage = new VideoMessage();
		videoMessage.setFromUserName(toUserName);
		videoMessage.setToUserName(fromUserName);
		videoMessage.setMsgType(RESP_MESSAGE_TYPE_VIDEO);
		videoMessage.setCreateTime(new Date().getTime());
		videoMessage.setVideo(video);
		message = videoMessageToXml(videoMessage);
		return message;
	}

	/**
	 * 语音消息-回复
	 */
	public static String initVoiceMessage(String toUserName, String fromUserName) {
		String message = null;
		String meidiaId = "AhtJGpzTrw8Iz9iGMtKwuhUan-LWOZS2HMWSrGG-ThGMEFprohvD2wtRLo4LZzjZ";
		Voice voice = new Voice(meidiaId);
		VoiceMessage voiceMessage = new VoiceMessage();
		voiceMessage.setFromUserName(toUserName);
		voiceMessage.setToUserName(fromUserName);
		voiceMessage.setMsgType(RESP_MESSAGE_TYPE_VOICE);
		voiceMessage.setCreateTime(new Date().getTime());
		voiceMessage.setVoice(voice);
		message = voiceMessageToXml(voiceMessage);
		return message;
	}

	/**
	 * 音乐消息-回复
	 * 
	 * @author san
	 */
	public static String initMusicMessage(String toUserName, String fromUserName) {
		String message = null;
		String title = "天空之城";
		String description = "";
		String musicUrl = "";
		String thumbMediaId = "fg0prqIzyghxYVHIOaRUv5DwvHXlAC_b9LnkfaQR35UUAlCglHQzBBmHABSjXeAq";
		String HQmusicUrl = "";

		Music music = new Music();
		music.setTitle(title);
		music.setDescription(description);
		music.setMusicUrl(musicUrl);
		music.setThumbMediaId(thumbMediaId);
		music.setHQMusicUrl(HQmusicUrl);
		MusicMessage musicMessage = new MusicMessage();
		musicMessage.setFromUserName(toUserName);
		musicMessage.setToUserName(fromUserName);
		musicMessage.setMsgType(RESP_MESSAGE_TYPE_MUSIC);
		musicMessage.setCreateTime(new Date().getTime());
		musicMessage.setMusic(music);
		message = musicMessageToXml(musicMessage);
		return message;
	}
}
