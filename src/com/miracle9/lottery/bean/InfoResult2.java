package com.miracle9.lottery.bean;

public class InfoResult2 extends Result {
	public int id;

	public InfoResult2(int success, String message, int id) {
		super(success, message);
		this.id= id;
	}

}
