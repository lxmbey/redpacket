package com.miracle9.lottery.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.miracle9.lottery.DbThreads;
import com.miracle9.lottery.GameConfig;
import com.miracle9.lottery.GameController;
import com.miracle9.lottery.bean.InfoResult;
import com.miracle9.lottery.bean.InfoResult2;
import com.miracle9.lottery.bean.RedPacketResult;
import com.miracle9.lottery.bean.Result;
import com.miracle9.lottery.bean.SignResult;
import com.miracle9.lottery.bean.TimeValue;
import com.miracle9.lottery.entity.AuthorizeLog;
import com.miracle9.lottery.entity.RedPacketLog;
import com.miracle9.lottery.entity.TableDetilCheck;
import com.miracle9.lottery.entity.TableDetilShare;
import com.miracle9.lottery.entity.TableInfo;
import com.miracle9.lottery.service.AuthorizeLogService;
import com.miracle9.lottery.service.RedPacketLogService;
import com.miracle9.lottery.service.TableInfoService;
import com.miracle9.lottery.utils.HttpUtil;
import com.miracle9.lottery.utils.LogManager;
import com.miracle9.lottery.utils.MyUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/")
public class RedPacketController {
	private Gson gson = new Gson();
	private String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token";
	private String ticketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
	private String redpacketUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";

	@Autowired
	private RedPacketLogService redPacketLogService;
	@Autowired
	private GameConfig gameConfig;
	@Autowired
	private AuthorizeLogService authorizeLogService;
	@Autowired
	private GameController gameController;
	@Autowired
	private TableInfoService tableInfoService;

	public enum RedPacketType {
		FIRST_GET(0), AGAIN_GET(1), FIRST_NOT(2), AGAIN_NOT(3), AWARD_OVER(4), ACTIVITY_OVER(5), NOT_TIME(6);
		public int value;

		RedPacketType(int value) {
			this.value = value;
		}
	}

	/**
	 * 全局缓存
	 */
	public static Map<String, TimeValue> cache = new ConcurrentHashMap<>();

	// 抽奖
	@ResponseBody
	@RequestMapping(value = "/getResult", produces = "text/html;charset=UTF-8")
	public String getResult(final String openId) {
		// openId授权验证
		if (openId == null || !AuthorizeLogService.openidCacheMap.containsKey(openId)) {
			RedPacketResult result = new RedPacketResult(0, 0, "未授权用户");
			return gson.toJson(result);
		}
		if (!isOver()) {// 活动结束
			RedPacketResult result = new RedPacketResult(RedPacketType.ACTIVITY_OVER.value, 1, "");
			return gson.toJson(result);
		}
		if (!isCanDraw()) {// 未在时间段
			RedPacketResult result = new RedPacketResult(RedPacketType.AWARD_OVER.value, 1, "");
			return gson.toJson(result);
		}
		final AuthorizeLog log = AuthorizeLogService.openidCacheMap.get(openId);
		if (log.getCanDrawNum() <= 0) {
			RedPacketResult result = new RedPacketResult(RedPacketType.NOT_TIME.value, 1, "");
			return gson.toJson(result);
		}
		log.setCanDrawNum(log.getCanDrawNum() - 1);
		DbThreads.executor(new Runnable() {

			@Override
			public void run() {
				authorizeLogService.update(log);
			}
		});
		int money = gameController.draw();// 抽奖
		int type;
		if (money == 0) {
			if (!log.isShare()) {
				type = RedPacketType.FIRST_NOT.value;
			} else {
				type = RedPacketType.AGAIN_NOT.value;
			}
			RedPacketResult result = new RedPacketResult(type, 1, "");
			return gson.toJson(result);
		} else {
			if (!log.isShare()) {
				type = RedPacketType.FIRST_GET.value;
			} else {
				type = RedPacketType.AGAIN_GET.value;
			}
			RedPacketResult result = new RedPacketResult(type, 1, "");

			final int finalMoney = money;
			DbThreads.executor(new Runnable() {

				@Override
				public void run() {
					// 发红包
					Map<String, String> param = new HashMap<>();
					param.put("nonce_str", MyUtil.getStr(16));
					param.put("mch_id", gameConfig.getMchId());
					param.put("mch_billno", gameConfig.getMchId() + MyUtil.getOrderId());
					param.put("wxappid", gameConfig.getAppId());
					param.put("send_name", "鹏华基金");
					param.put("re_openid", openId);
					param.put("total_amount", String.valueOf(finalMoney));
					param.put("total_num", String.valueOf(1));
					param.put("wishing", "感谢您的夸赞，我们会继续努力的！");
					param.put("client_ip", getIp());
					param.put("act_name", "我被表扬了!赞下就送礼!");
					param.put("remark", "万份红包，快来抢！");

					Set<String> keys = new TreeSet<>(param.keySet());
					StringBuilder sb = new StringBuilder();
					for (String key : keys) {
						sb.append(key + "=" + param.get(key) + "&");
					}
					sb.append("key=" + gameConfig.getKey());
					String stringA = sb.toString();
					String sign = DigestUtils.md5Hex(stringA).toUpperCase();
					param.put("sign", sign);

					// 组装成XML传入
					sb = new StringBuilder("<xml>");
					for (String key : param.keySet()) {
						sb.append("<" + key + ">" + param.get(key) + "</" + key + ">");
					}
					sb.append("</xml>");
					boolean isSend = true;
					String reXml = "";
					try {
						reXml = sendSsl(sb.toString());
						Map<String, String> map = parseXml(reXml);
						String returnCode = map.get("return_code");
						if (!"SUCCESS".equals(returnCode)) {
							LogManager.info("openId " + openId + " send redpacket error：" + reXml);
							isSend = false;
						}
						String resultCode = map.get("result_code");
						if (!"SUCCESS".equals(resultCode)) {
							LogManager.info("openId " + openId + " send redpacket error：" + reXml);
							isSend = false;
						}
					} catch (Exception e) {
						LogManager.info("openId " + openId + " send redpacket error：" + reXml);
						LogManager.error(e);
						isSend = false;
					}
					// 保存到数据库
					redPacketLogService.add(new RedPacketLog(openId, finalMoney, isSend));
				}
			});
			return gson.toJson(result);
		}
	}

	private String sendSsl(String xmlParam) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File(gameConfig.getCertsPath()));
		keyStore.load(instream, gameConfig.getMchId().toCharArray());
		// Trust own CA and all self-signed certs
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, gameConfig.getMchId().toCharArray())
				.build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

		HttpPost httpPost = new HttpPost(redpacketUrl);
		StringEntity reqEntity = new StringEntity(xmlParam, "UTF-8");
		httpPost.setEntity(reqEntity);

		CloseableHttpResponse response = httpclient.execute(httpPost);

		HttpEntity entity = response.getEntity();
		StringBuffer message = new StringBuffer();
		if (entity != null) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
			String text;
			while ((text = bufferedReader.readLine()) != null) {
				message.append(text);
			}

		}
		EntityUtils.consume(entity);

		return message.toString();
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> parseXml(String msg) throws Exception {
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();

		// 从request中取得输入流
		InputStream inputStream = new ByteArrayInputStream(msg.getBytes("UTF-8"));

		// 读取输入流
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
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
	}

	private String getIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "";
		}
	}

	private boolean isCanDraw() {
		int hour = MyUtil.getCurrentHour();
		if (hour >= 10 && hour < 18) {
			return true;
		}
		return false;
	}

	private boolean isOver() {
		return MyUtil.isBetween(gameConfig.begin, gameConfig.end);
	}

	// 获取签名
	@ResponseBody
	@RequestMapping("/getSign")
	public String getSign(String url) {
		TimeValue token = cache.get("token");
		// 为空或过期
		if (token == null || System.currentTimeMillis() - token.time >= 719000) {
			String param = "grant_type=client_credential&appid=" + gameConfig.getAppId() + "&secret="
					+ gameConfig.getAppSecret();
			String accessToken = HttpUtil.sendPost(tokenUrl, param);
			if (accessToken == "") {
				Result result = new Result(0, "获取access_token异常");
				return gson.toJson(result);
			}
			JSONObject jo = JSONObject.fromObject(accessToken);
			String tokenStr = jo.getString("access_token");
			if (tokenStr == null) {
				Result result = new Result(0, jo.getString("errmsg"));
				return gson.toJson(result);
			}
			cache.put("token", new TimeValue(System.currentTimeMillis(), tokenStr));

			token = cache.get("token");
		}

		// 获取ticket
		TimeValue ticket = cache.get("ticket");
		if (ticket == null || System.currentTimeMillis() - ticket.time >= 719000) {
			String param = "access_token=" + token.value + "&type=jsapi";
			String jsapiTicket = HttpUtil.sendPost(ticketUrl, param);
			if (jsapiTicket == "") {
				Result result = new Result(0, "获取jsapi_ticket异常");
				return gson.toJson(result);
			}
			JSONObject jo = JSONObject.fromObject(jsapiTicket);
			String ticketStr = jo.getString("ticket");
			if (ticketStr == null) {
				Result result = new Result(0, jo.getString("errmsg"));
				return gson.toJson(result);
			}
			cache.put("ticket", new TimeValue(System.currentTimeMillis(), ticketStr));

			ticket = cache.get("ticket");
		}
		LogManager.info("ticket=" + ticket.value);
		String nonceStr = MyUtil.getStr(16);
		long timestamp = System.currentTimeMillis() / 1000;
		String signStr = "jsapi_ticket=" + ticket.value + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url="
				+ url;

		String signature = "";
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(signStr.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (Exception e) {
			LogManager.error(e);
		}
		SignResult sr = new SignResult(1, "", signature, timestamp, nonceStr, gameConfig.getAppId());
		return gson.toJson(sr);
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	// 获取授权接口地址
	@ResponseBody
	@RequestMapping("/getAuthorizeUrl")
	public String getAuthorizeUrl() {
		try {
			// snsapi_base
			// scope=snsapi_userinfo 弹出授权页面
			return "https://open.weixin.qq.com/connect/oauth2/authorize?" + "appid=" + gameConfig.getAppId()
					+ "&redirect_uri=" + URLEncoder.encode("http://h5.9shadow.com/redpacket/praise/index.html", "UTF-8")
					+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
		} catch (UnsupportedEncodingException e) {
			LogManager.error(e);
			return "";
		}
	}

	// 授权回调
	@ResponseBody
	@RequestMapping("/authorizeCallback")
	public String authorizeCallback(String code) {
		String getTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";
		String param = "appid=" + gameConfig.getAppId() + "&" + "secret=" + gameConfig.getAppSecret() + "&code=" + code
				+ "&grant_type=authorization_code";
		String tokenJson = HttpUtil.sendPost(getTokenUrl, param);
		if (tokenJson != "") {
			JSONObject jo = JSONObject.fromObject(tokenJson);
			String openid = jo.optString("openid");
			if (StringUtils.isNotBlank(openid) && !AuthorizeLogService.openidCacheMap.containsKey(openid)) {
				AuthorizeLog log = new AuthorizeLog(openid, new Date());
				authorizeLogService.add(log);
				AuthorizeLogService.openidCacheMap.put(openid, log);
			}
		}
		return tokenJson;
	}

	// 获取抽奖和分享信息
	@ResponseBody
	@RequestMapping(value = "/getInfo", produces = "text/html;charset=UTF-8")
	public String getInfo(String openId) {
		AuthorizeLog log = AuthorizeLogService.openidCacheMap.get(openId);
		if (log == null) {
			Result result = new Result(0, "未找到授权记录");
			return gson.toJson(result);
		}
		InfoResult r = new InfoResult(1, "", log.getCanDrawNum(), log.isShare() ? 1 : 0);
		return gson.toJson(r);
	}

	// 1、信息录入：
	@ResponseBody
	@RequestMapping(value = "/save", produces = "text/html;charset=UTF-8")
	public String save(HttpServletRequest request) {
		String address = request.getParameter("address");
		String contact = request.getParameter("contact");
		String phone = request.getParameter("phone");
		TableInfo info = new TableInfo();
		info.setAddress(address);
		info.setContact(contact);
		info.setPhone(phone);
		tableInfoService.add(info);
		int id = tableInfoService.maxID();
		InfoResult2 r = new InfoResult2(1, "", id);
		return gson.toJson(r);
	}

	// 2、转发统计：
	@ResponseBody
	@RequestMapping(value = "/share2", produces = "text/html;charset=UTF-8")
	public String share2(HttpServletRequest request) {
		int id = Integer.parseInt(request.getParameter("id"));
		TableInfo info = tableInfoService.getById(id);
		info.setCountShare(info.getCountShare() + 1);
		tableInfoService.update(info);

		TableDetilShare obj = new TableDetilShare();
		obj.setPid(id);
		obj.setDate(new Date());
		tableInfoService.addShare(obj);
		Result r = new Result(1, "");
		return gson.toJson(r);
	}

	// 3、阅读统计：
	@ResponseBody
	@RequestMapping(value = "/read", produces = "text/html;charset=UTF-8")
	public String read(HttpServletRequest request) {
		int id = Integer.parseInt(request.getParameter("id"));
		TableInfo info = tableInfoService.getById(id);
		info.setCountCheck(info.getCountCheck() + 1);
		tableInfoService.update(info);

		TableDetilCheck obj = new TableDetilCheck();
		obj.setPid(id);
		obj.setDate(new Date());
		tableInfoService.addCheck(obj);
		Result r = new Result(1, "");
		return gson.toJson(r);
	}

	// 获取抽奖和分享信息
	@ResponseBody
	@RequestMapping(value = "/share", produces = "text/html;charset=UTF-8")
	public String share(String openId) {
		synchronized (openId) {
			AuthorizeLog log = AuthorizeLogService.openidCacheMap.get(openId);
			if (log == null) {
				Result result = new Result(1, "未找到授权记录");
				return gson.toJson(result);
			}
			if (log.isShare()) {
				Result result = new Result(1, "不能重复分享");
				return gson.toJson(result);
			}
			log.setShare(true);
			log.setCanDrawNum(log.getCanDrawNum() + 1);
			authorizeLogService.update(log);

			Result result = new Result(1, "");
			return gson.toJson(result);
		}
	}

}
