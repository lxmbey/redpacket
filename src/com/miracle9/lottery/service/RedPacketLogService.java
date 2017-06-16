package com.miracle9.lottery.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miracle9.lottery.dao.BaseDao;
import com.miracle9.lottery.entity.RedPacketLog;

@Service
public class RedPacketLogService {

	@Autowired
	private BaseDao baseDao;

	public void add(RedPacketLog log) {
		baseDao.add(log);
	}

	public void update(RedPacketLog log) {
		baseDao.update(log);
	}

	public List<RedPacketLog> getAll() {
		return baseDao.getList(RedPacketLog.class, "from RedPacketLog where isSend = false");
	}

	public int getCount(String openId) {
		return baseDao.getCount("select count(*) from RedPacketLog where openId = ?", openId);
	}

	/**
	 * 统计已经发出去的红包个数
	 * 
	 * @param money 红包金额
	 * @return
	 */
	public int getCount(int money) {
		return baseDao.getCount("select count(*) from RedPacketLog where money = ? and isSend = true", money);
	}
}
