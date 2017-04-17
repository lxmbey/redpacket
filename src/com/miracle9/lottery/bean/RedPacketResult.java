package com.miracle9.lottery.bean;

/**
 * 抽奖返回
 */
public class RedPacketResult extends Result {
	public int type;

	public RedPacketResult(int type, int success, String message) {
		super(success, message);
		this.type = type;
	}

}
