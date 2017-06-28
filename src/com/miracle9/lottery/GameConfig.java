package com.miracle9.lottery;

import java.util.Date;

public class GameConfig {
	private String beginDate;
	private String endDate;
	private String appId;
	private String appSecret;
	private String mchId;
	private String key;
	private String certsPath;// 证书路径
	private int beginHour;// 能抽中奖的开始时间点
	private int endHour;// 能抽中奖的结束时间点

	public Date begin;
	public Date end;

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCertsPath() {
		return certsPath;
	}

	public void setCertsPath(String certsPath) {
		this.certsPath = certsPath;
	}

	public int getBeginHour() {
		return beginHour;
	}

	public void setBeginHour(int beginHour) {
		this.beginHour = beginHour;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

}
