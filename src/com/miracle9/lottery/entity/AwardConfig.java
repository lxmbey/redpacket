package com.miracle9.lottery.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 奖项配置
 */
@Entity
public class AwardConfig {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int money;
	private int dayNum;
	private int hourNum;// 每天的开奖小时数

	@Transient
	private long dayinterval;// 其他日开奖间毫秒

	public AwardConfig() {

	}

	public AwardConfig(int money, int dayNum, int hourNum) {
		this.money = money;
		this.dayNum = dayNum;
		this.hourNum = hourNum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHourNum() {
		return hourNum;
	}

	public void setHourNum(int hourNum) {
		this.hourNum = hourNum;
	}

	public int getDayNum() {
		return dayNum;
	}

	public void setDayNum(int dayNum) {
		this.dayNum = dayNum;
	}

	public long getDayinterval() {
		return dayinterval;
	}

	public void setDayinterval(long dayinterval) {
		this.dayinterval = dayinterval;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

}
