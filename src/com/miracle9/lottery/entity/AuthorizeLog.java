package com.miracle9.lottery.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AuthorizeLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(nullable = false, unique = true)
	private String openId;
	private Date authorizeDate;
	private int canDrawNum = 1;// 可抽奖次数
	private boolean isShare;// 是否分享过

	public AuthorizeLog() {

	}

	public AuthorizeLog(String openId, Date authorizeDate) {
		this.openId = openId;
		this.authorizeDate = authorizeDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Date getAuthorizeDate() {
		return authorizeDate;
	}

	public void setAuthorizeDate(Date authorizeDate) {
		this.authorizeDate = authorizeDate;
	}

	public int getCanDrawNum() {
		return canDrawNum;
	}

	public void setCanDrawNum(int canDrawNum) {
		this.canDrawNum = canDrawNum;
	}

	public boolean isShare() {
		return isShare;
	}

	public void setShare(boolean isShare) {
		this.isShare = isShare;
	}

}
