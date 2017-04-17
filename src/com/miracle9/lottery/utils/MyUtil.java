package com.miracle9.lottery.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class MyUtil {
	private static String str = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789";
	private static Random random = new Random();
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat orderIdSdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	/**
	 * 随机获取指定长度的字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getStr(int length) {
		char[] carr = str.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(carr[random.nextInt(carr.length)]);
		}

		return sb.toString();
	}

	public static Date dateformat(String date) {
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			LogManager.error(e);
			return null;
		}
	}

	/**
	 * 判断两个日期是否同一天
	 * 
	 * @param oneDate
	 * @param twoDate
	 * @return
	 */
	public static boolean isSameDay(Date oneDate, Date twoDate) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(oneDate);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(twoDate);

		return isSameDay(c1, c2);
	}

	/**
	 * 判断两个日期是否同一天
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public static boolean isSameDay(Calendar one, Calendar two) {
		return ((one.get(Calendar.YEAR) == two.get(Calendar.YEAR))
				&& (one.get(Calendar.DAY_OF_YEAR) == two.get(Calendar.DAY_OF_YEAR)));
	}

	public static int getCurrentHour() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 当前是否在指定时间范围内
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public static boolean isBetween(Date begin, Date end) {
		return isBetween(Calendar.getInstance().getTime(), begin, end);
	}

	/**
	 * 指定时间是否在两个时间点之间
	 * 
	 * @param checkPoint
	 * @param begin
	 * @param end
	 * @return
	 */
	public static boolean isBetween(Date checkPoint, Date begin, Date end) {
		return checkPoint.getTime() > begin.getTime() && checkPoint.getTime() < end.getTime();
	}

	/**
	 * 获取当前时间至指定时的点间隔毫秒数 如果当前钟点大于指定钟点数，则结果是当前时间至第二天指定时的间隔毫秒数
	 * 
	 * @param taskHour
	 * @param taskMiniute
	 * @return
	 */
	public static long betweenTaskHourMillis(int taskHour, int taskMiniute) {
		if (taskHour < 0) {
			taskHour = 0;
		}
		if (taskHour > 23) {
			taskHour = 23;
		}
		if (taskMiniute < 0) {
			taskMiniute = 0;
		}
		if (taskMiniute > 59) {
			taskMiniute = 59;
		}

		Calendar c = Calendar.getInstance();
		int nowHour = c.get(Calendar.HOUR_OF_DAY);
		if (nowHour > taskHour || (nowHour == taskHour && c.get(Calendar.MINUTE) >= taskMiniute)) {
			c.add(Calendar.DAY_OF_MONTH, 1);
		}
		c.set(Calendar.HOUR_OF_DAY, taskHour);
		c.set(Calendar.MINUTE, taskMiniute);
		c.set(Calendar.SECOND, 0);
		return c.getTimeInMillis() - System.currentTimeMillis();
	}

	/**
	 * 获取订单号
	 * 
	 * @return
	 */
	public static String getOrderId() {
		int i = random.nextInt(10);
		return orderIdSdf.format(new Date()) + i;
	}

	public static String md5(String str1) {
		// 用于加密的字符
		char md5String[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = str1.getBytes("ISO-8859-1");
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();

			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) { // i = 0
				byte byte0 = md[i]; // 95
				str[k++] = md5String[byte0 >>> 4 & 0xf]; // 5
				str[k++] = md5String[byte0 & 0xf]; // F
			}

			return new String(str).toUpperCase();
		} catch (Exception e) {
			LogManager.error(e);
			return str1;
		}
	}

	/**
	 * 根据Map组装xml消息体，值对象仅支持基本数据类型、String、BigInteger、BigDecimal，以及包含元素为上述支持数据类型的Map
	 * 
	 * @param vo
	 * @param rootElement
	 * @return
	 */
	public static String map2xmlBody(Map<String, Object> vo, String rootElement) {
		org.dom4j.Document doc = DocumentHelper.createDocument();
		Element body = DocumentHelper.createElement(rootElement);
		doc.add(body);
		__buildMap2xmlBody(body, vo);
		return doc.asXML();
	}

	private static void __buildMap2xmlBody(Element body, Map<String, Object> vo) {
		if (vo != null) {
			Iterator<String> it = vo.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (StringUtils.isNotEmpty(key)) {
					Object obj = vo.get(key);
					Element element = DocumentHelper.createElement(key);
					if (obj != null) {
						if (obj instanceof java.lang.String) {
							element.setText((String) obj);
						} else {
							if (obj instanceof java.lang.Character || obj instanceof java.lang.Boolean
									|| obj instanceof java.lang.Number || obj instanceof java.math.BigInteger
									|| obj instanceof java.math.BigDecimal) {
								org.dom4j.Attribute attr = DocumentHelper.createAttribute(element, "type",
										obj.getClass().getCanonicalName());
								element.add(attr);
								element.setText(String.valueOf(obj));
							} else if (obj instanceof java.util.Map) {
								org.dom4j.Attribute attr = DocumentHelper.createAttribute(element, "type",
										java.util.Map.class.getCanonicalName());
								element.add(attr);
								__buildMap2xmlBody(element, (Map<String, Object>) obj);
							} else {
							}
						}
					}
					body.add(element);
				}
			}
		}
	}
	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<>();
		map.put("abc", "asdfasdf");
		map.put("asdf", "asdfasdf");
		
		System.out.println(map2xmlBody(map, "lxm"));
	}
}
