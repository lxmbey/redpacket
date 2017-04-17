package com.miracle9.lottery.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TableInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String address;
	private String contact;
	private String phone;
	private int  countCheck;
	private int  countShare;
	
	public TableInfo(){};
	
	public TableInfo(int id, String address, String contact, String phone, int countCheck, int countShare) {
		super();
		this.id = id;
		this.address = address;
		this.contact = contact;
		this.phone = phone;
		this.countCheck = countCheck;
		this.countShare = countShare;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getCountCheck() {
		return countCheck;
	}

	public void setCountCheck(int countCheck) {
		this.countCheck = countCheck;
	}

	public int getCountShare() {
		return countShare;
	}

	public void setCountShare(int countShare) {
		this.countShare = countShare;
	}
	

}
