package com.miracle9.lottery.bean;

public class InfoResult extends Result {
	public int canDrawNum;// 可抽奖次数
	public int isShare;

	public InfoResult(int success, String message, int canDrawNum, int isShare) {
		super(success, message);
		this.canDrawNum = canDrawNum;
		this.isShare = isShare;
	}

}
